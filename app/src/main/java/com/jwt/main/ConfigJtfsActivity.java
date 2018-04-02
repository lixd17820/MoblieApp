package com.jwt.main;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jwt.activity.ActionBarListActivity;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.adapter.OneLineSelectAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TwoLineSelectBean;
import com.jwt.utils.GlobalData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class ConfigJtfsActivity extends ActionBarListActivity {

    ArrayList<TwoLineSelectBean> jtfsApapterList = null;
    Spinner spinJtfsjc;
    EditText editPinyin;
    EditText editZwhz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_jtfs);
        setTitle("交通方式");
        spinJtfsjc = (Spinner) findViewById(R.id.Spin_jtfs_catalog);
        editPinyin = (EditText) findViewById(R.id.Edit_pinyin);
        editZwhz = (EditText) findViewById(R.id.Edit_zwhz);
        ArrayAdapter<CharSequence> ard = ArrayAdapter.createFromResource(this,
                R.array.jtfs_jc, android.R.layout.simple_spinner_item);
        ard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinJtfsjc.setAdapter(ard);
        // 接受车辆分类
        String clfl = getIntent().getStringExtra("clfl");
        if (!TextUtils.isEmpty(clfl) && "F".equals(clfl)) {
            queryJtfs(clfl, null, null);
            spinJtfsjc.setSelection(5);
        } else {
            changeListContent(GlobalData.jtfsList);
        }

        // 查找符合条件的交通方式
        findViewById(R.id.But_jtfs).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                queryJtfs();
            }
        });
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long arg3) {
                // 单选,修改其他为不选
                for (int i = 0; i < jtfsApapterList.size(); i++) {
                    TwoLineSelectBean c = jtfsApapterList.get(i);
                    if (i == position)
                        c.setSelect(!c.isSelect());
                    else
                        c.setSelect(false);
                }
                OneLineSelectAdapter ad = (OneLineSelectAdapter) parent
                        .getAdapter();
                ad.notifyDataSetChanged();
            }
        });
        findViewById(R.id.jtfs_OKButton).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int postion = getSelectItem();
                        if (postion > -1) {
                            TwoLineSelectBean jtfs = jtfsApapterList
                                    .get(postion);
                            //Intent i = new Intent();
                            //Bundle b = new Bundle();
                            //b.putString("jtfsDm", jtfs.getText2());
                            //i.putExtras(b);
                            //setResult(RESULT_OK, i);
                            EventBus.getDefault().post(new KeyValueBean(jtfs.getText2(), jtfs.getText1()));
                            finish();
                        } else {
                            Toast.makeText(ConfigJtfsActivity.this,
                                    "请选择一条交通方式", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        findViewById(R.id.jtfs_CancelButton).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        spinJtfsjc.setOnItemSelectedListener(new OnSpinnerItemSelected() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queryJtfs();
            }
        });
    }

    private void queryJtfs() {
        int pos = spinJtfsjc.getSelectedItemPosition();
        String jtfszl = "";
        if (pos > 0)
            jtfszl = (String) spinJtfsjc.getSelectedItem();
        String pinyin = editPinyin.getText().toString();
        String hz = editZwhz.getText().toString();
        queryJtfs(jtfszl, pinyin, hz);
    }

    private void queryJtfs(String jtfsZl, String pinyin, String hz) {
//        String where = "";
//        if (!TextUtils.isEmpty(pinyin))
//            where += " AND " + Fixcode.FrmCode.DMSM2 + " like '%"
//                    + pinyin.toString().toUpperCase() + "%'";
//        if (!TextUtils.isEmpty(hz))
//            where += " AND " + Fixcode.FrmCode.DMSM1 + " like '%" + hz + "%'";
//        if (!TextUtils.isEmpty(jtfsZl)) {
//            where += " AND " + Fixcode.FrmCode.DMZ + " like '"
//                    + jtfsZl.substring(0, 1) + "%'";
//        }
//        if (!TextUtils.isEmpty(where)) {
//            where = "1=1" + where;
//            ArrayList<KeyValueBean> list = ViolationDAO.getAllFrmCode(
//                    GlobalConstant.JTFS, getContentResolver(), new String[]{
//                            Fixcode.FrmCode.DMZ, Fixcode.FrmCode.DMSM1},
//                    where, Fixcode.FrmCode.DMZ
//            );
//            changeListContent(list);
//        }
    }

    /**
     * @param list
     */
    private void changeListContent(ArrayList<KeyValueBean> list) {
        if (jtfsApapterList == null)
            jtfsApapterList = new ArrayList<TwoLineSelectBean>();
        jtfsApapterList.clear();
        for (KeyValueBean kv : list) {
            jtfsApapterList.add(new TwoLineSelectBean(kv.getValue(), kv
                    .getKey()));
        }
        OneLineSelectAdapter ard = (OneLineSelectAdapter) getListView()
                .getAdapter();
        if (ard == null) {
            ard = new OneLineSelectAdapter(this, R.layout.one_row_select_item,
                    jtfsApapterList);
            getListView().setAdapter(ard);
        }
        ard.notifyDataSetChanged();
    }

    /**
     * 得到当前自选地点的选择项
     *
     * @return
     */
    private int getSelectItem() {
        int position = -1;
        int i = 0;
        while (jtfsApapterList.size() > 0 && i < jtfsApapterList.size()) {
            if (jtfsApapterList.get(i).isSelect()) {
                position = i;
                break;
            }
            i++;
        }
        return position;
    }
}
