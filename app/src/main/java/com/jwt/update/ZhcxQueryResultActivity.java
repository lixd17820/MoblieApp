package com.jwt.update;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.globalquery.Glcx;
import com.jwt.globalquery.GlobalQueryResult;
import com.jwt.globalquery.ZhcxHandler;
import com.jwt.globalquery.ZhcxThread;
import com.jwt.utils.GlobalMethod;

import org.w3c.dom.Text;


public class ZhcxQueryResultActivity extends AppCompatActivity {

    private static final int MENU_COPY_VALUE = 0;
    private ExpandableListAdapter mAdapter;
    private GlobalQueryResult zhcx;
    private Glcx[] glcxs;
    private Context self;
    private int selecedGroup = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhcx_query_result);
        self = this;
        zhcx = (GlobalQueryResult) getIntent().getSerializableExtra("zhcx");
        if (zhcx != null) {
            String[][] contents = zhcx.getContents();
            setTitle(zhcx.getCxms() + " 共查询到" + contents.length + "个结果");
            String[] comment = zhcx.getComments();
            glcxs = zhcx.getGlcxs();
            String[] gr = new String[contents.length];
            String[][] childs = new String[contents.length][];
            for (int i = 0; i < gr.length; i++) {
                String[] temp = new String[contents[i].length];
                for (int j = 0; j < temp.length; j++) {
                    temp[j] = comment[j] + "：" + contents[i][j];
                }
                gr[i] = temp[0] + "\n" + temp[1];
                childs[i] = temp;
            }
            mAdapter = new MyExpandableListAdapter(gr, childs);
            // mAdapter = new MyExpandableListAdapter(g, c);
            getExpandableListView().setAdapter(mAdapter);
            if (zhcx != null && !TextUtils.isEmpty(zhcx.getBdxx())) {
                GlobalMethod.showDialog("系统比对信息", zhcx.getBdxx(), "确定", self);
            }
        }
        // register context menu, when long click the item, it will show a
        // dialog
        registerForContextMenu(getExpandableListView());

        getExpandableListView().setOnGroupExpandListener(
                new OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int sg) {
                        if (selecedGroup > -1)
                            getExpandableListView().collapseGroup(selecedGroup);
                        selecedGroup = sg;
                    }
                });

        getExpandableListView().setOnGroupCollapseListener(
                new OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int sg) {
                        selecedGroup = -1;
                    }
                });
    }

    private ExpandableListView getExpandableListView() {
        return (ExpandableListView) findViewById(R.id.exp_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (glcxs != null && glcxs.length > 0) {
            for (int i = 0; i < glcxs.length; i++) {
                menu.add(0, i, Menu.NONE, glcxs[i].getGlcxms());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (selecedGroup > -1) {
            if (id > -1) {
                String cxid = glcxs[id].getGlcxid();
                String[] destFields = glcxs[id].getDestField().split(",");
                String[] sourFields = glcxs[id].getSourField().split(",");
                String where = "";
                for (int i = 0; i < sourFields.length; i++) {
                    where += " AND " + destFields[i] + "='"
                            + getContentsValue(sourFields[i]) + "'";
                }
                if (!TextUtils.isEmpty(where)) {
                    where = where.substring(5);
                    new ZhcxThread(new ZhcxHandler(self)).doStart(self, cxid, where);
                    Log.e("where", where);
                }
                return true;
            }
        } else {
            GlobalMethod.showErrorDialog("请打开一个查询项", self);
        }
        return false;
    }

    private String getContentsValue(String sourField) {
        String[] names = zhcx.getNames();
        String[][] contents = zhcx.getContents();
        for (int i = 0; i < names.length; i++) {
            if (names[i].toUpperCase().equals(sourField.toUpperCase()))
                return contents[selecedGroup][i];
        }
        return "";
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v == getExpandableListView()) {
            ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
            int type = ExpandableListView
                    .getPackedPositionType(info.packedPosition);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                menu.setHeaderTitle("请选择");
                menu.add(Menu.NONE, MENU_COPY_VALUE, Menu.NONE, "复制内容");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == MENU_COPY_VALUE) {
            int type = ExpandableListView
                    .getPackedPositionType(info.packedPosition);
            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                int groupPos = ExpandableListView
                        .getPackedPositionGroup(info.packedPosition);
                int childPos = ExpandableListView
                        .getPackedPositionChild(info.packedPosition);
                if (groupPos > -1 && childPos > -1) {
                    String value = zhcx.getContents()[groupPos][childPos];
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(value);
                    Toast.makeText(self, value + " 已复制", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
        }
        // else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
        // int groupPos = ExpandableListView
        // .getPackedPositionGroup(info.packedPosition);
        // Toast.makeText(this, title + ": Group " + groupPos + " clicked",
        // Toast.LENGTH_SHORT).show();
        // return true;
        // }

        return false;
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        private String[] groups;
        private String[][] children;

        // Sample data set. children[i] contains the children (String[]) for
        // groups[i].
        public MyExpandableListAdapter(String[] groups, String[][] children) {
            this.groups = groups;
            this.children = children;
        }

        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        public TextView getTextView() {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(ZhcxQueryResultActivity.this);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setPadding(36, 10, 10, 10);
            //textView.setTextSize(20);
            return textView;
        }

//        public TextView getGrView() {
//            // Layout parameters for the ExpandableListView
//            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
//                    ViewGroup.LayoutParams.FILL_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//
//            TextView textView = new TextView(ZhcxQueryResultActivity.this);
//            textView.setLayoutParams(lp);
//            // Center the text vertically
//            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//            // Set the text starting position
//            textView.setPadding(50, 10, 0, 10);
//            textView.setTextSize(18);
//            return textView;
//        }
//
//        public TextView getChView() {
//            // Layout parameters for the ExpandableListView
//            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
//                    ViewGroup.LayoutParams.FILL_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//
//            TextView textView = new TextView(ZhcxQueryResultActivity.this);
//            textView.setLayoutParams(lp);
//            // Center the text vertically
//            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//            // Set the text starting position
//            textView.setPadding(36, 5, 0, 5);
//            textView.setTextSize(16);
//            return textView;
//        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = getTextView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        //设置每个组选项的外观
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view = ZhcxQueryResultActivity.this.getLayoutInflater().inflate(R.layout.one_row_img_item, parent, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image1);
            TextView textView = (TextView) view.findViewById(R.id.text1);
//            LinearLayout ll = new LinearLayout(ZhcxQueryResultActivity.this);
//            ll.setOrientation(LinearLayout.HORIZONTAL);
//            ImageView imageView = new ImageView(ZhcxQueryResultActivity.this);
            if (isExpanded) {
                //判断选项组状态是否是打开
                imageView.setImageResource(R.drawable.ic_arrow_right);
            } else {
                imageView.setImageResource(R.drawable.ic_arrow_down);
            }
//            ll.addView(imageView);
//            TextView textView = getTextView();
            textView.setText(getGroup(groupPosition).toString());
//            ll.addView(textView);
            return view;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}
