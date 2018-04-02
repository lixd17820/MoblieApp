package com.jwt.main;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.SpringDjItf;
import com.jwt.bean.SpringKcdjBean;
import com.jwt.bean.SpringWhpdjBean;
import com.jwt.dao.MessageDao;
import com.jwt.dao.ViolationDAO;
import com.jwt.jbyw.VioDrvBean;
import com.jwt.jbyw.VioVehBean;
import com.jwt.thread.CommQueryThread;
import com.jwt.thread.UploadSpringThread;
import com.jwt.utils.ClientVars;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.IDCard;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;
import com.sdses.bean.ID2Data;

import org.json.JSONObject;

public class JbywSpringDjMainActivity extends AppCompatActivity {

    protected static final int REQCODE_JTFS = 100;
    protected static final int SCAN_IDCARD = 200;
    private EditText edit_jcdd, edit_hphm, edit_hzrs, edit_szrs, edit_lxjssj,
            edit_hzzl;
    private EditText edit_szzl, edit_wpmc, edit_yyryqk, edit_claqss, edit_dabh;
    private EditText edit_xm, edit_jszh, edit_wfxw, edit_clqk;
    private Spinner spin_hpzl, spin_jtfs, spin_jzsy, spin_cljy;
    private Button btn_query_veh, btn_query_drv, btn_query_jtfs,
            btn_save_spring, btn_exit, btn_scan_idcard;
    private LinearLayout line_kchz, line_whphz, line_root;
    private Spinner spin_hpqz;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Context self;

    private boolean isSave = false;

    private List<KeyValueBean> jszsyList, cljyList;
    private int djlx = 0;
    // 信息查询种类
    private static final int QUERY_DRV_INFO = 30;
    private static final int QUERY_VEH_INFO = 31;

    private static final int SAVE_SPRING_HANDLER = 40;
    private static final int QUERY_DRV_HANDLER = 41;
    private static final int QUERY_VEH_HANDLER = 42;
    private static final int UPLOAD_SPRING_HANDLER = 0;

    private static final int HAS_INSTALL = 101;

    private SpringKcdjBean kcdj;
    private SpringWhpdjBean whpdj;

    //-------------读卡服务--------------------------
    private IReadCardService mIReadCardService = null;
    byte[] ID2Bytes = new byte[1280];
    /**
     * 二代证读卡服务接收器
     */
    private ID2DataReceiver mID2DataReceiver;
    private boolean mBindReadServiceIsOk = false;
    private MediaPlayer m_soundSucess;
    private boolean isAlps = false;

    private CommQueryThread downloadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jbyw_spring_dj);
        self = this;
        edit_jcdd = (EditText) findViewById(R.id.edit_jcdd);
        edit_hphm = (EditText) findViewById(R.id.edit_hphm);
        edit_hzrs = (EditText) findViewById(R.id.edit_hzrs);
        edit_szrs = (EditText) findViewById(R.id.edit_szrs);
        edit_lxjssj = (EditText) findViewById(R.id.edit_lxjssj);
        edit_hzzl = (EditText) findViewById(R.id.edit_hzzl);
        edit_szzl = (EditText) findViewById(R.id.edit_szzl);
        edit_wpmc = (EditText) findViewById(R.id.edit_wpmc);
        edit_yyryqk = (EditText) findViewById(R.id.edit_yyryqk);
        edit_claqss = (EditText) findViewById(R.id.edit_claqss);
        edit_dabh = (EditText) findViewById(R.id.edit_dabh);
        edit_xm = (EditText) findViewById(R.id.edit_xm);
        edit_jszh = (EditText) findViewById(R.id.edit_jszh);
        edit_wfxw = (EditText) findViewById(R.id.edit_wfxw);
        edit_clqk = (EditText) findViewById(R.id.edit_clqk);
        spin_hpzl = (Spinner) findViewById(R.id.spin_hpzl);
        spin_jtfs = (Spinner) findViewById(R.id.spin_jtfs);
        spin_jzsy = (Spinner) findViewById(R.id.spin_jzsy);
        spin_cljy = (Spinner) findViewById(R.id.spin_cljy);
        spin_hpqz = (Spinner) findViewById(R.id.spin_hpqz);
        btn_query_veh = (Button) findViewById(R.id.btn_query_veh);
        btn_query_drv = (Button) findViewById(R.id.btn_query_drv);
        btn_query_jtfs = (Button) findViewById(R.id.btn_query_jtfs);
        btn_save_spring = (Button) findViewById(R.id.btn_save_spring);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_scan_idcard = (Button) findViewById(R.id.btn_scan_idcard);
        line_kchz = (LinearLayout) findViewById(R.id.line_kchz);
        line_whphz = (LinearLayout) findViewById(R.id.line_whphz);
        line_root = (LinearLayout) findViewById(R.id.line_root);

        GlobalMethod.changeAdapter(spin_hpzl, GlobalData.hpzlList, this, false);
        GlobalMethod.changeAdapter(spin_hpqz, GlobalData.hpqlList, this, false);
        GlobalMethod.changeSpinnerSelect(spin_hpqz, "苏", GlobalConstant.VALUE,
                true);
        btn_query_veh.setOnClickListener(butClickListener);
        btn_query_drv.setOnClickListener(butClickListener);
        btn_query_jtfs.setOnClickListener(butClickListener);
        btn_save_spring.setOnClickListener(butClickListener);
        btn_exit.setOnClickListener(butClickListener);
        btn_scan_idcard.setOnClickListener(butClickListener);
        jszsyList = new ArrayList<KeyValueBean>();
        jszsyList.add(new KeyValueBean("0", "驾驶证已审验"));
        jszsyList.add(new KeyValueBean("1", "驾驶证逾期未审验"));
        cljyList = new ArrayList<KeyValueBean>();
        cljyList.add(new KeyValueBean("0", "车辆已检验"));
        cljyList.add(new KeyValueBean("1", "车辆逾期未检验"));
        djlx = getIntent().getExtras().getInt("lx");
        GlobalMethod.changeAdapter(spin_jzsy, jszsyList, this, false);
        GlobalMethod.changeAdapter(spin_cljy, cljyList, this, false);
        GlobalMethod.changeAdapter(spin_jtfs, getXfJtfs(djlx), this, true);
        if (djlx == 0) {
            setTitle("春运大客车登记");
            line_root.removeView(line_whphz);
            kcdj = new SpringKcdjBean();
        } else {
            setTitle("春运危化品登记");
            line_root.removeView(line_kchz);
            whpdj = new SpringWhpdjBean();
        }
        edit_jcdd.setText(MessageDao.getLastVioWfdd(djlx, GlobalMethod.getBoxStore(self)));
        String od = Environment.getExternalStorageDirectory().getPath()
                + "/jwtdb/";
        outSideDir = new File(od);
        if (!outSideDir.exists())
            outSideDir.mkdirs();
        m_soundSucess = MediaPlayer.create(getApplicationContext(),
                R.raw.success);
        isAlps = TextUtils.equals(Build.BRAND, "alps");
        if (isAlps) {
            boolean isApk = hasApk();
            if (isApk) {
                startReadID2Service();
                initRecevier();
                if (mBindReadServiceIsOk) {
                    setTitle(getTitle() + "--已启动读卡");
                }
            } else {
                GlobalMethod.showDialogTwoListener("系统提示", "未安装二代证扫描程序，请下载安装", "下载", "取消",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        downloadThread = new CommQueryThread(new DownloadCardHandler(JbywSpringDjMainActivity.this),
//                                CommQueryThread.DOWNLOAD_CARD,
//                                new String[]{"/ydjw/DownloadNormalFile?file=" + apkFile,
//                                        new File(outSideDir, apkFile).getAbsolutePath()}, self
//                        );
//                        downloadThread.doStart();
                    }
                }, self);
                isAlps = false;
            }
        }
    }

    static class DownloadCardHandler extends Handler {

        private final WeakReference<JbywSpringDjMainActivity> myActivity;

        public DownloadCardHandler(JbywSpringDjMainActivity activity) {
            myActivity = new WeakReference<JbywSpringDjMainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            JbywSpringDjMainActivity ac = myActivity.get();
            if (ac != null) {
                ac.installApk(msg);
            }
        }
    }

    private String apkFile = "ReadCardService.apk";
    private File outSideDir;

    private void installApk(Message msg) {
        Bundle b = msg.getData();
        int what = b.getInt("length");
        if (what == 0) {
            int step = b.getInt("step");
            //downloadThread.setProgressText("文件下载进度" + step + "%");
        } else if (what == 1) {
            File f = new File(outSideDir, apkFile);
            if (f.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + f),
                        "application/vnd.android.package-archive");
                startActivityForResult(intent, HAS_INSTALL);
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (isAlps) {
            unregisterReceiver(mID2DataReceiver);
            unbindService(readCardCon);
        }
        super.onDestroy();
    }

    View.OnClickListener butClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btn_query_drv) {
                // 判断在离线模式
                if (!GlobalMethod.isOnline()) {
                    GlobalMethod.showErrorDialog("离线模式下不能上网查询比对！", self);
                    return;
                }

                // 根据档案号查驾驶员信息
                Editable dabh = edit_dabh.getText();
                if (TextUtils.isEmpty(dabh)) {
                    GlobalMethod.showErrorDialog("档案编号不能为空", self);
                    return;
                }
                // String bd = edFzjgms.getText().toString().trim();
                // GlobalMethod.getKeyFromSpinnerSelected(spChenShi,
                // cityList, GlobalConstant.KEY);
                SpringQueryInfoThread thread = new SpringQueryInfoThread(
                        new SpringDjHandler(JbywSpringDjMainActivity.this,
                                QUERY_DRV_HANDLER), QUERY_DRV_INFO,
                        new String[]{dabh.toString().trim(), ""});
                thread.doStart();
            } else if (v == btn_scan_idcard) {
                queryDrvBySfzh();
            } else if (v == btn_query_veh) {
                // 判断在离线模式
                if (!GlobalMethod.showOfflineNotQuery(self))
                    return;
                Editable hp = edit_hphm.getText();
                if (TextUtils.isEmpty(hp)) {
                    GlobalMethod.showErrorDialog("号牌号码不能为空", self);
                    return;
                }
                String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spin_hpzl,
                        GlobalConstant.KEY);
                String hphm = GlobalMethod.getKeyFromSpinnerSelected(spin_hpqz,
                        GlobalConstant.VALUE)
                        + hp.toString().trim().toUpperCase();
                SpringQueryInfoThread thread = new SpringQueryInfoThread(
                        new SpringDjHandler(JbywSpringDjMainActivity.this,
                                QUERY_VEH_HANDLER), QUERY_VEH_INFO,
                        new String[]{hpzl, hphm});
                thread.doStart();
            } else if (v == btn_query_jtfs) {
                Intent intent = new Intent(self, ConfigJtfsActivity.class);
                startActivityForResult(intent, REQCODE_JTFS);
            } else if (v == btn_save_spring) {
                if (isSave) {
                    GlobalMethod.showErrorDialog("记录已保存，无需重复保存", self);
                    return;
                }
                String err = saveViewIntoPojo();
                if (!TextUtils.isEmpty(err)) {
                    GlobalMethod.showErrorDialog(err, self);
                    return;
                }
                long row = 0;
                if (djlx == 0) {
                    row = MessageDao.insertSpringKcdj(kcdj,GlobalMethod.getBoxStore(self));
                } else {
                    row = MessageDao.insertSpringWhpdj(whpdj,GlobalMethod.getBoxStore(self));
                }
                if (row > 0) {
                    isSave = true;
                    if (djlx == 0) {
                        kcdj.setId(row + "");
                        uploadDj(kcdj);
                    } else {
                        whpdj.setId(row + "");
                        uploadDj(whpdj);
                    }
                    GlobalMethod.showDialog("系统提示", "记录保存成功", "确定", self);
                } else {
                    GlobalMethod.showErrorDialog("记录保存失败，原因未知", self);
                }
            } else if (v == btn_exit) {
                exitSystem();
            }
        }
    };

    private void exitSystem() {
        if (!isSave) {
            GlobalMethod.showDialogTwoListener("系统提示", "记录还没有保存，是否退出？", "退出",
                    "继续登记", exitListener, self);
            return;
        }
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }


    @Override
    public void onBackPressed() {
        exitSystem();
    }

    protected DialogInterface.OnClickListener exitListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    class SpringQueryInfoThread extends Thread {
        private Handler mHandler;
        private int queryCata;
        private String[] params;
        private ProgressDialog progressDialog;

        public SpringQueryInfoThread(Handler mHandler, int queryCata,
                                     String[] params) {
            this.mHandler = mHandler;
            this.queryCata = queryCata;
            this.params = params;
        }

        public void doStart() {
            progressDialog = ProgressDialog.show(self, "提示", "正在请求数据,请稍等...",
                    true);
            progressDialog.setCancelable(true);
            start();
        }

        @Override
        public void run() {
            RestfulDao dao = RestfulDaoFactory.getDao();
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            if (queryCata == QUERY_DRV_INFO) {
                WebQueryResult<String> j = dao.jycxQueryDrv(params[0], params[1]);
                String sjson = j.getResult();
                WebQueryResult<VioDrvBean> re = new WebQueryResult<VioDrvBean>();
                JSONObject json = GlobalMethod.getErrorMessageFromJson(re, sjson);
                if (json != null) {
                    VioDrvBean drv = new VioDrvBean();
                    drv.setDabh(json.optString("dabh"));
                    drv.setSfzmhm(json.optString("sfzmhm"));
                    drv.setXm(json.optString("xm"));
                    re.setResult(drv);
                }
                b.putSerializable("drv", re);
            } else if (queryCata == QUERY_VEH_INFO) {
                String sjson = dao.jycxQueryVeh(params[0],
                        params[1]).getResult();
                WebQueryResult<VioVehBean> re = new WebQueryResult<VioVehBean>();
                JSONObject json = GlobalMethod.getErrorMessageFromJson(re, sjson);
                if (json != null) {
                    VioVehBean veh = new VioVehBean();
                    veh.setHpzl(json.optString("hpzl"));
                    veh.setHphm(json.optString("hphm"));
                    String cllx = json.optString("cllx");
                    if (!TextUtils.isEmpty(cllx)) {
                        veh.setCllx(cllx);
                        if (cllx.startsWith("K")) {
                            veh.setHdzzl(json.optString("hdzk"));
                        } else {
                            veh.setHdzzl(json.optString("hdzzl"));
                        }
                    }
                    re.setResult(veh);
                }
                b.putSerializable("veh", re);
            }
            msg.setData(b);
            mHandler.sendMessage(msg);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }

    }

    static class SpringDjHandler extends Handler {

        private final WeakReference<JbywSpringDjMainActivity> myActivity;
        private int cata;

        public SpringDjHandler(JbywSpringDjMainActivity activity, int cata) {
            myActivity = new WeakReference<JbywSpringDjMainActivity>(activity);
            this.cata = cata;
        }

        @Override
        public void handleMessage(Message msg) {
            JbywSpringDjMainActivity ac = myActivity.get();
            if (ac != null) {
                if (cata == QUERY_DRV_HANDLER)
                    ac.queryDrvHandler(msg);
                else if (cata == QUERY_VEH_HANDLER)
                    ac.queryVehHandler(msg);
            }
        }
    }


    private void queryDrvHandler(Message msg) {
        Bundle b = msg.getData();
        WebQueryResult<VioDrvBean> re = (WebQueryResult<VioDrvBean>) b
                .getSerializable("drv");
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            VioDrvBean drv = re.getResult();
            if (drv != null) {
                edit_xm.setText(drv.getXm());
                edit_jszh.setText(drv.getSfzmhm());
                edit_dabh.setText(drv.getDabh());
            } else
                GlobalMethod.showErrorDialog("未查询到对应的驾驶员", self);
        } else {
            GlobalMethod.showErrorDialog(err, self);
        }
    }

    private void queryVehHandler(Message msg) {
        Bundle b = msg.getData();
        WebQueryResult<VioVehBean> re = (WebQueryResult<VioVehBean>) b
                .getSerializable("veh");
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            VioVehBean veh = re.getResult();
            if (veh != null) {
                if (!TextUtils.isEmpty(veh.getCllx())) {
                    GlobalMethod.changeSpinnerSelect(spin_jtfs, veh.getCllx(),
                            GlobalConstant.KEY);
                }
                if (!TextUtils.isEmpty(veh.getHdzzl())) {
                    if (djlx == 0)
                        edit_hzrs.setText(veh.getHdzzl());
                    else
                        edit_hzzl.setText(veh.getHdzzl());
                }
            } else
                GlobalMethod.showErrorDialog("未查询到对应的机动车", self);
        } else {
            GlobalMethod.showErrorDialog(err, self);
        }
    }

    private String saveViewIntoPojo() {
        String jcdd = edit_jcdd.getText().toString();
        if (TextUtils.isEmpty(jcdd))
            return "检查地点不能为空";
        String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spin_hpzl,
                GlobalConstant.KEY);
        String hphm = edit_hphm.getText().toString().trim();
        if (TextUtils.isEmpty(hphm)
                || (hphm.length() != 5 && hphm.length() != 6))
            return "号牌号码不正确";
        hphm = GlobalMethod.getKeyFromSpinnerSelected(spin_hpqz,
                GlobalConstant.VALUE) + hphm.toUpperCase();
        String jtfs = GlobalMethod.getKeyFromSpinnerSelected(spin_jtfs,
                GlobalConstant.KEY);
        if (TextUtils.isEmpty(jtfs))
            return "车辆类型即交通方式不能为空";
        String dabh = edit_dabh.getText().toString();
        if (TextUtils.isEmpty(dabh) || !TextUtils.isDigitsOnly(dabh)
                || dabh.length() != 12)
            return "档案编号不正确";

        String xm = edit_xm.getText().toString();
        if (TextUtils.isEmpty(xm))
            return "姓名不能为空";
        String jszh = edit_jszh.getText().toString();
        if (!IDCard.Verify(jszh))
            return "驾驶证号不正确";
        if (djlx == 0) {
            String hzrs = edit_hzrs.getText().toString();
            if (TextUtils.isEmpty(hzrs) || !TextUtils.isDigitsOnly(hzrs))
                return "核载人数不正确";
            String szrs = edit_szrs.getText().toString();
            if (TextUtils.isEmpty(szrs) || !TextUtils.isDigitsOnly(szrs))
                return "实载人数不正确";
            String lxjssj = edit_lxjssj.getText().toString();
            if (TextUtils.isEmpty(szrs) || !GlobalMethod.isDouble(lxjssj)) {
                return "连续驾驶时间不正确，可以为小数";
            }
            kcdj.setJcdd(jcdd);
            kcdj.setHpzl(hpzl);
            kcdj.setHphm(hphm);
            kcdj.setCllx(jtfs);
            kcdj.setDabh(dabh);
            kcdj.setDsr(xm);
            kcdj.setSfzh(jszh);
            kcdj.setJszsyqk(GlobalMethod.getKeyFromSpinnerSelected(spin_jzsy,
                    GlobalConstant.KEY));
            kcdj.setCljyqk(GlobalMethod.getKeyFromSpinnerSelected(spin_cljy,
                    GlobalConstant.KEY));
            kcdj.setWfxw(edit_wfxw.getText().toString());
            kcdj.setWfcljg(edit_clqk.getText().toString());
            kcdj.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
            kcdj.setDjjg(GlobalData.grxx.get(GlobalConstant.YBMBH));
            // -------------------------------------------------------
            kcdj.setHzrs(hzrs);
            kcdj.setSzrs(szrs);
            kcdj.setLxjssj(lxjssj);
            kcdj.setJcsj(sdf.format(new Date()));
            kcdj.setGxsj(sdf.format(new Date()));
        } else {
            String hzzl = edit_hzzl.getText().toString();
            if (TextUtils.isEmpty(hzzl) || !TextUtils.isDigitsOnly(hzzl))
                return "核载质量不正确";
            String szzl = edit_szzl.getText().toString();
            if (TextUtils.isEmpty(szzl) || !TextUtils.isDigitsOnly(szzl))
                return "实载质量不正确";
            whpdj.setJcdd(jcdd);
            whpdj.setHpzl(hpzl);
            whpdj.setHphm(hphm);
            whpdj.setCllx(jtfs);
            whpdj.setDabh(dabh);
            whpdj.setDsr(xm);
            whpdj.setSfzh(jszh);
            whpdj.setJszsyqk(GlobalMethod.getKeyFromSpinnerSelected(spin_jzsy,
                    GlobalConstant.KEY));
            whpdj.setCljyqk(GlobalMethod.getKeyFromSpinnerSelected(spin_cljy,
                    GlobalConstant.KEY));
            whpdj.setWfxw(edit_wfxw.getText().toString());
            whpdj.setWfcljg(edit_clqk.getText().toString());
            whpdj.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
            whpdj.setDjjg(GlobalData.grxx.get(GlobalConstant.YBMBH));
            whpdj.setHzzl(hzzl);
            whpdj.setSzzl(szzl);
            whpdj.setZzwpmc(edit_wpmc.getText().toString());
            whpdj.setYyryqk(edit_yyryqk.getText().toString());
            whpdj.setClaqss(edit_claqss.getText().toString());
            whpdj.setJcsj(sdf.format(new Date()));
            whpdj.setGxsj(sdf.format(new Date()));
        }
        return null;
    }

    private List<KeyValueBean> getXfJtfs(int lx) {
        String wh = "substr(dmz,1,1) IN ('K')";
        if (lx == 1)
            wh = "substr(dmz,1,1) IN ('H')";
        ArrayList<KeyValueBean> jtfsList = ViolationDAO.getAllFrmCode(
                GlobalConstant.JTFS, "dmz","dmsm1",GlobalMethod.getBoxStore(self));
        return jtfsList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            if (requestCode == REQCODE_JTFS) {
                String j = b.getString("jtfsDm");
                GlobalMethod.changeSpinnerSelect(spin_jtfs, j,
                        GlobalConstant.KEY);
            }
        } else {
            // edWfdd.setText("");
        }
        if (requestCode == HAS_INSTALL) {
            if (!hasApk()) {
                GlobalMethod.showErrorDialog("二代证服务程序未能正常安装，请重试", self);
            } else {
                isAlps = TextUtils.equals(Build.BRAND, "alps");
                if (isAlps) {
                    startReadID2Service();
                    initRecevier();
                }
            }
        }
    }

    private void uploadDj(SpringDjItf dj) {
        SpringHandler h = new SpringHandler(JbywSpringDjMainActivity.this,
                dj.getId(), dj.getDjlx());
        UploadSpringThread thread = new UploadSpringThread(h, dj, self);
        thread.start();
    }

    static class SpringHandler extends Handler {

        private final WeakReference<JbywSpringDjMainActivity> myActivity;
        private int lx;
        private String id;

        public SpringHandler(JbywSpringDjMainActivity activity, String id,
                             int lx) {
            myActivity = new WeakReference<JbywSpringDjMainActivity>(activity);
            this.lx = lx;
            this.id = id;
        }

        @Override
        public void handleMessage(Message msg) {
            JbywSpringDjMainActivity ac = myActivity.get();
            if (ac != null) {
                ac.uploadHandler(msg, id, lx);
            }
        }
    }

    private void uploadHandler(Message msg, String id, int lx) {
        Bundle data = msg.getData();
        WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) data
                .getSerializable(UploadSpringThread.UPLOAD_RESULT);
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            ZapcReturn zr = re.getResult();
            if (TextUtils.equals(zr.getCgbj(), "1"))
                MessageDao.updateSpringScbj(id, lx,GlobalMethod.getBoxStore(self));
            Toast.makeText(self, zr.getScms(), Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(self, err, Toast.LENGTH_LONG).show();

    }

    //------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        //if (!mBindReadServiceIsOk) {
        //     startReadID2Service();
        // }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if (mBindReadServiceIsOk) {
        //    unbindService(readCardCon);
        //    mBindReadServiceIsOk = false;
        //}
    }

    /**
     * @throws
     * @Title: startReadID2Service
     * @author xc
     * @Description: TODO(打开二代证读卡服务)
     * @param:
     * @return: void
     */
    public void startReadID2Service() {
        if (bindService(new Intent(IReadCardService.class.getName()),
                readCardCon, Context.BIND_AUTO_CREATE)) {
            mBindReadServiceIsOk = readCardCon != null;
        } else {
            myToast("绑定读卡服务不成功，请确认是否安装并启动服务");
        }
    }

    private void myToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private String TAG = "JbywSpringDjMain";

    /**
     * @Fields readCardCon : TODO(读卡连接)
     */
    public ServiceConnection readCardCon = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.e(TAG, "读卡服务连接不成功");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mIReadCardService = IReadCardService.Stub.asInterface(service);
            if (mIReadCardService != null) {
                Log.e(TAG, "读卡服务连接成功");
                try {
                    mIReadCardService.startReadCard();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    private void initRecevier() {
        // 二代证
        mID2DataReceiver = new ID2DataReceiver();
        IntentFilter filterID2 = new IntentFilter();// 创建IntentFilter对象
        // 注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
        // filterID2.addAction(ClientVars.receivefromserver);
        filterID2.addAction("com.sdses.readercontrol");
        registerReceiver(mID2DataReceiver, filterID2);
    }

    public class ID2DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // log.info("ID2DataReceiver接收消息" + intent.getAction());
            Bundle bundle = intent.getExtras();
            if (intent.getAction().equals("com.sdses.readercontrol")) {
                Log.w(TAG, "开启读卡成功");
                Log.w(TAG, "readcard value is" + bundle.getInt(ClientVars.para));
                switch (bundle.getInt(ClientVars.para)) {
                    case ClientVars.deviceConnect_OK:
                        // 启动读卡服务成功
                        break;
                    case ClientVars.deviceDisConnect:
                        // 断开设备连接成功
                        break;
                    case ClientVars.receivefromcardOk:
                        Log.w(TAG, "寻卡成功...");
                        // tv_readCardStatus.setText("正在读卡...");
                        myToast("正在读卡...");
                        break;
                    case ClientVars.readAllInfoOk:
                        Log.w(TAG, "读卡成功");
                        m_soundSucess.start();
                        Arrays.fill(ID2Bytes, (byte) 0x00);
                        ID2Bytes = bundle.getByteArray("dataSrc");
                        Log.w(TAG, "id2 length" + ID2Bytes.length);
                        showID2Info(ID2Bytes);
                        break;
                    case ClientVars.readAddressAdd:
                        // 追加地址。建议不要读取追加地址，很少有这种证件。
                        //
                        break;
                    case ClientVars.readSAMSN:
                        Log.w(TAG, "receive samsn message");
                        // setWarningInfo("" + bundle.getString("samsn"));
                        break;

                    case ClientVars.deviceConnect_ERROR:
                        // F0 连接读卡设备失败原因
                        // setWarningInfo(bundle.getString(ClientVars.extra));
                        // mButtonReader.setChecked(false);
                        break;
                    case ClientVars.deviceDisConnect_ERROR:
                        // F3断开读卡设备失败原因
                        // setWarningInfo(bundle.getString(ClientVars.extra));
                        // mButtonReader.setAccessibilityDelegate(delegate);
                        break;
                    default: // 命令字 信息提示
                        // tv_readCardStatus.setText("读卡失败");
                        Log.w(TAG, "未知错误");
                        // setWarningInfo(bundle.getString(ClientVars.extra));
                        // mButtonReader.setChecked(false);
                        break;
                }
            }
        }
    }

    /**
     * @throws
     * @Title: clearID2Info
     * @author xc
     * @Description: TODO(清除二代证信息)
     * @param:
     * @return: void
     */

    private void showID2Info(byte[] data) {
        try {
            ID2Data _id2Data = new ID2Data();
            _id2Data.decode_debug(data);
            _id2Data.rePackage();
            Log.w(TAG, "sex" + _id2Data.getmID2Txt().getmGender().trim());
            String name = _id2Data.getmID2Txt().getmName().trim(); // 姓名
            String sex = _id2Data.getmID2Txt().getmGender().trim();

            String et_nation = _id2Data.getmID2Txt().getmNational().trim(); // 民族
            String bir = _id2Data.getmID2Txt().getmBirthYear().trim() + "年"
                    + _id2Data.getmID2Txt().getmBirthMonth().trim() + "月"
                    + _id2Data.getmID2Txt().getmBirthDay().trim() + "日";
            String et_address = (_id2Data.getmID2Txt().getmAddress().trim()); // 住址
            String et_id2Num = (_id2Data.getmID2Txt().getmID2Num().trim()); // 公民身份号码
            edit_jszh.setText(et_id2Num);
            queryDrvBySfzh();
            String et_issnue = (_id2Data.getmID2Txt().getmIssue().trim()); // 签发机关
            String et_time = (_id2Data.getmID2Txt().getmBegin().trim()
                    + "--" + _id2Data.getmID2Txt().getmEnd().trim()); // 有效期限
            //Bitmap bm = BitmapFactory.decodeByteArray(_id2Data.getmID2Pic()
            //        .getHeadFromCard(), 0, 38862);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryDrvBySfzh() {
        String sfzh = edit_jszh.getText().toString();
        if (TextUtils.isEmpty(sfzh)) {
            GlobalMethod.showErrorDialog("身份证号不能为空", self);
            return;
        }
        SpringQueryInfoThread thread = new SpringQueryInfoThread(
                new SpringDjHandler(JbywSpringDjMainActivity.this,
                        QUERY_DRV_HANDLER), QUERY_DRV_INFO,
                new String[]{"", sfzh.toUpperCase()});
        thread.doStart();
    }

    private boolean hasApk() {
        PackageManager pm = self.getPackageManager();
        //List<ApplicationInfo> listAppcations = pm
        //        .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        //for(ApplicationInfo p:listAppcations){
        //    Log.e("JbywDj",p.packageName);
        //}
        boolean hasQr = false;
        try {
            PackageInfo info = pm.getPackageInfo("com.sdses.readcardservice500", 0);
            hasQr = info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return hasQr;
    }

}
