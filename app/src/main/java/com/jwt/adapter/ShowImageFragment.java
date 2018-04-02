package com.jwt.adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.jwt.main.R;
import com.jwt.utils.GlobalMethod;

import java.io.InputStream;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowImageFragment extends Fragment {

    private String imgFile;

    public ShowImageFragment() {
        // Required empty public constructor
    }

    public static ShowImageFragment newInstance(String imgFile) {
        return newInstance(imgFile,0);
    }

    public static ShowImageFragment newInstance(String imgFile,int imgCatalog) {
        ShowImageFragment myFragment = new ShowImageFragment();

        Bundle args = new Bundle();
        args.putString("imgFile", imgFile);
        args.putInt("catalog",imgCatalog);
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
        String url = getArguments().getString("imgFile");
        int catalog = getArguments().getInt("catalog",0);
        PhotoView bmImage = (PhotoView) v.findViewById(R.id.image);
        if(catalog == 0) {
            if (url.startsWith("http")) {
                new DownloadImageTask(bmImage).execute(url);
            } else {
                Bitmap bmp = GlobalMethod.getImageFromFile(url);
                // 设置图片
                bmImage.setImageBitmap(bmp);
            }
        }else if(catalog == 1){
            //base64
            byte[] b = Base64.decode(url, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            if (bitmap != null)
                bmImage.setImageBitmap(bitmap);

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        PhotoView bmImage;

        public DownloadImageTask(PhotoView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
