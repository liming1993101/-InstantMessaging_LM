package com.lm.im_huanxin.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.adapter.ContactRequestsAdapter;
import com.lm.im_huanxin.entity.InviteInfoEntity;
import com.lm.im_huanxin.utils.Contstants;
import com.lm.im_huanxin.utils.InviteInfoDB;
import com.lm.im_huanxin.utils.SharePreferenceUtil;

import java.util.List;

public class ContactRequestctivity extends AppCompatActivity implements ContactRequestsAdapter.UpdataDBListener{

    private TextView mReturn;
    private RecyclerView mRecyclerView;
    private List<InviteInfoEntity>listItems;
    private ContactRequestsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_requestctivity);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mReturn= (TextView) findViewById(R.id.contact_request_return);
        mRecyclerView= (RecyclerView) findViewById(R.id.requset_list);
    }
    private void initData() {

        listItems=new InviteInfoDB(this).getInviteInfo();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new ContactRequestsAdapter(this,listItems);
        mAdapter.setUpDataListener(this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPosition(listItems.size()-1);
    }

    private void initListener()
    {
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void upData(int id, String user, boolean btType,boolean inviteType,String groupID) {

        InviteInfoDB db=new  InviteInfoDB(ContactRequestctivity.this);
        if (btType)
        {
            if (inviteType)
            {
                try {
                    EMClient.getInstance().groupManager().acceptInvitation(groupID,user);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
            else {
             try {
                    EMClient.getInstance().contactManager().acceptInvitation(user);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }}


            db.disposeState(id);

        }
        else
        {
            if (!btType) {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(user);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    EMClient.getInstance().groupManager().declineInvitation(groupID,user,"你太丑了");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
            db.disposeState(id);
        }
        listItems=new InviteInfoDB(this).getInviteInfo();
        mAdapter.setListItems(listItems);
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPosition(listItems.size()-1);
        mAdapter.notifyDataSetChanged();
        db.colse();
    }
}
