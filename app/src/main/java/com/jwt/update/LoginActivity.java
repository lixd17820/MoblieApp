package com.jwt.update;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jwt.bean.LoginMjxxBean;
import com.jwt.bean.LoginResultBean;
import com.jwt.bean.MessageEvent;
import com.jwt.bean.UpdateFile;
import com.jwt.event.DownSpeedEvent;
import com.jwt.event.LoginEvent;
import com.jwt.pojo.FrmCode;
import com.jwt.pojo.FrmRoadItem;
import com.jwt.pojo.FrmRoadSeg;
import com.jwt.pojo.SysParaValue;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.thread.LoginUpdateThread;
import com.jwt.thread.UpdateDictThread;
import com.jwt.utils.CommParserXml;
import com.jwt.utils.ConnCata;
import com.jwt.utils.Encrypt;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.ParserJson;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.Query;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.edit_login_mjjh)
    EditText editMjjh;
    @BindView(R.id.edit_login_passwd)
    EditText editPasswd;
    @BindView(R.id.linear_layout_1)
    LinearLayout tv1;
    @BindView(R.id.tv_version)
    TextView tvVersion;


    private Activity self;
    // 需下载的文件列表
    private int returnCount = 0;
    public static String outSideDir = "";

    // 登录成功后返回的信息对象,要重新发送给主程序
    private List<UpdateFile> needUps, oldNeedUps;
    private String mjjh, mm;
    private String newMd5, oldMd5;

    private long tvClickTime = 0;

    private boolean isLoging = false;

    public final static int DOWNFILE = 0;
    public final static int REALLOGIN = 1;
    public final static int DOWNLOAD_FAIL = 6;
    public final static int DOWNLOAD_OK = 7;
    public final static int DOWNLOADING_APK = 9;
    public final static int DOWN_APK_STATE = 20;
    private static final int MENU_CHECK_UPDATE = 101;
    private static final int MENU_READ_SERIAL = 102;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        self = this;
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        findViewById(R.id.unless_linear).requestFocus();
        //读取已保存的民警信息
        SharedPreferences sharedPreferences = this.getSharedPreferences(GlobalConstant.MJXX_INFO, MODE_PRIVATE);
        String mjxx = sharedPreferences.getString("mjxx", "{}");
        JSONObject obj = ParserJson.getJsonObject(mjxx);
        String jybh = obj.optString("jh", "3206");
        editMjjh.setText(jybh);
        outSideDir = Environment.getExternalStorageDirectory().getPath()
                + "/jwtdb/";
        File f = new File(outSideDir);
        if (!f.exists())
            f.mkdirs();


        findViewById(R.id.but_login).setOnClickListener(loginClick);
        findViewById(R.id.but_login_cancel).setOnClickListener(cancelLogin);
        setTitle("系统登录");

        double version = GlobalMethod.getApkVerionName("com.jwt.update", self);
        tvVersion.setText("系统版本号：" + version);
        progressDialog = new ProgressDialog(self);
        progressDialog.setTitle("提示");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        PermissionGen.with(LoginActivity.this)
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

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_READ_SERIAL, Menu.NONE, "读本机串号");
        return super.onCreateOptionsMenu(menu);
    }

    private void copySerial() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", GlobalData.serialNumber);
        clipboard.setPrimaryClip(clip);
        GlobalMethod.showDialog("系统提示", "本机串号为：" + GlobalData.serialNumber + "，并已复制到剪贴板，可直接粘贴。", "确定", self);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_READ_SERIAL:
                copySerial();
                break;
            default:
                break;
        }
        return true;
    }

    private long clickTime = 0;
    private int count = 0;

    @OnClick(R.id.tv_version)
    public void tvClick() {
        long cuTime = System.currentTimeMillis();
        if (cuTime - clickTime < 500) {
            count++;
        } else {
            count = 0;
        }
        clickTime = cuTime;
        if (count >= 5) {
            Toast.makeText(self, "已改变连接方式", Toast.LENGTH_SHORT).show();
            GlobalData.connCata = ConnCata.INSIDECONN;
        }
    }

    /**
     * 取消登录监听
     */
    private OnClickListener cancelLogin = new OnClickListener() {

        public void onClick(View arg0) {
            GlobalMethod.showDialogTwoListener("系统提示", "是否退出登录", "退出", "返回",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, self);
        }
    };

    private OnClickListener loginClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            mjjh = editMjjh.getText().toString();
            Editable passwd = editPasswd.getText();
            if (TextUtils.isEmpty(mjjh) || mjjh.length() != 8) {
                GlobalMethod.showErrorDialog("警号不能为空或不是八位", self);
                return;
            }
            if (TextUtils.isEmpty(passwd) || passwd.length() != 6) {
                GlobalMethod.showErrorDialog("密码不能为空或不是六位", self);
                return;
            }
            Encrypt enc = Encrypt.getInstance();
            try {
                mm = enc.hashEncrypt(passwd.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //
            returnCount = 0;
            needUps = new ArrayList<UpdateFile>();
            // 开始登录，隐藏控件
            //setVisibleView(true);
            // 启动线程
            progressDialog.setTitle("系统登录");
            progressDialog.setMessage("正在登录");
            progressDialog.setMax(100);
            progressDialog.show();
            new LoginUpdateThread().doStart(mjjh, mm, GlobalData.serialNumber);

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEventBus(LoginEvent re) {

        if (re == null || re.getStatus() != 200
                || re.getResult() == null) {
            progressDialog.dismiss();
            exitLogin("登录网络连接错误");
            return;
        }
        LoginResultBean login = re.getResult();
        if (!TextUtils.equals(login.getCode(), "1")) {
            progressDialog.dismiss();
            exitLogin(login.getCwms());
            return;
        }
        // 保存个人信息
        LoginMjxxBean mj = login.getMj();
        JSONObject obj = ParserJson.objToJson(mj);
        SharedPreferences sharedPreferences = self.getSharedPreferences(GlobalConstant.MJXX_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("mjxx", obj.toString());
        Log.e("cxMenus", re.getCxMenuStr());
        editor.putString("cxMenus", re.getCxMenuStr());
        editor.commit();//提交修改
        if (mj != null) {
            Map<String, String> mjxx = null;
            try {
                mjxx = CommParserXml.objToMap(mj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mjxx == null) {
                progressDialog.dismiss();
                exitLogin("服务器返回数据错误");
                return;
            }
            GlobalData.grxx = mjxx;
        }
        Log.e("realLoginHandler", obj.toString());
        Log.e("realLoginHandler", "1");
        UpdateFile[] ufs = login.getUfs();
        if (ufs == null || ufs.length == 0) {
            exitLogin("服务器出现错误，请与管理员联系");
            return;
        }
        JSONArray jufs = ParserJson.arrayToJsonArray(ufs);
        Log.e("jufs", jufs.toString());
        Log.e("realLoginHandler", "3");
        // 检查SD卡
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            exitLogin("版本更新需加载SD卡");
            return;
        }
        progressDialog.setTitle("初始化数据");
        progressDialog.setMessage("准备中....");
        //progressDialog.show();
        App app = ((App) getApplication());
        //frmCodeBox.removeAll();
        new UpdateDictThread(app.getBoxStore(), self).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(DownSpeedEvent event) {
        if (event.getTotal() == event.getStep()) {
            progressDialog.dismiss();
            startMainSystem();
            return;
        }
        if (event.getStep() == 0) {
            if (!progressDialog.isShowing())
                progressDialog.show();
            return;
        }
        progressDialog.setTitle(event.getTitle());
        progressDialog.setMessage(event.getCurrentName());
        progressDialog.setMax(event.getTotal());
        progressDialog.setProgress(event.getStep());
    }

    private void exitLogin(String err) {
        //setVisibleView(false);
        GlobalMethod.showErrorDialog(err, self);
    }

    /**
     * 启动主程序
     */
    private void startMainSystem() {
        Intent intent = new Intent(self, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(self);
        // if (progressDialog != null && progressDialog.isShowing())
        // progressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        GlobalData.serialNumber = GlobalMethod.getSerial(self);
        Toast.makeText(this, "Contact permission is granted", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        Toast.makeText(this, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
    }

}
