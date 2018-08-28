package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gjiazhe.wavesidebar.WaveSideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.ClubWallListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.global.json.ClubInfoConstants;
import ccoderad.bnds.shiyiquanevent.global.json.JsonConstants;
import ccoderad.bnds.shiyiquanevent.global.json.UserInfoConstants;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import cn.bingoogolapple.photopicker.util.BGASpaceItemDecoration;

public class ClubWallActivity extends AppCompatActivity implements RecyclerViewItemClickListener {

    private WaveSideBar mSidebar;
    private RecyclerView mClubWallList;
    private SearchView mClubListSearch;
    private FloatingActionButton mFab;
    private TextView mDataLoadingIndicator;

    private List<ClubModel> mDataList;
    private Map<String, Integer> mClubNameIndex = new HashMap<>();

    private RequestQueue mRequestQueue;

    private ClubWallListAdapter mAdapter;

    private final SearchPosition mSearchPosition = new SearchPosition();

    private void handleJsonData(String response) {
        try {
            JSONObject clubWallData = new JSONObject(response);
            JSONArray clubSection = clubWallData.getJSONArray("club_section");
            int clubCnt = 0;
            for (int i = 0; i < clubSection.length(); ++i) {
                JSONArray innerContainer = clubSection.getJSONArray(i);
                String index = innerContainer.getString(0);
                mClubNameIndex.put(index, clubCnt);
                JSONArray clubList = innerContainer.getJSONArray(1);
                for (int j = 0; j < clubList.length(); ++j) {
                    ClubModel model = new ClubModel(index);
                    JSONObject club = clubList.getJSONObject(j);
                    model.LargeAvatarURL = Utils.cleanAvatarURL(URLConstants.HOME_URL_WITHOUT_DASH
                            + club.getString(ClubInfoConstants.AVATAR_TAG), "small");
                    model.club_name = club.getJSONObject(JsonConstants.DATA_TAG)
                            .getString(ClubInfoConstants.FULL_NAME_TAG);
                    model.sname = club.getJSONObject(JsonConstants.DATA_TAG)
                            .getString(UserInfoConstants.UserClub.SIMP_NAME_TAG);
                    mDataList.add(model);
                    ++clubCnt;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSidebar.setVisibility(View.VISIBLE);
        mDataLoadingIndicator.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(this);
        ToastUtil.initialize(this);
        setContentView(R.layout.activity_club_wall);
        mSidebar = (WaveSideBar) findViewById(R.id.club_wall_sidebar);
        mClubWallList = (RecyclerView) findViewById(R.id.club_wall_recyclerview);
        mClubListSearch = (SearchView) findViewById(R.id.club_wall_list_searchview);
        mDataLoadingIndicator = (TextView) findViewById(R.id.club_wall_loading_indicator);
        mFab = (FloatingActionButton) findViewById(R.id.club_wall_back_to_top);

        mDataLoadingIndicator.setVisibility(View.VISIBLE);
        mDataList = new ArrayList<>();
        mAdapter = new ClubWallListAdapter(this, mDataList);
        mClubWallList.setAdapter(mAdapter);
        mClubWallList.addItemDecoration(new BGASpaceItemDecoration(5));
        mClubWallList.setLayoutManager(new LinearLayoutManager(this
                , LinearLayoutManager.VERTICAL, false));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClubWallList.smoothScrollToPosition(0);
            }
        });

        mClubListSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int searchStatus = 0;
                int firstPosition = -1;
                int startPosition = ((LinearLayoutManager) mClubWallList
                        .getLayoutManager()).findFirstVisibleItemPosition();
                for (int i = startPosition; i < mDataList.size(); ++i) {
                    if (mDataList.get(i).sname.equals(query)
                            || mDataList.get(i).club_name.contains(query)) {
                        searchStatus = 1;
                        firstPosition = firstPosition == -1 ? i : firstPosition;
                        if (mSearchPosition.position < i) {
                            ((LinearLayoutManager) mClubWallList.getLayoutManager())
                                    .scrollToPositionWithOffset(i, 0);
                            mSearchPosition.position = i;
                            searchStatus = 2;
                            return false;
                        }
                    }
                }
                if (searchStatus == 0) {
                    ToastUtil.makeText("没有搜索到结果", false);
                } else if (searchStatus == 1) {
                    ToastUtil.makeText("后面没有相关信息\n返回顶部", false);
                    mClubWallList.smoothScrollToPosition(0);
                    mSearchPosition.position = firstPosition;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchPosition.position = -1;
                return false;
            }
        });

        mAdapter.setOnItemClickListener(this);
        mSidebar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                ((LinearLayoutManager) mClubWallList.getLayoutManager())
                        .scrollToPositionWithOffset(mClubNameIndex.get(index), 0);
            }
        });

        String reqUrl = Utils.makeRequest(URLConstants.CLUB_WALL_URL
                , new String[]{"user-agent"}
                , new String[]{URLConstants.USER_AGENT});
        StringRequest request = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleJsonData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (Utils.isNetWorkAvailable(ClubWallActivity.this)) {
                    ToastUtil.makeText("AD1024或EricStdlib -1s", true);
                } else {
                    ToastUtil.makeText("出现了一些小问题", true);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Utils.createDefaultExtraHeader(ClubWallActivity.this);
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onClubItemClick(View v, int position) {
        Intent it = new Intent(this, ClubInfoActivity.class);
        it.putExtra("clubUrl", mDataList.get(position).sname);
        startActivity(it);
    }

    private class SearchPosition {
        int position;

        public SearchPosition() {
            position = -1;
        }
    }
}
