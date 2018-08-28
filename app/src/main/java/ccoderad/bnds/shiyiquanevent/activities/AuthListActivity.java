package ccoderad.bnds.shiyiquanevent.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.AuthListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.AuthItemModel;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.global.json.AuthDataConstants;
import ccoderad.bnds.shiyiquanevent.global.json.JsonConstants;
import ccoderad.bnds.shiyiquanevent.global.json.UserInfoConstants;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import cn.bingoogolapple.photopicker.util.BGASpaceItemDecoration;

public class AuthListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private List<String> mCategoryData;
    private Map<String, List<AuthItemModel>> mDataMap;

    private RecyclerView mAuthList;
    private Spinner mStatusFilter;
    private Spinner mClubCategoryFilter;
    private ProgressBar mLoadingProgressBar;
    private TextView mNoDataIndicator;

    private RequestQueue mRequestQueue;
    private AuthListAdapter mAdapter;

    private void filterData() {
        String category = mClubCategoryFilter.getSelectedItem().toString();
        String status = mStatusFilter.getSelectedItem().toString();
        List<AuthItemModel> data = new ArrayList<>();
        data.addAll(mDataMap.get(category));
        if (!status.equals("全部")) {
            for (int i = data.size() - 1; i >= 0; --i) {
                if (!data.get(i).authStatus.equals(status)) {
                    data.remove(i);
                }
            }
        }
        if (data.size() == 0) {
            mNoDataIndicator.setVisibility(View.VISIBLE);
        } else {
            mNoDataIndicator.setVisibility(View.GONE);
        }
        mAdapter.modifyDataSet(data);
        mAdapter.notifyDataSetChanged();
    }

    private void handleRawData(String response) {
        try {
            JSONObject authData = new JSONObject(response);
            if (mCategoryData.size() == 0) {
                JSONArray categoryArray = authData.getJSONArray(AuthDataConstants.CATEGORY_TAG);
                mCategoryData.add("全部");
                for (int i = 0; i < categoryArray.length(); ++i) {
                    String cat = categoryArray
                            .getJSONArray(i)
                            .getString(AuthDataConstants.CATEGORY_IDX);
                    mDataMap.put(cat, new ArrayList<AuthItemModel>());
                    mCategoryData.add(cat);
                }
                mClubCategoryFilter
                        .setAdapter(
                                new ArrayAdapter<String>
                                        (this, android.R.layout.simple_list_item_1
                                                , mCategoryData));
            }
            JSONArray authItemArray = authData.getJSONArray(AuthDataConstants.AUTH_RECORD_TAG);
            for (int i = 0; i < authItemArray.length(); ++i) {
                AuthItemModel model = new AuthItemModel();
                JSONObject obj = authItemArray.getJSONObject(i);
                JSONObject clubInfo = obj.getJSONObject("club");
                String category = obj.getString(AuthDataConstants.CATEGORY_ZHCN_TAG);
                String status = obj.getString(AuthDataConstants.STATUS_TAG);
                model.category = category;
                model.authStatus = status;
                model.modifyDate = obj.getString(JsonConstants.DATE_TAG);
                model.clubName = clubInfo.getString(UserInfoConstants.UserClub.FULL_NAME_TAG);
                if (model.clubName.length() > 18
                        && model.clubName.matches("^[A-Za-z0-9\\s]+$")) {
                    int mid = model.clubName.length() >> 1;
                    model.clubName = model.clubName.substring(0, mid)
                            + "\n-" + model.clubName.substring(mid - 1);
                } else if (model.clubName.length() > 15) {
                    int mid = model.clubName.length() >> 1;
                    model.clubName = model.clubName.substring(0, mid)
                            + "\n-" + model.clubName.substring(mid - 1);
                }
                model.simpName = clubInfo.getString(UserInfoConstants.UserClub.SIMP_NAME_TAG);
                mDataMap.get(category).add(model);
                mDataMap.get("全部").add(model);
            }
            if (mDataMap.get("全部").size() != 0) {
                mNoDataIndicator.setVisibility(View.GONE);
                mLoadingProgressBar.setVisibility(View.GONE);
                mAdapter.modifyDataSet(mDataMap.get("全部"));
                mAdapter.notifyDataSetChanged();
            } else {
                mNoDataIndicator.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ToastUtil.initialize(this);
        mRequestQueue = Volley.newRequestQueue(this);
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_list);

        mDataMap = new HashMap<>();
        mCategoryData = new ArrayList<>();
        mDataMap.put("全部", new ArrayList<AuthItemModel>());

        mAuthList = (RecyclerView) findViewById(R.id.auth_list);
        mStatusFilter = (Spinner) findViewById(R.id.auth_list_status_spinner);
        mClubCategoryFilter = (Spinner) findViewById(R.id.auth_list_category_spinner);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.auth_list_progressbar);
        mNoDataIndicator = (TextView) findViewById(R.id.auth_list_no_data_indicator);

        mStatusFilter.setOnItemSelectedListener(this);
        mClubCategoryFilter.setOnItemSelectedListener(this);

        mAuthList.setLayoutManager
                (new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new AuthListAdapter(this, mDataMap.get("全部"));
        mAuthList.setAdapter(mAdapter);
        mAuthList.addItemDecoration(new BGASpaceItemDecoration(10));
        String reqUrl = Utils
                .makeRequest("auth/apply"
                        , new String[]{"user-agent"}
                        , new String[]{URLConstants.USER_AGENT});
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleRawData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (Utils.isNetWorkAvailable(AuthListActivity.this)) {
                    ToastUtil.makeText("AD1024或Eric Stdlib被折寿1s", false);
                } else {
                    ToastUtil.makeText("出现了一点小问题", true);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Utils.createDefaultExtraHeader(AuthListActivity.this);
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mCategoryData.size() > 0) {
            filterData();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
