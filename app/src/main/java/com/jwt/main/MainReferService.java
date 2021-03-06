package com.jwt.main;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
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
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.jwt.event.MqttEvent;
import com.jwt.event.ServiceEvent;
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
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainReferService extends Service {

    public static final String TAG = MainReferService.class.getSimpleName();

    public static final int MANAGE_CONN = 1;
    public static final int MANAGE_TOPIC = 2;
    private static final int NOTIFICATIONS_ID = 1000;
    private static final int NETWROK_NOTIFICATIONS_ID = 1001;
    private static final int MSG_UPDATE_MJ_INFO = 1;
    private static final int MSG_UPDATE_NETWORK = 2;

    private LocationManager locm;
    public static Location location = null;
    private long preLocationTime = 0;
    private NotificationManager noticeManager;

    //public static ConnCata serverConnCatalog;

    //Mqtt client
    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    //
    private Box<Bjbd> bjbdBox;

    public static int netWorkStatus = 1;


//    public class DownLoadServiceBinder extends Binder {
//        public MainReferService getService() {
//            return MainReferService.this;
//        }
//    }
//
//    private Binder serviceBinder = new DownLoadServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "警务通后台服务进程启动");
        //检验是否有数据保存，如果为空说明服务是开机启动的
        if (GlobalData.grxx == null || GlobalData.grxx.isEmpty()) {
            Map<String, String> mjxx = GlobalMethod.getSavedMjInfo(this);
            if (mjxx == null || mjxx.isEmpty()) {
                Log.e(TAG, "无基本信息，无法启动后台服务");
                return;
            }
            GlobalData.grxx = mjxx;
            GlobalSystemParam.readSysParamFormSpf(this);
        }
        noticeManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 初始化全局数据
        App app = ((App) getApplication());
        if (!GlobalData.isInitLoadData) {
            Box<FrmCode> box = app.getBoxStore().boxFor(FrmCode.class);
            GlobalData.initGlobalData(app.getBoxStore());
        }
        bjbdBox = app.getBoxStore().boxFor(Bjbd.class);
        EventBus.getDefault().register(this);
        GlobalMethod.parseZdgzVehCbz(this);
        //测试网络
        new Timer("testNetwrok").scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                testNetwork();
            }
        }, 1 * 60 * 1000, 2 * 60 * 1000);
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
        GlobalSystemParam.syncBjzl();
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
        if (!isConn && isNetWork && GlobalSystemParam.isConnBjbd) {
            Log.e("Main server", "开始连接服务器");
            try {
                client.connect(conOpt, null, iMqttActionListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void closeMqttConn() {
        Log.e("Mainserver", "关闭报警连接");
        if (client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMqttConn() {
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
                Set<String> bjfw = GlobalSystemParam.recBjbdFW;
                if (bjfw != null && !bjfw.isEmpty()) {
                    for (String s : bjfw) {
                        list.add("clgj." + s);
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
            if (bjbd == null || !GlobalMethod.isRecBjbd(bjbd))
                return;
            bjbdBox.put(bjbd);
            EventBus.getDefault().post(bjbd);
            if (GlobalData.isBadger) {
                long count = bjbdBox.query().notEqual(Bjbd_.ydbj, 1).build().count();
                if (count > 99)
                    count = 99;
                ShortcutBadger.applyCount(MainReferService.this, (int) count);
            }
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained() + message.getId() + ";";
            Log.e(TAG, str2);
            Log.e(TAG, GlobalSystemParam.isNotNotice + "");
            if (GlobalSystemParam.isNotNotice)
                return;
            sendNotification(bjbd);
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

    private void setNetworkErrorNotification() {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(
                MainReferService.this)
                .setSmallIcon(R.drawable.warn_yellow)
                .setContentTitle("警务通网络未连接报警");
        String txt = netWorkStatus < 0 ? "安全平台未启动" : "最近一次连接测试失败";
        nb.setContentText(txt);
        nb.setAutoCancel(true);
        nb.setWhen(System.currentTimeMillis());
        Notification ni = nb.build();
        ni.flags |= Notification.FLAG_AUTO_CANCEL;
        noticeManager.notify(NETWROK_NOTIFICATIONS_ID, ni);
    }

    private void sendNotification(Bjbd bjbd) {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(
                MainReferService.this);
        nb.setSmallIcon(R.drawable.ic_speech_bubble);
        nb.setTicker("警务通报警提醒");
        nb.setContentTitle("警务通报警");
        String txt = bjbd.getBjyy();
        if ("1".equals(bjbd.getType())) {
            txt = bjbd.getDdsj() + GlobalMethod.getStringFromKVListByKey(GlobalData.hpzlList, bjbd.getHpzl())
                    + bjbd.getHphm() + "通过" + bjbd.getCbz() + "，布控原因：" + bjbd.getBjyy();
        }
        nb.setContentText(txt);
        nb.setAutoCancel(true);
        nb.setWhen(System.currentTimeMillis());
        ComponentName comp = new ComponentName("com.jwt.main",
                "com.jwt.main.JbywBjbdListActivity");
        Intent intent = new Intent();
        intent.setComponent(comp);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, intent, 0);
        nb.setContentIntent(contentIntent);
        Notification ni = nb.build();
        if (!TextUtils.isEmpty(GlobalSystemParam.bjRingtone))
            ni.sound = Uri.parse(GlobalSystemParam.bjRingtone);
        noticeManager.notify(NOTIFICATIONS_ID, ni);
    }


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
        //在安全平台运行时，保持网络连接，可以使网络不中断
        //serverConnCatalog = ConnCata.OUTSIDECONN;
        if (GlobalMethod.isSslRunning(this)) {
            RestfulDao dao = RestfulDaoFactory.getDao();
            boolean isOK = dao.testNetwork();
            netWorkStatus = isOK ? 1 : 0;
            String info = " 测试网络" + (isOK ? " 成功" : " 失败");
            //this.serverConnCatalog = ConnCata.OUTSIDECONN;
            //return;
            GlobalMethod.logFileInfo(info);
            if (isOK) {
                noticeManager.cancel(NETWROK_NOTIFICATIONS_ID);
            }
        } else {
            GlobalMethod.logFileInfo("安全平台未运行");
            netWorkStatus = -1;
        }
        if (netWorkStatus == 0)
            setNetworkErrorNotification();
        //dao = RestfulDaoFactory.getDao(ConnCata.INSIDECONN);
        //isOK = dao.testNetwork();
        //if (isOK) {
        //    this.serverConnCatalog = ConnCata.INSIDECONN;
        //   return;
        //}
        //if (serverConnCatalog != null)
        //   Log.e(TAG, "后台服务连接类型为：" + serverConnCatalog.getName());
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

    private boolean isGpsComeIn;
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
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
        Log.e(TAG, "警务通服务被停止");
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //locm.removeUpdates(ll);
        //locm.removeGpsStatusListener(stlist);
        EventBus.getDefault().unregister(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mqttEvent(MqttEvent event) {
        //管理连接
        if (event.getCatalog() == MANAGE_CONN) {
            if (GlobalSystemParam.isConnBjbd && !client.isConnected()) {
                //打开
                doClientConnection();
            } else if (!GlobalSystemParam.isConnBjbd && client.isConnected()) {
                closeMqttConn();
            }
        } else if (event.getCatalog() == MANAGE_TOPIC) {
            subscribeTopic();
        }
    }

    //-------------------------------------------------------------------------
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent arg0) {
        GlobalMethod.toast(this, "主服务绑定成功");
        return mMessenger.getBinder();
    }

    /**
     * 登出系统
     */
    public void logoutJwt() {
        GlobalData.isBadger = true;
        LogoutJwtNewThread logout = new LogoutJwtNewThread();
        logout.doStart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void serviceOpen(ServiceEvent event) {
        //服务中的回调
        if (event.catalog == GlobalConstant.SERVCIE_LOGOUT) {
            //登出系统线程返回
            GlobalMethod.toast(MainReferService.this,
                    event.code ? "警务系统正常退出" : "未能正常退出，可重新登录后退出");
        } else if (event.catalog == 200) {
            logoutJwt();
        }
    }

    /**
     * 主要用于外部程序调用
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_MJ_INFO:
                    saveUpdateMjxx(msg);
                    break;
                case MSG_UPDATE_NETWORK:
                    int connCata = msg.getData().getInt("connCata", 0);
                    GlobalData.connCata = ConnCata.getValByIndex(connCata);
                    GlobalSystemParam.loadParam(MainReferService.this, GlobalConstant.NETWORK_STATUS, connCata + "");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        private void saveUpdateMjxx(Message msg) {
            GlobalMethod.toast(MainReferService.this, "接收到更新民警信息的请求");
            String mjxx = msg.getData().getString(GlobalConstant.SP_MJXX);
            boolean isMjxxOk = false;
            JSONObject obj = null;
            try {
                obj = new JSONObject(mjxx);
                isMjxxOk = true;
            } catch (Exception e) {

            }
            //保存并更新民警信息的服务
            GlobalSystemParam.loadParam(MainReferService.this, GlobalConstant.SP_MJXX, obj.toString());
            Map<String, String> map = GlobalMethod.getSavedMjInfo(MainReferService.this);
            GlobalData.grxx = map;
            Message message = new Message();
            //message.setData(data);
            message.what = MSG_UPDATE_MJ_INFO;
            message.arg1 = isMjxxOk ? 0 : 1;
            try {
                msg.replyTo.send(message);
            } catch (RemoteException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }


}
