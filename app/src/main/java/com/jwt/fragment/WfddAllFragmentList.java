package com.jwt.fragment;

import java.util.ArrayList;
import java.util.List;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.adapter.SelectCommAdapter;
import com.jwt.adapter.SelectObjectBean;
import com.jwt.adapter.TextWatcherImpl;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TwoLineSelectBean;
import com.jwt.bean.WfddBean;
import com.jwt.dao.WfddDao;
import com.jwt.pojo.FavorWfdd;
import com.jwt.main.R;
import com.jwt.pojo.FrmRoadItem;
import com.jwt.pojo.FrmRoadSeg;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class WfddAllFragmentList extends ListFragment {

    private Activity self;

    private Button btnOk, btnAddFavor, btnFind;

    private Spinner spinXzqh;

    private EditText editText, editGls, editMs, editLdmc, editPtLdmc;

    private List<SelectObjectBean> wfddList = new ArrayList<>();

    //private ArrayList<SelectObjectBean> wfddList = new ArrayList<SelectObjectBean>();

    private MaterialDialog mdialog = null;

    private WfddBean curWfdd;

    private boolean isAddFavor = true;

    private static final String TAG = "WfddAllFragmentList";

    @Override
    public View onCreateView(LayoutInflater in, ViewGroup c, Bundle si) {
        return in.inflate(R.layout.wfdd_all_list, c, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        self = getActivity();
//		resolver = self.getContentResolver();
        spinXzqh = (Spinner) self.findViewById(R.id.spin_wfdd_xzqh);
        editText = (EditText) self.findViewById(R.id.edit_wfdd_text);
        btnOk = (Button) self.findViewById(R.id.btn_wfdd_ok);
        btnAddFavor = (Button) self.findViewById(R.id.btn_wfdd_add_favor);
        btnFind = (Button) self.findViewById(R.id.btn_wfdd_find);
        btnOk.setOnClickListener(ck);
        btnAddFavor.setOnClickListener(ck);
        btnFind.setOnClickListener(ck);
        self.findViewById(R.id.fouce_line).requestFocus();
        // 设置行政区划
        GlobalMethod.changeAdapter(
                spinXzqh, WfddDao.getOwnerXzqhList(GlobalData.grxx.get(GlobalConstant.YBMBH),
                        GlobalMethod.getBoxStore(self)), self);
        referView();
    }


    private void createMDialog() {
        mdialog = new MaterialDialog.Builder(self)
                .title("自定义国省道路")
                .customView(R.layout.wfdd_gsd_dlmc, true)
                .positiveText("确定")
                .onPositive(sc).build();
        editGls = (EditText) mdialog.findViewById(R.id.edit_wfdd_gls);
        editMs = (EditText) mdialog.findViewById(R.id.edit_wfdd_ms);
        editLdmc = (EditText) mdialog.findViewById(R.id.edit_wfdd_ldmc);
        editGls.addTextChangedListener(new TextWatcherImpl() {

            @Override
            public void afterTextChanged(Editable s) {
                if (curWfdd == null)
                    return;
                editLdmc.setText(curWfdd.getLdmc() + s + "公里"
                        + editMs.getText() + "米");
            }
        });

        editMs.addTextChangedListener(new TextWatcherImpl() {

            @Override
            public void afterTextChanged(Editable s) {
                if (curWfdd == null)
                    return;
                editLdmc.setText(curWfdd.getLdmc() + editGls.getText() + "公里"
                        + s + "米");
            }
        });
    }

    MaterialDialog.SingleButtonCallback sc = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            int index = getSelectItem();
            if (index < 0)
                return;
            WfddBean curWfdd = (WfddBean) wfddList.get(index).getBean();
            String gls = editGls.getText().toString();
            String ms = editMs.getText().toString();
            String ldmc = editLdmc.getText().toString();
            if (TextUtils.isEmpty(gls)
                    || !TextUtils.isDigitsOnly(gls)
                    || gls.length() > 4) {
                Toast.makeText(self, "公里数不能为空或不是数字",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(ms)
                    || !TextUtils.isDigitsOnly(ms)
                    || ms.length() > 3) {
                Toast.makeText(self, "米数不能为空或不是数字",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(ldmc)) {
                Toast.makeText(self, "路段名称不能为空", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            gls = GlobalMethod.paddingZero(gls, 4);
            ms = GlobalMethod.paddingZero(ms, 3);
            FavorWfdd favor = new FavorWfdd();
            favor.setDldm(curWfdd.getDldm());
            favor.setFavorLdmc(ldmc);
            favor.setLddm(gls);
            favor.setMs(ms);
            favor.setSysLdmc(curWfdd.getLdmc()
                    + Integer.valueOf(gls) + "公里"
                    + Integer.valueOf(ms) + "米");
            favor.setXzqh(curWfdd.getXzqh());
            boolean isWfddOk = WfddDao.checkGsdGls(favor.getXzqh(), favor.getDldm(), favor.getLddm(), self);
            if (!isWfddOk) {
                GlobalMethod.toast(self, "公里数不在单位辖区内");
                return;
            }
            if (isAddFavor) {
                int re = WfddDao.addFavorWfdd(favor, GlobalMethod.getBoxStore(self));
                Toast.makeText(self, re > 0 ? "自选路段加入成功" : "存在重复自选地点名称", Toast.LENGTH_LONG)
                        .show();
            } else {
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putString(
                        "wfddDm",
                        favor.getXzqh() + favor.getDldm()
                                + favor.getLddm() + favor.getMs());
                b.putString("wfddMc", favor.getFavorLdmc());
                i.putExtras(b);
                self.setResult(Activity.RESULT_OK, i);
                self.finish();
            }
        }
    };

    private View.OnClickListener ck = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnOk || v == btnAddFavor) {
                isAddFavor = (v == btnAddFavor);
                int index = getSelectItem();
                if (index < 0) {
                    Toast.makeText(self, "请选择一条记录", Toast.LENGTH_LONG).show();
                    return;
                }
                curWfdd = (WfddBean) wfddList.get(index).getBean();
                if (curWfdd.isGsd()) {
                    if (mdialog == null)
                        createMDialog();
                    editGls.setText("0");
                    editMs.setText("0");
                    editLdmc.setText(curWfdd.getLdmc() + "0公里0米");
                    mdialog.show();
                } else {
                    showDialog(curWfdd);
                }
            } else if (v == btnFind) {
                wfddList.clear();
                List<WfddBean> temp = new ArrayList<>();

                //boolean isFilter = !TextUtils.isEmpty(filter);
                String xzqh = GlobalMethod.getKeyFromSpinnerSelected(spinXzqh,
                        GlobalConstant.KEY);
                List<FrmRoadItem> roads = WfddDao.getRoadItemsByXzqh(xzqh,
                        GlobalMethod.getBoxStore(self));
                for (FrmRoadItem road : roads) {
                    if (TextUtils.isEmpty(road.getDldm()) || TextUtils.isEmpty(road.getDlmc()))
                        continue;
                    if (WfddDao.isGsd(road.getDldm())) {
                        //if (!isFilter || road.getValue().indexOf(filter) > -1)
                        temp.add(new WfddBean(xzqh, road.getDldm(), "", "", road.getDlmc(), true));
                    } else {
                        List<FrmRoadSeg> segs = WfddDao.getRoadSegByRoad(
                                road.getDldm(), xzqh, GlobalMethod.getBoxStore(self));
                        if (segs != null && !segs.isEmpty()) {
                            for (FrmRoadSeg seg : segs) {
                                temp.add(new WfddBean(xzqh, road.getDldm(), seg.getLddm()
                                        , "", road.getDlmc() + seg.getLdmc(), false));
                            }
                        }
                    }
                }
                String filter = editText.getText().toString();
                boolean isFilter = !TextUtils.isEmpty(filter);
                for (WfddBean w : temp) {
                    if (!w.isGsd() && TextUtils.isEmpty(w.getLddm()))
                        continue;
                    SelectObjectBean ob = new SelectObjectBean(w);
                    ob.setText(getWfddText(w));
                    if (isFilter) {
                        String[] fs = filter.trim().split(" ");
                        String ldmc = w.getLdmc();
                        if (fs.length == 1 && ldmc.indexOf(fs[0].trim()) > -1) {
                            if (ldmc.indexOf(fs[0].trim()) > -1) {
                                wfddList.add(ob);
                            }
                        } else if (fs.length > 1) {
                            int findCount = 0;
                            for (String f : fs) {
                                if (ldmc.indexOf(f.trim()) > -1) {
                                    findCount++;
                                }
                            }
                            if (findCount == fs.length)
                                wfddList.add(ob);
                        }
                    } else {
                        wfddList.add(ob);
                    }
                }
                referView();
            }
        }
    };

    private void showDialog(final WfddBean wfdd) {
        new MaterialDialog.Builder(self)
                .content("请输入自定义地点：")
                .positiveText("确定")
                .input("请输入自定义地点名称", wfdd.getLdmc(), false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //Log.e("dialog", dialog.getInputEditText().getText().toString());
                        String ldmc = dialog.getInputEditText().getText().toString();
                        if (TextUtils.isEmpty(ldmc)) {
                            GlobalMethod.toast(self, "路段名称不能为空");
                            return;
                        }
                        if (TextUtils.isEmpty(wfdd.getDldm())) {
                            GlobalMethod.toast(self, "道路代码不能为空");
                            return;
                        }
                        if (TextUtils.isEmpty(wfdd.getLddm())) {
                            GlobalMethod.toast(self, "路段代码不能为空");
                            return;
                        }
                        FavorWfdd favor = new FavorWfdd();
                        favor.setDldm(wfdd.getDldm());
                        favor.setFavorLdmc(ldmc);
                        favor.setLddm(wfdd.getLddm());
                        favor.setMs("000");
                        favor.setSysLdmc(wfdd.getLdmc());
                        favor.setXzqh(wfdd.getXzqh());
                        if (isAddFavor) {
                            int re = WfddDao.addFavorWfdd(favor, GlobalMethod.getBoxStore(self));
                            GlobalMethod.toast(self, re > 0 ? "自选路段加入成功" : "存在重复自选地点名称");
                        } else {
                            Intent i = new Intent();
                            Bundle b = new Bundle();
                            b.putString(
                                    "wfddDm",
                                    favor.getXzqh() + favor.getDldm()
                                            + favor.getLddm() + favor.getMs());
                            b.putString("wfddMc", favor.getFavorLdmc());
                            i.putExtras(b);
                            self.setResult(Activity.RESULT_OK, i);
                            self.finish();
                        }
                    }
                }).show();
    }

    private void referView() {
        //      wfddList.clear();
        //       int row = 1;
        //Log.e(TAG, "违法地点列表：" + wfddList.size());
//        for (SelectObjectBean wfddSel : wfddList) {
//            //Log.e(TAG, "列表中的道路代码：" + wfdd.getDldm());
//            //Log.e(TAG, wfdd.toString());
//            WfddSelBean wfdd = (WfddSelBean) wfddSel;
//            if (!wfdd.isGsd() && TextUtils.isEmpty(wfdd.getLddm()))
//                continue;
//            wfddLineList.add(new TwoLineSelectBean(row + ". " + wfdd.getLdmc()
//                    + "--" + (wfdd.isGsd() ? "国省道" : "普通道路"), wfdd.getDldm()));
//            row++;
//        }
        SelectCommAdapter ard = (SelectCommAdapter) this.getListAdapter();
        if (ard == null) {
            ard = new SelectCommAdapter(self, R.layout.one_row_select_item,
                    wfddList);
            this.getListView().setAdapter(ard);
        }
        ard.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        for (int i = 0; i < wfddList.size(); i++) {
            SelectObjectBean c = wfddList.get(i);
            if (i == position)
                c.setSel(!c.isSel());
            else
                c.setSel(false);
        }
        SelectCommAdapter ad = (SelectCommAdapter) this.getListView()
                .getAdapter();
        ad.notifyDataSetChanged();
    }

    private int getSelectItem() {
        for (int i = 0; i < wfddList.size(); i++) {
            if (wfddList.get(i).isSel())
                return i;
        }
        return -1;
    }

    private String getWfddText(WfddBean wfdd) {
        return wfdd.getLdmc()
                + "--" + (wfdd.isGsd() ? "国省道" : "普通道路");

    }

}
