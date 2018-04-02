package com.jwt.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.event.DownDictEvent;
import com.jwt.thread.UpdateDictThread;
import com.jwt.thread.WsglThread;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.ParserJson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class WelcomeActivity extends AppCompatActivity {

    private Activity self;
    private ProgressBar pb;
    private TextView tvContent, tvPrecent;
    private String mjjh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        GlobalSystemParam.readSysParamFormSpf(self);
        setContentView(R.layout.activity_welcome);
        pb = findViewById(R.id.progressBar);
        tvContent = findViewById(R.id.tv_down_content);
        tvPrecent = findViewById(R.id.tv_down_precent);
        String mjxx = getIntent().getStringExtra(GlobalConstant.SP_MJXX);
        Log.e("WelcomeActivity", mjxx);
        int conn = getIntent().getIntExtra(GlobalConstant.NETWORK_STATUS, 1);
        //读取已保存的民警信息
        JSONObject obj = ParserJson.getJsonObject(mjxx);
        if (obj == null) {
            GlobalMethod.toast(self, "无法读取民警信息，系统退出");
            finish();
            return;
        }
        GlobalSystemParam.loadParam(self, GlobalConstant.SP_MJXX, mjxx);
        GlobalData.grxx = GlobalMethod.getSavedMjInfo(self);
        mjjh = obj.optString("jh", "3206");
        GlobalData.connCata = ConnCata.getValByIndex(conn);
        EventBus.getDefault().register(this);
        PermissionGen.with(self)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CONTACTS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        new WsglThread(self, mjjh).start();
        new UpdateDictThread(GlobalMethod.getBoxStore(self), self).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(DownDictEvent event) {
        if (event.getTotal() == event.getStep()) {
            startMainSystem();
            return;
        }
        tvContent.setText(event.getCurrentName());
        String precent = event.getStep() + "/" + event.getTotal();
        tvPrecent.setText(precent);
        pb.setMax(event.getTotal());
        pb.setProgress(event.getStep());
    }

    /**
     * 启动主程序
     */
    private void startMainSystem() {
        //try {
        //    Thread.sleep(1000);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        Intent intent = new Intent(self, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
