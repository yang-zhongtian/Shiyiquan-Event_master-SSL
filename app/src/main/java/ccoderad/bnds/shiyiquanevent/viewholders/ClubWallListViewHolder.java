package ccoderad.bnds.shiyiquanevent.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.listeners.RecyclerViewItemClickListener;

/**
 * Created by CCoderAD on 2017/3/11.
 */

public class ClubWallListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private RecyclerViewItemClickListener mListener;
    public SimpleDraweeView clubAvatar;
    public TextView mClubName;
    public TextView mIndex;

    public ClubWallListViewHolder(View itemView) {
        super(itemView);
    }

    public ClubWallListViewHolder(View itemView, RecyclerViewItemClickListener listener){
        super(itemView);
        mListener = listener;
        clubAvatar = (SimpleDraweeView) itemView.findViewById(R.id.club_wall_list_item_avatar);
        mClubName = (TextView) itemView.findViewById(R.id.club_wall_list_item_name);
        mIndex = (TextView) itemView.findViewById(R.id.club_wall_list_item_index);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onClubItemClick(v, (int)v.getTag());
        }
    }
}
