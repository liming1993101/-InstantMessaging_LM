package com.lm.im_huanxin.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hyphenate.chat.EMClient;
import com.lm.im_huanxin.R;
import com.lm.im_huanxin.ui.InviteMemberActivity;
import com.lm.im_huanxin.ui.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionsFragment extends Fragment implements View.OnClickListener {


    public OptionsFragment() {
        // Required empty public constructor
    }
    private LinearLayout mInsert;
    private LinearLayout mQuit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_options, container, false);
        initView(view);
        return view;
    }

    private void initView(View v) {
        mInsert= (LinearLayout) v.findViewById(R.id.option_insett_contacts);
        mQuit= (LinearLayout) v.findViewById(R.id.quit_loging);
        mInsert.setOnClickListener(this);
        mQuit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.option_insett_contacts:
                startActivity(new Intent(getActivity(),InviteMemberActivity.class));
                break;
            case R.id.quit_loging:
                EMClient.getInstance().logout(true);
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
                break;

        }
    }
}
