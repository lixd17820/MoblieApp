package com.jwt.main;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.jwt.adapter.ShowImageFragment;

public class ImageViewPage extends AppCompatActivity {

    private String[] imgIdArray = null;
    private ImageView[] tips;
    private LinearLayout ll;
    // private String temp = "/storage/emulated/0/jwtdb/small/JPEG_20170919_101750.jpg,/storage/emulated/0/jwtdb/small/JPEG_20170919_101808.jpg";

    private static int light = Color.argb(255, 221, 221, 221);
    private static int dark = Color.argb(255, 30, 30, 30);

    private int catalog = 0;

    public static final int BASE64_IMG = 1;
    public static final int HTTP_IMG = 2;
    public static final int FILE_IMG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_page);
        setTitle("查看照片");
        ll = (LinearLayout) findViewById(R.id.image_linear);
        String images = getIntent().getStringExtra("images");
        catalog = getIntent().getIntExtra("catalog", FILE_IMG);
        if (!TextUtils.isEmpty(images))
            imgIdArray = images.split(",");
        if (imgIdArray != null && imgIdArray.length > 0) {
            tips = new ImageView[imgIdArray.length];
            for (int i = 0; i < tips.length; i++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                lp.leftMargin = 10;
                lp.rightMargin = 10;
                imageView.setLayoutParams(lp);
                imageView.setImageResource(R.drawable.ic_image_focused);
                //imageView.setColorFilter(i == 0 ? R.color.blank :
                //       R.color.colorWhite);
                int color = (i == 0) ? light : dark;
                imageView.setColorFilter(color);
                //imageView.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                tips[i] = imageView;
                ll.addView(imageView);
            }
        }
        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("imageShow", "select : " + position);
                for (int i = 0; i < tips.length; i++) {
                    tips[i].setColorFilter(dark);
                }
                tips[position].setColorFilter(light);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return imgIdArray.length;
        }

        @Override
        public Fragment getItem(int position) {
            String url = imgIdArray[position];
            return ShowImageFragment.newInstance(url, catalog);
        }


    }
}
