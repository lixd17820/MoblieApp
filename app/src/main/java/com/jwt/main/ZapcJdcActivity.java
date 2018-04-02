package com.jwt.main;

import java.net.HttpURLConnection;
import java.util.Date;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jwt.dao.ZaPcdjDao;
import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.globalquery.ZhcxThread;
import com.jwt.pojo.ZapcWppcxxBean;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.WebQueryResult;

public class ZapcJdcActivity extends AppCompatActivity {
    private Spinner spHpzl, spHpqz, spPcyy, spCljg;
    private EditText edHphm, edClpp, edFdjh, edSbdm, edClxh, edSyr, edSfzmhm;
    private Context self;
    private ZapcWppcxxBean wpxx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zapc_pcjdcxx);
        long pcrybh = getIntent().getLongExtra("pcrybh", 0);
        long gzbh = getIntent().getLongExtra("gzbh", 0);
        String pcdd = getIntent().getStringExtra("pcdd");
        initView();
        wpxx = (ZapcWppcxxBean) getIntent().getSerializableExtra("pcwpxx");
        if (wpxx == null) {
            wpxx = new ZapcWppcxxBean();
            wpxx.setBpcwpgzqkbh(String.valueOf(gzbh));
            wpxx.setBpcwprybh(String.valueOf(pcrybh));
            wpxx.setBpcwppcdd(TextUtils.isEmpty(pcdd) ? "" : pcdd);
        } else {
            setViewValueFromWpxx();
            LinearLayout line = (LinearLayout) findViewById(R.id.bottom_but);
            line.setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.But_save_wpxx).setOnClickListener(saveWpxxListiner);
        findViewById(R.id.but_wp_cxjdc).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        String hpzl = GlobalMethod.getKeyFromSpinnerSelected(
                                spHpzl, GlobalConstant.KEY);
                        String hpqz = GlobalMethod.getKeyFromSpinnerSelected(
                                spHpqz, GlobalConstant.VALUE);
                        if (!TextUtils.isEmpty(edHphm.getText())
                                && TextUtils.getTrimmedLength(edHphm.getText()) > 4) {
                            String hphm = hpqz + edHphm.getText();
                            ZhcxThread thread = new ZhcxThread(jdcHandler);
                            String where = "HPZL='" + hpzl + "' AND HPHM='"
                                    + hphm + "'";
                            // if (hphm.startsWith("苏F")) {
                            thread.doStart(self, "R004", where);
                            // } else {
                            // 全国机动车
                            // thread.doStart(self, "Q001", where);
                            // }
                            Log.e("where", where);
                        } else {
                            GlobalMethod.showErrorDialog("机动车号码不正确", self);
                        }
                    }
                });
        findViewById(R.id.But_end_pc).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    private void initView() {
        setTitle("机动车盘查");
        spHpzl = (Spinner) findViewById(R.id.spin_wp_hpzl);
        spHpqz = (Spinner) findViewById(R.id.spin_wp_hpqz);
        spPcyy = (Spinner) findViewById(R.id.spin_wp_pcyy);
        spCljg = (Spinner) findViewById(R.id.spin_wp_cljg);
        edHphm = (EditText) findViewById(R.id.edit_wp_hphm);
        edClpp = (EditText) findViewById(R.id.edit_wp_clpp);
        edFdjh = (EditText) findViewById(R.id.edit_wp_fdjh);
        edSbdm = (EditText) findViewById(R.id.edit_wp_sbdm);
        edClxh = (EditText) findViewById(R.id.edit_wp_clxh);
        edSyr = (EditText) findViewById(R.id.edit_wp_syr);
        edSfzmhm = (EditText) findViewById(R.id.edit_wp_sfzmhm);
        // 设置号牌种类
        GlobalMethod.changeAdapter(spHpzl, GlobalData.hpzlList, this);
        // 设置号牌前辍
        GlobalMethod.changeAdapter(spHpqz, GlobalData.hpqlList, this);
        spHpzl.setSelection(
                GlobalMethod.getPositionByKey(GlobalData.hpzlList, "02"), true);
        spHpqz.setSelection(
                GlobalMethod.getPositionByKey(GlobalData.hpqlList, "320000"),
                true);
        GlobalMethod.changeAdapter(spCljg,
                ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCCLJG), this);
        GlobalMethod.changeAdapter(spPcyy,
                ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCYY), this);

    }

    private Handler jdcHandler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
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
                        String[] names = zhcx.getNames();
                        String[] content = zhcx.getContents()[0];
                        // 根据查询的内容对界面赋值
                        int pos = GlobalMethod.getPositionFromArray(names,
                                "CLPP1");
                        edClpp.setText(pos > -1 ? content[pos] : "");
                        pos = GlobalMethod.getPositionFromArray(names, "CLXH");
                        edClxh.setText(pos > -1 ? content[pos] : "");

                        pos = GlobalMethod.getPositionFromArray(names, "SYR");
                        edSyr.setText(pos > -1 ? content[pos] : "");
                        pos = GlobalMethod
                                .getPositionFromArray(names, "SFZMHM");
                        edSfzmhm.setText(pos > -1 ? content[pos] : "");
                        pos = GlobalMethod
                                .getPositionFromArray(names, "CLSBDH");
                        edSbdm.setText(pos > -1 ? content[pos] : "");
                        pos = GlobalMethod.getPositionFromArray(names, "FDJH");
                        edFdjh.setText(pos > -1 ? content[pos] : "");
                    }
                }
            } else if (webResult.getStatus() == 204) {
                GlobalMethod.showDialog("提示信息", "未查询到符合条件的记录！", "确定", self);
            } else if (webResult.getStatus() == 500) {
                GlobalMethod.showDialog("提示信息", "该查询在服务器不能实现，请与管理员联系！", "确定",
                        self);
            } else {
                GlobalMethod.showDialog("提示信息", "网络连接失败，请检查配查或与管理员联系！", "确定",
                        self);
            }
        }

    };

    private String checkJdcPcxx() {
        if (TextUtils.isEmpty(edHphm.getText())
                || TextUtils.getTrimmedLength(edHphm.getText()) < 4)
            return "号牌号码不正确";
        if (spCljg.getSelectedItemPosition() < 1)
            return "处理结果是必填项";
        if (spPcyy.getSelectedItemPosition() < 1)
            return "盘查原因是必填项";
        if (TextUtils.isEmpty(edClpp.getText()))
            return "车辆品牌不能为空";
        if (TextUtils.isEmpty(edClxh.getText()))
            return "车辆型号不能为空";
        if (TextUtils.isEmpty(edSyr.getText()))
            return "车辆所有人不能为空";
        //if (TextUtils.isEmpty(edSfzmhm.getText()))
        //	return "身份证号或组织机构代码证号不能为空";
        return null;

    }

    private void setViewValueFromWpxx() {
        spHpzl.setSelection(
                GlobalMethod.getPositionByKey(GlobalData.hpzlList,
                        wpxx.getClhpzl()), true);
        if (!TextUtils.isEmpty(wpxx.getBhy())) {
            String hpqz = wpxx.getBhy().substring(0, 1);
            String hphm = wpxx.getBhy().substring(1);
            edHphm.setText(hphm);
            spHpqz.setSelection(
                    GlobalMethod.getPositionByValue(GlobalData.hpqlList, hpqz),
                    true);
        }
        edClxh.setText(wpxx.getClxh());
        edClpp.setText(wpxx.getClpp());
        edSyr.setText(wpxx.getSyr());
        edSfzmhm.setText(wpxx.getSfzmhm());
        edFdjh.setText(wpxx.getBhe());
        edSbdm.setText(wpxx.getBhs());
        spPcyy.setSelection(
                GlobalMethod.getPositionByKey(
                        ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCYY), wpxx.getPcyy()),
                true);
        spCljg.setSelection(
                GlobalMethod.getPositionByKey(
                        ZaPcdjDao.zapcDic.get(ZaPcdjDao.PCCLJG),
                        wpxx.getBpcwpcljg()), true);
    }

    private void saveViewValueToWpxx() {
        wpxx.setClhpzl(GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                GlobalConstant.KEY));
        String hpqz = GlobalMethod.getKeyFromSpinnerSelected(spHpqz,
                GlobalConstant.VALUE);
        String hphm = hpqz + edHphm.getText();
        wpxx.setBhy(hphm);
        wpxx.setPcyy(GlobalMethod.getKeyFromSpinnerSelected(spPcyy,
                GlobalConstant.KEY));
        wpxx.setBpcwpcljg(GlobalMethod.getKeyFromSpinnerSelected(spCljg,
                GlobalConstant.KEY));
        wpxx.setClxh(edClxh.getText().toString());
        wpxx.setClpp(edClpp.getText().toString());
        wpxx.setSyr(edSyr.getText().toString());
        wpxx.setSfzmhm(edSfzmhm.getText().toString());
        wpxx.setBhe(edFdjh.getText().toString());
        wpxx.setBhs(edSbdm.getText().toString());
        wpxx.setBpcwppcsj(ZaPcdjDao.sdfDpt.format(new Date()));

    }

    private View.OnClickListener saveWpxxListiner = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            String err = checkJdcPcxx();
            if (err == null) {
                saveViewValueToWpxx();
                ZaPcdjDao.insertWpxx(wpxx, GlobalMethod.getBoxStore(self));
                Log.e("ZapcjdcActivyt", "save id: " + wpxx.getId() + "/" + wpxx.getBpcwpgzqkbh() + "/" + wpxx.getBpcwprybh());
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            } else {
                GlobalMethod.showErrorDialog(err, self);
            }
        }
    };

}
