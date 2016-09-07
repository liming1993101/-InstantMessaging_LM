package com.lm.im_huanxin.ui.fragment;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.adapter.GroupAdapter;
import com.lm.im_huanxin.ui.CreateGroupActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements View.OnClickListener {


    private GroupAdapter mAdapter;
    public GroupFragment() {
        // Required empty public constructor
    }

    private   Handler handler;
    private RecyclerView mRecyclerView;
    private LinearLayout mInsertGroup;
    private LinearLayout mInsertPbGroup;
    private List<EMGroup>listGroup=new ArrayList<EMGroup>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_group, container, false);




        initView(view);
        initThread();
        return view;
    }

    private void initThread() {

        Observable.create(new Observable.OnSubscribe<List<EMGroup> >() {
            @Override
            public void call(Subscriber<? super List<EMGroup> > subscriber) {
                List<EMGroup> grouplist = null;
                try {
                    grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(grouplist);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<List<EMGroup>>() {
                    @Override
                    public void onNext(List<EMGroup>  list) {
                        mAdapter.setList(list);
                        mAdapter.notifyDataSetChanged();

//                        ViewGroup.LayoutParams linearParams1 = mRecyclerView.getLayoutParams();
//                        linearParams1.height = (int) (200*getActivity().getResources().getDisplayMetrics().density*(mRecyclerView.getChildCount()));
//                        mRecyclerView.setLayoutParams(linearParams1);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    private void initView(View view) {
        mRecyclerView= (RecyclerView) view.findViewById(R.id.group_rv);
        mInsertGroup= (LinearLayout) view.findViewById(R.id.insert_group);
        mInsertPbGroup= (LinearLayout) view.findViewById(R.id.insert_public);
        mInsertPbGroup.setOnClickListener(this);
        mInsertGroup.setOnClickListener(this);
        mAdapter=new GroupAdapter(getActivity(),listGroup);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.insert_group:
                startActivity(new Intent(getActivity(),CreateGroupActivity.class));
                break;
            case R.id.insert_public:
                startActivity(new Intent(getActivity(),CreateGroupActivity.class));
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        initThread();
    }
}
