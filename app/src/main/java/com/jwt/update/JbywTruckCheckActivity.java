package com.jwt.update;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TruckVehicleBean;
import com.jwt.dao.MessageDao;
import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.thread.CommQueryThread;
import com.jwt.thread.CommUploadThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.IDCard;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

public class JbywTruckCheckActivity extends AppCompatActivity implements OnClickListener {

    private static final int REQCODE_JTFS = 0;
    private static final int REQCODE_QYMC = 5;
    public static final int HANDLER_VEH_INFO = 10;
    public static final int HANDLER_UP_TRUCK_VEH = 11;
    public static final int HANDLER_QGRYXX = 12;
    private Spinner spHpzl, spinCllx;
    private EditText edHphm, editSjsyr, editSjsfzh, editSjsjhm;
    private EditText editSjfwdw, editSyr, editSjlxdz;
    private KeyValueBean kvFwdw;
    private Context self;
    private TruckVehicleBean oldTruck;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jbyw_truck_check);
        self = this;
        spHpzl = (Spinner) findViewById(R.id.spin_hpzl);
        edHphm = (EditText) findViewById(R.id.edit_hphm);
        editSjsyr = (EditText) findViewById(R.id.edit_sjsyr);
        editSjsfzh = (EditText) findViewById(R.id.edit_sjsfzh);
        editSjlxdz = (EditText) findViewById(R.id.edit_sjlxdz);
        editSjsjhm = (EditText) findViewById(R.id.edit_sjsjhm);
        editSjfwdw = (EditText) findViewById(R.id.edit_sjfwdw);
        editSyr = (EditText) findViewById(R.id.edit_syr);
        spinCllx = (Spinner) findViewById(R.id.spin_cllx);
        String hphm = getIntent().getStringExtra("hphm");
        hphm = TextUtils.isEmpty(hphm) ? "" : hphm;
        oldTruck = (TruckVehicleBean) getIntent().getSerializableExtra(
                JbywTruckVehicleActivity.INTENT_VEH);
        isUpdate = oldTruck != null && !TextUtils.isEmpty(oldTruck.getId());
        String hpzl = getIntent().getStringExtra("hpzl");
        String v = GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList,
                hpzl);
        List<KeyValueBean> kvs = new ArrayList<KeyValueBean>();
        kvs.add(new KeyValueBean(hpzl, v));
        GlobalMethod.changeAdapter(spHpzl, kvs, this);
        GlobalMethod.changeAdapter(spinCllx, GlobalData.jtfsList, this, true);
        edHphm.setText(hphm);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_query_jtfs).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.btn_query_veh).setOnClickListener(this);
        findViewById(R.id.btn_query_fwdw).setOnClickListener(this);
        findViewById(R.id.btn_query_ryxx).setOnClickListener(this);
        GlobalMethod.setEnable(false, edHphm);
        if (isUpdate)
            chargeView(oldTruck);
    }

    private void chargeView(TruckVehicleBean truck) {
        editSyr.setText(truck.getSyr());
        GlobalMethod.changeSpinnerSelect(spinCllx, truck.getCllx(),
                GlobalConstant.KEY);
        editSjsyr.setText(truck.getSjsyr());
        editSjlxdz.setText(truck.getSjlxdz());
        editSjsfzh.setText(truck.getSjsfzh());
        editSjsjhm.setText(truck.getSjsjhm());
        kvFwdw = MessageDao.queryQymcByBy(truck.getFwdwdm(),GlobalMethod.getBoxStore(self));
        editSjfwdw.setText(kvFwdw.getValue() == null ? "" : kvFwdw.getValue());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                // 上传
                TruckVehicleBean tc = getTcFromView();
                String err = checkTruckCheck(tc);
                if (!TextUtils.isEmpty(err)) {
                    GlobalMethod.showErrorDialog(err, self);
                    return;
                }
                if (isUpdate && oldTruck != null)
                    tc.setId(oldTruck.getId());
                CommUploadThread th = new CommUploadThread(
                        CommUploadThread.UPLOAD_TRUCK_VEH, new Object[]{tc},
                        this);
                th.doStart();
                break;
            case R.id.btn_query_jtfs:
                Intent intent = new Intent(self, ConfigJtfsActivity.class);
                startActivityForResult(intent, REQCODE_JTFS);
                break;
            case R.id.btn_right:
                // 退出
                finish();
                break;
            case R.id.btn_query_veh:
                // 查询车辆情况，显示一个对话框，并填充实际车主和交通方式
                String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                        GlobalConstant.KEY);
                if (!TextUtils.isEmpty(edHphm.getText())
                        && TextUtils.getTrimmedLength(edHphm.getText()) > 4) {
                    String hphm = edHphm.getText().toString();
                    String where = "HPZL='" + hpzl + "' AND HPHM='" + hphm + "'";
                    // if (hphm.startsWith("苏F")) {
                    String[] params = new String[]{"R004", where};
                    TurckHandler handler = new TurckHandler(
                            JbywTruckCheckActivity.this, HANDLER_VEH_INFO);
                    CommQueryThread thread = new CommQueryThread(
                            CommQueryThread.QUERY_ZHCX, params, self);
                    thread.doStart();
                } else {
                    GlobalMethod.showErrorDialog("机动车号码不正确", self);
                }
                break;
            case R.id.btn_query_fwdw:
                Intent in = new Intent(this, JbywTruckQymcActivity.class);
                startActivityForResult(in, REQCODE_QYMC);
                break;
            case R.id.btn_query_ryxx:
                String sfzh = editSjsfzh.getText().toString();
                if (!IDCard.Verify(sfzh)) {
                    GlobalMethod.showErrorDialog("身份证号不正确", self);
                    return;
                }
                String[] param = new String[]{"Q003",
                        "SFZH='" + sfzh.toUpperCase() + "'"};
                CommQueryThread thread = new CommQueryThread(CommQueryThread.QUERY_ZHCX, param, self);
                thread.doStart();
                break;
            default:
                break;
        }

    }

    private TruckVehicleBean getTcFromView() {
        // TruckCheckBean tc = new TruckCheckBean();
        TruckVehicleBean truck = new TruckVehicleBean();
        truck.setHpzl(GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                GlobalConstant.KEY));
        truck.setHphm(edHphm.getText().toString());
        if (!TextUtils.isEmpty(editSjfwdw.getText())) {
            truck.setSjfwdw(editSjfwdw.getText().toString());
            if (kvFwdw != null)
                truck.setFwdwdm(kvFwdw.getKey());
        }
        truck.setSjlxdz(editSjlxdz.getText().toString());
        truck.setSjsfzh(editSjsfzh.getText().toString().toUpperCase());
        truck.setSjsjhm(editSjsjhm.getText().toString());
        truck.setSjsyr(editSjsyr.getText().toString());
        truck.setDjdw(GlobalData.grxx.get(GlobalConstant.YBMBH));
        truck.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
        truck.setCllx(GlobalMethod.getKeyFromSpinnerSelected(spinCllx,
                GlobalConstant.KEY));
        truck.setSyr(editSyr.getText().toString());
        return truck;
    }

    private String checkTruckCheck(TruckVehicleBean tc) {
        if (TextUtils.isEmpty(tc.getSyr()))
            return "登记车主不能为空";
        if (TextUtils.isEmpty(tc.getCllx()))
            return "交通方式不能为空";
        if (TextUtils.isEmpty(tc.getSjsyr()))
            return "实际所有人不能为空";
        if (TextUtils.isEmpty(tc.getSjsfzh()))
            return "实际所有人证件号不能为空";
        if (TextUtils.isEmpty(tc.getSjlxdz()))
            return "实际所有人联系地址不能为空";
        if (TextUtils.isEmpty(tc.getSjsjhm()))
            return "实际所有人手机号码不能为空";
        // if (TextUtils.isEmpty(tc.getSjfwdw()))
        // return "实际服务单位不能为空";
        if (!IDCard.Verify(tc.getSjsfzh())) {
            return "必须为自然人或法人十八位身份证号";
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_JTFS) {
                Bundle b = data.getExtras();
                String j = b.getString("jtfsDm");
                GlobalMethod.changeSpinnerSelect(spinCllx, j,
                        GlobalConstant.KEY);
            } else if (requestCode == REQCODE_QYMC) {
                Bundle b = data.getExtras();
                KeyValueBean qymc = (KeyValueBean) b
                        .getSerializable("");
                if (qymc != null) {
                    kvFwdw = qymc;
                    editSjfwdw.setText(kvFwdw.getValue());
                }

            }
        }
    }

    static class TurckHandler extends Handler {

        private final WeakReference<JbywTruckCheckActivity> myActivity;
        private int cata;

        public TurckHandler(JbywTruckCheckActivity activity, int _cata) {
            myActivity = new WeakReference<JbywTruckCheckActivity>(activity);
            this.cata = _cata;
        }

        @Override
        public void handleMessage(Message msg) {
            JbywTruckCheckActivity ac = myActivity.get();
            if (ac != null) {
                if (cata == HANDLER_VEH_INFO)
                    ac.operQueryVehHandler(msg);
                else if (cata == HANDLER_UP_TRUCK_VEH)
                    ac.operUploadTruck(msg);
                else if (cata == HANDLER_QGRYXX)
                    ac.operQueryQgryxx(msg);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void operQueryQgryxx(Message msg) {
        Bundle b = msg.getData();
        WebQueryResult<GlobalQueryResult> webResult = (WebQueryResult<GlobalQueryResult>) b
                .getSerializable("queryResult");
        if (webResult.getStatus() == HttpURLConnection.HTTP_OK) {
            if (webResult.getResult() == null
                    || webResult.getResult().getContents() == null
                    || webResult.getResult().getContents().length == 0)
                GlobalMethod.showDialog("提示信息", "没有相应的查询结果！", "确定", self);
            else {
                GlobalQueryResult zhcx = webResult.getResult();
                if (zhcx != null) {
                    String bdxx = zhcx.getBdxx();
                    if (!TextUtils.isEmpty(bdxx)) {
                        GlobalMethod.showDialog("系统比对信息", bdxx, "确定", self);
                    }
                    String[] names = zhcx.getNames();
                    String[] content = zhcx.getContents()[0];
                    // 根据查询的内容对界面赋值
                    int pos = -1;
                    pos = GlobalMethod.getPositionFromArray(names, "SFZH");
                    if (pos > -1 && !TextUtils.isEmpty(content[pos])) {
                        editSjsfzh.setText(content[pos]);
                        pos = GlobalMethod.getPositionFromArray(names, "XM");
                        if (pos > -1 && !TextUtils.isEmpty(content[pos]))
                            editSjsyr.setText(content[pos]);
                        pos = GlobalMethod.getPositionFromArray(names, "ZZXZ");
                        if (pos > -1 && !TextUtils.isEmpty(content[pos]))
                            editSjlxdz.setText(content[pos]);
                    } else {
                        GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定",
                                self);
                    }
                }
            }
        } else if (webResult.getStatus() == 204) {
            GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定", self);
        } else if (webResult.getStatus() == 500) {
            GlobalMethod.showDialog("提示信息", "该查询在服务器不能实现，请与管理员联系！", "确定", self);
        } else {
            GlobalMethod.showDialog("提示信息", "网络连接失败，请检查配查或与管理员联系！", "确定", self);
        }
    }

    @SuppressWarnings("unchecked")
    public void operQueryVehHandler(Message msg) {
        Bundle b = msg.getData();
        WebQueryResult<GlobalQueryResult> webResult = (WebQueryResult<GlobalQueryResult>) b
                .getSerializable("queryResult");
        if (webResult.getStatus() == HttpURLConnection.HTTP_OK) {
            if (webResult.getResult() == null
                    || webResult.getResult().getContents() == null
                    || webResult.getResult().getContents().length == 0)
                GlobalMethod.showDialog("提示信息", "没有相应的查询结果！", "确定", self);
            else {
                GlobalQueryResult zhcx = webResult.getResult();
                String info = "";
                if (zhcx != null) {
                    String[] names = zhcx.getNames();
                    String[] content = zhcx.getContents()[0];
                    // 根据查询的内容对界面赋值
                    int pos = GlobalMethod.getPositionFromArray(names, "CLPP1");
                    info += "车辆品牌：" + (pos > -1 ? content[pos] : "");
                    pos = GlobalMethod.getPositionFromArray(names, "CLXH");
                    info += "\n车辆型号：" + (pos > -1 ? content[pos] : "");

                    pos = GlobalMethod.getPositionFromArray(names, "SYR");
                    String syr = (pos > -1 ? content[pos] : "");
                    info += "\n所有人：" + syr;
                    pos = GlobalMethod.getPositionFromArray(names, "SFZMHM");
                    info += "\n所有人证件号：" + (pos > -1 ? content[pos] : "");
                    pos = GlobalMethod.getPositionFromArray(names, "CLSBDH");
                    info += "\n车辆识别码：" + (pos > -1 ? content[pos] : "");
                    pos = GlobalMethod.getPositionFromArray(names, "FDJH");
                    info += "\n发动机号：" + (pos > -1 ? content[pos] : "");
                    editSyr.setText(syr);
                    pos = GlobalMethod.getPositionFromArray(names, "CLLX");
                    String cllx = pos > -1 ? content[pos] : "";
                    GlobalMethod.changeSpinnerSelect(spinCllx, cllx,
                            GlobalConstant.KEY);
                }
                GlobalMethod.showDialog("查询结果", info, "确定", self);
            }
        } else if (webResult.getStatus() == 204) {
            GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定", self);
        } else if (webResult.getStatus() == 500) {
            GlobalMethod.showDialog("提示信息", "该查询在服务器不能实现，请与管理员联系！", "确定", self);
        } else {
            GlobalMethod.showDialog("提示信息", "网络连接失败，请检查配查或与管理员联系！", "确定", self);
        }
    }

    @SuppressWarnings("unchecked")
    public void operUploadTruck(Message msg) {
        Bundle data = msg.getData();
        WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) data
                .getSerializable(CommUploadThread.RESULT_UP_TRUCK_VEH);
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            ZapcReturn zr = re.getResult();
            if (TextUtils.equals(zr.getCgbj(), "1")) {
                GlobalMethod.showDialogWithListener("系统提示", "上传登记信息成功", "确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent i = new Intent();
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        }, self);
                return;
            }
            GlobalMethod.showDialog("系统提示", zr.getScms(), "确定", self);
            return;
        }
        GlobalMethod.showErrorDialog(err, self);

    }

}
