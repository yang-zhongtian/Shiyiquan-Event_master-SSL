package ccoderad.bnds.shiyiquanevent.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.adapters.EventSquareListAdapter;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;
import ccoderad.bnds.shiyiquanevent.global.json.ClubInfoConstants;
import ccoderad.bnds.shiyiquanevent.global.json.EventConstants;
import ccoderad.bnds.shiyiquanevent.global.json.JsonConstants;
import ccoderad.bnds.shiyiquanevent.utils.MultiThreadUtil;
import ccoderad.bnds.shiyiquanevent.utils.ToastUtil;
import ccoderad.bnds.shiyiquanevent.utils.Utils;
import cn.bingoogolapple.photopicker.util.BGASpaceItemDecoration;

public class EventSquareActivity extends AppCompatActivity implements XRecyclerView.LoadingListener {

    private List<EventBean> mDataList;

    private XRecyclerView mEventList;
    private ProgressBar pgLoading;

    private RequestQueue mQueue;
    private EventSquareListAdapter mAdapter;
    private int indexCount = 1;

    private void handleRawData(String data) {
        try {
            JSONObject outData = new JSONObject(data);
            JSONArray eventList = outData.getJSONArray(ClubInfoConstants.SQUARE_EVENT_LIST_TAG);
            for (int i = 0; i < eventList.length(); ++i) {
                EventBean eventData = new EventBean();
                JSONObject event = eventList.getJSONObject(i);
                eventData.eventAvatar = event.getString(ClubInfoConstants.AVATAR_TAG);
                eventData.eventDate = event.getString(EventConstants.DAY_SET_TAG);
                eventData.eventDuration = event.getString(EventConstants.TIME_LAST_TAG);
                eventData.eventTime = event.getString(EventConstants.TIME_SET_TAG);
                eventData.sponsorName = event.getString(EventConstants.SPONSOR_FNAME_TAG);
                eventData.sponsorSname = event.getString(EventConstants.SPONSOR_SNAME);
                eventData.eventFollower = event.getInt(EventConstants.FOLLOWER_TAG);

                JSONObject innerData = event.getJSONObject(JsonConstants.DATA_TAG);
                eventData.eventTitle = innerData.getString(EventConstants.TITLE_TAG);
                eventData.eventContent = innerData.getString(EventConstants.CONTENT_TAG);
                eventData.eventLocation = innerData.getString(EventConstants.LOCATION_TAG);
                mDataList.add(eventData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("ClubNum", Utils.Int2String(mDataList.size()));
        mAdapter.notifyDataSetChanged();
        pgLoading.setVisibility(View.GONE);
        mEventList.refreshComplete();
        mEventList.loadMoreComplete();
    }

    private void makeReq(int index) {
        String reqUrl = "";
        if (index == -1) {
            reqUrl = Utils.makeRequest("square"
                    , new String[]{"user-agent"}
                    , new String[]{URLConstants.USER_AGENT});
        } else {
            reqUrl = Utils.makeRequest("square"
                    , new String[]{"user-agent", "index"}
                    , new String[]{URLConstants.USER_AGENT, Utils.Int2String(index)});
        }
        Log.i("Req", reqUrl);
        StringRequest request = new StringRequest(Request.Method.GET, reqUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleRawData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (!Utils.isNetWorkAvailable(EventSquareActivity.this)) {
                    ToastUtil.makeText("请检查网络连接", true);
                } else {
                    ToastUtil.makeText("AD1024或Eric Stdlib被折寿1s", true);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Utils.createDefaultExtraHeader(EventSquareActivity.this);
            }
        };
        request.setRetryPolicy(MultiThreadUtil.createDefaultRetryPolicy());
        mQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtil.initialize(this);
        mQueue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_event_square);

        mEventList = (XRecyclerView) findViewById(R.id.event_square_list);
        pgLoading = (ProgressBar) findViewById(R.id.event_square_pgbar);

        mEventList.setLayoutManager
                (new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mDataList = new ArrayList<>();
        mAdapter = new EventSquareListAdapter(this, mDataList);

        mEventList.setAdapter(mAdapter);
        mEventList.addItemDecoration(new BGASpaceItemDecoration(10));
        mEventList.setLoadingMoreEnabled(true);
        mEventList.setPullRefreshEnabled(true);
        mEventList.setLoadingListener(this);

        makeReq(-1);
    }

    @Override
    public void onRefresh() {
        mDataList.clear();
        makeReq(-1);
        indexCount = 1;
    }

    @Override
    public void onLoadMore() {
        makeReq(++indexCount);
    }
}
