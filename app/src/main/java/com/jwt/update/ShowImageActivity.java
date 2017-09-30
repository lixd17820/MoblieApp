package com.jwt.update;

import java.io.InputStream;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.Window;

import com.github.chrisbanes.photoview.PhotoView;
import com.jwt.fragment.MenuConfigFragment;
import com.jwt.fragment.MenuJbywFragment;
import com.jwt.fragment.MenuZhcxFragment;
import com.jwt.utils.GlobalMethod;

public class ShowImageActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.jpgdialog);
		String bf = getIntent().getStringExtra("image");
		if (TextUtils.isEmpty(bf))
			finish();
		PhotoView bmImage = (PhotoView) findViewById(R.id.image);
		Bitmap bmp = GlobalMethod.getImageFromFile(bf);
		// 设置图片
		bmImage.setImageBitmap(bmp);
	}

	/**
	 * 读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

}
