package com.lm.im_huanxin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.ContactsSortModel;
import com.lm.im_huanxin.entity.FridendsEntity;
import com.lm.im_huanxin.ui.ChatActivity;
import com.lm.im_huanxin.utils.StickyHeaderAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/15.
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> implements StickyHeaderAdapter<FriendsAdapter.HeaderHolder> {

    private Context context;
    private long headId=0;
    List<ContactsSortModel> listItems;
    public FriendsAdapter(Context context, List<ContactsSortModel> listItems) {
        this.context=context;
        this.listItems=listItems;
    }

    public int getSectionForPosition(int position) {
        return listItems.get(position).getSortLetters().charAt(0);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i <getItemCount(); i++) {
            String sortStr = listItems.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_friends_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.v("onBindViewHolder",position+"");
        holder.mTvName.setText(listItems.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatActivity.class).
                        putExtra("contacts",listItems.get(position).getName()));
            }
        });

    }



    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public long getHeaderId(int position) {

        if (position==getPositionForSection(getSectionForPosition(position)))
        {
            position++;
        }
        Log.v("中文就是好辨认",position+"");
        return position;
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_friends_head_layout,parent,false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {

        if (position==getPositionForSection(getSectionForPosition(position))) {


                viewholder.mTvHead.setText(listItems.get(position).getSortLetters());
                viewholder.mTvHead.setVisibility(View.VISIBLE);

        }
        else
        {
            if (viewholder.mTvHead.getText().toString().equals(""))
            {
                viewholder.linearLayout.setVisibility(View.GONE);
            }
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTvName;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView= (ImageView) itemView.findViewById(R.id.item_friends_img);
            mTvName= (TextView) itemView.findViewById(R.id.item_friends_name);
        }
    }
    public class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView mTvHead;
        private LinearLayout linearLayout;
        public HeaderHolder(View itemView) {
            super(itemView);
            mTvHead= (TextView) itemView.findViewById(R.id.item_friends_head);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.head_layout);
        }
    }
}
