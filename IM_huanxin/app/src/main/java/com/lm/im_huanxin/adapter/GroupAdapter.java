package com.lm.im_huanxin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hyphenate.chat.EMGroup;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.emoji.Image;
import com.lm.im_huanxin.entity.GroupInfoEntity;
import com.lm.im_huanxin.ui.GroupInfoActivity;

import java.io.Serializable;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by Administrator on 2016/8/25.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context context;
    private List<EMGroup>list;

    public void setList(List<EMGroup> list) {
        this.list = list;
    }

    public GroupAdapter(Context context, List<EMGroup> listGroup) {
        this.context=context;
        list=listGroup;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_friends_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.groupName.setText(list.get(position).getGroupName());
        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                GroupInfoEntity entity=new GroupInfoEntity();
                EMGroup group=list.get(position);
                entity.setGroupDesc(group.getDescription());
                entity.setGroupID(group.getGroupId());
                entity.setGroupMember(group.getMembers());
                entity.setGroupName(group.getGroupName());
                entity.setGroupOwner(group.getOwner());
                context.startActivity(new Intent(context, GroupInfoActivity.class).putExtra("group_info", entity));
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView groupHeadIcon;
        private TextView groupName;
        public ViewHolder(View itemView) {
            super(itemView);
            groupName= (TextView) itemView.findViewById(R.id.item_friends_name);
            groupHeadIcon= (ImageView) itemView.findViewById(R.id.item_friends_img);
        }
    }
}
