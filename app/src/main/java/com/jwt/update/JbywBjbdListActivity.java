package com.jwt.update;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jwt.adapter.BjbdAdapter;
import com.jwt.event.MenuPosEvent;
import com.jwt.pojo.Bjbd;
import com.jwt.pojo.Bjbd_;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.ParserJson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import me.leolin.shortcutbadger.ShortcutBadger;

public class JbywBjbdListActivity extends AppCompatActivity {

    private Button btnSendMes;
    private EditText editMessage;
    private BjbdAdapter adapter;
    private RecyclerView mRecycleView;
    private Box<Bjbd> bjbdBox;

    private List<Bjbd> bjbdList = new ArrayList<Bjbd>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jbyw_bjbd_list);
        setTitle("报警信息");
        EventBus.getDefault().register(this);
        int position = getIntent().getIntExtra("position",0);
        EventBus.getDefault().post(new MenuPosEvent(position));
        btnSendMes = (Button) findViewById(R.id.button_send);
        editMessage = (EditText) findViewById(R.id.edit_message);
        btnSendMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mes = editMessage.getText().toString();
                if (!TextUtils.isEmpty(mes)) {
                    editMessage.setText("");
                    Bjbd bjbd = new Bjbd();
                    bjbd.setType("0");
                    bjbd.setBjyy(mes);
                    bjbd.setDdsj(sdf.format(new Date()));
                    String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
                    bjbd.setSender(jybh);
                    String message = ParserJson.objToJson(bjbd).toString();
                    Log.e("Send mes", message);
                    MainReferService.publish(message);
                }
            }
        });
        bjbdBox = ((App) getApplication()).getBoxStore().boxFor(Bjbd.class);
        long count = bjbdBox.count();
        List<Bjbd> bjlist = bjbdBox.query().notEqual(Bjbd_.ydbj, 1).build().find();
        for (Bjbd bj : bjlist) {
            bj.setYdbj(1);
        }
        bjbdBox.put(bjlist);
        bjbdList = bjbdBox.query().build().find(count - 20 > 0 ? (count - 20) : 0, 20);
        adapter = new BjbdAdapter(clickListener);
        mRecycleView = (RecyclerView) findViewById(R.id.rec_view_bjbd);
        //noinspection ConstantConditions
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(adapter);
        adapter.setBjbds(bjbdList);
        mRecycleView.smoothScrollToPosition(adapter.getItemCount() - 1);
        ShortcutBadger.removeCount(this);
        GlobalData.isBadger = false;
    }

    public BjbdAdapter.BjbdClickListener clickListener = new BjbdAdapter.BjbdClickListener() {

        @Override
        public void onBjbdItemClick(int position) {
            Bjbd bjbd = adapter.getBjbd(position);
            bjbd.setYdbj(1);
            bjbdBox.put(bjbd);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // 主线程调用
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBusMain(Bjbd bjbd) {
        Log.i("TAG", "MAIN:" + bjbd.getBjyy() + " Thread=" + Thread.currentThread().getId());
        bjbdList.add(bjbd);
        bjbdBox.put(bjbd);
        adapter.notifyDataSetChanged();
        mRecycleView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
}
