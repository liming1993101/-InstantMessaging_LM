package com.lm.im_huanxin.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lm.im_huanxin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoAlbumFragment extends Fragment {

    PhotoAlbumFragment context;
    public PhotoAlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_photo_album, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        Button bt= (Button) view.findViewById(R.id.finsh);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(new PhotoAlbumFragment()).commit();
            }
        });
    }

}
