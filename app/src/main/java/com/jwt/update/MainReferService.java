package com.jwt.update;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.jwt.pojo.Bjbd;
import com.jwt.pojo.Bjbd_;
import com.jwt.pojo.FrmCode;
import com.jwt.thread.LogoutJwtNewThread;
import com.jwt.utils.ConnCata;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainReferService extends Service {

    public static final String TAG = MainReferService.class.getSimpleName();

    public static final int NOT_ID = 1;
    public static final int JQTB_DOWN_NOTICE_ID = 9876;
    public static final String SERVER_BROADCAST = "com.ntga.jwt.main.server";
    public static final String JQTB_DOWNLOAD_BROADCAST = "com.ntga.jwt.main.jqtb";

    private boolean isGpsComeIn = false;
    private LocationManager locm;
    public static Location location = null;
    private long preLocationTime = 0;
    private NotificationManager noticeManager;
    private Notification notification, jqtbDownNotification;

    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static ConnCata serverConnCatalog;

    //Mqtt client
    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    //
    private Box<Bjbd> bjbdBox;

    @Override
    public IBinder onBind(Intent arg0) {
        return serviceBinder;
    }

    public class DownLoadServiceBinder extends Binder {
        public MainReferService getService() {
            return MainReferService.this;
        }
    }

    private Binder serviceBinder = new DownLoadServiceBinder();

    /**
     * 登出系统
     */
    public void logoutJwt() {
        GlobalData.isBadger = true;
        LogoutJwtNewThread logout = new LogoutJwtNewThread(logoutHandler);
        logout.doStart();
    }


    private Handler logoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int log = msg.what;
            Toast.makeText(MainReferService.this,
                    log == 0 ? "未能正常退出，可重新登录后退出" : "警务系统正常退出",
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "警务通后台服务进程启动");

        noticeManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 初始化全局数据
        App app = ((App) getApplication());
        if (!GlobalData.isInitLoadData) {
            Box<FrmCode> box = app.getBoxStore().boxFor(FrmCode.class);
            GlobalData.initGlobalData(app.getBoxStore());
        }
        bjbdBox = app.getBoxStore().boxFor(Bjbd.class);
        //测试网络
//        new Timer("testNetwrok").scheduleAtFixedRate(new TimerTask() {
//
//            @Override
//            public void run() {
//                testNetwork();
//            }
//        }, 10 * 60 * 1000, 60 * 60 * 1000);
        // 初如化位置管理
        //initLocation();
        //定期管理违法行为定时器
//        new Timer("uploadViolation").scheduleAtFixedRate(new TimerTask() {
//
//            @Override
//            public void run() {
//                backgroundRefreshMessage();
//            }
//        }, 20 * 60 * 1000, 60 * 60 * 1000);

        //启动报警
        initMqtt();
        checkMqtt();
    }

    /**
     * 验证报警是否打开，每五分钟验证一次
     */
    private void checkMqtt() {
        new Timer("check mqtt").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (client != null)
                    doClientConnection();
            }
        }, 60 * 1000, 5 * 60 * 1000);
    }


    public void initMqtt() {
        //
        String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
        RestfulDao dao = RestfulDaoFactory.getDao();
        String url = dao.getMqttUrl();
        Log.e("main url", url);
        client = new MqttAndroidClient(this, url, jybh);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);
        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(20);
        // 用户名
        conOpt.setUserName("admin");
        // 密码
        conOpt.setPassword("password".toCharArray());
        // last will message
        boolean doConnect = isConn();
        if (doConnect) {
            doClientConnection();
        }
    }

    private boolean isConn() {
        String message = "{\"terminal_uid\":\"" + 32061247 + "\"}";
        String topic = "info";
        Integer qos = 0;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            try {
                conOpt.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.e(TAG, "报警连接错误", e);
                iMqttActionListener.onFailure(null, e);
                return false;
            }
        }
        return true;
    }

    /**
     * 连接MQTT服务器
     */
    public void doClientConnection() {
        boolean isConn = client.isConnected();
        boolean isNetWork = isConnectIsNomarl();
        Log.e("Main server", (isConn ? "已连接" : "无连接") + "/" + (isNetWork ? "有网络" : "无网络"));
        if (!isConn && isNetWork && (GlobalSystemParam.isReciveBj || GlobalSystemParam.isReciveText)) {
            Log.e("Main server", "开始连接服务器");
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void closeMqttConn(){
        if(client.isConnected()){
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMqttConn(){
        return client.isConnected();
    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.e(TAG, "报警服务连接成功 ");
            subscribeTopic();
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
            Log.e(TAG, "连接失败 ");
        }
    };

    public void subscribeTopic() {
        try {
            //消除所有订阅
            String[] untopics = new String[GlobalData.glbmList.size() * 2];
            for (int i = 0; i < GlobalData.glbmList.size(); i++) {
                String glbm = GlobalData.glbmList.get(i).getKey();
                untopics[i * 2] = "clgj." + glbm;
                untopics[i * 2 + 1] = "info." + glbm;
            }
            //for (String un : untopics) {
            //    Log.e("MainReferServer", "取消订阅" + un);
            //}
            client.unsubscribe(untopics);
            // 订阅myTopic话题
            List<String> list = new ArrayList<>();
            if (GlobalSystemParam.isReciveBj) {
                String bjfw = GlobalMethod.getSavedInfo(this, "bjfw");
                if (!TextUtils.isEmpty(bjfw)) {
                    String[] array = bjfw.split(",");
                    if (array != null && array.length > 0) {
                        for (String s : array) {
                            list.add("clgj." + s);
                        }
                    }
                }
            }
            if (GlobalSystemParam.isReciveText) {
                String bmbh = GlobalData.grxx.get(GlobalConstant.YBMBH);
                if (!TextUtils.isEmpty(bmbh) && bmbh.length() > 6)
                    list.add("info." + bmbh.substring(0, 6));
            }
            if (list == null || list.isEmpty())
                return;
            //Log.e("MainReferServer", list.size() + "个订阅");
            String[] topics = new String[list.size()];
            int[] ops = new int[topics.length];
            for (int i = 0; i < topics.length; i++) {
                topics[i] = list.get(i);
                ops[i] = 1;
            }
            for (String un : topics) {
                Log.e("MainReferServer", "订阅" + un);
            }
            client.subscribe(topics, ops);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // MQTT监听并且接受消息
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String str1 = new String(message.getPayload());
            Log.e(TAG, "messageArrived:" + str1);
            Bjbd bjbd = null;
            try {
                bjbd = ParserJson.parseJsonToObj(str1, Bjbd.class);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("main server", e.getMessage());
            }
            if (bjbd != null) {
                bjbdBox.put(bjbd);
                EventBus.getDefault().post(bjbd);
            } else {
                Log.e("main server", "parse json error");
            }
            if (GlobalData.isBadger) {
                long count = bjbdBox.query().notEqual(Bjbd_.ydbj, 1).build().count();
                if (count > 99)
                    count = 99;
                ShortcutBadger.applyCount(MainReferService.this, (int) count);
            }
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained() + message.getId() + ";";
            Log.e(TAG, str2);
        }


        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            // 失去连接，重连
            //if (client != null && !client.isConnected()) {
            //    doClientConnection();
            //}
        }
    };

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.e(TAG, "MQTT当前网络名称：" + name);
            return true;
        } else {
            Log.e(TAG, "MQTT 没有可用网络");
            return false;
        }
    }

    public static void publish(String msg, String topic) {
        Integer qos = 0;
        Boolean retained = false;
        try {
            if (client != null && client.isConnected())
                client.publish(topic, msg.getBytes(), qos.intValue(), retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void testNetwork() {
        serverConnCatalog = ConnCata.OUTSIDECONN;

        RestfulDao dao = RestfulDaoFactory.getDao(ConnCata.OUTSIDECONN);
        boolean isOK = dao.testNetwork();
        if (isOK) {
            this.serverConnCatalog = ConnCata.OUTSIDECONN;
            return;
        }
        dao = RestfulDaoFactory.getDao(ConnCata.INSIDECONN);
        isOK = dao.testNetwork();
        if (isOK) {
            this.serverConnCatalog = ConnCata.INSIDECONN;
            return;
        }
        if (serverConnCatalog != null)
            Log.e(TAG, "后台服务连接类型为：" + serverConnCatalog.getName());
    }

    /**
     * 发送GPS更新信息
     */
    private void sendGpsUpdateInfo() {
//        if (serverConnCatalog == ConnCata.UNKNOW || serverConnCatalog == ConnCata.OFFCONN)
//            return;
//        boolean isGpsEnable = locm
//                .isProviderEnabled(LocationManager.GPS_PROVIDER);
//        if (!GlobalSystemParam.isGpsUpload || !isGpsEnable || !isGpsComeIn
//                || location == null || !isLocationIsChange())
//            return;
//        preLocationTime = location.getTime();
//        String jybh = GlobalData.grxx.get(GlobalConstant.YHBH);
//        byte[] b = new GpsUtils().getByteFromGps(jybh,
//                location.getLongitude(), location.getLatitude(), (byte) 0,
//                (byte) 0, (short) 0, 0, new Date());
//        InetAddress curIp = getIpAddress();
//        b = TypeCenvert.addByte(b,
//                TypeCenvert.ip2Byte(curIp.getHostAddress()));
//        b = TypeCenvert.addByte(b,
//                TypeCenvert.long2Byte(GlobalData.loginStatus));
//        RestfulDaoFactory.getDao(serverConnCatalog).uploadGpsInfo(b);
//        Log.e("MainReferService", "gps package length: " + b.length);
    }

    GpsStatus.Listener stlist = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int st) {
            if (st == GpsStatus.GPS_EVENT_STARTED) {
            } else if (st == GpsStatus.GPS_EVENT_STOPPED) {
            } else if (st == GpsStatus.GPS_EVENT_FIRST_FIX) {
                isGpsComeIn = true;
            } else if (st == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            }
        }
    };

    LocationListener ll = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onLocationChanged(Location loc) {
            location = loc;
        }
    };

    private void initLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setBearingRequired(true);
        criteria.setAltitudeRequired(true);
        criteria.setCostAllowed(true);
        String serverName = Context.LOCATION_SERVICE;
        locm = (LocationManager) getSystemService(serverName);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10, ll);
            locm.addGpsStatusListener(stlist);
        } catch (Exception e) {
        }
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        locm.removeUpdates(ll);
        locm.removeGpsStatusListener(stlist);
        super.onDestroy();
    }


    /**
     * 比较两个位置是否有变化
     *
     * @return
     */
    private boolean isLocationIsChange() {
        if (preLocationTime == 0)
            return true;
        return location.getTime() > preLocationTime;
    }

    /**
     * 每60分种运行一次
     */
    protected void backgroundRefreshMessage() {
        Log.e("MainReferService", "60 min run");
//        ViolationDAO.delOldViolation(GlobalConstant.MAX_RECORDS,
//                getContentResolver());
//        // 检查自选路段是否合法改在主程序验证
//        //WfddDao.checkFavorWfld(getContentResolver());
//        if (serverConnCatalog == ConnCata.UNKNOW || serverConnCatalog == ConnCata.OFFCONN)
//            return;
//        GlobalMethod.updateSysConfig(serverConnCatalog);
//        //检查版本状态
//        checkFileNeedUpdate();
//        // 从服务器更新系统参数，GPS未打开的，打开GPS
//        if (GlobalSystemParam.isGpsUpload
//                && !locm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            //GlobalMethod.toggleGPS(this);
//            Toast.makeText(this, "支队通知：因工作需要，请打开GPS！", Toast.LENGTH_LONG).show();
//            Log.e("MainReferService", "toggleGPS");
//        }
//        // 取得所有未上传的决定书
//        List<VioViolation> vios = ViolationDAO
//                .getUnloadViolations(getContentResolver());
//        // 无需检测空
//        for (int i = 0; i < vios.size(); i++) {
//            ViolationDAO.uploadViolationSaveIt(vios.get(i), this, serverConnCatalog);
//        }

    }


}
