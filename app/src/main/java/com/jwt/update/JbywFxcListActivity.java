package com.jwt.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.jwt.activity.CommTwoRowSelectAcbarListActivity;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TwoColTwoSelectBean;
import com.jwt.dao.FxczfDao;
import com.jwt.pojo.VioFxcFileBean;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintJdsTools;
import com.jwt.thread.FxcListUploadThread;
import com.jwt.thread.QueryDrvVehThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.web.WebQueryResult;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JbywFxcListActivity extends CommTwoRowSelectAcbarListActivity {
    public static final int SEQ_NEW_FXC = 100;

    public static final int SEQ_MODIFY_FXC = 103;

    private Button btnShowFxc, btnUpload, btnPrint, btnNew;

    private Spinner spinXslx;

    private List<VioFxczfBean> fxcList;

    private Context self;

    private ProgressDialog progressDialog;

    private BlueToothPrint btp = null;

    private static final int HANDLER_CATALOG_UPLOAD_FXCZF = 111;

    private static final int HANDLER_CATALOG_QUERY_RKQK = 112;

    private static final int HANDLER_CATALOG_DEL_FILE = 113;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.fxczf_show_list);
        btnUpload = (Button) findViewById(R.id.btn_two);
        btnUpload.setText("上传");
        btnNew = (Button) findViewById(R.id.btn_one);
        btnNew.setText("新增");
        btnPrint = (Button) findViewById(R.id.btn_three);
        btnPrint.setText("打印");
        btnShowFxc = (Button) findViewById(R.id.btn_four);
        btnShowFxc.setText("详细");
        btnShowFxc.setOnClickListener(clickListener);
        btnUpload.setOnClickListener(clickListener);
        btnPrint.setOnClickListener(clickListener);
        btnNew.setOnClickListener(clickListener);
        spinXslx = (Spinner) findViewById(R.id.spin_xslx);
        List<KeyValueBean> wslbs = new ArrayList<KeyValueBean>();
        wslbs.add(new KeyValueBean("0", "全部"));
        wslbs.add(new KeyValueBean("1", "未上传"));
        wslbs.add(new KeyValueBean("2", "上传成功"));
        GlobalMethod.changeAdapter(spinXslx, wslbs, (Activity) self);
        spinXslx.setSelection(1);
        initView();
        spinXslx.setOnItemSelectedListener(xslxChangeListener);
        changeDataFromDb();
    }

    private OnSpinnerItemSelected xslxChangeListener = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1,
                                   int position, long arg3) {
            referView();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fxczf_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_all_upload: {
                if (fxcList == null || fxcList.isEmpty()) {
                    GlobalMethod.showErrorDialog("没有记录可供上传", self);
                    return true;
                }
                List<VioFxczfBean> unloads = new ArrayList<VioFxczfBean>();
                int timeout = 0;
                for (VioFxczfBean fxc : fxcList) {
                    if (!isUploadTime(fxc)) {
                        timeout++;
                        continue;
                    }
                    boolean isSc = TextUtils.equals(fxc.getScbj(), "1");
                    if (!isSc)
                        unloads.add(fxc);
                }
                if (unloads.isEmpty()) {
                    if (timeout > 0)
                        GlobalMethod.showErrorDialog("没有记录可供上传，" + GlobalSystemParam.unsend_fxc_hours + "个小时前的记录不能上传！", self);
                    else
                        GlobalMethod.showErrorDialog("没有记录可供上传", self);
                    return true;
                }
                UploadAllFxczfHandler handler = new UploadAllFxczfHandler(this);
                FxcListUploadThread thread = new FxcListUploadThread(self, handler,
                        unloads);
                thread.doStart();
            }
            return true;
            case R.id.menu_del: {
                if (selectedIndex < 0) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return false;
                }
                GlobalMethod.showDialogTwoListener("系统提示", "是否确定删除，此操作无法恢复", "删除",
                        "取消", delRecodeListener, self);
            }
            return true;
            case R.id.menu_del_all: {
                if (fxcList == null || fxcList.isEmpty()) {
                    GlobalMethod.showErrorDialog("没有记录可供删除", self);
                    return true;
                }
                List<VioFxczfBean> loaded = new ArrayList<VioFxczfBean>();
                for (VioFxczfBean fxc : fxcList) {
                    boolean isSc = TextUtils.equals(fxc.getScbj(), "1");
                    if (isSc)
                        loaded.add(fxc);
                }
                if (loaded.isEmpty()) {
                    GlobalMethod.showErrorDialog("没有记录可供删除", self);
                    return true;
                }
                List<String> fileList = new ArrayList<String>();
                for (VioFxczfBean fxc : loaded) {
                    List<VioFxcFileBean> files = FxczfDao.queryFxczfFileByFId(fxc.getId(), GlobalMethod.getBoxStore(self));
                    for (VioFxcFileBean p : files) {
                        File small = new File(p.getWjdz());
                        File big = new File(small.getParentFile().getParentFile(), small.getName());
                        fileList.add(small.getAbsolutePath());
                        fileList.add(big.getAbsolutePath());
                        Log.e("FXCLIST SMALL", small.getAbsolutePath());
                        Log.e("FXCLIST BIG", big.getAbsolutePath());
                    }
                    FxczfDao.delFxczf(fxc.getId(), GlobalMethod.getBoxStore(self));
                }
                if (fileList != null && !fileList.isEmpty()) {
                    String[] params = new String[fileList.size()];
                    params = fileList.toArray(params);
                    //UpHandler handler = new UpHandler(JbywFxcListActivity.this, HANDLER_CATALOG_DEL_FILE);
                    //QueryDrvVehThread thread = new QueryDrvVehThread(handler, QueryDrvVehThread.DEL_PHOTO_FILE, params, self);
                    //thread.doStart();
                } else {
                    referView();
                }

            }
            return true;
            case R.id.menu_rkqk: {
                if (selectedIndex < 0) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return false;
                }
                VioFxczfBean fxc = fxcList.get(selectedIndex);
                if (!TextUtils.equals(fxc.getScbj(), "1")) {
                    GlobalMethod.showErrorDialog("记录未上传，不能查询", self);
                    return true;
                }
                //UpHandler handler = new UpHandler(this, HANDLER_CATALOG_QUERY_RKQK);
                //QueryDrvVehThread thread = new QueryDrvVehThread(handler, QueryDrvVehThread.QUERY_FXCZF_RKQK, new String[]{fxc.getXtxh()}, self);
                //thread.doStart();
            }
            return true;
            default:
                break;
        }
        return false;
    }

    /**
     * 按扭的监听
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnNew) {
                Intent intent = new Intent(self, JbywFxcActivity.class);
                startActivityForResult(intent, SEQ_NEW_FXC);
                return;
            }
            if (selectedIndex < 0 || fxcList == null
                    || fxcList.get(selectedIndex) == null) {
                GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                return;
            }
            VioFxczfBean fxc = fxcList.get(selectedIndex);
            if (v == btnShowFxc) {
                Intent intent = new Intent(self, JbywFxcShowActivity.class);
                intent.putExtra("fxc", fxc);
                startActivity(intent);
            } else if (v == btnUpload) {
                if (!isUploadTime(fxc)) {
                    GlobalMethod.showErrorDialog("超过" + GlobalSystemParam.unsend_fxc_hours + "个小时不能上传了", self);
                    return;
                }
                boolean isSc = FxczfDao.isCompleteUpload(fxc, GlobalMethod.getBoxStore(self));
                if (isSc) {
                    GlobalMethod.showDialogTwoListener("系统提示", "记录已上传，是否需重复上传", "重传", "取消", reUploadFxc, self);
                    return;
                }
                uploadFxc(fxc);
            } else if (v == btnPrint) {
                printFxcTzs(fxc);
            }
        }
    };

    /**
     * 是否可以上传，未到达上传时间
     *
     * @param fxc
     * @return 真 可以上传，假 不可以上传
     */
    private boolean isUploadTime(VioFxczfBean fxc) {
        if (!TextUtils.isEmpty(fxc.getXtxh()))
            return true;
        try {
            Calendar c = Calendar.getInstance();
            Date wfsj = sdf.parse(fxc.getWfsj());
            Calendar cw = Calendar.getInstance();
            cw.setTime(wfsj);
            //违法时间加上12个小时，如果还是小于当前时间，则不能上传
            cw.add(Calendar.HOUR, GlobalSystemParam.unsend_fxc_hours);
            Log.e("JbywFxcList", "加上13个小时的违法时间：" + sdf.format(cw.getTime()));
            //加上特定时间大于当前时间，可以上传
            return cw.compareTo(c) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询记录是否已完全上传
     *
     * @param fxc
     * @param dao
     * @return
     */
    private boolean isCompleteUpload(VioFxczfBean fxc, FxczfDao dao) {
        boolean isSc = TextUtils.equals(fxc.getScbj(), "1");
        //int unUploadFile = dao.queryUnUploadPhotoCount(fxc.getId());
        //isSc = isSc & (unUploadFile <= 0);
        return isSc;
    }

    private DialogInterface.OnClickListener reUploadFxc = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (selectedIndex < 0 || fxcList == null
                    || fxcList.get(selectedIndex) == null) {
                GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                return;
            }
            VioFxczfBean fxc = fxcList.get(selectedIndex);
            uploadFxc(fxc);
        }
    };

    /**
     * 删除对话框中删除的监听
     */
    private DialogInterface.OnClickListener delRecodeListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (selectedIndex < 0 || fxcList == null || fxcList.isEmpty())
                return;
            VioFxczfBean fxc = fxcList.get(selectedIndex);
            List<VioFxcFileBean> files = FxczfDao.queryFxczfFileByFId(fxc.getId(), GlobalMethod.getBoxStore(self));
            List<String> list = new ArrayList<String>();
            for (VioFxcFileBean p : files) {
                File small = new File(p.getWjdz());
                File big = new File(small.getParentFile().getParentFile(), small.getName());
                list.add(small.getAbsolutePath());
                list.add(big.getAbsolutePath());
                Log.e("FXCLIST SMALL", small.getAbsolutePath());
                Log.e("FXCLIST BIG", big.getAbsolutePath());
            }
            FxczfDao.delFxczf(fxc.getId(), GlobalMethod.getBoxStore(self));
            if (list != null && !list.isEmpty()) {
                String[] params = new String[list.size()];
                params = list.toArray(params);
                //UpHandler handler = new UpHandler(JbywFxcListActivity.this, HANDLER_CATALOG_DEL_FILE);
                //QueryDrvVehThread thread = new QueryDrvVehThread(handler, QueryDrvVehThread.DEL_PHOTO_FILE, params, self);
                //thread.doStart();
            } else {
                referView();
            }
        }
    };

    /**
     * 重新加载数据列表，并更新显示列表，不触发重显示列表
     */
    private void changeDataFromDb() {
        selectedIndex = -1;
        if (beanList == null)
            beanList = new ArrayList<TwoColTwoSelectBean>();
        beanList.clear();
        String xslx = GlobalMethod.getKeyFromSpinnerSelected(spinXslx, GlobalConstant.KEY);
        Log.e("fxc list", xslx);
        fxcList = FxczfDao.getFxczfByScbj(xslx, 200, GlobalMethod.getBoxStore(self));
        Log.e("fxc list", "list size: " + fxcList.size());
        if (fxcList == null || fxcList.isEmpty()) {
            setTitle("非现场执法");
            return;
        }
        for (VioFxczfBean fxc : fxcList) {
            String text1 = fxc.getWfsj() + "，号牌：" + fxc.getHphm() + "，"
                    + fxc.getPhotos() + "张";
            String text2 = fxc.getWfxw() + "，地点：" + fxc.getWfdz();
            boolean isSc = FxczfDao.isCompleteUpload(fxc, GlobalMethod.getBoxStore(self));
            beanList.add(new TwoColTwoSelectBean(text1, text2, isSc, false));
        }
        setTitle("非现场执法－" + fxcList.size() + "条");
    }

    private void referView() {
        changeDataFromDb();
        getCommAdapter().notifyDataSetChanged();
    }

    private void uploadFxc(VioFxczfBean fxc) {
        List<VioFxcFileBean> files = FxczfDao.queryFxczfFileByFId(fxc.getId(), GlobalMethod.getBoxStore(self));
        if (files == null || files.isEmpty()) {
            GlobalMethod.showErrorDialog("无图片", self);
            return;
        }
        progressDialog = new ProgressDialog(self);
        int maxStep = files.size() + 1;
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在上传非现场信息...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxStep * 25);
        progressDialog.show();
        //UpHandler upHandler = new UpHandler(this, HANDLER_CATALOG_UPLOAD_FXCZF);
        //FxcUploadPhotoThread thread = new FxcUploadPhotoThread(self, upHandler,
        //       fxc, files);
        //thread.doStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEQ_NEW_FXC || requestCode == SEQ_MODIFY_FXC) {
            if (resultCode == RESULT_OK) {
                changeDataFromDb();
                getCommAdapter().notifyDataSetChanged();
                selectedIndex = -1;
            }
        }
    }

    static class UploadAllFxczfHandler extends Handler {

        private final WeakReference<JbywFxcListActivity> myActivity;

        public UploadAllFxczfHandler(JbywFxcListActivity activity) {
            myActivity = new WeakReference<JbywFxcListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            JbywFxcListActivity ac = myActivity.get();
            if (ac != null) {
                ac.handListUpload(msg);
            }
        }
    }

    public void handListUpload(Message msg) {
        Bundle data = msg.getData();
        if (data != null) {
            int maxStep = data.getInt("all");
            int error = data.getInt("error");
            if (error > 0) {
                GlobalMethod.showErrorDialog("上传完成，但有" + error + "处错误，请检查", self);
            }
            if (maxStep > 0) {
                selectedIndex = -1;
                changeDataFromDb();
                getCommAdapter().notifyDataSetChanged();
            }
        }
    }

    static class UpHandler extends Handler {


        private final WeakReference<JbywFxcListActivity> myActivity;
        private int catalog;

        public UpHandler(JbywFxcListActivity activity, int _catalog) {
            myActivity = new WeakReference<JbywFxcListActivity>(activity);
            this.catalog = _catalog;
        }

        @Override
        public void handleMessage(Message msg) {
            JbywFxcListActivity ac = myActivity.get();
            if (ac != null) {
                if (catalog == HANDLER_CATALOG_UPLOAD_FXCZF)
                    ac.handleUploadMessage(msg);
                else if (catalog == HANDLER_CATALOG_QUERY_RKQK)
                    ac.handlerQueryRkqk(msg);
                else if (catalog == HANDLER_CATALOG_DEL_FILE) {
                    ac.referView();
                }
            }
        }
    }

    /**
     * 入库情况查询
     *
     * @param msg
     */
    private void handlerQueryRkqk(Message msg) {
        Bundle data = msg.getData();
        if (data == null) {
            GlobalMethod.showErrorDialog("查询出现错误", self);
            return;
        }
        WebQueryResult<String> re = (WebQueryResult<String>) data.get(QueryDrvVehThread.RESULT_FXCZF_RKQK);
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (!TextUtils.isEmpty(err)) {
            GlobalMethod.showErrorDialog(err, self);
            return;
        }
        if (TextUtils.isEmpty(re.getResult())) {
            GlobalMethod.showErrorDialog("无查询结果", self);
            return;
        }
        GlobalMethod.showDialog("入库情况", re.getResult(), "知道了", self);
    }

    public void handleUploadMessage(Message msg) {
        int what = msg.what;
        Bundle data = msg.getData();
        int step = 0;
        switch (what) {
            case GlobalConstant.WHAT_ERR:
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                String err = data.getString("err");
                Toast.makeText(self, err, Toast.LENGTH_LONG).show();
                //GlobalMethod.showErrorDialog(err, self);
                break;
            case GlobalConstant.WHAT_RECODE_OK:
                step = msg.arg1;
                progressDialog.setProgress(step);
                break;
            case GlobalConstant.WHAT_PHOTO_OK:
                step = msg.arg1;
                progressDialog.setProgress(step);
                String info = data.getString("err");
                progressDialog.setMessage(info);
                break;
            case GlobalConstant.WHAT_ALL_OK:
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (msg.getData() != null) {
                    changeDataFromDb();
                    getCommAdapter().notifyDataSetChanged();
                }
                break;
            case GlobalConstant.WHAT_ALL_ERR:
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                changeDataFromDb();
                getCommAdapter().notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void printFxcTzs(VioFxczfBean fxczf) {
        // 设置打印的名字，打印时在数据库中取
        KeyValueBean printerInfo = new KeyValueBean(
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME),
                GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS));

        if (TextUtils.isEmpty(printerInfo.getValue())) {
            GlobalMethod.showDialog("错误信息", "没有配置默认打印机!", "返回", self);
            return;
        }
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return;
        }

        if (!TextUtils.isEmpty(printerInfo.getValue()) && btp == null) {
            btp = new BlueToothPrint(printerInfo.getValue());
        }

        if (btp == null)
            return;
        if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
            // 没有建立蓝牙串口流
            int errorStaus = btp.createSocket(btAdapter);
            if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
                GlobalMethod.showErrorDialog(
                        btp.getBluetoothCodeMs(errorStaus), self);
                return;
            }
        }
        List<JdsPrintBean> content = PrintJdsTools.getPrintFxczfContent(fxczf);
        int status = btp.printJdsByBluetooth(content);
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btp != null) {
            btp.closeConn();
        }
    }

}
