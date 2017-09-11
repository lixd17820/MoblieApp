package com.jwt.printer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.util.Log;
import android.view.Gravity;


import com.jwt.jbyw.AcdSimpleBean;
import com.jwt.jbyw.AcdSimpleHumanBean;
import com.jwt.jbyw.VioViolation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.objectbox.BoxStore;

/**
 * 蓝牙打印的工具类
 *
 * @author li
 */
public class BlueToothPrint {
    private BluetoothAdapter bluetooth;
    private BluetoothSocket socket;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice device;
    private OutputStream tmpOut;
    private String address;
    private int bluetoothStatus;

    String TAG = "BlueToothPrint";

    public static final int BLUETOOTH_NONE = 0;
    public static final int BLUETOOTH_CONNECTED = 1;
    public static final int BLUETOOTH_SOCKETED = 2;
    public static final int BLUETOOTH_STREAMED = 3;
    public static final int BLUETOOTH_CONNECT_CLOSE = 4;
    public static final int BLUETOOTH_STREAMED_CLOSE = 5;

    public static final int PRINT_INIT = 0;
    public static final int ADAPTER_ERROR = 1;
    public static final int DEVICE_CREATE_ERROR = 2;
    public static final int DEVICE_CONNECT_ERROR = 3;
    public static final int OUTPUT_CREATE_ERROR = 4;
    public static final int MESSAGE_WRITE_ERROR = 5;
    public static final int SOCKET_SUCCESS = 6;
    public static final int PRINT_SUCCESS = 7;

    public BlueToothPrint(String address) {
        this.address = address;
        bluetoothStatus = BLUETOOTH_NONE;
    }

    /**
     * 检验本机蓝牙的状态
     *
     * @return 可用返回真，不可用返回false
     */
    public boolean isBluetoothEnable() {
        boolean isb = true;
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null || !bluetooth.isEnabled()) {
            isb = false;
        }
        return isb;
    }

    public int getBluetoothStatus() {
        return bluetoothStatus;
    }

    public String getBluetoothCodeMs(int status) {
        String ms = "未知蓝牙错误";
        if (status == ADAPTER_ERROR)
            ms = "不具备蓝牙打印功能";
        else if (status == DEVICE_CREATE_ERROR)
            ms = "设备不可见";
        else if (status == DEVICE_CONNECT_ERROR)
            ms = "不能连接蓝牙设备";
        else if (status == OUTPUT_CREATE_ERROR)
            ms = "不能创建输出";
        else if (status == MESSAGE_WRITE_ERROR)
            ms = "文字打印不成功";
        return ms;
    }

    public String getStatusMs(int status) {
        String ms = "未知状态";
        switch (status) {
            case BLUETOOTH_NONE:
                ms = "蓝牙初始化";
                break;
            case BLUETOOTH_CONNECTED:
                ms = "蓝牙已连接";
                break;
            case BLUETOOTH_SOCKETED:
                ms = "已创建套接字";
                break;
            case BLUETOOTH_STREAMED:
                ms = "已创建串口流";
                break;
            case BLUETOOTH_CONNECT_CLOSE:
                ms = "连接已关闭";
                break;
            case BLUETOOTH_STREAMED_CLOSE:
                ms = "串口流已关闭";
                break;
            default:
                break;
        }
        return ms;
    }

    /**
     * 创建套接字和输出流
     *
     * @return 错误信息
     */
    public int createSocket(BluetoothAdapter b) {
        this.bluetooth = b;
        device = bluetooth.getRemoteDevice(address);
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothStatus = BLUETOOTH_SOCKETED;
            Log.e(TAG, getStatusMs(bluetoothStatus));
        } catch (IOException e) {
            e.printStackTrace();
            return DEVICE_CREATE_ERROR;
        }

        try {
            socket.connect();
            bluetoothStatus = BLUETOOTH_CONNECTED;
            Log.e(TAG, getStatusMs(bluetoothStatus));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return DEVICE_CONNECT_ERROR;
        }

        try {
            tmpOut = socket.getOutputStream();
            bluetoothStatus = BLUETOOTH_STREAMED;
            Log.e(TAG, getStatusMs(bluetoothStatus));
        } catch (IOException e) {
            return OUTPUT_CREATE_ERROR;
        }
        return SOCKET_SUCCESS;
    }

    /**
     * 打印字节流
     *
     * @param b 输入字节数组
     * @return 打印状态
     */
    private int printMessage(byte[] b) {
        try {
            if (tmpOut != null)
                tmpOut.write(b);
            else
                return OUTPUT_CREATE_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return MESSAGE_WRITE_ERROR;
        }
        return PRINT_SUCCESS;

    }

    /**
     * 关闭输出流和套接字
     */
    public void closeConn() {
        if (tmpOut != null) {
            try {
                tmpOut.close();
                bluetoothStatus = BLUETOOTH_STREAMED_CLOSE;
                Log.e(TAG, getStatusMs(bluetoothStatus));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
                bluetoothStatus = BLUETOOTH_CONNECT_CLOSE;
                Log.e(TAG, getStatusMs(bluetoothStatus));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印空行，即出纸
     *
     * @param lines 行数
     */
    private void printSendLine(int lines) {
        byte[] b = new byte[]{0x1b, 0x64, (byte) (lines)};
        printMessage(b);
    }

    /**
     * 数组增加对齐功能
     *
     * @param align 对齐方式
     * @param bIn   输入数组
     * @return
     */
    private byte[] byte2Align(int align, byte[] bIn) {

        byte[] b = new byte[bIn.length + 3];
        b[0] = 27;
        b[1] = 97;
        b[2] = (byte) align;
        for (int i = 3; i < b.length; i++) {
            b[i] = bIn[i - 3];
        }
        // printMessage(b);
        return b;
    }

    /**
     * 初始化数组
     */
    private void printInit() {
        byte[] b = new byte[]{29, 64};
        printMessage(b);
    }

    /**
     * 将字符串转化成可打印的数组
     *
     * @param text 字符串
     * @return
     */
    private byte[] str2byte(String text) {
        int len = text.length();
        byte[] SendStr = new byte[len * 2 + 5];
        SendStr[0] = 0x1C;
        SendStr[1] = 0x55;
        SendStr[2] = (byte) len;
        SendStr[3] = 0;
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            SendStr[4 + 2 * i] = (byte) (c & 0xff);
            SendStr[5 + 2 * i] = (byte) (c >> 8);
        }
        SendStr[SendStr.length - 1] = 0x0a;
        return SendStr;
    }

    /**
     * 打印决定书,前提是蓝牙已连接且流已创建
     *
     * @param vio
     * @param resolver
     * @return
     */
    public int printJdsByBluetooth(VioViolation vio, Activity resolver) {
        int status = MESSAGE_WRITE_ERROR;
        if (bluetoothStatus != BLUETOOTH_STREAMED)
            return status;
        List<JdsPrintBean> conts = PrintJdsTools.getPrintJdsContent(vio,
                resolver);
        // status = createSocket();
        // // socket create error

        printSendLine(3);
        for (JdsPrintBean pjt : conts) {
            status = printMessage(byte2Align(changeGravityAlign(pjt
                    .getAlignMode()), str2byte(pjt.getContent())));
            if (status != BlueToothPrint.PRINT_SUCCESS)
                break;
        }
        String jdsbh = vio.getJdsbh();
        int jyw = Integer.valueOf(jdsbh.substring(6)) % 7;
        printTxm(jdsbh.substring(4) + jyw);
        printSendLine(5);
        printInit();
        return status;
    }

    public int printJdsByBluetooth(List<JdsPrintBean> conts) {
        int status = MESSAGE_WRITE_ERROR;
        if (bluetoothStatus != BLUETOOTH_STREAMED)
            return status;
        printSendLine(3);
        for (JdsPrintBean pjt : conts) {
            status = printMessage(byte2Align(changeGravityAlign(pjt
                    .getAlignMode()), str2byte(pjt.getContent())));
            if (status != BlueToothPrint.PRINT_SUCCESS)
                break;
        }
        printSendLine(3);
        printInit();
        return status;
    }

    public int printAcdByBluetooth(AcdSimpleBean acd, List<AcdSimpleHumanBean> humans) {
        int status = MESSAGE_WRITE_ERROR;
        if (bluetoothStatus != BLUETOOTH_STREAMED)
            return status;
        List<JdsPrintBean> conts = PrintAcdTools.getPrintAcdContent(acd,
                humans);
        // status = createSocket();
        // // socket create error

        printSendLine(3);
        for (JdsPrintBean pjt : conts) {
            status = printMessage(byte2Align(changeGravityAlign(pjt
                    .getAlignMode()), str2byte(pjt.getContent())));
            if (status != BlueToothPrint.PRINT_SUCCESS)
                break;
        }
        printSendLine(3);
        printInit();
        return status;
    }

    /**
     * 系统中对齐模式改为打印机对齐模式
     *
     * @param gravity
     * @return
     */
    private int changeGravityAlign(int gravity) {
        int align = 0;
        if (gravity == Gravity.CENTER) {
            align = 1;
        } else if (gravity == Gravity.RIGHT) {
            align = 2;
        }
        return align;
    }

    /**
     * 打印条形码配置
     */
    private void printTxmConfig() {
        //高度
        byte[] b = new byte[]{29, 104, 36};
        printMessage(b);
        //宽度
        b = new byte[]{29, 119, 2};
        printMessage(b);
        //文字位置0不打印1上方2下方
        b = new byte[]{29, 72, 2};
        printMessage(b);
        b = new byte[]{29, 102, 0};
        printMessage(b);
    }

    /**
     * 打印条形码
     *
     * @param jdsbh
     * @return
     */
    public byte[] printTxm(String jdsbh) {
        printTxmConfig();
//        int len = jdsbh.length();
//        byte[] b = new byte[len + 7];
//        b[0] = 29;
//        b[1] = 107;
//        b[2] = 69;
//        b[3] = (byte) (len + 2);
//        b[4] = 0x31;
//        for (int i = 0; i < len; i++) {
//            b[5 + i] = (byte) jdsbh.charAt(i);
//        }
//        b[b.length - 2] = 0x39;
//        b[b.length - 1] = 0x0a;
        List<Byte> bs = new ArrayList<Byte>();
        bs.add((byte) 29);
        bs.add((byte) 'k');
        bs.add((byte) 4);
        bs.add((byte) '*');
        int len = jdsbh.length();
        for (int i = 0; i < len; i++) {
            bs.add((byte) jdsbh.charAt(i));
        }
        bs.add((byte) '*');
        bs.add((byte) 0);
        byte[] b = new byte[bs.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = bs.get(i);
        }
        printMessage(b);
        return b;
    }

    /**
     * 一个测试方法
     */
    public void printTest() {
        List<Byte> lb = new ArrayList<Byte>();
        byte[] SendStr = new byte[25];
        SendStr[0] = 0x1C;
        SendStr[1] = 0x55;
        SendStr[2] = 10;
        SendStr[3] = 0;
        SendStr[4] = 0x55;
        SendStr[5] = 0x00;
        SendStr[6] = 0x4E;
        SendStr[7] = 0x00;
        SendStr[8] = 0x49;
        SendStr[9] = 0x00;
        SendStr[10] = 0x43;
        SendStr[11] = 0x00;
        SendStr[12] = 0x4F;
        SendStr[13] = 0x00;
        SendStr[14] = 0x44;
        SendStr[15] = 0x00;
        SendStr[16] = 0x45;
        SendStr[17] = 0x00;
        SendStr[18] = 0x53;
        SendStr[19] = 0x62;
        SendStr[20] = 0x70;
        SendStr[21] = 0x53;
        SendStr[22] = 0x4B;
        SendStr[23] = 0x6D;
        SendStr[24] = 0x0a;
        for (byte b : SendStr) {
            lb.add(b);
        }
        lb.add(0, (byte) 1);
        lb.add(0, (byte) 97);
        lb.add(0, (byte) 27);
        //
        byte[] pb = new byte[lb.size()];
        int i = 0;
        for (Byte byte1 : lb) {
            pb[i] = byte1;
            i++;
        }
        printMessage(pb);
    }

    // public int printJds(TTViolation v){
    //
    // }
}
