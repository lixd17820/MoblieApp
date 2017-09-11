package com.jwt.adapter;

import java.util.List;

import com.jwt.pojo.THmb;
import com.jwt.update.R;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WsglListAdapter extends ArrayAdapter<THmb> {

    Activity context;

    public WsglListAdapter(Activity _context, List<THmb> objects) {
        super(_context, R.layout.wsgl_list_row, objects);
        this.context = _context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.wsgl_list_row, null);
        }
        THmb hm = getItem(position);
        long sysl = Long.valueOf(hm.getJshm().substring(6))
                - Long.valueOf(hm.getDqhm().substring(6)) + 1;
        TextView wslb = ((TextView) row.findViewById(R.id.text_wslb));
        if (TextUtils.equals(hm.getHdzl(), "1"))
            wslb.setText("简易处罚决定书");
        if (TextUtils.equals(hm.getHdzl(), "3"))
            wslb.setText("强制措施凭证");
        if (TextUtils.equals(hm.getHdzl(), "9"))
            wslb.setText("简易事故处理决定书");
        ((TextView) row.findViewById(R.id.Text_wssl)).setText("文书数量："
                + String.valueOf(sysl));
        ((TextView) row.findViewById(R.id.Text_ksbh)).setText("领取日期：");
        ((TextView) row.findViewById(R.id.Text_dqbh)).setText("当前编号："
                + hm.getDqhm());
        ((TextView) row.findViewById(R.id.Text_jsbh)).setText("结束编号："
                + hm.getJshm());
        return row;

    }
}
