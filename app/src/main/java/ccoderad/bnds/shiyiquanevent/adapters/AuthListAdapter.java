package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.AuthItemModel;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.utils.Utils;

/**
 * Created by CCoderAD on 2017/3/16.
 */

public class AuthListAdapter extends RecyclerView.Adapter<AuthListAdapter.AuthListViewHolder> {

    private List<AuthItemModel> mDatalist;
    private Context context;
    private LayoutInflater mInflater;
    private RecyclerViewItemClickListener mListener = null;

    public AuthListAdapter(Context parent, List<AuthItemModel> data) {
        context = parent;
        mDatalist = data;
        mInflater = LayoutInflater.from(parent);
    }

    public void setItemOnclickListener(RecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public void modifyDataSet(List<AuthItemModel> data) {
        mDatalist = data;
    }

    @Override
    public AuthListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new AuthListViewHolder
                (mInflater.inflate(R.layout.auth_list_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(AuthListViewHolder holder, int position) {
        AuthItemModel data = mDatalist.get(position);
        holder.mStatus.setText(data.authStatus);
        holder.mClubName.setText(data.clubName);
        holder.mModifyDate.setText(data.modifyDate);
        holder.itemView.setTag(position);
        holder.mId.setText("#" + Utils.Int2String(position + 1));
    }

    @Override
    public int getItemCount() {
        return mDatalist.size();
    }

    class AuthListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewItemClickListener mListener;
        private TextView mModifyDate;
        private TextView mClubName;
        private TextView mStatus;
        private TextView mId;

        public AuthListViewHolder(View itemView) {
            super(itemView);
            mModifyDate = (TextView) itemView.findViewById(R.id.auth_list_item_modify_date);
            mClubName = (TextView) itemView.findViewById(R.id.auth_list_item_sname);
            mStatus = (TextView) itemView.findViewById(R.id.auth_list_status);
        }

        public AuthListViewHolder(View itemView, RecyclerViewItemClickListener listener) {
            super(itemView);
            mListener = listener;
            mModifyDate = (TextView) itemView.findViewById(R.id.auth_list_item_modify_date);
            mClubName = (TextView) itemView.findViewById(R.id.auth_list_item_sname);
            mStatus = (TextView) itemView.findViewById(R.id.auth_list_status);
            mId = (TextView) itemView.findViewById(R.id.auth_list_item_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClubItemClick(v, (int) v.getTag());
            }
        }
    }
}
