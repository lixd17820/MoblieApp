package com.jwt.update;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.jwt.dao.AcdSimpleDao;
import com.jwt.pojo.AcdLawBean;
import com.jwt.pojo.AcdSimpleBean;
import com.jwt.pojo.AcdSimpleHumanBean;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

public class AcdSsZrTjjgActivity extends AppCompatActivity {

    private AcdSimpleBean sgjbqk;
    private ArrayList<AcdSimpleHumanBean> ryjbqkList;
    private EditText editAcdSszr;
    private EditText editTjjg;
    private Context self;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");

    private int operMod;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.acd_sgrds);
        Bundle b = getIntent().getExtras();
        operMod = b.getInt(AcdSimpleDao.OPER_MOD, AcdSimpleDao.ACD_MOD_NEW);
        sgjbqk = (AcdSimpleBean) b.getSerializable("sgjbqk");
        ryjbqkList = (ArrayList<AcdSimpleHumanBean>) b
                .getSerializable("ryjbqk");
        editAcdSszr = (EditText) findViewById(R.id.edit_acd_sszr);
        editTjjg = (EditText) findViewById(R.id.edit_acd_tjjg);
        if (sgjbqk != null && ryjbqkList != null && ryjbqkList.size() > 0) {
            editAcdSszr
                    .setText(TextUtils.isEmpty(sgjbqk.getSgss()) ? procSgss()
                            : sgjbqk.getSgss());
            editTjjg.setText(TextUtils.isEmpty(sgjbqk.getZrtjjg()) ? procZrtjjg()
                    : sgjbqk.getZrtjjg());
        }
        setTitle("调解认定信息");
    }

    private void saveZrTj() {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putString("sgss", editAcdSszr.getText().toString());
        b.putString("tjjg", editTjjg.getText().toString());
        i.putExtras(b);
        setResult(RESULT_OK, i);
        finish();
    }


    private String procSgss() {
        if (ryjbqkList == null || ryjbqkList.size() == 0)
            return "";
        // 生成交通事故基本事实
        String s = "上述时间地点，";
        AcdSimpleHumanBean ry1 = ryjbqkList.get(0);
        char jtfs = ry1.getJtfs().charAt(0);
        String jtfsMs = GlobalMethod.getStringFromKVListByKey(
                GlobalData.acdJtfsList, ry1.getJtfs());
        if (jtfs >= 'G' && jtfs <= 'T') {
            s += ry1.getXm() + "驾驶" + ry1.getHphm()
                    + (TextUtils.isEmpty(jtfsMs) ? "" : jtfsMs.substring(2))
                    + "，";
        } else if (TextUtils.equals(ry1.getJtfs(), "A1")) {
            s += ry1.getXm() + "，";
        } else {
            s += ry1.getXm() + "骑" + jtfsMs + "，";
        }
        s += "与";
        if (ryjbqkList.size() >= 2) {
            AcdSimpleHumanBean ry2 = ryjbqkList.get(1);
            char jtfs2 = ry2.getJtfs().charAt(0);
            jtfsMs = GlobalMethod.getStringFromKVListByKey(
                    GlobalData.acdJtfsList, ry2.getJtfs());
            if (jtfs2 >= 'G' && jtfs2 <= 'T') {
                s += ry2.getXm()
                        + "驾驶"
                        + ry2.getHphm()
                        + (TextUtils.isEmpty(jtfsMs) ? "" : jtfsMs.substring(2));
            } else if (TextUtils.equals(ry2.getJtfs(), "A1")) {
                s += "行人" + ry2.getXm();
            } else {
                s += ry2.getXm() + "骑" + jtfsMs;
            }
            s += getSgxt(sgjbqk.getSgxt()) + "，发生交通事故，";
        }
        s += "致";
        for (AcdSimpleHumanBean hb : ryjbqkList) {
            s += hb.getHphm() + "车损，" + hb.getXm() + "受伤，";
        }
        // if (!TextUtils.isEmpty(sgjbqk.getSsrs())
        // && TextUtils.isDigitsOnly(sgjbqk.getSsrs())
        // && Integer.valueOf(sgjbqk.getSsrs()) > 0) {
        // s += "受伤" + sgjbqk.getSsrs() + "人，";
        // }
        // s += "直接财产损失" + sgjbqk.getZjccss() + "元的交通事故。";

        String s1 = "";
        for (AcdSimpleHumanBean ry : ryjbqkList) {
            s1 += "当事人" + ry.getXm();
            if (!TextUtils.isEmpty(ry.getTk1())) {
                AcdLawBean tk = AcdSimpleDao.queryWftknrByXh(
                         ry.getTk1(),GlobalMethod.getBoxStore(self));
                s1 += "的行为违反了" + tk.getTkmc() + "之规定，";
            }
            if (!TextUtils.isEmpty(ry.getTk2())) {
                AcdLawBean tk = AcdSimpleDao.queryWftknrByXh(
                         ry.getTk2(),GlobalMethod.getBoxStore(self));
                s1 += "以及" + tk.getTkmc() + "之规定，";
            }
            if (!TextUtils.isEmpty(ry.getTk3())) {
                AcdLawBean tk = AcdSimpleDao.queryWftknrByXh(
                        ry.getTk3(),GlobalMethod.getBoxStore(self));
                s1 += "以及" + tk.getTkmc() + "之规定，";
            }
            if (TextUtils.equals(ry.getSgzr(), "5"))
                s1 += "无责任；";
            else
                s1 += "负"
                        + (GlobalMethod.getStringFromKVListByKey(
                        GlobalData.acdSgzrList, ry.getSgzr())) + "责任；";
        }
        return s + s1;
    }

    private String getXb(String xb) {
        return GlobalMethod.getStringFromKVListByKey(GlobalData.xbList, xb);
    }

    private String getSgxt(String sgxt) {
        return GlobalMethod.getStringFromKVListByKey(GlobalData.arrayAcdSgxt,
                sgxt);
    }

    private String procZrtjjg() {
        if (ryjbqkList == null || ryjbqkList.size() == 0)
            return "";
        // 生成责任调解结果12121212
        String s = "  ";
        if (TextUtils.equals(sgjbqk.getJafs(), "1")) {
            s += "经双方共同请求调解达成一致：" + ryjbqkList.get(0).getXm() + " 赔偿 元，就此结案。";
        } else if (TextUtils.equals(sgjbqk.getJafs(), "2")) {
            s += "因当事人不同意调解，根据《道路交通事故处理程序规定》第十八条第三款，不适用调解。";
        } else if (TextUtils.equals(sgjbqk.getJafs(), "3")) {
            s += "因当事人对事故认定有异议，根据《道路交通事故处理程序规定》第十八条第一款，不适用调解。";
        } else if (TextUtils.equals(sgjbqk.getJafs(), "4")) {
            s += "因当事人拒绝在“交通事故事实及责任”栏签字，根据《道路交通事故处理程序规定》第十八条第二款，不适用调解。";
        }
        return s;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (operMod == AcdSimpleDao.ACD_MOD_SHOW) {
            menu.removeItem(R.id.menu_reset_tj);
            menu.removeItem(R.id.menu_reset_zr);
            menu.removeItem(R.id.save_file);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acd_zrtj_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_file:
                saveZrTj();
                return true;
            case R.id.menu_reset_tj:
                editTjjg.setText(procZrtjjg());
                return true;
            case R.id.menu_reset_zr:
                editAcdSszr.setText(procSgss());
                return true;
        }
        return false;
    }
}
