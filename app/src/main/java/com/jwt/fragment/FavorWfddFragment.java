package com.jwt.fragment;

import java.util.ArrayList;
import java.util.List;


import com.jwt.adapter.OneLineSelectAdapter;
import com.jwt.bean.TwoLineSelectBean;
import com.jwt.dao.WfddDao;
import com.jwt.pojo.FavorWfdd;
import com.jwt.update.App;
import com.jwt.update.R;
import com.jwt.utils.GlobalMethod;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import io.objectbox.Box;

public class FavorWfddFragment extends ListFragment {

    /**
     * 用户自定义执勤地点数组
     */
    List<FavorWfdd> favorWfddList;
    private Activity self;
    private Button buttonDetail, favorOKBt, delFavorButton;
    private OneLineSelectAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater in, ViewGroup c, Bundle si) {
        return in.inflate(R.layout.wfdd_fever_list, c, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        self = getActivity();
        delFavorButton = (Button) self.findViewById(R.id.delButton);
        buttonDetail = (Button) self.findViewById(R.id.detailButton);
        // 自选地点页面标签"确定"按扭动作
        favorOKBt = (Button) self.findViewById(R.id.favorOKButton);
        adapter = new OneLineSelectAdapter(self, R.layout.one_row_select_item,
                new ArrayList<TwoLineSelectBean>());
        this.getListView().setAdapter(adapter);
        changeFavorWfddList();
        buttonDetail.setOnClickListener(clickListener);
        favorOKBt.setOnClickListener(clickListener);
        delFavorButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int postion = getSelectPosition();
            if (postion < 0) {
                Toast.makeText(self, "请选择一条违法地点", Toast.LENGTH_LONG).show();
                return;
            }
            FavorWfdd wfdd = favorWfddList.get(postion);
            if (v == buttonDetail) {
                String content = "地点名称：" + wfdd.getFavorLdmc() + "\n";
                content += "系统名称：" + wfdd.getSysLdmc() + "\n";
                content += "行政区划：" + wfdd.getXzqh() + "\n";
                content += "道路名称：" + wfdd.getDldm() + "\n";
                content += "路段(公里)：" + wfdd.getLddm() + "\n";
                content += "路段(米)：" + wfdd.getMs();
                GlobalMethod.showDialog("自定义地点详细信息", content, "返回", self);
            } else if (v == favorOKBt) {
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putString("wfddDm", wfdd.getXzqh() + wfdd.getDldm() + wfdd.getLddm() + wfdd.getMs());
                b.putString("wfddMc", wfdd.getFavorLdmc());
                i.putExtras(b);
                self.setResult(Activity.RESULT_OK, i);
                self.finish();
            } else if (v == delFavorButton) {
                WfddDao.delFavorWfddById(wfdd.getId(), GlobalMethod.getBoxStore(self));
                changeFavorWfddList();
            }
        }
    };

    private void changeFavorWfddList() {
        favorWfddList = WfddDao.getAllFavorWfdd(GlobalMethod.getBoxStore(self));
        List<TwoLineSelectBean> oneLineList = adapter.getList();
        if (favorWfddList != null) {
            for (FavorWfdd fwfdd : favorWfddList) {
                oneLineList.add(new TwoLineSelectBean(fwfdd.getFavorLdmc(),
                        fwfdd.getDldm()));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        List<TwoLineSelectBean> oneLineList = adapter.getList();
        for (int i = 0; i < oneLineList.size(); i++) {
            TwoLineSelectBean c = oneLineList.get(i);
            if (i == position)
                c.setSelect(!c.isSelect());
            else
                c.setSelect(false);
        }
        adapter.notifyDataSetChanged();
    }


    private int getSelectPosition() {
        List<TwoLineSelectBean> oneLineList = adapter.getList();
        for (int i = 0; i < oneLineList.size(); i++) {
            if (oneLineList.get(i).isSelect())
                return i;
        }
        return -1;
    }

}
