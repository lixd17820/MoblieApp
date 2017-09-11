package com.jwt.fragment;

import java.util.List;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.jwt.adapter.SpinnerCustomAdapter;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.WfddDao;
import com.jwt.pojo.FavorWfdd;
import com.jwt.pojo.FrmCode;
import com.jwt.pojo.FrmRoadItem;
import com.jwt.pojo.FrmRoadSeg;
import com.jwt.pojo.SysParaValue;
import com.jwt.update.App;
import com.jwt.update.R;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import io.objectbox.Box;

public class SystemWfddFragment extends Fragment {

    private Activity self;
    private Spinner spinnerXzqh, spinnerRoads, spinnerRoadseg;
    private ImageButton buttonAddEditWfdd;
    private Button systemOKBt, addFavorButton;

    /**
     * 公里数编辑框
     */
    private EditText editTextGls;
    /**
     * 米数编辑框
     */
    private EditText editTextMs;
    /**
     * 道路名称编辑框
     */
    private EditText editTextLdqc;

    private String TAG = "SystemWfddFragment";

    enum SenderSpin {
        XZQH, ROAD, SEG
    }

    ;

    @Override
    public View onCreateView(LayoutInflater in, ViewGroup c, Bundle si) {
        return in.inflate(R.layout.wfdd_config, c, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        self = getActivity();
        spinnerXzqh = (Spinner) self.findViewById(R.id.spinner_xzqh);
        spinnerRoads = (Spinner) self.findViewById(R.id.spinner_road_list);
        spinnerRoadseg = (Spinner) self
                .findViewById(R.id.spinner_road_seg_list);
        editTextGls = (EditText) self.findViewById(R.id.edit_kls);
        editTextMs = (EditText) self.findViewById(R.id.edit_ms);
        editTextLdqc = (EditText) self.findViewById(R.id.edit_eidtable_dlmc);
        buttonAddEditWfdd = (ImageButton) self
                .findViewById(R.id.button_add_edit_wfdd);
        addFavorButton = (Button) self.findViewById(R.id.addFavorButton);
        systemOKBt = (Button) self.findViewById(R.id.sysOKButton);
        buttonAddEditWfdd.setOnClickListener(clickListener);
        addFavorButton.setOnClickListener(clickListener);
        systemOKBt.setOnClickListener(clickListener);

        List<KeyValueBean> kvs = WfddDao.getOwnerXzqhList(
                GlobalData.grxx.get(GlobalConstant.YBMBH),GlobalMethod.getBoxStore(self));
        if (kvs != null) {
            GlobalMethod.changeAdapter(spinnerXzqh, kvs, self);
            String xzqh = kvs.get(0).getKey();
            List<KeyValueBean> roads = WfddDao.getRoadItemsByXzqh(xzqh, GlobalMethod.getBoxStore(self));
            GlobalMethod.changeAdapter(spinnerRoads, roads, self);
            spinnerRoads.setSelection(0, false);
        }
        //spinnerFirstLoadData();
        spinnerXzqh.setOnItemSelectedListener(xzqhSelListener);
        spinnerRoads.setOnItemSelectedListener(roadSelListener);
        spinnerRoadseg.setOnItemSelectedListener(segSelListener);
    }

    /**
     * 初始加载三个下拉框的数据
     * <p>
     * <br>
     * 3是三个下拉表依次更新,就是初始化状态<br>
     * 2更新下两个,就是行政区划下拉框更新了<br>
     * 1是最后一个更新,道路下拉框更新了
     */
    private void spinnerFirstLoadData() {
        List<KeyValueBean> kvs = WfddDao.getOwnerXzqhList(
                GlobalData.grxx.get(GlobalConstant.YBMBH),GlobalMethod.getBoxStore(self));
        GlobalMethod.changeAdapter(spinnerXzqh, kvs, self);

        String xzqh = (kvs != null && kvs.size() > 0) ? kvs.get(0).getKey()
                : null;
        if (!TextUtils.isEmpty(xzqh)) {
            spinnerRoadseg.setSelection(0, false);
            List<KeyValueBean> roads = WfddDao.getRoadItemsByXzqh(xzqh,
                    GlobalMethod.getBoxStore(self));
            GlobalMethod.changeAdapter(spinnerRoads, roads, self);
            String road = (roads != null && roads.size() > 0) ? roads.get(0)
                    .getKey() : null;
            if (!TextUtils.isEmpty(road)) {
                spinnerRoads.setSelection(0, false);
                if (WfddDao.isGsd(road)) {
                    GlobalMethod.changeAdapter(spinnerRoadseg, null, self);
                    editTextLdqc.setText(((KeyValueBean) spinnerRoads
                            .getSelectedItem()).getValue());
                } else {
                    List<KeyValueBean> segs = WfddDao.getRoadSegByRoad(road,
                            xzqh, GlobalMethod.getBoxStore(self));
                    GlobalMethod.changeAdapter(spinnerRoadseg, segs, self);
                    if (segs != null && segs.size() > 0)
                        spinnerRoadseg.setSelection(0, false);
                }
            }
        }
        changeViewStatus();
    }

    /**
     * 改变下拉框数据联动
     *
     * @param sender 发送者标记
     */
    private void changeSpinnerData(SenderSpin sender) {
        if (sender == SenderSpin.XZQH) {
            // 行政区划变了,级联下拉框均要清空
            GlobalMethod.changeAdapter(spinnerRoads, null, self);
            GlobalMethod.changeAdapter(spinnerRoadseg, null, self);
            editTextLdqc.setText("");
            editTextGls.setText("");
            editTextMs.setText("");

            String xzqh = GlobalMethod.getKeyFromSpinnerSelected(spinnerXzqh,
                    GlobalConstant.KEY);
            if (!TextUtils.isEmpty(xzqh)) {
                List<KeyValueBean> roads = WfddDao.getRoadItemsByXzqh(xzqh,
                        GlobalMethod.getBoxStore(self));
                if (roads != null && !roads.isEmpty()) {
                    GlobalMethod.changeAdapter(spinnerRoads, roads, self);
                    spinnerRoads.setSelection(0, false);
                }
            }
        } else if (sender == SenderSpin.ROAD) {
            // 道路变了
            GlobalMethod.changeAdapter(spinnerRoadseg, null, self);
            editTextLdqc.setText("");
            editTextGls.setText("");
            editTextMs.setText("");
            // -------------------------------------------
            String road = GlobalMethod.getKeyFromSpinnerSelected(spinnerRoads,
                    GlobalConstant.KEY);
            if (!TextUtils.isEmpty(road)) {
                if (WfddDao.isGsd(road)) {
                    GlobalMethod.changeAdapter(spinnerRoadseg, null, self);
                    editTextLdqc.setText(((KeyValueBean) spinnerRoads
                            .getSelectedItem()).getValue());
                } else {
                    String xzqh = GlobalMethod.getKeyFromSpinnerSelected(
                            spinnerXzqh, GlobalConstant.KEY);
                    List<KeyValueBean> segs = WfddDao.getRoadSegByRoad(road,
                            xzqh, GlobalMethod.getBoxStore(self));
                    GlobalMethod.changeAdapter(spinnerRoadseg, segs, self);
                    if (segs != null && segs.size() > 0)
                        spinnerRoadseg.setSelection(0, false);
                }
            }
        } else if (sender == SenderSpin.SEG) {

        }
        changeViewStatus();
    }

    private AdapterView.OnItemSelectedListener xzqhSelListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long i) {
            Log.e(TAG, "spinnerXzqh" + position + "/" + i);
            SpinnerCustomAdapter ad = (SpinnerCustomAdapter) parent
                    .getAdapter();
            if (ad != null && position > -1) {
                changeSpinnerData(SenderSpin.XZQH);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    private AdapterView.OnItemSelectedListener roadSelListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long i) {
            SpinnerCustomAdapter ad = (SpinnerCustomAdapter) parent
                    .getAdapter();
            if (ad != null && position > -1) {
                changeSpinnerData(SenderSpin.ROAD);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    private AdapterView.OnItemSelectedListener segSelListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long i) {
            SpinnerCustomAdapter ad = (SpinnerCustomAdapter) parent
                    .getAdapter();
            if (ad != null && position > -1) {
                KeyValueBean kv = ad.getItem(position);
                // 路口则是路段的名称
                String roadseg = kv.getValue();
                // 开始与11的是路段
                if (kv.getKey().startsWith("11"))
                    roadseg = GlobalMethod.getKeyFromSpinnerSelected(
                            spinnerRoads, GlobalConstant.VALUE) + roadseg;
                editTextLdqc.setText(roadseg);
                changeSpinnerData(SenderSpin.SEG);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    /**
     * 根据公里数和米数创建系统自认的路段描述,用于国省道
     *
     * @return
     */
    private String createEditableSysDlmc() {
        String glsTxt = editTextGls.getText().toString();
        String msTxt = editTextMs.getText().toString();
        glsTxt = GlobalMethod.paddingZero(glsTxt, 4);
        msTxt = GlobalMethod.paddingZero(msTxt, 3);
        String result = null;
        if (TextUtils.isDigitsOnly(glsTxt) && TextUtils.isDigitsOnly(msTxt)) {
            result = GlobalMethod.getKeyFromSpinnerSelected(spinnerRoads,
                    GlobalConstant.VALUE)
                    + Integer.valueOf(glsTxt)
                    + "公里"
                    + Integer.valueOf(msTxt) + "米";

        }
        return result;
    }

    /**
     * 通过界面选择生成地点
     *
     * @param dd
     * @return 空，说明通过验证，否则返回错误描述
     */
    private String createFavorWfddFromSelect(FavorWfdd dd) {
        String xzqh = ((KeyValueBean) spinnerXzqh.getSelectedItem()).getKey();
        String road = ((KeyValueBean) spinnerRoads.getSelectedItem()).getKey();
        String s = "";
        String ms = "000";
        String sysMc = "";
        if (WfddDao.isGsd(road)) {
            s = editTextGls.getText().toString();
            s = GlobalMethod.paddingZero(s, 4);
            ms = GlobalMethod.paddingZero(editTextMs.getText().toString(), 3);
            sysMc = createEditableSysDlmc();
        } else {
            if (spinnerRoadseg.getSelectedItemPosition() > -1) {
                KeyValueBean kv = (KeyValueBean) spinnerRoadseg
                        .getSelectedItem();
                s = kv.getKey();
                sysMc = kv.getValue();
            }
        }
        // String dldm = x + r + s;

        String favorMc = editTextLdqc.getText().toString();

        // 系统验证公里数或米数为数字

        if (!TextUtils.isDigitsOnly(s))
            return "公里数不是数字!";
        if (!TextUtils.isDigitsOnly(ms))
            return "米数不是数字!";
        // 自取名不能为空
        if (favorMc == null || TextUtils.isEmpty(favorMc.trim()))
            return "道路名称不能为空";

        if (ms == null || TextUtils.isEmpty(ms.trim()))
            return "必须要选择一条道路";
        dd.setXzqh(xzqh);
        dd.setDldm(road);
        dd.setLddm(s);
        dd.setMs(ms);
        dd.setFavorLdmc(favorMc);
        dd.setSysLdmc(sysMc);
        return null;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonAddEditWfdd) {
                // 加入地点编辑框
                String txt = createEditableSysDlmc();
                if (txt != null)
                    editTextLdqc.setText(txt);
                else
                    Toast.makeText(self, "不是有效数字", Toast.LENGTH_SHORT).show();
            } else if (v == addFavorButton) {
                // 加入自选违法地点
                FavorWfdd dd = new FavorWfdd();
                String res = createFavorWfddFromSelect(dd);
                if (res == null) {
                    int re = WfddDao.addFavorWfdd(dd, GlobalMethod.getBoxStore(self));
                    if (re > 0)
                        Toast.makeText(self, "加入成功", Toast.LENGTH_LONG).show();
                    else if (re == -10)
                        Toast.makeText(self, "存在重复的地点名称", Toast.LENGTH_LONG).show();
                    // changeFavorWfddList();
                } else {
                    Toast.makeText(self, res, Toast.LENGTH_LONG).show();
                }
            } else if (v == systemOKBt) {
                // 系统地点,确定按扭
                FavorWfdd dd = new FavorWfdd();
                String isOk = createFavorWfddFromSelect(dd);
                if (isOk == null) {
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putString(
                            "wfddDm",
                            dd.getXzqh() + dd.getDldm() + dd.getLddm()
                                    + dd.getMs());
                    b.putString("wfddMc", dd.getFavorLdmc());
                    i.putExtras(b);
                    self.setResult(Activity.RESULT_OK, i);
                    self.finish();
                } else {
                    Toast.makeText(self, isOk, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    /**
     * 根据控件的状态改变状态
     */
    private void changeViewStatus() {
        String road = GlobalMethod.getKeyFromSpinnerSelected(spinnerRoads,
                GlobalConstant.KEY);
        String seg = GlobalMethod.getKeyFromSpinnerSelected(spinnerRoadseg,
                GlobalConstant.KEY);
        if (TextUtils.isEmpty(road)) {
            editTextGls.setEnabled(false);
            editTextMs.setEnabled(false);
            buttonAddEditWfdd.setEnabled(false);
            editTextGls.setText("");
            editTextMs.setText("");
            spinnerRoadseg.setEnabled(false);
            editTextLdqc.setEnabled(false);
            editTextLdqc.setText("");
        } else {
            boolean g = WfddDao.isGsd(road);
            editTextGls.setEnabled(g);
            editTextMs.setEnabled(g);
            buttonAddEditWfdd.setEnabled(g);
            if (!g) {
                editTextGls.setText("");
                editTextMs.setText("");
            }
            spinnerRoadseg.setEnabled(!g);
            boolean isEdit = (g || !TextUtils.isEmpty(seg));
            editTextLdqc.setEnabled(isEdit);
            if (!isEdit)
                editTextLdqc.setText("");
        }

    }

}
