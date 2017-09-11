package com.jwt.activity;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by lixd on 14-4-3.
 */
public class ActionBarListActivity extends AppCompatActivity {

    public ListView getListView() {
        return (ListView) findViewById(android.R.id.list);
    }

    public void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView mEmptyTextView = (TextView) findViewById(android.R.id.empty);
        if (mEmptyTextView == null)
            return;
        Log.e("ActionBarListActivity", "mEmptyTextView not null");
        ListView lv = getListView();
        if (lv == null){
            mEmptyTextView.setVisibility( View.VISIBLE);
            return;
        }
        Log.e("ActionBarListActivity", "lv not null");
        ListAdapter la = lv.getAdapter();
        if (la == null) {
            mEmptyTextView.setVisibility( View.VISIBLE);
            return;
        }
        Log.e("ActionBarListActivity", "la not null");
        int count = la.getCount();
        Log.e("ActionBarListActivity", "listAdapter count: " + count);
        mEmptyTextView.setVisibility(count > 0 ? View.INVISIBLE : View.VISIBLE);
    }
}
