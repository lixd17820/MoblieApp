package com.jwt.update;

import java.util.ArrayList;
import java.util.List;

import com.jwt.activity.ActionBarListActivity;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.adapter.TwoLineSelectAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TwoLineSelectBean;
import com.jwt.dao.ViolationDAO;
import com.jwt.event.VioUploadEvent;
import com.jwt.jbyw.JdsPreviewActivity;
import com.jwt.pojo.VioViolation;
import com.jwt.pojo.VioViolation_;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintJdsTools;
import com.jwt.thread.UploadVioThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.objectbox.query.QueryBuilder;

public class JbywPrintJdsList extends ActionBarListActivity {

    private KeyValueBean printerInfo;
    private BlueToothPrint btp = null;
    public static final int REQUEST_ENABLE_BT = 2;
    private static final int MENU_PREVIEW = 10;
    private static final int MENU_PRINT = 11;
    private static final int MENU_UPLOAD = 12;
    private static final int MENU_DETAIL = 13;
    // private int printState = BlueToothPrint.BLUETOOTH_NONE;
    // 在单选列表中显示的对象
    private List<TwoLineSelectBean> strList = null;
    private List<VioViolation> puList;
    // private TextView title;
    private String title;

    private List<KeyValueBean> wslbs;
    private Spinner spinWslb;

    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wslbs = createWslbs();
        self = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.jwt_print_jds);
        // 删除过多
        ViolationDAO.delOldViolation(GlobalConstant.MAX_RECORDS,
                GlobalMethod.getBoxStore(self));
        spinWslb = (Spinner) findViewById(R.id.spin_wszl);
        GlobalMethod.changeAdapter(spinWslb, wslbs, (Activity) self);
        String wslb = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                GlobalConstant.KEY);
        puList = getViolationByConds(wslb);
        strList = new ArrayList<TwoLineSelectBean>();
        getList(puList);
        String pname = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME);
        String paddress = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS);
        if (!TextUtils.isEmpty(pname) && !TextUtils.isEmpty(paddress)) {
            printerInfo = new KeyValueBean(pname, paddress);
        }

        // title = ((TextView) findViewById(R.id.title_left_text));
        title = "打印决定书-" + puList.size() + "条";
        // TextView t2 = (TextView) findViewById(R.id.title_right_text);
        TwoLineSelectAdapter ad = new TwoLineSelectAdapter(this,
                R.layout.two_line_list_item, strList);
        getListView().setAdapter(ad);
        setTitle(title);

        // 文书打印
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long arg3) {
                // 单选,修改其他为不选
                for (int i = 0; i < strList.size(); i++) {
                    TwoLineSelectBean c = strList.get(i);
                    if (i == position)
                        c.setSelect(!c.isSelect());
                    else
                        c.setSelect(false);
                }

                TwoLineSelectAdapter ad = (TwoLineSelectAdapter) parent
                        .getAdapter();
                ad.notifyDataSetChanged();
            }
        });
        spinWslb.setOnItemSelectedListener(new OnSpinnerItemSelected() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String wslb = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                        GlobalConstant.KEY);
                puList = getViolationByConds(wslb);
                getList(puList);
                ((TwoLineSelectAdapter) getListView().getAdapter())
                        .notifyDataSetChanged();
            }
        });
    }

    public List<VioViolation> getViolationByConds(String wslb) {
        QueryBuilder<VioViolation> query = GlobalMethod.getBoxStore(this).boxFor(VioViolation.class).query();
        if ("1".equals(wslb)) {
            query = query.equal(VioViolation_.wslb, "1").equal(VioViolation_.cfzl, "2");
        } else if ("2".equals(wslb))
            query = query.equal(VioViolation_.wslb, "1").equal(VioViolation_.cfzl, "1");
        else if ("3".equals(wslb))
            query = query.equal(VioViolation_.wslb, "3");
        else if ("6".equals(wslb))
            query = query.equal(VioViolation_.wslb, "6");
        return query.build().find();
    }

    private List<KeyValueBean> createWslbs() {
        List<KeyValueBean> list = new ArrayList<KeyValueBean>();
        list.add(new KeyValueBean("1", "简易处罚"));
        list.add(new KeyValueBean("2", "轻微警告"));
        list.add(new KeyValueBean("3", "强制措施"));
        list.add(new KeyValueBean("6", "违法通知"));
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_vio_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position = getSelectItem();
        if (position < 0) {
            GlobalMethod.showDialog("错误信息", "请选择一条记录打印!", "返回",
                    JbywPrintJdsList.this);
            return true;
        }
        VioViolation punish = puList.get(position);
        switch (item.getItemId()) {
            case R.id.menu_jds_preview:
                ArrayList<JdsPrintBean> jds = PrintJdsTools.getPrintJdsContent(
                        punish, JbywPrintJdsList.this);
                Log.e("JbywPrintList", "决定书" + jds.size());
                Intent intent = new Intent(self, JdsPreviewActivity.class);
                intent.putExtra("jds", jds);
                startActivity(intent);
                break;
            case R.id.menu_jds_print:
                printJdsBySelect(punish);
                break;
            case R.id.menu_jds_upload:
                if (TextUtils.equals(punish.getScbj(), "1")) {
                    GlobalMethod.showErrorDialog("文书已上传,无需重复上传", self);
                    return true;
                }
                UploadVioThread thread = new UploadVioThread(punish, JbywPrintJdsList.this, true);
                thread.doStart();
                break;
            case R.id.menu_jds_detail:
                Intent intent2 = new Intent(self, JbywPrintJdsDetailActivity.class);
                intent2.putExtra("jdsbh", punish.getJdsbh());
                startActivity(intent2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // @Override
    // public void onClick(View v) {
    // int position = getSelectItem();
    // if (position < 0) {
    // GlobalMethod.showDialog("错误信息", "请选择一条记录打印!", "返回",
    // PrintJdsList.this);
    // return;
    // }
    // VioViolation punish = puList.get(position);
    // if (v.getId() == R.id.Butt_print) {
    // printJdsBySelect(punish);
    // } else if (v.getId() == R.id.Butt_prev) {
    // ArrayList<JdsPrintBean> jds = PrintJdsTools.getPrintJdsContent(
    // punish, resolver);
    // Intent intent = new Intent(self, JdsPreviewActivity.class);
    // intent.putExtra("jds", jds);
    // startActivity(intent);
    // } else if (v.getId() == R.id.Butt_send) {
    // if (TextUtils.equals(punish.getScbj(), "1")) {
    // GlobalMethod.showErrorDialog("文书已发送,无需重复发送", self);
    // return;
    // }
    // UploadViolationThread thread = new UploadViolationThread(
    // uploadVioHandler, punish);
    // thread.doStart();
    // }
    // }

    /**
     * 上传处罚决定书的控制操作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadVioHandler(VioUploadEvent event) {
        if (event.scbj == 1) {
            GlobalMethod.showToast("决定书已上传", self);
            ViolationDAO.setVioUploadStatus(event.id, true, GlobalMethod.getBoxStore(self));
            String wslb = GlobalMethod.getKeyFromSpinnerSelected(spinWslb,
                    GlobalConstant.KEY);
            puList = getViolationByConds(wslb);
            getList(puList);
            ((TwoLineSelectAdapter) getListView().getAdapter())
                    .notifyDataSetChanged();
        } else {
            GlobalMethod.showErrorDialog(event.message, self);
        }
    }

    private int getSelectItem() {
        int position = -1;
        int i = 0;
        while (strList.size() > 0 && i < strList.size()) {
            if (strList.get(i).isSelect()) {
                position = i;
                break;
            }
            i++;
        }
        return position;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (btp != null) {
            btp.closeConn();
        }
    }

    private void printJdsBySelect(VioViolation vio) {
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
        if (btp == null)
            btp = new BlueToothPrint(printerInfo.getValue());
        if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
            // 没有建立蓝牙串口流
            int errorStaus = btp.createSocket(btAdapter);
            if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
                GlobalMethod.showErrorDialog(
                        btp.getBluetoothCodeMs(errorStaus), self);
                return;
            }
        }
        int status = btp.printJdsByBluetooth(vio, this);
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    private void getList(List<VioViolation> puList) {
        strList.clear();
        if (puList == null)
            return;
        for (VioViolation v : puList) {
            TwoLineSelectBean ts = new TwoLineSelectBean();
            String text1 = "";
            String text2 = v.getWfsj() + v.getDsr();
            if (Integer.valueOf(v.getWslb()) == 1
                    && TextUtils.equals(v.getCfzl(), "1")) {
                text1 = "警告";
            } else if (Integer.valueOf(v.getWslb()) == 1) {
                text1 = "简易";
            } else if (Integer.valueOf(v.getWslb()) == 3) {
                text1 = "强制";
            } else if (Integer.valueOf(v.getWslb()) == 6) {
                text1 = "通知";
            }
            text1 += " " + v.getJdsbh()
                    + (Integer.valueOf(v.getScbj()) == 0 ? " 未上传" : " 已上传");
            ts.setText1(text1);
            ts.setText2(text2);
            ts.setSelect(false);
            strList.add(ts);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
