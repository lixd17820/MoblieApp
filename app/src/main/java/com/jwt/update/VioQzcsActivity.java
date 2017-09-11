package com.jwt.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.bean.KeyValueBean;
import com.jwt.bean.WfxwBzz;
import com.jwt.dao.WfdmDao;
import com.jwt.dao.WsglDAO;
import com.jwt.event.QzcsxmEvent;
import com.jwt.jbyw.JdsPreviewActivity;
import com.jwt.jbyw.VerifyData;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.ParserJson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VioQzcsActivity extends ViolationActivity {

    private static final String TAG = "VioQzcsActivity";

    private Context self;

    //private EditText edAllWfxw;

    private Button btnQzcs;
    private TextView tvQzcsDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        //edAllWfxw = (EditText) findViewById(R.id.Edit_wfxw_all);
        btnQzcs = (Button) findViewById(R.id.btn_mod_qzcs);
        tvQzcsDetail = (TextView) findViewById(R.id.tv_qzcs_detail);
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
        textJdsbh.setText("强制措施编号：" + jdsbh.getDqhm());
        initViolation();
        // violation.setCfzl("2");
        wslb = "3";
        violation.setWslb(wslb);
        violation.setFkje(0);
        RelativeLayout r2 = (RelativeLayout) findViewById(R.id.layout_jycx);
        r2.setVisibility(View.GONE);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout_jkfs);
        rl.setVisibility(View.GONE);
        //registerForContextMenu(edAllWfxw);
        btnQzcs.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addWfdmAndQzcsxm();
            }
        });
    }

    @Override
    protected String getViolationTitle() {
        return "强制措施";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.force_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.save_quite):
                return menuSaveViolation();
            case (R.id.print_preview):
                // 预览打印
                return menuPreviewViolation();

            case (R.id.pre_print):
                // 单据已保存，打印决定书
                return menuPrintViolation();
            case R.id.con_vio:
                if (violation != null && isViolationSaved)
                    showConVio(violation);
                else
                    GlobalMethod.showToast("请保存当前决定书", self);

                return true;
            case R.id.sys_config:
                Intent intent = new Intent(self, ConfigParamSetting.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    protected String showWfdmDetail(VioWfdmCode w) {
        String s = w.getWfxw() + ": " + w.getWfms();
        // 强制措施将显示收缴或扣留项目
        s += "| 强制措施  "
                + (TextUtils.isEmpty(w.getQzcslx()) ? "无" : w.getQzcslx());
        s += "| " + (WfdmDao.isYxWfdm(w) ? "有效代码" : "无效代码");
        return s;
    }

    private List<WfxwBzz> wfxws = new ArrayList<>();
    private String qzxm = "";
    private String sjxm = "";

    @Override
    protected String saveAndCheckVio() {
        getViolationFromView(violation);
        String err = VerifyData.verifyCommVio(violation, self);
        if (!TextUtils.isEmpty(err))
            return err;
        if (wfxws == null || wfxws.isEmpty())
            return "至少需要一个违法行为";
        violation.setWfxw(ParserJson.arrayToJsonArray(wfxws).toString());
        if (!TextUtils.isEmpty(sjxm))
            violation.setSjxm(sjxm);
        violation.setQzcslx(qzxm);
        err = VerifyData.verifyQzcsVio(violation);
        return err;
    }

    private void addWfdmAndQzcsxm() {
        Intent intent = new Intent(self, QzcsxmActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getQzcsxmEvent(QzcsxmEvent xm) {
        wfxws = xm.wfxw;
        qzxm = xm.qzcsxms;
        sjxm = xm.sjxms;
        String s = "违法代码：";
        for (WfxwBzz w : wfxws) {
            s += w.getWfxw() + ", ";
        }
        s += "\n强制措施项目：";
        for (int i = 0; i < qzxm.length(); i++) {
            String w = qzxm.substring(i, i + 1);
            s += "\n" + GlobalMethod.getStringFromKVListByKey(GlobalData.qzcslxList, w);
        }
        if (!TextUtils.isEmpty(sjxm)) {
            s += "\n收缴项目：";
            for (int i = 0; i < sjxm.length(); i++) {
                String w = sjxm.substring(i, i + 1);
                s += "\n" + GlobalMethod.getStringFromKVListByKey(GlobalData.sjxmList, w);
            }
        }
        tvQzcsDetail.setText(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
