package com.jwt.adapter;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.jwt.update.R;
import com.jwt.utils.GlobalMethod;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowImageFragment extends Fragment {

    private String imgFile;

    public ShowImageFragment() {
        // Required empty public constructor
    }

    public static ShowImageFragment newInstance(String imgFile) {
        ShowImageFragment myFragment = new ShowImageFragment();

        Bundle args = new Bundle();
        args.putString("imgFile", imgFile);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_image, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View v = getView();
        String bf = getArguments().getString("imgFile");
        PhotoView bmImage = (PhotoView) v.findViewById(R.id.image);
        Bitmap bmp = GlobalMethod.getImageFromFile(bf);
        // 设置图片
        bmImage.setImageBitmap(bmp);
    }
}
