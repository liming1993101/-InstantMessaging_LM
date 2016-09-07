package com.lm.im_huanxin.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.adapter.FriendsAdapter;
import com.lm.im_huanxin.entity.ContactsSortModel;
import com.lm.im_huanxin.entity.FridendsEntity;
import com.lm.im_huanxin.ui.widget.PinyinComparator;
import com.lm.im_huanxin.ui.widget.PinyinUtils;
import com.lm.im_huanxin.ui.widget.SideBar;
import com.lm.im_huanxin.utils.DividerDecoration;
import com.lm.im_huanxin.utils.StickyHeaderDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private List<ContactsSortModel>listItmes=new ArrayList<ContactsSortModel>();
    private FriendsAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mTipTv;
    private SideBar mSideBar;
    List<String> userlist;
    private ProgressBar mProgressBar;
    public FriendsFragment() {
        // Required empty public constructor
    }


    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            userlist= msg.getData().getStringArrayList("list");
            initData();
            mProgressBar.setVisibility(View.GONE);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friends, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        mProgressBar= (ProgressBar) view.findViewById(R.id.contacts_pb);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.friends_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSideBar= (SideBar) view.findViewById(R.id.sidrbar);
        mTipTv= (TextView) view.findViewById(R.id.dialog_tv);
        Thread t=new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {

                    List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    Message msg=new Message();

                    Bundle bundle=new Bundle();
                    bundle.putStringArrayList("list", (ArrayList<String>) usernames);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

    }

    private void initData() {



        for (int i=0;i<userlist.size();i++)
        {
            ContactsSortModel model=new ContactsSortModel();
            model.setId(i+"");
            model.setName(userlist.get(i).toString());
            model.setMobilePhone(12345667+"");
            listItmes.add(model);
        }
        listItmes = filledData();
        Collections.sort(listItmes, new PinyinComparator());
        Log.v("tag1",listItmes.size()+"data");
        mAdapter=new FriendsAdapter(getActivity(),listItmes);
        final DividerDecoration divider = new DividerDecoration.Builder(getActivity())
                .setHeight(0f)
                .setPadding(0f)
                .setColorResource(R.color.white)
                .build();
        mRecyclerView.addItemDecoration(divider);
        Log.v("tag2",listItmes.size()+"data");
        StickyHeaderDecoration  decor = new StickyHeaderDecoration(mAdapter);
        Log.v("tag3",listItmes.size()+"data");
        mRecyclerView.addItemDecoration(decor, 1);
        Log.v("tag4",listItmes.size()+"data");
        mRecyclerView.setAdapter(mAdapter);

        mSideBar.setTextView(mTipTv);

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    manager.scrollToPosition(position);
                }
            }
        });
    }

    private List<ContactsSortModel> filledData() {
        List<ContactsSortModel> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        for (int i = 0; i <listItmes.size(); i++) {
            String pinyin = PinyinUtils.getPingYin(listItmes.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                listItmes.get(i).setSortLetters(sortString.toUpperCase());
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }
            mSortList.add(listItmes.get(i));
        }
        Collections.sort(indexString);
        mSideBar.setIndexText(indexString);
        listItmes.clear();
        return mSortList;
    }
}
