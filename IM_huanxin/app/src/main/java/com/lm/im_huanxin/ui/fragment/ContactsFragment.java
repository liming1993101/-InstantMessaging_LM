package com.lm.im_huanxin.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.adapter.FragmentAdapter;
import com.lm.im_huanxin.entity.GroupInfoEntity;
import com.lm.im_huanxin.ui.GroupInfoActivity;
import com.lm.im_huanxin.utils.ToastUtil;

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
public class ContactsFragment extends Fragment {


    public ContactsFragment() {
        // Required empty public constructor
    }

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SearchView mSearchView;
    private List<EMGroup> grouplist;
    private SearchHistoryTable mHistoryDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friend, container, false);
        initView(view);
        initData();
        initThread();

        return view;
    }

    private void initSearchView(final List<String>listContacts) {
        grouplist= EMClient.getInstance().groupManager().getAllGroups();
        List<SearchItem> suggestionsList = new ArrayList<>();
        for (int i=0;i<listContacts.size();i++)
        {
            suggestionsList.add(new SearchItem("好友   "+listContacts.get(i).toString()));
        }
        for (int i=0;i<grouplist.size();i++)
        {
            suggestionsList.add(new SearchItem("群   "+grouplist.get(i).getGroupName()+"  "+grouplist.get(i).getGroupId()));
        }
        SearchAdapter searchAdapter = new SearchAdapter(getActivity(), suggestionsList);
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mSearchView.close(false);
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                String query = textView.getText().toString();

                for (int i=0;i<listContacts.size();i++)
                {
                    if (query.equals("好友   "+listContacts.get(i).toString()))
                    {
                        new ToastUtil(getActivity()).longToast("暂未开放好友资料查询");
                    }
                }
                for (int i=0;i<grouplist.size();i++)
                {
                    if (query.equals("群   "+grouplist.get(i).getGroupName()+"  "+grouplist.get(i).getGroupId()))
                    {
                        getDataPage(i);
                    }
                }
            }
        });
        mSearchView.setAdapter(searchAdapter);
    }
    private void getDataPage(int position)
    {

            GroupInfoEntity entity = new GroupInfoEntity();
            EMGroup group = grouplist.get(position);
            entity.setGroupDesc(group.getDescription());
            entity.setGroupID(group.getGroupId());
            entity.setGroupMember(group.getMembers());
            entity.setGroupName(group.getGroupName());
            entity.setGroupOwner(group.getOwner());
            startActivity(new Intent(getActivity(), GroupInfoActivity.class).putExtra("group_info", entity));

    }

    private void initView(View view) {

        mTabLayout = (TabLayout)view.findViewById(R.id.tablayout_contacts);
        mViewPager = (ViewPager)view.findViewById(R.id.viewpager_contacts);
        mSearchView= (SearchView) view.findViewById(R.id.contacts_searchView);
        mHistoryDatabase = new SearchHistoryTable(getActivity());
        if (mSearchView != null) {
//            mSearchView.setVersion(SearchView.VERSION_MENU_ITEM);
//            mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_MENU_ITEM);
            mSearchView.setHint("请输入你要搜索的内容");
            mSearchView.setTextSize(16);
            mSearchView.setDivider(false);
            mSearchView.setVoice(true);
            mSearchView.setVoiceText("Set permission on Android 6+ !");
            mSearchView.setAnimationDuration(SearchView.ANIMATION_DURATION);
            mSearchView.setShadowColor(ContextCompat.getColor(getActivity(), R.color.search_shadow_layout));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchView.close(false);

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

        }
    }
    private void initData()
    {
        List<String> titles = new ArrayList<>();//通过tittles来List来给顶部的导航条赋值
        titles.add("好友");
        titles.add("群聊");
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        List<Fragment> fragments = new ArrayList<>();//通过一个Fragment列表来填充相应的Fragment
        fragments.add(new FriendsFragment());
        fragments.add(new GroupFragment());
        FragmentAdapter adapter =
                new FragmentAdapter(getChildFragmentManager(), fragments, titles);//通过一个适配器来匹配相应的导航条和Fragment
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);

    }

    private void initThread() {

        Observable.create(new Observable.OnSubscribe<List<String> >() {
            @Override
            public void call(Subscriber<? super List<String> > subscriber) {
                List<String> usernames=null;
                try {
                    usernames= EMClient.getInstance().contactManager().getAllContactsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(usernames);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onNext(List<String>  list) {
                        initSearchView(list);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }


}
