package com.lm.im_huanxin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.entity.InviteInfoEntity;
import com.lm.im_huanxin.ui.ContactRequestctivity;
import com.lm.im_huanxin.utils.Contstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class ContactRequestsAdapter extends RecyclerView.Adapter<ContactRequestsAdapter.ViewHolder>{

    private List<InviteInfoEntity>listItems=new ArrayList<InviteInfoEntity>();
    private Context context;
    private static int TYPE1=1;
    private static int TYPE2=2;

    public void setListItems(List<InviteInfoEntity> listItems) {
        this.listItems = listItems;
    }

    public  UpdataDBListener updataDBListener;

    public ContactRequestsAdapter(Context context, List<InviteInfoEntity> listItems) {
        this.context=context;
        this.listItems=listItems;
    }

    public interface UpdataDBListener
    {
        void upData(int id,String user,boolean btType,boolean inviteType,String groupID);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_inviter_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final InviteInfoEntity entity=listItems.get(position);
        if (entity.getInviterType()==Contstants.CONTACTS_FROM_INVITE)
        {
            holder.mTvState.setVisibility(View.GONE);
            holder.mTvReson.setText(entity.getInviterReson());
            holder.mBtAccpet.setVisibility(View.VISIBLE);
            holder.mBtNo.setVisibility(View.VISIBLE);
        }
        else if (entity.getInviterType()==Contstants.GROUP_FROM_INVITE)
        {
            holder.mTvState.setVisibility(View.GONE);
            holder.mTvReson.setText(entity.getInviterReson());
            holder.mBtAccpet.setVisibility(View.VISIBLE);
            holder.mBtNo.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.mBtAccpet.setVisibility(View.GONE);
            holder.mBtNo.setVisibility(View.GONE);
            holder.mTvReson.setVisibility(View.GONE);
        }
        if (entity.getDispose()==2)
        {
            holder.mTvReson.setVisibility(View.VISIBLE);
            holder.mTvState.setVisibility(View.VISIBLE);
            holder.mBtAccpet.setVisibility(View.GONE);
            holder.mBtNo.setVisibility(View.GONE);
        }
        holder.mTvReson.setText("留言:"+entity.getInviterReson());
        holder.mBtAccpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean a=false;
                if (entity.getInviterType()==Contstants.GROUP_FROM_INVITE)
                {
                    a=true;
                }
                updataDBListener.upData(entity.getId(),entity.getInviter(),true,a,entity.getGroupID());
            }
        });
        holder.mBtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean a=false;
                if (entity.getInviterType()==Contstants.GROUP_FROM_INVITE)
                {
                    a=true;
                }
                updataDBListener.upData(entity.getId(),entity.getInviter(),false,a,entity.getGroupID());
            }
        });
        holder.mTvInfo.setText(entity.getInviterInfo());
    }




    @Override
    public int getItemCount() {
        return listItems.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTvInfo;
        private TextView mTvState;
        private Button mBtAccpet;
        private Button mBtNo;
        private TextView mTvReson;
        public ViewHolder(View itemView) {
            super(itemView);
            mTvInfo= (TextView) itemView.findViewById(R.id.invite_re_tv);
            mTvState= (TextView) itemView.findViewById(R.id.invite_state);
            mBtAccpet= (Button) itemView.findViewById(R.id.invite_accpet_bt);
            mBtNo= (Button) itemView.findViewById(R.id.invite_no_bt);
            mTvReson= (TextView) itemView.findViewById(R.id.invite_reson);
        }
    }

    public void setUpDataListener(Context context)
    {
        updataDBListener= (UpdataDBListener) context;
    }
}
