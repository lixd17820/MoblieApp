package com.jwt.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jwt.bean.SchoolZtzBean;
import com.jwt.thread.CommQueryThread;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;


public class JbywSchoolVehActivity extends AppCompatActivity {

    private static final int QRCODE_SCAN = 1000;
    private static final int HAS_INSTALL = 100;

    private EditText editTczbh;
    private Button btnQuery, btnQrCode, btnCancel;
    private TextView tvSchoolInfo;
    private Context self;

    private View.OnClickListener clQuery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnQuery) {
                String code = editTczbh.getText().toString();
                if (TextUtils.isEmpty(code) || code.length() != 9) {
                    GlobalMethod.showErrorDialog("停车证号码长度不正确", self);
                    return;
                }
                querySchoolTcz(code);
            } else if (v == btnCancel) {
                finish();
            } else if (v == btnQrCode) {
                IntentIntegrator integrator = new IntentIntegrator(JbywSchoolVehActivity.this);
//                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
//                integrator.setPrompt("Scan a barcode");
//                integrator.setCameraId(0);  // Use a specific camera of the device
//                integrator.setBeepEnabled(false);
//                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.jbyw_school_veh);
        editTczbh = (EditText) findViewById(R.id.edit_school_tczbh);
        tvSchoolInfo = (TextView) findViewById(R.id.school_info);
        btnQuery = (Button) findViewById(R.id.btn_left);
        btnQrCode = (Button) findViewById(R.id.btn_center);
        btnCancel = (Button) findViewById(R.id.btn_right);
        btnQuery.setOnClickListener(clQuery);
        btnCancel.setOnClickListener(clQuery);
        btnQrCode.setOnClickListener(clQuery);
    }

    private void querySchoolTcz(String code) {
        CommQueryThread thread = new CommQueryThread(
                CommQueryThread.QUERY_SCHOOL,
                new String[]{code}, self
        );
        thread.doStart();
    }

    private boolean hasApk() {
        PackageManager pm = self.getPackageManager();
        boolean hasQr = false;
        try {
            PackageInfo info = pm.getPackageInfo("com.google.zxing.client.android", 0);
            hasQr = info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return hasQr;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == QRCODE_SCAN) {
                String code = data.getStringExtra("SCAN_RESULT");
                if (!TextUtils.isEmpty(code)) {
                    editTczbh.setText(code);
                    querySchoolTcz(code);
                }
            } else if (requestCode == HAS_INSTALL) {
                if (!hasApk()) {
                    GlobalMethod.showErrorDialog("扫描程序未能正常安装，请重试", self);
                }
            }else{
                if (result != null) {
                    if (result.getContents() == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }


    private String getChinaBanji(String s) {
        String nj = s.substring(0, 1);
        String bj = s.substring(1);
        String c = nj + "年级" + Integer.valueOf(bj) + "班";
        return c;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void schoolEvent(JSONObject obj){
        try {
            SchoolZtzBean tcz = ParserJson.parseJsonToObj(obj,SchoolZtzBean.class);
            if (tcz != null) {
                String s = "准停证信息：\n";
                s += "　　停车证号：" + tcz.getTczph().substring(1) + "\n";
                s += "　　学校：" + tcz.getSchoolName() + "\n";
                s += "　　班级：" + getChinaBanji(tcz.getBanji()) + "\n";
                s += "　　号牌种类：" + GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList, tcz.getHpzl()) + "\n";
                s += "　　号牌号码：" + tcz.getHphm() + "\n";
                s += "　　准停证种类：" + tcz.getZtzmc() + "\n";
                s += "　　准停时间：" + tcz.getTime1() + "\n";
                s += "　　准停时间：" + tcz.getTime2();
                tvSchoolInfo.setText(s);
            }else {
                GlobalMethod.showErrorDialog("", self);
                tvSchoolInfo.setText("准停证信息：\n　　无相关登记信息");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
