package com.jwt.update;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.adapter.FxczfListAdapter;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.adapter.SelectObjectBean;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.FxczfDao;
import com.jwt.event.CommEvent;
import com.jwt.event.FxcUploadEvent;
import com.jwt.pojo.VioFxczfBean;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintJdsTools;
import com.jwt.thread.CommQueryThread;
import com.jwt.thread.FxcListUploadThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JbywFxcListActivity extends AppCompatActivity {
    public static final int SEQ_NEW_FXC = 100;

    public static final int SEQ_MODIFY_FXC = 103;

    private Button btnShowFxc, btnUpload, btnPrint, btnNew;

    private Spinner spinXslx;

    private Context self;

    private BlueToothPrint btp = null;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private FxczfListAdapter adapter;

    private List<SelectObjectBean<VioFxczfBean>> fxcList;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        self = this;
        setTitle("非现场执法");
        EventBus.getDefault().register(this);
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
        spinXslx.setOnItemSelectedListener(xslxChangeListener);
        changeDataFromDb();
        RecyclerView view = (RecyclerView) findViewById(R.id.gridView1);
        view.setHasFixedSize(false);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        adapter = new FxczfListAdapter(fxcList, itemClickListener);
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialog = new MaterialDialog.Builder(self)
                .title("正在上传")
                .content("上传中...")
                .progress(false, 150, true).build();
    }

    private OnSpinnerItemSelected xslxChangeListener = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1,
                                   int position, long arg3) {
            changeDataFromDb();
            adapter.notifyDataSetChanged();
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
                for (SelectObjectBean<VioFxczfBean> f : fxcList) {
                    VioFxczfBean fxc = f.getBean();
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
                dialog.show();
                FxcListUploadThread thread = new FxcListUploadThread(self, unloads);
                thread.doStart();
            }
            return true;
            case R.id.menu_del: {
                int[] sels = adapter.getSelectIndexs();
                if (sels == null || sels.length <= 0) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return false;
                }
                GlobalMethod.showDialogTwoListener("系统提示", "是否确定删除，此操作无法恢复", "删除",
                        "取消", delRecodeListener, self);
            }
            return true;
            case R.id.menu_rkqk: {
                int[] sels = adapter.getSelectIndexs();
                if (sels == null || sels.length <= 0 || sels.length > 1) {
                    GlobalMethod.showErrorDialog("请选择一条记录操作", self);
                    return false;
                }
                VioFxczfBean fxc = adapter.getFirstSelectItem();
                if (!TextUtils.equals(fxc.getScbj(), "1")) {
                    GlobalMethod.showErrorDialog("记录未上传，不能查询", self);
                    return true;
                }
                //UpHandler handler = new UpHandler(this, HANDLER_CATALOG_QUERY_RKQK);
                CommQueryThread thread = new CommQueryThread(CommQueryThread.QUERY_FXCZF_RKQK, new String[]{fxc.getXtxh()}, self);
                thread.doStart();
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
            if (v == btnShowFxc) {
                int count = adapter.getItemCount();
                if (count != 1) {
                    GlobalMethod.showErrorDialog("请选择一条数据操作", self);
                    return;
                }
                VioFxczfBean fxc = adapter.getFirstSelectItem();
                Intent intent = new Intent(self, JbywFxcShowActivity.class);
                intent.putExtra("fxc", fxc);
                startActivity(intent);
            } else if (v == btnUpload) {
                int count = adapter.getItemCount();
                if (count <= 0) {
                    GlobalMethod.showErrorDialog("请选择数据操作", self);
                    return;
                }
                List<VioFxczfBean> fxcs = adapter.getSelectItems();
                List<VioFxczfBean> unloads = new ArrayList<VioFxczfBean>();
                int timeout = 0;
                for (VioFxczfBean fxc : fxcs) {
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
                    return;
                }
                FxcListUploadThread thread = new FxcListUploadThread(self, unloads);
                thread.doStart();
                dialog.show();
            } else if (v == btnPrint) {
                int count = adapter.getItemCount();
                if (count != 1) {
                    GlobalMethod.showErrorDialog("请选择一条数据操作", self);
                    return;
                }
                VioFxczfBean fxc = adapter.getFirstSelectItem();
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
     * 删除对话框中删除的监听
     */
    private DialogInterface.OnClickListener delRecodeListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            List<VioFxczfBean> list = adapter.getSelectItems();
            GlobalMethod.getBoxStore(self).boxFor(VioFxczfBean.class).remove(list);
            changeDataFromDb();
            adapter.notifyDataSetChanged();
        }
    };

    /**
     * 重新加载数据列表，并更新显示列表，不触发重显示列表
     */
    private void changeDataFromDb() {
        if (fxcList == null)
            fxcList = new ArrayList<>();
        fxcList.clear();
        String xslx = GlobalMethod.getKeyFromSpinnerSelected(spinXslx, GlobalConstant.KEY);
        List<VioFxczfBean> list = FxczfDao.getFxczfByScbj(xslx, 200, GlobalMethod.getBoxStore(self));
        for (VioFxczfBean fxc : list) {
            fxcList.add(new SelectObjectBean(fxc, false));
        }
        setTitle("非现场执法－" + fxcList.size() + "条");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEQ_NEW_FXC || requestCode == SEQ_MODIFY_FXC) {
            if (resultCode == RESULT_OK) {
                changeDataFromDb();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rkqkEvent(CommEvent event) {
        if (event.getStatus() != 200) {
            if (TextUtils.isEmpty(event.getMessage())) {
                GlobalMethod.showErrorDialog("无查询结果", self);
                return;
            }
        }
        GlobalMethod.showDialog("入库情况", event.getMessage(), "知道了", self);
    }

    /**
     * 上传数据时的更新
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadEvent(FxcUploadEvent event) {
        if (event.total < 0 || event.step < 0) {
            dialog.dismiss();
            GlobalMethod.showErrorDialog(event.message, self);
            return;
        }
        if (event.isDone) {
            Toast.makeText(self, "上传成功", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            changeDataFromDb();
            adapter.notifyDataSetChanged();
            return;
        }
        dialog.setMaxProgress(event.total);
        dialog.setProgress(event.step);
    }

    private void printFxcTzs(VioFxczfBean fxczf) {
        // 设置打印的名字，打印时在数据库中取
        String pname = GlobalMethod.getSavedInfo(this, GlobalConstant.GRXX_PRINTER_NAME);
        String paddress = GlobalMethod.getSavedInfo(this, GlobalConstant.GRXX_PRINTER_ADDRESS);
        Log.e("PrintList", pname + "/" + paddress);
        KeyValueBean printerInfo = null;
        if (!TextUtils.isEmpty(pname) && !TextUtils.isEmpty(paddress)) {
            printerInfo = new KeyValueBean(pname, paddress);
        }

        if (printerInfo == null || TextUtils.isEmpty(printerInfo.getValue())) {
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

    FxczfListAdapter.ClickListener itemClickListener = new FxczfListAdapter.ClickListener() {

        @Override
        public void onNoteClick(int position) {
            boolean isSel = fxcList.get(position).isSel();
            fxcList.get(position).setSel(!isSel);
            adapter.notifyItemChanged(position);
            //for (SelectObjectBean tt : fxcList)
            //    tt.setSel(false);
            //fxcList.get(position).setSel(!isSel);
            //adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (btp != null) {
            btp.closeConn();
        }

    }

}
