package com.jwt.utils;

import java.util.HashMap;
import java.util.Map;

public class GlobalConstant {
    public static Map<String, String> hdzh;
    public static Map<String, String> fxjgMap;
    public static Map<String, String> xsfxMap;

    static {
        hdzh = new HashMap<String, String>();
        hdzh.put("001", "1");
        hdzh.put("002", "3");
        hdzh.put("009", "9");
        hdzh.put("1", "001");
        hdzh.put("3", "002");
        hdzh.put("9", "009");
        fxjgMap = new HashMap<String, String>();
        fxjgMap.put("320601", "一大队");
        fxjgMap.put("320602", "二大队");
        fxjgMap.put("320603", "三大队");
        fxjgMap.put("320604", "四大队");
        fxjgMap.put("320605", "五大队");
        fxjgMap.put("320616", "六大队");
        fxjgMap.put("320606", "八大队");
        fxjgMap.put("320607", "七大队");
        fxjgMap.put("320608", "高一大队");
        fxjgMap.put("320609", "九大队");
        fxjgMap.put("320610", "十大队");
        fxjgMap.put("320611", "高二大队");
        fxjgMap.put("320612", "高三大队");
        fxjgMap.put("320613", "高四大队");
        fxjgMap.put("320615", "高五大队");
        fxjgMap.put("320614", "高六大队");
        fxjgMap.put("320617", "高七大队");
        fxjgMap.put("320621", "海安大队");
        fxjgMap.put("320623", "如东大队");
        fxjgMap.put("320681", "启东大队");
        fxjgMap.put("320682", "如皋大队");
        fxjgMap.put("320683", "通州大队");
        fxjgMap.put("320684", "海门大队");

        xsfxMap = new HashMap<String, String>();
        xsfxMap.put("0", "由北向南");
        xsfxMap.put("1", "由西向东");
        xsfxMap.put("2", "由南向北");
        xsfxMap.put("3", "由东向西");
        xsfxMap.put("4", "由东向南");
        xsfxMap.put("5", "由东向北");
        xsfxMap.put("6", "由西向南");
        xsfxMap.put("7", "由西向北");
        xsfxMap.put("8", "由北向东");
        xsfxMap.put("9", "由北向西");
        xsfxMap.put("A", "由南向西");
        xsfxMap.put("B", "由南向东");
        xsfxMap.put("C", "进城");
        xsfxMap.put("D", "出城");
    }

    public final static String SERIAL_NUMBER = "SERIAL_NUMBER";

    public final static String SHENFEN = "00/0032";
    public final static String QGXZQH = "00/0033";
    public final static String CHENSHI = "00/0034";
    public final static String JTFS = "04/0001";
    public final static String HPZL = "00/1007";
    public final static String JKFS = "04/0008";
    public final static String JKBJ = "04/0029";
    public final static String HPQL = "00/3140";
    public final static String CLFL = "04/0081";
    public final static String CFZL = "04/0002";
    public final static String RYFL = "04/0080";
    public final static String WSLB = "04/0015";
    public final static String QZCSLX = "04/0011";
    public final static String SJXM = "04/0012";
    public final static String KLXM = "04/0016";
    public final static String XZQH = "00/0050";
    public final static String ZJCX = "00/2001";
    public final static String ZJLX = "00/2019";

    /**
     * 车辆使用性质
     */
    public final static String SYXZ = "00/1003";
    /**
     * 政治面貌
     */
    public final static String ZZMM = "00/6131";
    /**
     * 职业信息
     */
    public final static String ZYXX = "04/0101";

    // 公共的字典
    public final static String FRM_XB = "00/0035";
    // 事故处理字典表常量
    /**
     * 天气 *
     */
    public final static String ACD_TQ = "03/0111";
    /**
     * 事故形态 *
     */
    public final static String ACD_SGXT = "03/0112";
    /**
     * 车辆间碰撞 *
     */
    public final static String ACD_CLJPZ = "03/0116";
    /**
     * 单车碰撞 *
     */
    public final static String ACD_DLPZ = "03/0138";
    /**
     * 结案方式 *
     */
    public final static String ACD_JAFS = "03/0167";
    /**
     * 调解方式 *
     */
    public final static String ACD_TJFS = "03/0166";
    /**
     * 人员类型 *
     */
    public final static String ACD_RYLX = "03/0135";
    /**
     * 驾驶证种类 *
     */
    public final static String ACD_JSZZL = "03/0136";
    /**
     * 事故责任 *
     */
    public final static String ACD_SGZR = "00/3138";
    /**
     * 事故交通方式 *
     */
    public final static String ACD_JTFS = "03/0130";
    /**
     * 交通事故违法行为 *
     */
    public final static String ACD_WFXW = "03/0160";

    public static final int KEY = 0;
    public static final int VALUE = 1;

    // public static final String QWWFJG = "0";
    // public static final String JYCFJDS = "1";
    // public static final String QZCSPZ = "3";
    // public static final String WFTZD = "6";
    // public static final String ACDSIMPLEWS = "9";

    public static final int QWWFJG = 0;
    public static final int JYCFJDS = 1;
    public static final int QZCSPZ = 3;
    public static final int WFTZD = 6;
    public static final int ACDSIMPLEWS = 9;

    public static final int DOWNLOADING = 11101;
    public static final int DOWNLOADOK = 11102;
    public static final int DOWNLOADERR = 11103;

    // 违法代码种类
    public static final int WFDMZL_JDC = 1;
    public static final int WFDMZL_FJDC = 2;
    public static final int WFDMZL_XRCCR = 3;
    public static final int WFDMZL_SG = 5;
    public static final int WFDMZL_OTHER = 9;

    // 综合查询
    /**
     * 本地驾驶员
     */
    public static final String BDJSY = "01005";
    public static final String QGJSY = "01012";
    public static final String BDJDC = "02001";
    public static final String QGJDC = "02004";
    /**
     * 常住人口
     */
    public static final String CZRK = "01101";
    /**
     * 全国人口请求服务
     */
    public static final String QGRK = "01010";
    public static final String WFFK = "01007";
    public static final String DZJK = "01008";
    public static final String JYDH = "01009";
    public static final String LGZS = "01125";
    public static final String ZTRY = "01118";
    public static final String PCRY = "01110";
    public static final String DQCL = "02005";

    public static final String ZHCX_BDJSY = "C002";
    public static final String ZHCX_QGJSY = "Q002";
    public static final String ZHCX_BDJDC = "C001";
    public static final String ZHCX_QGJDC = "Q001";

    // 使用三种不同连接
    //public static final int JWTCONN = 0;
    //public static final int OUTSIDECONN = 1;
    //public static final int INSIDECONN = 2;
    //public static final int OFFCONN = 3;

    /**
     * 系统中允许保留最大记录数
     */
    public static final int MAX_RECORDS = 200;

    // 民警个人信息中的字符常量
    public static final String GRXX_PRINTER_NAME = "print_name";
    public static final String GRXX_PRINTER_ADDRESS = "print_address";
    public static final String GRXX_CARD_READER_NAME = "card_reader_name";
    public static final String GRXX_CARD_READER_ADDRESS = "card_reader_address";

    /**
     * 32061247
     */
    public static final String YHBH = "YHBH";
    /**
     * 1701
     */
    public static final String YHLX = "YHLX";
    /**
     * 5E77ED215029279A2D00FF85353292B030D06B9C
     */
    public static final String MM = "MM";
    /**
     * 32061247
     */
    public static final String JH = "JH";
    /**
     * 李小冬
     */
    public static final String XM = "XM";
    /**
     * 1
     */
    public static final String XB = "XB";
    /**
     * 320600240500
     */
    public static final String BMBH = "BMBH";
    /**
     * 南通市公安局交通巡逻警察支队一大队
     */
    public static final String BMMC = "BMMC";
    /**
     * 南通市公安局、南通市人民政府
     */
    public static final String FYJG = "FYJG";
    /**
     * 南通市崇川区人民法院
     */
    public static final String SSJG = "SSJG";
    /**
     * 320600240500
     */
    public static final String CLJG = "CLJG";
    /**
     * 江苏银行南通市分行
     */
    public static final String JKYH = "JKYH";
    /***/
    public static final String ZSDWBM = "ZSDWBM";
    /***/
    public static final String SRXMZXM = "SRXMZXM";
    /***/
    public static final String YQJSFKM = "YQJSFKM";
    /***/
    public static final String DWDZ = "DWDZ";
    /***/
    public static final String DWDH = "DWDH";
    /**
     * 320601000000
     */
    public static final String YBMBH = "YBMBH";
    /**
     * 南通市公安局交通巡逻警察支队一大队一中队
     */
    public static final String CLJG1 = "CLJG1";
    /**
     * 2008-4-29 9:41:23
     */
    public static final String LRSJ = "LRSJ";
    /**
     * 2010-11-22 21:38:19
     */
    public static final String GXSJ = "GXSJ";
    /**
     * 2
     */
    public static final String JB = "JB";
    /**
     * 32060219721130201X
     */
    public static final String SFZH = "SFZH";
    /**
     * 320607000000 改了，县区中队与大队编码不一样
     */
    public static final String KSBMBH = "KSBMBH";
    /**
     * 1
     */
    public static final String YHJB = "YHJB";
    /***/
    public static final String WSBHID = "WSBHID";
    /***/
    public static final String WSLX = "WSLX";
    /***/
    public static final String WSDQBH = "WSDQBH";
    /***/
    public static final String WSZDBH = "WSZDBH";

    // 与服务器交互的XML模板
    // public static final String vxml =
    // "<?xml version=\"1.0\"?><VisitorEntity><IP></IP><YHBH>%S</YHBH><MM>5E77ED215029279A2D00FF85353292B030D06B9C</MM>"
    // +
    // "<SBLX>1</SBLX><SBDH>4442A1398D59DBA57CF5129045ABD6BED4786F4A</SBDH><DYMY></DYMY></VisitorEntity>";
    // public static final String visitorXMLMb =
    // "<?xml version=\"1.0\"?><VisitorEntity><IP></IP><YHBH>%S</YHBH><MM>%S</MM>"
    // + "<SBLX>1</SBLX><SBDH>%S</SBDH><DYMY></DYMY></VisitorEntity>";
    //
    // public static final String loginXml =
    // "<?xml version=\"1.0\"?><LoginEntity><YHBH>%S</YHBH><MM>%S</MM></LoginEntity>";
    // public static final String billXML =
    // "<?xml version=\"1.0\"?><GetBillEntity><WSLB>%S</WSLB><SYR>%S</SYR></GetBillEntity>";
    // public static final String uploadXML =
    // "<?xml version=\"1.0\"?><PunishEntity>"
    // +
    // "<CFBH>%S</CFBH><CFLB>%S</CFLB><WFXWRFL>%S</WFXWRFL><CLFL>%S</CLFL><ZQMJ>%S</ZQMJ>"
    // +
    // "<ZQJG>%S</ZQJG><DSR>%S</DSR><JSZH>%S</JSZH><DABH>%S</DABH><FZJG>%S</FZJG>"
    // +
    // "<LXDH>%S</LXDH><XZQH>%S</XZQH><LXFS>%S</LXFS><HPHM>%S</HPHM><HPZL>%S</HPZL>"
    // +
    // "<JTFS>%S</JTFS><WFDD>%S</WFDD><WFDDNR>%S</WFDDNR><WFSJ>%S</WFSJ><JKFS>%S</JKFS>"
    // +
    // "<JKFSNR>%S</JKFSNR><WFDM>%S</WFDM><WFFK>%S</WFFK><WFJF>%S</WFJF><QZCSXM>%S</QZCSXM>"
    // +
    // "<GZXM>%S</GZXM><GZXMNR>%S</GZXMNR><ZJCX>%S</ZJCX><WSBHID>%S</WSBHID><CLJG>%S</CLJG>"
    // + "</PunishEntity>";
    //
    // public static final String updateXML =
    // "<?xml version=\"1.0\"?><UpdateEntity>"
    // + "<DQBB>%S</DQBB><YHLX>%S</YHLX></UpdateEntity>";
    //
    // public static final String backBillXmlMb = "<?xml version=\"1.0\"?>"
    // + "<BillBackEntity><XFDJBH>%S</XFDJBH><DQZ>%S</DQZ></BillBackEntity>";

    // UDP短信服务的常量
    public static final int LISTENER_PORT = 8888;
    public static final String UDPSERVER = "10.142.136.245";
    public static final int PORT_STATUS = 5677;
    public static final int PORT_MESSAGE = 5678;
    public static final int PORT_BEAT = 3344;

    public static final String GPS_FREQ = "gps_freq";

    public static final int WHAT_ERR = 1;
    public static final int WHAT_RECODE_OK = 3;
    public static final int WHAT_PHOTO_OK = 4;
    public static final int WHAT_ALL_OK = 5;
    public static final int WHAT_ALL_ERR = 6;

    public static final int MAX_ACD_PHOTO_COUNT = 5;
    public static final int MIN_ACD_PHOTO_COUNT = 4;

    public static String jhjsCodes = "1604,1605,1711,1712,1713,6034,6035,";
    public static String cjjsCodes = "1702,1703,6032,6033,";
    public static String kccySmallCodes = "1202,1241,1348,1621,1623,1626,";
    public static String kccyLargeCodes = "1341,1601,1627,1710,1714,1716,";
    public static String hcczSmallCodes = "1353,1354,";
    public static String hcczLargeCodes = "1637,1639,";
    public static String sczCode = jhjsCodes + cjjsCodes + kccySmallCodes + kccyLargeCodes + hcczSmallCodes + hcczLargeCodes;


    /**
     * 用于保存常用信息的常量名称
     */
    public final static String MJXX_INFO = "mjxx_info";
}
