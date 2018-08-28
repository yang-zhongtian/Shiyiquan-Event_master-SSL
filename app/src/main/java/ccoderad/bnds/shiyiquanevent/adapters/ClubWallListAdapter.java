package ccoderad.bnds.shiyiquanevent.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.beans.ClubModel;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;
import ccoderad.bnds.shiyiquanevent.viewholders.ClubWallListViewHolder;

/**
 * Created by CCoderAD on 2017/3/11.
 */

public class ClubWallListAdapter extends RecyclerView.Adapter<ClubWallListViewHolder> {

    private List<ClubModel> mDataList;
    private RecyclerViewItemClickListener mlistener;
    private Context mparent;
    private LayoutInflater mInflater;

    public ClubWallListAdapter(Context context, List<ClubModel> data) {
        mparent = context;
        mDataList = data;
        mInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener listener) {
        this.mlistener = listener;
    }

    @Override
    public ClubWallListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ClubWallListViewHolder holder =
                new ClubWallListViewHolder
                        (mInflater.inflate(R.layout.club_wall_list_item, parent, false), mlistener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ClubWallListViewHolder holder, int position) {
        ClubModel model = mDataList.get(position);
        if(position == 0 || !model.mIndex.equals(mDataList.get(position-1).mIndex)){
            holder.mIndex.setText(model.mIndex);
            holder.mIndex.setVisibility(View.VISIBLE);
        }else{
            holder.mIndex.setVisibility(View.GONE);
        }
        holder.clubAvatar.setImageURI(Uri.parse(mDataList.get(position).LargeAvatarURL));
        holder.mClubName.setText(mDataList.get(position).club_name);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
