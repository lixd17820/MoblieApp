package com.jwt.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jwt.activity.ActionBarListActivity;
import com.jwt.printer.JdsPrintBean;
import com.jwt.main.R;
import com.jwt.utils.GlobalMethod;

public class JdsPreviewActivity extends ActionBarListActivity {

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = "打印预览";
        String lt = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(lt))
            title = lt;
        setTitle(title);
        setContentView(R.layout.list_pic_backgournd);
        ArrayList<JdsPrintBean> jds = (ArrayList<JdsPrintBean>) getIntent()
                .getSerializableExtra("jds");
        if (jds == null)
            jds = new ArrayList<>();
        String data = getIntent().getStringExtra("data");
        if ((jds.isEmpty()) && !TextUtils.isEmpty(data)) {
            if ("log".equals(data)) {
                //读取当天的日志
                List<String> list = GlobalMethod.logReadInfo();
                Collections.reverse(list);
                for (String s : list) {
                    jds.add(new JdsPrintBean(s));
                }
            }
        }
//		Map<String, String> map = ViolationDAO.getMjgrxx(getContentResolver());
//		ArrayList<TwoLineSelectBean> list = changeMapIntoZh(map);
        OneLineWhiteAdapter ard = new OneLineWhiteAdapter(this,
                R.layout.one_row_white_item, jds);
        getListView().setAdapter(ard);
    }

//	private ArrayList<TwoLineSelectBean> changeMapIntoZh(Map<String, String> map) {
//		ArrayList<TwoLineSelectBean> list = new ArrayList<TwoLineSelectBean>();
//		Set<Entry<String, String>> set = map.entrySet();
//		for (Entry<String, String> entry : set) {
//			String zh = "";
//			if (TextUtils.equals(entry.getKey(), GlobalConstant.YHBH))
//				zh = "民警警号";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.YBMBH))
//				zh = "值勤机关代码";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.XM))
//				zh = "民警姓名";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.FYJG))
//				zh = "行政复议机关";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.SSJG))
//				zh = "行政诉讼机关";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.BMMC))
//				zh = "值勤机关名称";
//			else if (TextUtils.equals(entry.getKey(), GlobalConstant.JKYH))
//				zh = "缴款银行";
//			else if (TextUtils.equals(entry.getKey(),
//					GlobalConstant.GRXX_PRINTER_NAME))
//				zh = "蓝牙打印机";
//			else if (TextUtils.equals(entry.getKey(),
//					GlobalConstant.GRXX_PRINTER_ADDRESS))
//				zh = "打印机地址";
//			if (!TextUtils.isEmpty(zh))
//				list.add(new TwoLineSelectBean(zh, entry.getValue(), true));
//		}
//		return list;
//	}

    public class OneLineWhiteAdapter extends ArrayAdapter<JdsPrintBean> {

        private int resourceId;

        public OneLineWhiteAdapter(Context context, int textViewResourceId,
                                   List<JdsPrintBean> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = convertView;
            if (row == null) {
                row = inflater.inflate(resourceId, parent, false);
            }
            JdsPrintBean jds = getItem(position);
            if (jds != null) {
                TextView tv = (TextView) row.findViewById(R.id.text1);
                tv.setText(jds.getContent());
                tv.setGravity(jds.getAlignMode());
            }
            return row;
        }

    }

}
