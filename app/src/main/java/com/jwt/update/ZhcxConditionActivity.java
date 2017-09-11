package com.jwt.update;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.ArrayList;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.bean.KeyValueBean;
import com.jwt.globalquery.CxItem;
import com.jwt.globalquery.CxMenus;
import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.globalquery.ZhcxHandler;
import com.jwt.globalquery.ZhcxThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ZhcxConditionActivity extends AppCompatActivity {

    private CxMenus zhcxItem;
    private Context self;
    private View[] allViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.zhcx_item);
        zhcxItem = (CxMenus) getIntent().getSerializableExtra("zhcxItem");
        setTitle(zhcxItem.getCxMenuName());
        LinearLayout rlayout = (LinearLayout) findViewById(R.id.rootLayout);

        if (zhcxItem != null && zhcxItem.getCxItems() != null
                && zhcxItem.getCxItems().length > 0) {
            CxItem[] items = zhcxItem.getCxItems();
            allViews = new View[items.length];
            for (int i = 0; i < items.length; i++) {
                TextView tv = new TextView(this);
                tv.setText(items[i].getItemLabel());
                tv.setTextSize(18);
                tv.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                rlayout.addView(tv);
                if (items[i].getItemLx().equals("EditText")) {
                    EditText edSfzh = new EditText(this);
                    edSfzh.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    // 设置文本框的默认值
                    edSfzh.setText(items[i].getItemDeValue());
                    // 设置文本框的输入法
                    edSfzh.setRawInputType(GlobalMethod
                            .getInputMethodByName(items[i].getItemInMethod()));
                    rlayout.addView(edSfzh);
                    allViews[i] = edSfzh;
                } else if (items[i].getItemLx().equals("Spinner")) {
                    Spinner spin = new Spinner(this);
                    ArrayList<KeyValueBean> ar = getItemArray(items[i]
                            .getItemArray());
                    GlobalMethod.changeAdapter(spin, ar, this);
                    spin.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    rlayout.addView(spin);
                    allViews[i] = spin;
                }
            }

        }
        findViewById(R.id.okButton).setOnClickListener(queryButListener);
        findViewById(R.id.cancelButton).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public ArrayList<KeyValueBean> getItemArray(String zl) {
        Class<GlobalData> className = GlobalData.class;

        ArrayList<KeyValueBean> clbjs = new ArrayList<KeyValueBean>();
        Field[] fs = className.getDeclaredFields();
        for (Field field : fs) {
            if (zl.equals(field.getName())) {
                try {
                    ArrayList<KeyValueBean> temp = (ArrayList<KeyValueBean>) field
                            .get(className);
                    for (KeyValueBean keyValueBean : temp) {
                        clbjs.add(keyValueBean);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return clbjs;
    }

    private OnClickListener queryButListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String where = "";
            if (allViews != null && allViews.length > 0) {
                for (int i = 0; i < allViews.length; i++) {
                    View allView = allViews[i];
                    String clName = allView.getClass().getSimpleName();
                    if (clName.equals("EditText")) {
                        EditText ev = (EditText) allView;
                        // 这是一个编辑框
                        if (!TextUtils.isEmpty(ev.getText())) {
                            where += " AND "
                                    + zhcxItem.getCxItems()[i].getItemMc()
                                    + getBjtj(
                                    zhcxItem.getCxItems()[i]
                                            .getItemBjgx(),
                                    ev.getText().toString());
                        }
                    } else if (clName.equals("Spinner")) {
                        Spinner sp = (Spinner) allView;
                        if (sp.getSelectedItemPosition() > -1) {
                            String key = GlobalMethod
                                    .getKeyFromSpinnerSelected(sp,
                                            GlobalConstant.KEY);
                            where += " AND "
                                    + zhcxItem.getCxItems()[i].getItemMc()
                                    + getBjtj(
                                    zhcxItem.getCxItems()[i]
                                            .getItemBjgx(),
                                    key);
                        }
                    }
                }
            }
            if (TextUtils.isEmpty(where)) {
                Toast.makeText(self, "查询内容不能全为空", Toast.LENGTH_LONG).show();
            } else {
                where = where.substring(5);
                ZhcxThread thread = new ZhcxThread(new ZhcxHandler(self));
                thread.doStart(self, zhcxItem.getCxId(), where);
            }
        }
    };

    private String getBjtj(String bjgx, String tj) {
        String re = "";
        if (bjgx.equals("like")) {
            return " like '%" + tj + "%'";
        } else if (bjgx.equals("eq")) {
            return "= '" + tj + "'";
        } else if (bjgx.equals("gt")) {
            return "> " + tj + "";
        } else if (bjgx.equals("lt")) {
            return "< " + tj + "";
        }
        return re;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
