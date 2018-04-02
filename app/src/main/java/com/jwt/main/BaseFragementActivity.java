package com.jwt.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jwt.fragment.JtssLightFragement;

import org.greenrobot.eventbus.EventBus;

public class BaseFragementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_fragement);
        String data = getIntent().getStringExtra("data");
        String title = getIntent().getStringExtra("title");
        Log.e("BaseFragementActivity","data: " + data);
        setTitle(title);
        if ("1".equals(data))
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, JtssLightFragement.newInstance())
                    .commit();
    }

}
