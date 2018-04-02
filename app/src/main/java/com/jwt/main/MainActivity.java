package com.jwt.main;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jwt.event.MenuPosEvent;
import com.jwt.event.OperPhotoEvent;
import com.jwt.event.ServiceEvent;
import com.jwt.fragment.MenuConfigFragment;
import com.jwt.fragment.MenuJbywFragment;
import com.jwt.fragment.MenuZhcxFragment;
import com.jwt.thread.OperPhotoThread;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.MenuParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity implements MenuJbywFragment.OnFragmentInteractionListener {


    private static final String TAG = "MainActivity";
    private Context self;
    //private MainReferService mrService;
    private long bjwd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "MainActivity 1");
        EventBus.getDefault().register(this);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        self = this;
        String mjxx = GlobalSystemParam.getSavedInfo(self, "mjxx");
        if (TextUtils.isEmpty(mjxx)) {
            Intent intent = new Intent(self, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        Log.e(TAG, "MainActivity 2");
        if (GlobalData.grxx == null) {
            Log.e(TAG, "GlobalData.grxx 正在载入");
            GlobalData.grxx = GlobalMethod.getSavedMjInfo(self);
        }
        Log.e(TAG, "MainActivity 3");
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        GlobalData.serialNumber = GlobalMethod.getSerial(self);
        Log.e(TAG, "MainActivity 4");
        // 运行服务
        if (!MenuParser.checkServerRunning(self, "com.jwt.main",
                "com.jwt.main.MainReferService")) {
            startService(new Intent(this, MainReferService.class));
        }
        GlobalMethod.logFileInfo("登录系统");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_page_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.menu_regc);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan: {
                IntentIntegrator integrator = new IntentIntegrator(this);
//                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
//                integrator.setPrompt("Scan a barcode");
//                integrator.setCameraId(0);  // Use a specific camera of the device
//                integrator.setBeepEnabled(false);
//                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
            return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void menuBackEvent(MenuPosEvent event) {
//        int pos = event.getPos();
//        Log.e("Main activity", "return " + pos);
//        boolean isBadge = event.isBadge();
//        MenuOptionBean m = menuOptionList.get(pos);
//        m.setBadge(isBadge);
//        menusAdapter.notifyItemChanged(pos);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this).title("系统确认")
                .content("是否确定退出系统?")
                .positiveText("退出").negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        GlobalData.loginStatus = 1;
                        GlobalMethod.logFileInfo("退出系统");
                        EventBus.getDefault().post(new ServiceEvent(200));
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        GlobalData.isBadger = true;
        Log.e("MainActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalData.isBadger = false;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MenuJbywFragment();
                case 1:
                    return new MenuZhcxFragment();
                case 2:
                    return new MenuConfigFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "交管业务";
                case 1:
                    return "综合查询";
                case 2:
                    return "系统配置";
            }
            return null;
        }
    }


}

