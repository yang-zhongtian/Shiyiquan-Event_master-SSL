package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.utils.Utils;

/**
 * Created by CCoderAD on 2017/3/18.
 */

public class EventSquareListAdapter
        extends RecyclerView.Adapter<EventSquareListAdapter.EventSquareListViewHolder> {

    private List<EventBean> mDataList;
    private LayoutInflater mInflater;
    private RecyclerViewItemClickListener mListener;

    public EventSquareListAdapter(Context context, List<EventBean> data) {
        mInflater = LayoutInflater.from(context);
        mDataList = data;
    }

    @Override
    public EventSquareListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new EventSquareListViewHolder
                (mInflater.inflate(R.layout.event_square_list_item, parent, false), mListener);
    }

    public void setItemOnClickListener(RecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(EventSquareListViewHolder holder, int position) {
        EventBean data = mDataList.get(position);
        holder.itemView.setTag(position);
        holder.title.setText(data.eventTitle);
        holder.duration.setText(data.eventDuration);
        holder.location.setText(data.eventLocation);
        holder.sponsor.setText(data.sponsorName);
        holder.date.setText(data.eventDate);
        holder.time.setText(data.eventTime);
        holder.follower.setText(Utils.Int2String(data.eventFollower));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class EventSquareListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView sponsor;
        public TextView location;
        public TextView follower;
        public TextView duration;
        public TextView date;
        public TextView time;

        private RecyclerViewItemClickListener mListener;

        public EventSquareListViewHolder(View itemView, RecyclerViewItemClickListener listener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.square_event_list_item_title);
            sponsor = (TextView) itemView.findViewById(R.id.square_event_list_item_sponsor);
            location = (TextView) itemView.findViewById(R.id.square_event_list_item_location);
            time = (TextView) itemView.findViewById(R.id.square_event_list_item_time);
            follower = (TextView) itemView.findViewById(R.id.square_event_list_item_follower);
            duration = (TextView) itemView.findViewById(R.id.square_event_list_duration);
            date = (TextView) itemView.findViewById(R.id.square_event_list_item_date);
            itemView.setOnClickListener(this);
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClubItemClick(v, (int)v.getTag());
            }
        }
    }
}
