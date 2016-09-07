/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lm.im_huanxin.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import com.lm.im_huanxin.R;
import com.lm.im_huanxin.RxBus.RxBus;
import com.lm.im_huanxin.adapter.PhotoSendAdapter;
import com.lm.im_huanxin.emoji.Folder;
import com.lm.im_huanxin.emoji.Image;
import com.lm.im_huanxin.ui.UpVideoActivity;
import com.lm.im_huanxin.utils.RecentContactsDB;

import org.kymjs.kjframe.ui.SupportFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天键盘功能界面
 *
 * @author kymjs (http://www.kymjs.com/) on 7/6/15.
 */
public class ChatFunctionFragment extends SupportFragment {

    private RxBus rxBus = RxBus.getInstance();
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout mVideo;
    private LinearLayout sengType;
    private LinearLayout photoLayout;
    private Button mBtSend;
    private RecyclerView mRecyclerView;
    private PhotoSendAdapter adapter;
    private OnOperationListener listener;
    private static final int LOADER_ALL = 0;
    private List<Image>listItems=new ArrayList<Image>();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = View.inflate(getActivity(), R.layout.chat_item_menu, null);
        return view;
    }

    @Override
    protected void initWidget(View parentView) {
        super.initWidget(parentView);
        mBtSend= (Button) parentView.findViewById(R.id.photo_send);
        mBtSend.setOnClickListener(this);
        sengType= (LinearLayout) parentView.findViewById(R.id.chat_seng_type_layout);
        photoLayout= (LinearLayout) parentView.findViewById(R.id.photo_layout);
        mVideo= (LinearLayout) parentView.findViewById(R.id.chatmenu_video);
        layout1 = (LinearLayout) parentView.findViewById(R.id.chat_menu_images);
        layout2 = (LinearLayout) parentView.findViewById(R.id.chat_menu_photo);
        mRecyclerView= (RecyclerView) parentView.findViewById(R.id.photo_rv_pv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        for(int i=0;i<15;i++)
        {
            Image image=new Image();
            listItems.add(image);

        }
        adapter=new PhotoSendAdapter(getActivity(),listItems);
        mRecyclerView.setAdapter(adapter);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        mVideo.setOnClickListener(this);
    }

    public void setOnOperationListener(OnOperationListener onOperationListener) {
        this.listener = onOperationListener;
    }

    @Override
    protected void widgetClick(View v) {
        super.widgetClick(v);
        if (v == layout1) {
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
            photoLayout.setVisibility(View.VISIBLE);
            sengType.setVisibility(View.GONE);
//            clickMenu(0);

        } else if (v == layout2) {
            clickMenu(1);
        }
          else if (v==mBtSend)
        {
            List<Image>list=new ArrayList<Image>();
            for (int i=0;i<adapter.getListItems().size();i++)
            {
                if (adapter.getListItems().get(i).isMark()) {
                    list.add(adapter.getListItems().get(i));
                    adapter.getListItems().get(i).setMark(false);
                }
            }
            adapter.notifyDataSetChanged();
            rxBus.post("photo_send",list);
        }
        else if (v==mVideo)
        {
           clickMenu(3);
        }
    }

    private void clickMenu(int i) {
        if (listener != null) {
            listener.selectedFunction(i);
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> images = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do{
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        Image image = new Image(path, name, dateTime);
                        images.add(image);
                    }while(data.moveToNext());
                    adapter.setListItems(images);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxBus.release();
    }
}
