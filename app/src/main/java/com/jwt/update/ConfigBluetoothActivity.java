package com.jwt.update;

import java.util.ArrayList;
import java.util.Set;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwt.activity.ActionBarListActivity;
import com.jwt.bean.BluetoothBean;
import com.jwt.utils.ClsUtils;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;

public class ConfigBluetoothActivity extends ActionBarListActivity {

    private BluetoothAdapter bluetoothAdapter;
    // private Button openBl;
    // private TextView blState;
    private TextView printState, cardState;
    private Button btnSearch, btnBound, btnUnBound, btnSavePrinter,
            btnSaveCard;
    private ArrayAdapter<String> jdsAdapter;
    private ArrayList<BluetoothBean> devsList;
    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.config_bluetooth);
        setTitle("配置蓝牙设备");
        // blState = (TextView) findViewById(R.id.Text_Bluetooth);
        printState = (TextView) findViewById(R.id.tv_blue_printer);
        cardState = (TextView) findViewById(R.id.tv_blue_card);
        // 打开按扭
        // openBl = (Button) findViewById(R.id.But_open_bluetooth);

        // 查找设备按扭
        btnSearch = (Button) findViewById(R.id.btn_bluetooth_search);
        btnBound = (Button) findViewById(R.id.btn_bluetooth_bound);
        btnUnBound = (Button) findViewById(R.id.btn_bluetooth_un_bound);
        btnSavePrinter = (Button) findViewById(R.id.btn_bluetooth_printer);
        btnSaveCard = (Button) findViewById(R.id.btn_bluetooth_card);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)// 开蓝牙
            bluetoothAdapter.enable();
        String defPrinterName = GlobalMethod.getSavedInfo(this, GlobalConstant.GRXX_PRINTER_NAME);
        String defCardReader = GlobalMethod.getSavedInfo(this, GlobalConstant.GRXX_CARD_READER_NAME);
        printState.setText(TextUtils.isEmpty(defPrinterName) ? "无打印机"
                : defPrinterName);
        cardState.setText(TextUtils.isEmpty(defCardReader) ? "无身份证读卡器"
                : defCardReader);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        jdsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                new ArrayList<String>());
        getListView().setAdapter(jdsAdapter);

        // 初始化设备数组
        devsList = new ArrayList<BluetoothBean>();
        changeListView();
        // openBl.setOnClickListener(butClick);
        btnSearch.setOnClickListener(butClick);
        btnBound.setOnClickListener(butClick);
        btnUnBound.setOnClickListener(butClick);
        btnSavePrinter.setOnClickListener(butClick);
        btnSaveCard.setOnClickListener(butClick);

        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, intent); // Don't forget to unregister
        // during onDestroy
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    View.OnClickListener butClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 设置默认蓝牙打印机,从列表框中选择
            if (v == btnSearch) {
                devsList.clear();
                jdsAdapter.clear();
                // 先加已配对的,再查未配对的
                Object[] lstDevice = bluetoothAdapter.getBondedDevices()
                        .toArray();
                for (int i = 0; i < lstDevice.length; i++) {
                    BluetoothDevice device = (BluetoothDevice) lstDevice[i];
                    jdsAdapter.add(device.getName() + ":已配对");
                    devsList.add(new BluetoothBean(device.getName(), device
                            .getAddress(), device.getBondState()));
                }
                jdsAdapter.notifyDataSetChanged();
                if (bluetoothAdapter.startDiscovery()) {
                    Toast.makeText(self, "正在查找蓝牙设备", Toast.LENGTH_LONG).show();
                }
            } else {
                int pos = getListView().getCheckedItemPosition();
                if (pos < 0) {
                    GlobalMethod.showErrorDialog("请选择一个设备进行操作", self);
                    return;
                }
                String dpName = devsList.get(pos).getName();
                String address = devsList.get(pos).getAddress();
                int status = devsList.get(pos).getStatus();
                if (v == btnBound) {
                    // 配对蓝牙设备
                    bluetoothAdapter.cancelDiscovery();
                    if (status == BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(self, "该设备已配对,无需重复配对", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    BluetoothDevice device = bluetoothAdapter
                            .getRemoteDevice(address);
                    Toast.makeText(self, dpName + "由未配对转为已配对",
                            Toast.LENGTH_LONG).show();
                    try {
                        ClsUtils.createBond(device.getClass(), device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (v == btnUnBound) {
                    // 解除配对
                    BluetoothDevice device = bluetoothAdapter
                            .getRemoteDevice(address);

                    try {
                        boolean a = ClsUtils.removeBond(device.getClass(),
                                device);
                        if (a) {
                            devsList.clear();
                            jdsAdapter.clear();
                            jdsAdapter.notifyDataSetChanged();
                            Toast.makeText(self, dpName + "已解除配对",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (v == btnSavePrinter) {
                    // 保存蓝牙打印机
                    if (status != BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(ConfigBluetoothActivity.this,
                                "设备还没有配对,请先配对!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    setupPrinter(dpName, address);
                    printState.setText(dpName);
                    Toast.makeText(ConfigBluetoothActivity.this, "打印机设置成功!",
                            Toast.LENGTH_LONG).show();
                } else if (v == btnSaveCard) {
                    // 保存蓝牙读卡器
                    if (status != BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(ConfigBluetoothActivity.this,
                                "设备还没有配对,请先配对!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    saveCardReader(dpName, address);
                    cardState.setText(dpName);
                    Toast.makeText(ConfigBluetoothActivity.this, "身份证读卡器设置成功!",
                            Toast.LENGTH_LONG).show();
                }

            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // String action = intent.getAction();
            // // When discovery finds a device
            // if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // // Get the BluetoothDevice object from the Intent
            // BluetoothDevice device = intent
            // .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // // Add the name and address to an array adapter to show in a
            // // ListView
            // devsList.add(new
            // KeyValueBean(device.getAddress(),device.getName()));
            // jdsAdapter.add(device.getName());
            // }
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Log.e(keyName, String.valueOf(b.get(keyName)));
            }
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // 搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (device.getBondState() == BluetoothDevice.BOND_NONE
                        && !TextUtils.equals(device.getName(), "null")) {
                    String str = device.getName() + ":未配对";
                    jdsAdapter.add(str); // 获取设备名称和mac地址
                    jdsAdapter.notifyDataSetChanged();
                    devsList.add(new BluetoothBean(device.getName(), device
                            .getAddress(), device.getBondState()));
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int preState = intent.getIntExtra(
                        BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                        BluetoothDevice.ERROR);
                int state = intent
                        .getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                                BluetoothDevice.ERROR);
                if (preState == BluetoothDevice.BOND_BONDING
                        && state == BluetoothDevice.BOND_BONDED) {
                    // 设备已配对
                    Toast.makeText(ConfigBluetoothActivity.this, "设备已配对!",
                            Toast.LENGTH_LONG).show();
                    jdsAdapter.clear();
                    for (int j = 0; j < devsList.size(); j++) {
                        if (TextUtils.equals(device.getAddress(),
                                devsList.get(j).getAddress())) {
                            devsList.get(j).setStatus(
                                    BluetoothDevice.BOND_BONDED);
                        }
                        String str = devsList.get(j).getName()
                                + (devsList.get(j).getStatus() == BluetoothDevice.BOND_BONDED ? ":已配对"
                                : ":未配对");
                        jdsAdapter.add(str); // 获取设备名称和mac地址
                    }
                    jdsAdapter.notifyDataSetChanged();
                } else if (state == BluetoothDevice.BOND_BONDING
                        && preState == BluetoothDevice.BOND_NONE) {
                    Toast.makeText(ConfigBluetoothActivity.this, "设备正在配对...",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    };

    // private final BroadcastReceiver changeReceiver = new BroadcastReceiver()
    // {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // Toast.makeText(self, "Bluetooth state is changed!",
    // Toast.LENGTH_LONG).show();
    // }
    // };

    // private void changeState() {
    // if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
    // blState.setText("蓝牙状态：未打开");
    // openBl.setText("打开蓝牙功能");
    // } else {
    // blState.setText("蓝牙状态：已打开");
    // openBl.setText("关闭蓝牙功能");
    // }
    // }

    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // super.onActivityResult(requestCode, resultCode, data);
    // switch (requestCode) {
    // case OPENBLUETOOTH: {
    // changeState();
    // changeListView();
    // }
    // break;
    // case 1:
    // changeListView();
    // break;
    // default:
    // break;
    // }
    // }

    private void changeListView() {
        Set<BluetoothDevice> bds = bluetoothAdapter.getBondedDevices();
        jdsAdapter.clear();
        devsList.clear();
        for (BluetoothDevice bd : bds) {
            devsList.add(new BluetoothBean(bd.getName(), bd.getAddress(), bd
                    .getBondState()));
            jdsAdapter.add(bd.getName() + ":已配对");
        }
        jdsAdapter.notifyDataSetChanged();
    }

    private int saveCardReader(String name, String address) {
        GlobalMethod.putSavedInfo(this, GlobalConstant.GRXX_CARD_READER_NAME, name);
        GlobalMethod.putSavedInfo(this, GlobalConstant.GRXX_CARD_READER_ADDRESS, address);
        return 1;
    }

    private int setupPrinter(String name, String address) {
        GlobalMethod.putSavedInfo(this, GlobalConstant.GRXX_PRINTER_NAME, name);
        GlobalMethod.putSavedInfo(this, GlobalConstant.GRXX_PRINTER_ADDRESS, address);
        return 1;
    }

}
