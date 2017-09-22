package com.jwt.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.dao.AcdSimpleDao;
import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.globalquery.ZhcxThread;
import com.jwt.jbyw.AcdWfxwBean;
import com.jwt.jbyw.VerifyData;
import com.jwt.pojo.AcdLawBean;
import com.jwt.pojo.AcdSimpleHumanBean;
import com.jwt.thread.CommQueryThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.IDCard;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.HttpURLConnection;

public class AcdRyclJbqkActivity extends AppCompatActivity {

    protected static final int REQCODE_JTFS = 0;
    protected static final int REQCODE_WFXW = 1;
    private final static String TAG = "AcdRyclJbqkActivity";
    protected static final int REQ_FIND_LAW_1 = 2;
    protected static final int REQ_FIND_LAW_2 = 3;
    protected static final int REQ_FIND_LAW_3 = 4;

    private Spinner spinAcdXb, spinAcdHpzl, spinAcdJtfs, spinAcdRylx;
    private Spinner spinAcdJzzl, spinAcdSgzr, spinAcdHpqz;
    private EditText editSfzh, editXm, editNl, editLxdh, editLxdz, editHphm;
    private EditText editBxgs, editBxh, editDabh;
    private EditText editWfxw, editClpp, editClxh, editTk1, editTk2, editTk3;

    private Button butFindWfxw;
    private Button btnFindTk1, btnFindTk2, btnFindTk3;
    private Button butQueryHuman, butQueryVehicle;
    private ToggleButton togButInOut;
    private AcdLawBean[] tks = new AcdLawBean[3];

    private int operMod;

    private Context self;

    // private ArrayList<KeyValueBean> wfxwList;

    private AcdSimpleHumanBean jbqk;

    private AcdWfxwBean acdWfxw = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acd_rycl_jbqk);
        EventBus.getDefault().register(this);
        self = this;
        editSfzh = (EditText) findViewById(R.id.edit_acd_ry_sfzh);
        editXm = (EditText) findViewById(R.id.edit_acd_ry_xm);
        editNl = (EditText) findViewById(R.id.edit_acd_ry_nl);
        editLxdh = (EditText) findViewById(R.id.edit_acd_ry_lxdh);
        editLxdz = (EditText) findViewById(R.id.edit_acd_ry_lxdz);
        editHphm = (EditText) findViewById(R.id.edit_acd_ry_hphm);
        editWfxw = (EditText) findViewById(R.id.edit_acd_ry_wfxw);
        editBxgs = (EditText) findViewById(R.id.edit_acd_ry_bxgs);
        editBxh = (EditText) findViewById(R.id.edit_acd_ry_bxh);
        editTk1 = (EditText) findViewById(R.id.edit_acd_tk1);
        editTk2 = (EditText) findViewById(R.id.edit_acd_tk2);
        editTk3 = (EditText) findViewById(R.id.edit_acd_tk3);
        editClpp = (EditText) findViewById(R.id.edit_acd_ry_clpp);
        editClxh = (EditText) findViewById(R.id.edit_acd_ry_clxh);
        editDabh = (EditText) findViewById(R.id.edit_acd_ry_dabh);


        spinAcdXb = (Spinner) findViewById(R.id.spin_acd_ry_xb);
        spinAcdHpzl = (Spinner) findViewById(R.id.spin_acd_ry_hpzl);
        spinAcdHpqz = (Spinner) findViewById(R.id.spin_acd_ry_hpqz);
        spinAcdJtfs = (Spinner) findViewById(R.id.spin_acd_ry_jtfs);

        spinAcdRylx = (Spinner) findViewById(R.id.spin_acd_ry_rylx);
        spinAcdJzzl = (Spinner) findViewById(R.id.spin_acd_ry_jzzl);
        spinAcdSgzr = (Spinner) findViewById(R.id.spin_acd_ry_sgzr);
        butFindWfxw = (Button) findViewById(R.id.but_acd_wfxw);
        butFindWfxw.setOnClickListener(findWfxw);
        butQueryHuman = (Button) findViewById(R.id.btn_acd_query_sfzh);
        butQueryVehicle = (Button) findViewById(R.id.btn_acd_query_vehicle);
        togButInOut = (ToggleButton) findViewById(R.id.tog_btn_acd_inout);
        butQueryHuman.setOnClickListener(queryListener);
        butQueryVehicle.setOnClickListener(queryListener);

        btnFindTk1 = (Button) findViewById(R.id.btn_acd_tk1);
        btnFindTk2 = (Button) findViewById(R.id.btn_acd_tk2);
        btnFindTk3 = (Button) findViewById(R.id.btn_acd_tk3);
        btnFindTk1.setOnClickListener(findTkListener);

        GlobalMethod.changeAdapter(spinAcdXb, GlobalData.xbList, this);
        GlobalMethod.changeAdapter(spinAcdHpzl, GlobalData.hpzlList, this, true);
        GlobalMethod.changeAdapter(spinAcdHpqz, GlobalData.hpqlList, this, true);
        GlobalMethod.changeAdapter(spinAcdJtfs, GlobalData.acdJtfsList, this);
        GlobalMethod.changeAdapter(spinAcdRylx, GlobalData.acdRylxList, this, true);
        GlobalMethod.changeAdapter(spinAcdJzzl, GlobalData.acdJszzlList, this, true);
        GlobalMethod.changeAdapter(spinAcdSgzr, GlobalData.acdSgzrList, this);

        jbqk = (AcdSimpleHumanBean) getIntent().getSerializableExtra("jbqk");
        operMod = getIntent().getIntExtra(AcdSimpleDao.OPER_MOD,
                AcdSimpleDao.ACD_MOD_NEW);
        if (jbqk != null) {
            Log.e(TAG, "jbqk is not null");
            changeRyjbqkByModify(jbqk);

        } else {
            GlobalMethod.changeSpinnerSelect(spinAcdJtfs, "K3", GlobalConstant.KEY, true);
            changeHpzlHphmState();
            jbqk = new AcdSimpleHumanBean();
            // 如果是新增的人员，编号为0，在基本情况中定义
            // 如果是修改人员，则人员编号至少为1，这里不做修改
            int rybh = getIntent().getIntExtra("rybh", 0);
            jbqk.setRybh(rybh);
        }
        spinAcdJtfs.setOnItemSelectedListener(jtfsChange);
        spinAcdJzzl.setOnItemSelectedListener(jzzlChange);
        setTitle("人车情况");
    }

    int jtfsSelect = 0;

    /**
     * 根据事故基本信息界面中传入的人员信息填充界面
     *
     * @param jbqk
     */
    private void changeRyjbqkByModify(AcdSimpleHumanBean jbqk) {
        Log.e(TAG, "changeRyjbqkByModify(AcdSimpleHumanBean: ");
        spinAcdJtfs.setOnItemSelectedListener(null);
        editSfzh.setText(jbqk.getSfzmhm());
        editXm.setText(jbqk.getXm());
        GlobalMethod.changeSpinnerSelect(spinAcdXb,
                jbqk.getXb(), GlobalConstant.KEY, true);
        editNl.setText(jbqk.getNl());
        editLxdh.setText(jbqk.getDh());
        editLxdz.setText(jbqk.getZz());
        GlobalMethod.changeSpinnerSelect(spinAcdRylx, jbqk.getRylx(), GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spinAcdJzzl, jbqk.getJszzl(), GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spinAcdJtfs, jbqk.getJtfs(), GlobalConstant.KEY, true);
        editClpp.setText(jbqk.getClpp());
        editClxh.setText(jbqk.getClxh());
        editBxgs.setText(jbqk.getBxgs());
        editBxh.setText(jbqk.getBxpzh());
        GlobalMethod.changeSpinnerSelect(spinAcdSgzr, jbqk.getSgzr(), GlobalConstant.KEY, true);
        if (!TextUtils.isEmpty(jbqk.getWfxw1())) {
            acdWfxw = AcdSimpleDao.getAcdWfxwByWfdm(jbqk.getWfxw1(), GlobalMethod.getBoxStore(self));
            if (acdWfxw != null) {
                editWfxw.setText(acdWfxw.getWfnr());
            }
        }
        if (!TextUtils.isEmpty(jbqk.getTk1())) {
            tks[0] = AcdSimpleDao.queryWftknrByXh(jbqk
                    .getTk1(), GlobalMethod.getBoxStore(self));
            editTk1.setText(tks[0] == null ? "" : tks[0].getTknr());
        }
        if (!TextUtils.isEmpty(jbqk.getTk2())) {
            tks[1] = AcdSimpleDao.queryWftknrByXh(jbqk
                    .getTk2(), GlobalMethod.getBoxStore(self));
            editTk2.setText(tks[1] == null ? "" : tks[1].getTknr());
        }
        if (!TextUtils.isEmpty(jbqk.getTk3())) {
            tks[2] = AcdSimpleDao.queryWftknrByXh(jbqk
                    .getTk3(), GlobalMethod.getBoxStore(self));
            editTk3.setText(tks[2] == null ? "" : tks[2].getTknr());
        }
        // 交通方式改变一下号牌种类和号牌号码的状态
        GlobalMethod.changeSpinnerSelect(spinAcdHpzl, jbqk.getHpzl(), GlobalConstant.KEY, true);
        String hphm = jbqk.getHphm();
        if (!TextUtils.isEmpty(hphm)) {
            String hpqz = hphm.substring(0, 1);
            String endHphm = hphm.substring(1);
            GlobalMethod.changeSpinnerSelect(spinAcdHpqz, hpqz, GlobalConstant.VALUE, true);
            int pos = GlobalMethod
                    .getPositionByValue(GlobalData.hpqlList, hpqz);
            editHphm.setText(pos > -1 ? endHphm : hphm);
            Log.e(TAG, "hphm: " + editHphm.getText());
        }
    }

    OnSpinnerItemSelected jtfsChange = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> av, View view, int position,
                                   long arg3) {
            Log.e("人员基本情况界面", "jtfsChange: " + position);
            jtfsSelect++;
            if (jtfsSelect < 3 && GlobalSystemParam.isSkipSpinner)
                return;
            changeHpzlHphmState();
        }
    };

    OnSpinnerItemSelected jzzlChange = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            clearAcdRyView(false);
        }
    };

    OnClickListener findWfxw = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(self, AcdFindWfxwActivity.class);
            Bundle b = new Bundle();
            b.putString("jtfs", GlobalMethod.getKeyFromSpinnerSelected(
                    spinAcdJtfs, GlobalData.acdJtfsList, GlobalConstant.KEY));
            //b.putSerializable("acdWfxw", acdWfxw);
            intent.putExtras(b);
            startActivityForResult(intent, REQCODE_WFXW);
        }
    };

    // private ArrayList<KeyValueBean> createWfxwList() {
    // int wfzl = 1;
    // // 改变违法行为选择项
    // int wfxwfl = spinAcdWfxwfl.getSelectedItemPosition();
    // if (wfxwfl > 0) {
    // // 非违法行为
    // wfzl = 6;
    // } else {
    // KeyValueBean skv = (KeyValueBean) spinAcdJtfs.getSelectedItem();
    // String key = skv.getKey().substring(0, 1);
    // wfzl = getWfclflByJtfs(key);
    // }
    // ArrayList<KeyValueBean> list = ViolationDAO.getAllFrmCode(
    // GlobalConstant.ACD_WFXW, self.getContentResolver(),
    // new String[] { Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM1 },
    // Fixcode.FrmCode.DMSM3 + "='" + wfzl + "'", Fixcode.FrmCode.DMZ);
    // list.add(0, new KeyValueBean("0", ""));
    // return list;
    // }

    private void clearAcdRyView(boolean isQuery) {
        editLxdz.setText("");
        editLxdh.setText("");
        editXm.setText("");
        if (!isQuery) {
            editSfzh.setText("");
            editDabh.setText("");
        }
        editNl.setText("");
        GlobalMethod.changeSpinnerSelect(spinAcdXb, "", GlobalConstant.KEY, true);
    }

    private void changeHpzlHphmState() {
        String jtfs = GlobalMethod.getKeyFromSpinnerSelected(spinAcdJtfs, GlobalConstant.KEY);
        Log.e("人员基本情况界面", "changeHpzlHphmState: " + jtfs);
        int wfclfl = getWfclflByJtfs(jtfs.substring(0, 1));
        editHphm.setEnabled(wfclfl < 3);
        if (wfclfl == 1) {
            // 是机动车违章
            spinAcdHpzl.setSelection(1);
            GlobalMethod.changeSpinnerSelect(spinAcdHpqz, "苏", GlobalConstant.VALUE, true);
            butQueryVehicle.setEnabled(true);
            editHphm.setText("F");
        } else {
            spinAcdHpzl.setSelection(0);
            spinAcdHpqz.setSelection(0);
            butQueryVehicle.setEnabled(false);
            editHphm.setText("");
        }
    }

    /**
     * 根据交通方式决定其违法的车辆分类
     *
     * @param jtfs
     * @return 1 机动车 2 非机动车 3 步行或乘车 5 其他
     */
    private int getWfclflByJtfs(String jtfs) {
        int wfzl = 1;
        if (TextUtils.equals(jtfs, "A") || TextUtils.equals(jtfs, "C")) {
            wfzl = 3;
        } else if (TextUtils.equals(jtfs, "F")) {
            wfzl = 2;
        } else if (TextUtils.equals(jtfs, "X")) {
            wfzl = 5;
        }
        return wfzl;
    }

    /**
     * 保存人员车辆基本情况
     */
    private void saveRycl() {
        jbqk.setSfzmhm(editSfzh.getText().toString());
        jbqk.setXm(editXm.getText().toString());
        jbqk.setXb(GlobalMethod.getKeyFromSpinnerSelected(spinAcdXb, GlobalConstant.KEY));
        jbqk.setNl(editNl.getText().toString());
        jbqk.setDh(editLxdh.getText().toString());
        jbqk.setZz(editLxdz.getText().toString());
        jbqk.setRylx(GlobalMethod.getKeyFromSpinnerSelected(spinAcdRylx, GlobalConstant.KEY));
        jbqk.setJszzl(GlobalMethod.getKeyFromSpinnerSelected(spinAcdJzzl, GlobalConstant.KEY));
        jbqk.setJtfs(GlobalMethod.getKeyFromSpinnerSelected(spinAcdJtfs, GlobalConstant.KEY));
        jbqk.setHpzl(GlobalMethod.getKeyFromSpinnerSelected(spinAcdHpzl, GlobalConstant.KEY));
        String hpqz = GlobalMethod.getKeyFromSpinnerSelected(spinAcdHpqz, GlobalConstant.VALUE);
        jbqk.setHphm(TextUtils.isEmpty(editHphm.getText()) ? "" : hpqz
                + editHphm.getText().toString());
        jbqk.setClxh(editClxh.getText().toString());
        jbqk.setClpp(editClpp.getText().toString());
        jbqk.setBxgs(editBxgs.getText().toString());
        jbqk.setBxpzh(editBxh.getText().toString());
        jbqk.setSgzr(GlobalMethod.getKeyFromSpinnerSelected(spinAcdSgzr, GlobalConstant.KEY));
        // jbqk.setWfxw(wfxwList.get(wfIndex).getKey());

        jbqk.setWfxw1(acdWfxw == null ? "" : acdWfxw.getWfxwdm());
        jbqk.setTk1(tks[0] == null ? "" : tks[0].getXh());
        jbqk.setTk2(tks[1] == null ? "" : tks[1].getXh());
        jbqk.setTk3(tks[2] == null ? "" : tks[2].getXh());
        jbqk.setCjcxbj("11");
        String err = VerifyData.verifyAcdHuman(jbqk);
        if (TextUtils.isEmpty(err)) {
            Log.e(TAG, "verify is passed!");
            Intent i = new Intent();
            Bundle b = new Bundle();
            b.putSerializable("jbqk", jbqk);
            i.putExtras(b);
            setResult(RESULT_OK, i);
            finish();
        } else {
            GlobalMethod.showErrorDialog(err, self);
        }
    }

    private OnClickListener queryListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == butQueryHuman) {
                String jzzl = GlobalMethod.getKeyFromSpinnerSelected(spinAcdJzzl, GlobalConstant.KEY);
                boolean isDrv = TextUtils.equals(jzzl, "1");
                String sfzh = editSfzh.getText().toString().toUpperCase();
                String dabh = editDabh.getText().toString();
                clearAcdRyView(true);
                if (isDrv) {
                    if (TextUtils.isEmpty(sfzh) && TextUtils.isEmpty(dabh)) {
                        GlobalMethod.showErrorDialog("身份证号码和档案编号不能全为空！", self);
                        return;
                    }
                    if (!TextUtils.isEmpty(sfzh) && (sfzh.length() != 18
                            || !IDCard.Verify(sfzh.toString()))) {
                        GlobalMethod.showErrorDialog("驾驶员身份证号码有错误！", self);
                        return;
                    }
                    if (!TextUtils.isEmpty(dabh) && (dabh.length() != 12 || !TextUtils.isDigitsOnly(dabh))) {
                        GlobalMethod.showErrorDialog("档案编号不是12位或不全部是数字！", self);
                        return;
                    }
                    new CommQueryThread(CommQueryThread.JSON_QUERY_DRV, new String[]{dabh, sfzh}, self).doStart();
                } else {
                    if (TextUtils.isEmpty(sfzh) || sfzh.length() != 18
                            || !IDCard.Verify(sfzh.toString())) {
                        GlobalMethod.showErrorDialog("人员身份证号码有错误！", self);
                        return;
                    }
                    editNl.setText(String.valueOf(GlobalMethod
                            .countAgeFromSfzh(sfzh.toString())));
                    GlobalMethod.changeSpinnerSelect(spinAcdXb, GlobalMethod.getXbFromSfzh(sfzh
                            .toString()), GlobalConstant.KEY, true);
                    ZhcxThread thread = new ZhcxThread(queryHumanhandler);
                    if (togButInOut.isChecked()) {
                        thread.doStart(self, "Q003", "SFZH='"
                                + sfzh + "'");
                    } else {
                        thread.doStart(self, "C005", "GMSFHM='"
                                + sfzh + "'");
                    }
                }
                editSfzh.setText(sfzh);
            } else if (v == butQueryVehicle) {
                String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spinAcdHpzl, GlobalConstant.KEY);
                String hphm = GlobalMethod.getKeyFromSpinnerSelected(spinAcdHpqz, GlobalConstant.VALUE);
                hphm += editHphm.getText();
                if (TextUtils.isEmpty(hpzl) || TextUtils.isEmpty(hphm)
                        || hphm.length() < 7) {
                    GlobalMethod.showErrorDialog("号牌种类或号牌号码不正确", self);
                    return;
                }
                hphm = hphm.toUpperCase();
                new CommQueryThread(CommQueryThread.JSON_QUERY_VEH, new String[]{hpzl, hphm}, self).doStart();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            if (requestCode == REQCODE_WFXW) {
                Serializable af = b.getSerializable("acdWfxw");
                if (af != null) {
                    acdWfxw = (AcdWfxwBean) af;
                    editWfxw.setText(acdWfxw.getWfnr());
                }
            } else if (requestCode == REQ_FIND_LAW_1) {
                Serializable af = b.getSerializable("wfxw");
                if (af != null) {
                    tks[0] = (AcdLawBean) af;
                    editTk1.setText(tks[0].getTknr());
                }
            } else if (requestCode == REQ_FIND_LAW_2) {
                Serializable af = b.getSerializable("wfxw");
                if (af != null) {
                    tks[1] = (AcdLawBean) af;
                    editTk2.setText(tks[1].getTknr());
                }
            } else if (requestCode == REQ_FIND_LAW_3) {
                Serializable af = b.getSerializable("wfxw");
                if (af != null) {
                    tks[2] = (AcdLawBean) af;
                    editTk3.setText(tks[2].getTknr());
                }
            }
        }
    }

    /**
     * 异步消息队列，用的是全国人口
     */
    Handler queryHumanhandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message m) {
            Bundle b = m.getData();
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
                        if (togButInOut.isChecked()) {
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "ZZXZ");
                            editLxdz.setText(pos > -1 ? content[pos] : "");
                        } else {
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "HJXZ");
                            editLxdz.setText(pos > -1 ? content[pos] : "");
                            pos = GlobalMethod.getPositionFromArray(names,
                                    "LXDH");
                            editLxdh.setText(pos > -1 ? content[pos] : "");
                        }
                        pos = GlobalMethod.getPositionFromArray(names, "XM");
                        editXm.setText(pos > -1 ? content[pos] : "");

                    } else if (webResult.getStatus() == 204) {
                        GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定",
                                self);
                    } else if (webResult.getStatus() == 500) {
                        GlobalMethod.showDialog("提示信息", "该查询在服务器不能实现，请与管理员联系！",
                                "确定", self);
                    } else {
                        GlobalMethod.showDialog("提示信息", "网络连接失败，请检查配查或与管理员联系！",
                                "确定", self);
                    }
                }
            }
        }
    };

    private OnClickListener findTkListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(self, AcdFindLawActivity.class);
            if (v == btnFindTk1)
                startActivityForResult(intent, REQ_FIND_LAW_1);
            else if (v == btnFindTk2)
                startActivityForResult(intent, REQ_FIND_LAW_2);
            else if (v == btnFindTk3)
                startActivityForResult(intent, REQ_FIND_LAW_3);
        }
    };



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handVehDriQuery(JSONObject json){
        Log.e("ryqk",json.toString());
        if (json == null) {
            GlobalMethod.showErrorDialog("服务器出错", self);
            return;
        }
        if (!TextUtils.isEmpty(json.optString("err", ""))) {
            GlobalMethod.showErrorDialog(json.optString("err"), self);
            return;
        }
        String bdjg = json.optString("bdjg", "");
        if (!TextUtils.isEmpty(bdjg)) {
            GlobalMethod.showDialog("比对信息", bdjg, "知道了", self);
        }
        int catalog = json.optInt("catalog");
        boolean isDrv = catalog == CommQueryThread.JSON_QUERY_DRV;
        // 根据查询的内容对界面赋值
        if (catalog == CommQueryThread.JSON_QUERY_DRV) {
            editLxdz.setText(json.optString("djzsxxdz", ""));
            editLxdh.setText(json.optString("sjhm", ""));
            editXm.setText(json.optString("xm", ""));
            String sfzh = json.optString("sfzmhm", "");
            // 符合身份证号规则
            if (!TextUtils.isEmpty(sfzh)) {
                editSfzh.setText(sfzh);
                editNl.setText(String.valueOf(GlobalMethod
                        .countAgeFromSfzh(sfzh.toString())));
                GlobalMethod.changeSpinnerSelect(spinAcdXb, GlobalMethod.getXbFromSfzh(sfzh
                        .toString()), GlobalConstant.KEY, true);
            }
        } else if(catalog == CommQueryThread.JSON_QUERY_VEH) {
            editClpp.setText(json.optString("clpp1"));
            editClxh.setText(json.optString("clxh"));
            editHphm.setText(editHphm.getText().toString().toUpperCase());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (operMod == AcdSimpleDao.ACD_MOD_SHOW) {
            menu.removeItem(R.id.save_file);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acd_rycl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_file:
                saveRycl();
                return true;
            case R.id.menu_quite:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
