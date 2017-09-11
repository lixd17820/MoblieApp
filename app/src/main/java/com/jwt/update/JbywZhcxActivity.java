package com.jwt.update;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.jwt.bean.KeyValueBean;
import com.jwt.globalquery.ZhcxHandler;
import com.jwt.globalquery.ZhcxThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalMethod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class JbywZhcxActivity extends AppCompatActivity {

    private EditText editSfzh, editXm, editNlSt, editNlEnd;
    private Spinner spinXb, spinXzqh;
    private Button btnQuery, btnCancel;
    private Context self;
    private static List<KeyValueBean> xbList = new ArrayList<KeyValueBean>();
    private static List<KeyValueBean> xzqhList = new ArrayList<KeyValueBean>();

    static {
        xbList.add(new KeyValueBean("", ""));
        xbList.add(new KeyValueBean("1", "男性"));
        xbList.add(new KeyValueBean("2", "女性"));
        xzqhList.add(new KeyValueBean("", ""));
        xzqhList.add(new KeyValueBean("320602", "市区"));
        xzqhList.add(new KeyValueBean("320621", "海安"));
        xzqhList.add(new KeyValueBean("320623", "如东"));
        xzqhList.add(new KeyValueBean("320681", "启东"));
        xzqhList.add(new KeyValueBean("320682", "如皋"));
        xzqhList.add(new KeyValueBean("320683", "通州"));
        xzqhList.add(new KeyValueBean("320684", "海门"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zhcx_bdrk);
        editSfzh = (EditText) findViewById(R.id.edit_sfzh);
        editXm = (EditText) findViewById(R.id.edit_xm);
        editNlSt = (EditText) findViewById(R.id.edit_nl_st);
        editNlEnd = (EditText) findViewById(R.id.edit_nl_end);
        spinXb = (Spinner) findViewById(R.id.spin_xb);
        spinXzqh = (Spinner) findViewById(R.id.spin_xzqh);
        btnQuery = (Button) findViewById(R.id.btn_query);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        GlobalMethod.changeAdapter(spinXb, xbList, this);
        GlobalMethod.changeAdapter(spinXzqh, xzqhList, this);
        btnQuery.setOnClickListener(clQuery);
        btnCancel.setOnClickListener(clQuery);
    }

    private View.OnClickListener clQuery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnQuery) {
                String sfzh = editSfzh.getText().toString().toUpperCase();
                String xm = editXm.getText().toString();
                String nlSt = editNlSt.getText().toString();
                String nlEnd = editNlEnd.getText().toString();
                String xb = GlobalMethod.getKeyFromSpinnerSelected(spinXb, GlobalConstant.KEY);
                String xzqh = GlobalMethod.getKeyFromSpinnerSelected(spinXzqh, GlobalConstant.KEY);
                if (TextUtils.isEmpty(sfzh) && TextUtils.isEmpty(xm)) {
                    GlobalMethod.showErrorDialog("身份证号和姓名不能全为空", self);
                    return;
                }
                String err = checkNl(nlSt, nlEnd);
                if (!TextUtils.isEmpty(err)) {
                    GlobalMethod.showErrorDialog(err, self);
                    return;
                }
                String conds = "";
                if (!TextUtils.isEmpty(sfzh))
                    conds += " AND GMSFHM='" + sfzh + "'";
                if (!TextUtils.isEmpty(xm))
                    conds += " AND XM = '" + xm + "'";
                if (!TextUtils.isEmpty(xb))
                    conds += " AND XB='" + xb + "'";
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                if (!TextUtils.isEmpty(nlSt)) {
                    String stYear = (year - Integer.valueOf(nlSt)) + "-01-01";
                    conds += " AND TO_DATE(CSRQ,'YYYY-MM-DD')<=TO_DATE('" + stYear + "','YYYY-MM-DD')";
                }
                if (!TextUtils.isEmpty(nlEnd)) {
                    String endYear = (year - Integer.valueOf(nlEnd)) + "-01-01";
                    conds += " AND TO_DATE(CSRQ,'YYYY-MM-DD')>=TO_DATE('" + endYear + "','YYYY-MM-DD')";
                }
                if (!TextUtils.isEmpty(xzqh)) {
                    conds += " AND HJQH='" + xzqh + "'";
                }
                if (TextUtils.isEmpty(conds)) {
                    GlobalMethod.showErrorDialog("查询内容不能全为空", self);
                } else {
                    conds = conds.substring(5);
                    ZhcxThread thread = new ZhcxThread(new ZhcxHandler(self));
                    thread.doStart(self, "C005", conds);
                }
            } else if (v == btnCancel) {
                finish();
            }
        }
    };

    private String checkNl(String nlSt, String nlEnd) {
        if (!TextUtils.isEmpty(nlSt) && !TextUtils.isDigitsOnly(nlSt))
            return "开始年龄不是数字";
        if (!TextUtils.isEmpty(nlEnd) && !TextUtils.isDigitsOnly(nlEnd))
            return "结束年龄不是数字";
        if (!TextUtils.isEmpty(nlSt) && !TextUtils.isEmpty(nlEnd)) {
            int nls = Integer.valueOf(nlSt);
            int nle = Integer.valueOf(nlEnd);
            if (nls > nle)
                return "开始年龄不能大于结束年龄";
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
