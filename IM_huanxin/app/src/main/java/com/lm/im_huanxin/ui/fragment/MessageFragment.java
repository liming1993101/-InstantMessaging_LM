package com.lm.im_huanxin.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lm.im_huanxin.R;
import com.lm.im_huanxin.RxBus.RxBus;
import com.lm.im_huanxin.RxBus.RxBusResult;
import com.lm.im_huanxin.adapter.MessageListAdapter;
import com.lm.im_huanxin.entity.MessageListEntity;
import com.lm.im_huanxin.ui.MainActivity;
import com.lm.im_huanxin.utils.DividerDecoration;
import com.lm.im_huanxin.utils.RecentContactsDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {


    private RxBus rxBus = RxBus.getInstance();
    private MessageListAdapter adapter;
    private List<MessageListEntity>listItems=new ArrayList<MessageListEntity>();
    private RecyclerView mRecyclerView;
    public MessageFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_message, container, false);
        initView(view);
        initData();
        rxBus.toObserverableOnMainThread("msg", new RxBusResult() {
            @Override
            public void onRxBusResult(Object o) {

                RecentContactsDB db=new RecentContactsDB(getActivity());
                listItems=db.getRctContacts();
                db.colse();
                adapter.setListItems(listItems);
                adapter.notifyDataSetChanged();
                Log.e("rxbus",o.toString() );
            }
        });

//        rxBus.toObserverableOnMainThread("chatmsg", new RxBusResult() {
//            @Override
//            public void onRxBusResult(Object o) {
//
//                RecentContactsDB db=new RecentContactsDB(getActivity());
//                listItems=db.getRctContacts();
//                db.colse();
//                adapter.setListItems(listItems);
//                adapter.notifyDataSetChanged();
//                Log.e("rxbus1",o.toString() );
//              Toast.makeText(getActivity(), "second收到消息;", Toast.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        RecentContactsDB db=new RecentContactsDB(getActivity());
        listItems=db.getRctContacts();
        db.colse();
        adapter.setListItems(listItems);
        adapter.notifyDataSetChanged();
    }

    private void initView(View view) {
       mRecyclerView= (RecyclerView) view.findViewById(R.id.message_rv);
    }

    private void initData()
    {
        RecentContactsDB db=new RecentContactsDB(getActivity());
        listItems=db.getRctContacts();
        db.colse();
        adapter=new MessageListAdapter(getActivity(),listItems);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final DividerDecoration divider = new DividerDecoration.Builder(getActivity())
                .setHeight(R.dimen.rv_item_line)
                .setPadding(0f)
                .setColorResource(R.color.bg_address_bk)
                .build();
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxBus.release();
    }
}
