package com.jwt.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jwt.bean.WfxwBzz;
import com.jwt.dao.WfdmDao;
import com.jwt.dao.WsglDAO;
import com.jwt.jbyw.VerifyData;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * 简易程序处罚，父类为处罚类
 *
 * @author lixd
 */
public class VioJycxActivity extends ViolationActivity {

    private static final String TAG = "VioJycxActivity";

    private Spinner spJkfs;

    private Activity self;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle ss) {
        super.onRestoreInstanceState(ss);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "Jycx ON CREATE");
        // 常量赋值
        self = this;
        spJkfs = (Spinner) findViewById(R.id.Sp_jkfs);
        GlobalMethod.changeAdapter(spJkfs, GlobalData.jkfsList, this, true);
        jdsbh = WsglDAO.getCurrentJdsbh(GlobalConstant.JYCFJDS, zqmj, GlobalMethod.getBoxStore(self));
        if (jdsbh == null) {
            GlobalMethod.showDialogWithListener("提示信息",
                    "没有相应的处罚编号，请到文书管理中获取编号", "确定", exitSystem, self);
            return;
        }
        if (WsglDAO.hmNotEqDw(jdsbh)) {
            GlobalMethod.showDialogWithListener("提示信息",
                    "当前文书编号与处罚机关不符，请上交文书后重新获取", "确定", exitSystem, self);
            return;
        }
        setTitle(getActivityTitle());
        textJdsbh.setText("决定书编号：" + jdsbh.getDqhm());
        initViolation();
        cfzl = "2";
        wslb = "1";
        violation.setCfzl(cfzl);
        violation.setWslb(wslb);
        // 移除多个违法列表
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_qzcs);
        rl.setVisibility(View.GONE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected String saveAndCheckVio() {
        getViolationFromView(violation);
        String err = VerifyData.verifyCommVio(violation, self);
        if (!TextUtils.isEmpty(err))
            return err;
        // 缴款方式
        violation.setJkfs(GlobalMethod.getKeyFromSpinnerSelected(spJkfs,
                GlobalConstant.KEY));
        String wfxwdm = edWfxw.getText().toString().trim();
        VioWfdmCode wfxwBean = WfdmDao.queryWfxwByWfdm(wfxwdm, GlobalMethod.getBoxStore(self));
        if (wfxwBean == null) {
            return "错误的违法代码!";
        }
        if (!WfdmDao.isYxWfdm(wfxwBean)) {
            return "违法代码不在有效期内!";
        }
        if (TextUtils.equals(wfxwBean.getFkbj(), "0"))
            return "此违法行为不能用于罚款";
        int bzz = GlobalMethod.getEditInt(edBzz);
        int scz = GlobalMethod.getEditInt(edScz);
        WfxwBzz wfxw = new WfxwBzz(wfxwBean.getWfxw(), bzz + "", scz + "");
        violation.setWfxw(ParserJson.createArrayByObjs(wfxw).toString());

        violation.setFkje(wfxwBean.getFkjeDut());
        // 缴款方式为当场交款,则交款标记为已交款
        if (TextUtils.equals(violation.getJkfs(), "1")) {
            violation.setJkbj("1");
            violation.setJkrq(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        } else {
            violation.setJkbj("0");
            violation.setJkrq("");
        }
        //Log.e("VioJycxActivity", "saveAndCheckVio: " + violation.toString());
        err = VerifyData.verifyJycxVio(violation, self);
        return err;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected String showWfdmDetail(VioWfdmCode w) {
        String s = w.getWfxw() + ": " + w.getWfms();
        // 简易程序将显示是否可罚款或警告
        s += ", 罚款" + w.getFkjeDut() + "元, 记" + w.getWfjfs() + "分";
        s += "| 罚款 " + (TextUtils.equals(w.getFkbj(), "1") ? "是" : "否");
        s += "| 警告 " + (TextUtils.equals(w.getJgbj(), "1") ? "是" : "否");
        s += "|" + (WfdmDao.isYxWfdm(w) ? "有效代码" : "无效代码");
        return s;
    }

    @Override
    protected int getCfzl() {
        return GlobalConstant.JYCFJDS;
    }

    @Override
    protected String getViolationTitle() {
        return "简易程序";
    }

}
