package com.jwt.update;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.jwt.dao.WfdmDao;
import com.jwt.dao.WsglDAO;
import com.jwt.jbyw.VerifyData;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;

public class VioWftzActivity extends ViolationActivity {
    private static final String TAG = "VioWftzActivity";
    private Context self;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        jdsbh = WsglDAO.getCurrentJdsbh(GlobalConstant.QZCSPZ, zqmj, GlobalMethod.getBoxStore(self));
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

        // 设置标题
        setTitle(getActivityTitle());
        textJdsbh.setText("通知书编号：" + jdsbh.getDqhm());
        // 罚款
        initViolation();
        wslb = "6";
        violation.setWslb(wslb);
        violation.setFkje(0);
        // 移除多个违法列表和缴款方式
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_qzcs);
        rl.removeAllViewsInLayout();
        RelativeLayout r2 = (RelativeLayout) findViewById(R.id.layout_jycx);
        r2.removeAllViewsInLayout();
    }


    @Override
    protected String getViolationTitle() {
        return "违法通知单";
    }

    @Override
    protected String saveAndCheckVio() {
        getViolationFromView(violation);
        String err = VerifyData.verifyCommVio(violation, self);
        if (!TextUtils.isEmpty(err))
            return err;
        String wfxwdm = edWfxw.getText().toString().trim();
        VioWfdmCode wfxwBean = WfdmDao.queryWfxwByWfdm(wfxwdm, GlobalMethod.getBoxStore(self));
        if (wfxwBean == null) {
            return "错误的违法代码!";
        }
        if (!WfdmDao.isYxWfdm(wfxwBean)) {
            return "违法代码不在有效期内!";
        }
        violation.setWfxw(wfxwBean.getWfxw());
        err = VerifyData.verifyWftzVio(violation, self);
        return err;
    }

    @Override
    protected String showWfdmDetail(VioWfdmCode w) {
        String s = w.getWfxw() + ": " + w.getWfms();
        s += ", 罚款" + w.getFkjeDut() + "元, 记" + w.getWfjfs() + "分";
        s += "| 罚款 " + (TextUtils.equals(w.getFkbj(), "1") ? "是" : "否");
        s += "| 警告 " + (TextUtils.equals(w.getJgbj(), "1") ? "是" : "否");
        // 强制措施将显示收缴或扣留项目
        s += "| 强制措施  "
                + (TextUtils.isEmpty(w.getQzcslx()) ? "无" : w.getQzcslx());
        s += "|" + (WfdmDao.isYxWfdm(w) ? "有效代码" : "无效代码");
        return s;
    }

    @Override
    protected int getCfzl() {
        return GlobalConstant.WFTZD;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
