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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.bean.LoginMjxxBean;
import com.jwt.bean.LoginResultBean;
import com.jwt.bean.MessageEvent;
import com.jwt.bean.UpdateFile;
import com.jwt.event.DownApkEvent;
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
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
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

    private List<UpdateFile> needs = new ArrayList<>();
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        self = this;
        GlobalMethod.saveParam(self);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        findViewById(R.id.unless_linear).requestFocus();
        //读取已保存的民警信息
        SharedPreferences sharedPreferences = this.getSharedPreferences(GlobalConstant.MJXX_INFO, MODE_PRIVATE);
        String mjxx = sharedPreferences.getString("mjxx", "{}");
        JSONObject obj = ParserJson.getJsonObject(mjxx);
        String jybh = obj.optString("jh", "3206");
        editMjjh.setText(jybh);
        findViewById(R.id.but_login).setOnClickListener(loginClick);
        findViewById(R.id.but_login_cancel).setOnClickListener(cancelLogin);
        setTitle("系统登录");

        dialog = new MaterialDialog.Builder(self)
                .title("系统提示")
                .content("正在登录")
                .progress(false, 1, true).build();

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
            // 开始登录，隐藏控件
            //setVisibleView(true);
            // 启动线程
            dialog.show();
            new LoginUpdateThread().doStart(mjjh, mm, GlobalData.serialNumber);

        }
    };

    List<String> notNeedPack = Arrays.asList("com.android.provider.userdata", "com.acd.simple.provider",
            "com.wonder.vpnClient", "com.android.provider.fixcode",
            "com.android.provider.flashcode",
            "com.ntga.jwt", "com.android.provider.wfdmcode",
            "com.android.provider.roadcode");

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginEventBus(LoginEvent re) {

        if (re == null || re.getStatus() != 200
                || re.getResult() == null) {
            dialog.dismiss();
            GlobalMethod.showErrorDialog(TextUtils.isEmpty(re.getStMs()) ? "登录网络连接错误" : re.getStMs(), self);
            return;
        }
        LoginResultBean login = re.getResult();
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
                dialog.dismiss();
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
        needs = checkNeedApk(ufs);
        if (!needs.isEmpty()) {
            //下载文件并安装，暂时不开
            new DownFileThread(needs).start();
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

        //progressDialog.show();
        App app = ((App) getApplication());
        //frmCodeBox.removeAll();
        new UpdateDictThread(app.getBoxStore(), self).start();
    }

    private List<UpdateFile> checkNeedApk(List<UpdateFile> ufs) {
        List<UpdateFile> list = new ArrayList<>();
        for (UpdateFile uf : ufs) {
            String packName = uf.getPackageName();
            if (notNeedPack.contains(packName))
                continue;
            double lv = GlobalMethod.getApkVerionName(packName, self);
            Double rv = Double.valueOf(uf.getVersionName());
            if (rv > lv) {
                list.add(uf);
            }
        }
        return list;
    }

    private List<UpdateFile> checkNeedApk(UpdateFile[] ufs) {
        return checkNeedApk(Arrays.asList(ufs));
    }

    private void installApk(File f,int rq){
        if(!f.exists()){
            Toast.makeText(self,"文件不存在",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(self, "com.jwt.update.fileprovider", f);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        // 安装的结果将返回进入验证
        startActivityForResult(intent, Integer.valueOf(rq));
    }

    private void installApk(UpdateFile apk) {
        File f = new File(outSideDir, apk.getFileName());
        installApk(f,Integer.valueOf(apk.getId()));
    }

    private void installApk() {
        for (UpdateFile uf : needs) {
            installApk(uf);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("update active return", "" + requestCode + "/" + resultCode);
        if (requestCode == Integer.valueOf(needs.get(returnCount).getId())) {
            returnCount++;
            if (returnCount >= needs.size()) {
                if (!checkNeedApk(needs).isEmpty()) {
                    exitLogin("未能安装所有更新，请重新登录下载");
                } else
                    finish();
            } else {
                installApk(needs.get(returnCount));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(DownSpeedEvent event) {
        if (event.getTotal() == event.getStep()) {
            dialog.dismiss();
            startMainSystem();
            return;
        }
        if (event.getStep() == 0) {
            if (!dialog.isShowing())
                dialog.show();
            return;
        }
        dialog.setTitle(event.getTitle());
        dialog.setContent(event.getCurrentName());
        dialog.setMaxProgress(event.getTotal());
        dialog.setProgress(event.getStep());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downApkEventBus(DownApkEvent event) {
        if (event.isOver) {
            dialog.dismiss();
            new InstallApk(new MyHandler(this)).start();
            return;
        }
        dialog.setProgress(event.step);
        dialog.setMaxProgress(100);
        dialog.setTitle(event.filename);
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
        outSideDir = Environment.getExternalStorageDirectory().getPath()
                + "/jwtdb/";
        File f = new File(outSideDir);
        if (!f.exists())
            f.mkdirs();
        double version = GlobalMethod.getApkVerionName("com.jwt.update", self);
        tvVersion.setText("系统版本号：" + version);
        Toast.makeText(this, "Contact permission is granted", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        Toast.makeText(this, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
    }

    private void printPer(SharedPreferences sp, String time) {
        Map<String, ?> map = sp.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Log.e("config value", time + " " + entry.getKey() + "/" + entry.getValue().getClass().getName() + "/"
                    + entry.getValue());
        }
    }

    class DownFileThread extends Thread {
        private List<UpdateFile> fs;


        public DownFileThread(List<UpdateFile> fs) {
            this.fs = fs;
        }

        /**
         * 线程运行
         */
        @Override
        public void run() {
            RestfulDao dao = RestfulDaoFactory.getDao();
            int writeCount = 0;
            int i = 0;
            Log.e("realLoginHandler", "5" + fs.size());
            for (UpdateFile uf : fs) {
                long writeByte = dao.downloadFile(dao.getFileUrl() + uf.getPackageName(),
                        new File(outSideDir,uf.getFileName()), Long.valueOf(uf.getHashValue()), uf.getFileName());
                if (writeByte > 0)
                    writeCount++;
                i++;
            }
            EventBus.getDefault().post(new DownApkEvent(true, 100, "下载完成"));
        }

    }

    class InstallApk extends Thread {
        Handler handler;

        public InstallApk(Handler handler){
            this.handler = handler;
        }

        @Override
        public void run() {
            //startMainSystem();
            handler.sendEmptyMessage(1);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<LoginActivity> myActivity;

        public MyHandler(LoginActivity activity) {
            myActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity ac = myActivity.get();
            ac.installApk();
        }
    }

}
