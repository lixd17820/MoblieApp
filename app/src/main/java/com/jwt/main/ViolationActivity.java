package com.jwt.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.jwt.adapter.OnSpinnerItemSelected;
import com.jwt.bean.KeyValueBean;
import com.jwt.dao.ViolationDAO;
import com.jwt.dao.WfddDao;
import com.jwt.dao.WfdmDao;
import com.jwt.dao.WsglDAO;
import com.jwt.event.VioUploadEvent;
import com.jwt.jbyw.VioDrvBean;
import com.jwt.jbyw.VioVehBean;
import com.jwt.pojo.THmb;
import com.jwt.pojo.VioViolation;
import com.jwt.pojo.VioWfdmCode;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintJdsTools;
import com.jwt.thread.CommQueryThread;
import com.jwt.thread.CommUploadThread;
import com.jwt.thread.UploadVioThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.utils.GlobalSystemParam;
import com.jwt.utils.ParserJson;
import com.jwt.web.RestfulDao;
import com.jwt.web.RestfulDaoFactory;
import com.jwt.web.WebQueryResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class ViolationActivity extends AppCompatActivity {

    private static final String TAG = "ViolationActivity";
    private Activity self;
    // 打开子页面的请求码
    public static final int REQCODE_WFDD = 10;
    public static final int REQCODE_WFXW = 11;
    public static final int REQCODE_JTFS = 12;
    public static final int REQCODE_PREVIEW_JDS = 13;
    public static final int REQCODE_FZJG = 14;
    // 快捷菜单码
    public static final int MENU_WFXW_ADD = 20;
    public static final int MENU_WFXW_MOD = 21;

    // 信息查询种类
    public static final int QUERY_DRV_INFO = 30;
    public static final int QUERY_VEH_INFO = 31;

    private static final int SAVE_VIO_HANDLER = 40;
    private static final int QUERY_DRV_HANDLER = 41;
    private static final int QUERY_VEH_HANDLER = 42;

    private static final int REQCODE_OCR = 15;

    // 车辆分类常量
    private static final int ERROR = -1;
    @BindView(R.id.tv_jdsbh)
    TextView textJdsbh;
    @BindView(R.id.Sp_ryfl)
    Spinner spRyfl;
    @BindView(R.id.Sp_hpzl)
    Spinner spHpzl;
    @BindView(R.id.Sp_cllx)
    Spinner spJtfs;
    @BindView(R.id.Sp_clfl)
    Spinner spClfl;
    @BindView(R.id.Sp_hpql)
    Spinner spHpql;
    @BindView(R.id.Edit_fzjgms)
    EditText edFzjgms;
    @BindView(R.id.sp_wfdd)
    Spinner spWfdd;
    @BindView(R.id.Edit_wfxw)
    EditText edWfxw;
    @BindView(R.id.Text_wfxwms)
    TextView textWfxwms;
    @BindView(R.id.Edit_wfsj)
    EditText edWfsj;
    // 人员基本信息控件
    @BindView(R.id.Edit_dabh)
    EditText edDabh;
    @BindView(R.id.Edit_xm)
    EditText edXm;
    @BindView(R.id.Edit_jszh)
    EditText edJszh;
    @BindView(R.id.Edit_lxdz)
    EditText edLxdz;
    @BindView(R.id.Edit_zjcx)
    EditText edZjcx;
    @BindView(R.id.Edit_lddh)
    EditText edLxdh;
    @BindView(R.id.Edit_ljjf)
    EditText edLjjf;
    @BindView(R.id.Edit_hphm)
    EditText edHphm;
    @BindView(R.id.edit_bzz)
    EditText edBzz;
    @BindView(R.id.edit_scz)
    EditText edScz;
    @BindView(R.id.sp_zzmm)
    Spinner spZzmm;
    @BindView(R.id.sp_zyxx)
    Spinner spZyxx;
    @BindView(R.id.sp_syxz)
    Spinner spSyxz;
    @BindView(R.id.sp_zjlx)
    Spinner spZjlx;
    @BindView(R.id.But_query_drv)
    Button btnQueryDriver;
    @BindView(R.id.But_clbd)
    Button btnQueryVeh;
    @BindView(R.id.btn_fzjg)
    Button btnFzjg;
    // 用于储存变量
    protected BlueToothPrint btp = null;

    // 用于保存处罚决定书
    protected THmb jdsbh;
    protected boolean isViolationSaved = false;
    protected VioViolation violation;
    protected String zqmj;
    private final String STATE_IS_SAVE = "isViolationSaved";
    private final String STATE_VIOLATION = "violation";
    private final String JDSBH = "jdsbh";
    private final String CFZL = "cfzl";
    private final String WSLB = "wslb";
    protected String wslb, cfzl;
    protected final int CAMER_REQUEST = 1111;
    protected String photoName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "VIOLATION ON CREATE");
        setContentView(R.layout.jbyw_violation_r);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        findViewById(R.id.unless_linear).requestFocus();
        // 常量赋值
        self = this;
        zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);

        // 初始化控件
        findView();
        // 由上一处罚取得
        VioViolation preViolation = (VioViolation) getIntent()
                .getSerializableExtra("violation");

        // 新的处罚则设默认值
        if (preViolation == null)
            preViolation = setDefaultVio();

        setViewDefaultValue(preViolation);

        // 设置监听
        setListeners();
    }

    /**
     * 退出界面的事件
     */
    OnClickListener exitSystem = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };


    /**
     * 初始化界面元素
     */
    protected void findView() {
        edLjjf.setKeyListener(null);
        // mTitle = (TextView) findViewById(R.id.title_left_text);
        // mTitle_r = (TextView) findViewById(R.id.title_right_text);
        // 初始化全局数据字典表
        if (!GlobalData.isInitLoadData)
            GlobalData.initGlobalData(GlobalMethod.getBoxStore(this));

        // 以下为固定的字典表，城市列表不在其中
        // 设置人员分类适配器
        GlobalMethod.changeAdapter(spRyfl, GlobalData.ryflList, this);
        // 设置号牌种类
        GlobalMethod.changeAdapter(spHpzl, GlobalData.hpzlList, this, true);
        // 设置号牌前辍
        GlobalMethod.changeAdapter(spHpql, GlobalData.hpqlList, this, true);
        // 设置车辆分类
        GlobalMethod.changeAdapter(spClfl, GlobalData.clflList, this);
        //政治面貌
        GlobalMethod.changeAdapter(spZzmm, GlobalData.zzmmList, this, true);
        //职业信息
        GlobalMethod.changeAdapter(spZyxx, GlobalData.zyxxList, this, true);
        //使用性质
        GlobalMethod.changeAdapter(spSyxz, GlobalData.syxzList, this, true);
        //证件类型
        GlobalMethod.changeAdapter(spZjlx, GlobalData.zjlxList, this, true);

    }

    /**
     * 设置控件的默认值
     */
    private void setViewDefaultValue(VioViolation v) {
        // 这时还没有监听，所以对应的变化均需手工录入
        // int ryfl = Integer.valueOf(v.getRyfl());
        GlobalMethod.changeSpinnerSelect(spRyfl, v.getRyfl(),
                GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spZjlx, v.getZjlx(),
                GlobalConstant.KEY, true);
        changeRyflActive(v.getRyfl());
        edFzjgms.setText(v.getFzjg());

        // if (ryfl == 4) {
        // GlobalMethod.changeSpinnerSelect(spShenFen,
        // v.getFzjg().substring(0, 1), GlobalConstant.VALUE, true);
        // GlobalMethod.changeAdapter(spChenShi, createCityList(v.getFzjg()
        // .substring(0, 1)), this, true);
        // GlobalMethod.changeSpinnerSelect(spChenShi, v.getFzjg(),
        // GlobalConstant.KEY, true);
        // }
        GlobalMethod.changeSpinnerSelect(spClfl, v.getClfl(),
                GlobalConstant.KEY, true);
        changeClflActive(v.getClfl());
        GlobalMethod.changeSpinnerSelect(spHpzl, v.getHpzl(),
                GlobalConstant.KEY, true);
        if (!TextUtils.isEmpty(v.getHphm())) {
            boolean isHpql = GlobalMethod.changeSpinnerSelect(spHpql, v
                    .getHphm().substring(0, 1), GlobalConstant.VALUE, true);
            edHphm.setText(v.getHphm().substring(isHpql ? 1 : 0));
        }
        // 设置交通方式
        GlobalMethod.changeSpinnerSelect(spJtfs, v.getJtfs(),
                GlobalConstant.KEY, true);
        if (!TextUtils.isEmpty(v.getWfdd()) && WfddDao.isWfddOk(v.getWfdd(), self)) {
            List<KeyValueBean> wfdds = new ArrayList<KeyValueBean>();
            wfdds.add(new KeyValueBean(v.getWfdd(), v.getWfdz()));
            GlobalMethod.changeAdapter(spWfdd, wfdds, this);
        }
        //三个数据均保存在GZXM字段中，解析并赋值
        JSONObject gzxm = ParserJson.getJsonObject(v.getGzxm());
        GlobalMethod.changeSpinnerSelect(spZzmm, gzxm.optString("zzmm"),
                GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spZyxx, gzxm.optString("zyxx"),
                GlobalConstant.KEY, true);
        GlobalMethod.changeSpinnerSelect(spSyxz, gzxm.optString("syxz"),
                GlobalConstant.KEY, true);
        edWfsj.setText(v.getWfsj());
        // 不弹出输入法
        edWfsj.setKeyListener(null);
        edDabh.setText(v.getDabh() == null ? "" : v.getDabh());
        edXm.setText(v.getDsr() == null ? "" : v.getDsr());
        edJszh.setText(v.getJszh() == null ? "" : v.getJszh());
        edLxdz.setText(v.getLxfs() == null ? "" : v.getLxfs());
        edLxdh.setText(v.getDh() == null ? "" : v.getDh());
        edZjcx.setText(v.getZjcx() == null ? "" : v.getZjcx());
        edLjjf.setText(v.getWfjfs() + "");
        edBzz.setText(GlobalMethod.ifNull(v.getBzz()));
        edScz.setText(GlobalMethod.ifNull(v.getScz()));
    }

    /**
     * 设置默认值
     *
     * @return
     */
    private VioViolation setDefaultVio() {
        VioViolation kvWfdd = ViolationDAO.getLastVio(GlobalMethod.getBoxStore(self));
        VioViolation v = new VioViolation();
        v.setRyfl("4");
        v.setFzjg("苏F");
        v.setClfl("3");
        v.setHpzl("02");
        v.setHphm("苏F");
        v.setZjlx("A");
        // v.setDsr("李小冬");
        // v.setJszh("32060219731130201X");
        //v.setDabh("320600075015");
        v.setWfsj(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        if (kvWfdd != null) {
            v.setWfdz(kvWfdd.getWfdz());
            v.setWfdd(kvWfdd.getWfdd());
        }
        return v;
    }

    private void setListeners() {
//        findViewById(R.id.But_wfdd).setOnClickListener(butClickListener);
//        findViewById(R.id.But_query_wfxw).setOnClickListener(butClickListener);
//        findViewById(R.id.but_query_drv_sfzh).setOnClickListener(butClickListener);
//        btnQueryDriver.setOnClickListener(butClickListener);
//        btnQueryVeh.setOnClickListener(butClickListener);
//        btnFzjg.setOnClickListener(butClickListener);
//        findViewById(R.id.But_cllx).setOnClickListener(butClickListener);
//        findViewById(R.id.But_wzsj).setOnClickListener(butClickListener);
//        findViewById(R.id.But_wzrq).setOnClickListener(butClickListener);


        // 人员分类变化时,控制档案编号和查询驾驶人按扭不可用,发证机关,准驾车型
        spRyfl.setOnItemSelectedListener(ryflChangeListener);

        // 车辆分类变化时的监听 1 非机动车 号牌种类，查车按扭，号牌前，不可用，号牌号码可用，交通方式过滤。
        // 2 无牌无证机动车 号牌种类，查车按扭，号牌前，号牌号码均不可用
        // 3 公安牌证机动车 不限制
        // 9 其它 同非机动车
        spClfl.setOnItemSelectedListener(clflChangeListener);

        // 隐藏违法时间控件的软键盘
        // edWfsj.setOnFocusChangeListener(fl);
        // edWfsj.setOnTouchListener(tl);

        // 违法行为列表设为不能编辑
        // edAllWfxws.setOnFocusChangeListener(fl);
        // edAllWfxws.setOnTouchListener(tl);

        //hideIM(edDabh);

        // 当违法代码为四位时自动生成违法行为和罚款记分的内容
        edWfxw.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 4 || s.length() == 5) {
                    char[] dest = new char[s.length()];
                    s.getChars(0, s.length(), dest, 0);
                    queryAndShowWfxw(new String(dest));
                } else {
                    textWfxwms.setText("");
                }
            }
        });

    }

    private int ryflCount = 0;

    private OnSpinnerItemSelected ryflChangeListener = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1,
                                   int position, long arg3) {
            Log.e(TAG, "ryflChangeListener: " + position);
            ryflCount++;
            if (ryflCount < 3 && GlobalSystemParam.isSkipSpinner)
                return;
            if (position > -1)
                changeRyflActive(((KeyValueBean) spRyfl.getSelectedItem())
                        .getKey());
        }
    };

    private int clflCount = 0;

    private OnSpinnerItemSelected clflChangeListener = new OnSpinnerItemSelected() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View arg1,
                                   int position, long arg3) {

            Log.e(TAG, "clflChangeListener: " + position);
            clflCount++;
            if (clflCount < 3 && GlobalSystemParam.isSkipSpinner)
                return;
            if (position > -1)
                changeClflActive(((KeyValueBean) spClfl.getSelectedItem())
                        .getKey());
        }
    };

    /**
     * 改变车辆分类下拦框时,控件状态相应进行的改变
     */
    private void changeClflActive(String clfl) {
        int xfJtfsfl = 4;
        int iClfl = Integer.valueOf(clfl);
        GlobalMethod.cleanText(edHphm);
        switch (iClfl) {
            case 1:
            case 9:
                // 非机动车或其他
                GlobalMethod.setEnable(false, spHpzl, spHpql, btnQueryVeh);
                GlobalMethod.setEnable(true, edHphm);
                // if (cfCatalog == GlobalConstant.JYCFJDS
                // && cfCatalog != GlobalConstant.QWWFJG && iClfl == 1) {
                // GlobalMethod.changeSpinnerSelect(spJkfs, "1",
                // GlobalConstant.KEY, true);
                // }
                xfJtfsfl = iClfl == 1 ? 2 : 1;
                break;
            case 2:
                // 无牌无证机动车
                GlobalMethod.setEnable(false, spHpzl, spHpql, edHphm, btnQueryVeh);
                break;
            case 3:
                // 公安牌证机动车
                GlobalMethod.setEnable(true, spHpzl, spHpql, edHphm, btnQueryVeh);
                GlobalMethod.changeSpinnerSelect(spHpzl, "02", GlobalConstant.KEY,
                        true);
                GlobalMethod.changeSpinnerSelect(spHpql, "苏", GlobalConstant.VALUE,
                        true);
                edHphm.setText("F");
                break;
            case 4:// 武警牌证机动车
            case 5:// 部队牌证机动车
            case 6:// 农机牌证机动车
                GlobalMethod.setEnable(true, spHpzl, spHpql, edHphm);
                GlobalMethod.setEnable(false, btnQueryVeh);
                if (iClfl == 6)
                    GlobalMethod.changeSpinnerSelect(spHpzl, "14", GlobalConstant.KEY,
                            true);
            default:
                break;
        }
        GlobalMethod.changeAdapter(spJtfs, getXfJtfs(xfJtfsfl), this, true);
        GlobalMethod.changeSpinnerSelect(spJtfs, xfJtfsfl == 1 ? "A01" : (xfJtfsfl == 2 ? "F07" : (iClfl == 6 ? "T21" : "")),
                GlobalConstant.KEY);
    }

    /**
     * 查询交通方式
     *
     * @param fl 1 行人 2 非机动车 其余 机动车
     * @return
     */
    private List<KeyValueBean> getXfJtfs(int fl) {
        ArrayList<KeyValueBean> jtfsList = new ArrayList<>();
        for (KeyValueBean kv : GlobalData.jtfsList) {
            String s = kv.getKey().substring(0, 1);
            if (fl == 1) {
                if ("AC".contains(s))
                    jtfsList.add(kv);
            } else if (fl == 2) {
                if ("FX".contains(s))
                    jtfsList.add(kv);
            } else {
                if (!"ACFX".contains(s))
                    jtfsList.add(kv);
            }
        }
        return jtfsList;
    }

    /**
     * 改变人员分类下拉框时控件改变行为
     */
    private void changeRyflActive(String ryfl) {
        spClfl.setOnItemSelectedListener(null);
        GlobalMethod.cleanText(edFzjgms, edXm, edDabh, edJszh, edLxdz, edLxdh,
                edZjcx);
        GlobalMethod.setEnable(false, btnQueryDriver);
        String clfl = "3";
        int iRyfl = Integer.valueOf(ryfl);
        switch (iRyfl) {
            case 4:
                // 公安驾驶证
                GlobalMethod.setEnable(true, edDabh, edZjcx, btnQueryDriver,
                        spClfl, spHpzl, spHpql, btnQueryVeh);
                GlobalMethod.setEnable(false, edFzjgms);

                // edFzjgms.setText(GlobalMethod.getKeyFromSpinnerSelected(spChenShi,
                // GlobalConstant.KEY));
                break;
            case 5:
            case 6:
                // 武警、部队驾驶证
                GlobalMethod.setEnable(false, edDabh);
                GlobalMethod.setEnable(true, edZjcx, edFzjgms, spClfl, spHpzl,
                        spHpql, btnQueryVeh);
                clfl = "" + (iRyfl - 1);
                // edDabh.setText("无");
                break;
            case 7:
                // 农机驾驶证
                GlobalMethod.setEnable(true, edDabh, edFzjgms, edZjcx, spClfl,
                        spHpzl, spHpql, btnQueryVeh);
                clfl = "" + (iRyfl - 1);
                break;
            case 1:
            case 2:
                // 非机动车或乘车人
                GlobalMethod.setEnable(false, edDabh, edFzjgms, edZjcx, spClfl,
                        spHpzl, spHpql, btnQueryVeh);
                clfl = iRyfl == 2 ? "1" : "9";
                break;
            case 3:
                GlobalMethod.setEnable(false, edDabh, edFzjgms, edZjcx);
                GlobalMethod.setEnable(true, spClfl, spHpzl, spHpql, btnQueryVeh);
                break;
            default:
                break;
        }
        // 修改车辆分类，调用车辆分类变化方法
        GlobalMethod.changeSpinnerSelect(spClfl, clfl, GlobalConstant.KEY);
        changeClflActive(clfl);
        //GlobalMethod.changeAdapter(spJtfs, getXfJtfs(iRyfl > 2), this, true);
        // GlobalMethod.changeSpinnerSelect(spJtfs, iRyfl > 2 ? "" : (iRyfl == 1 ? "A01" : "F07"),
        //         GlobalConstant.KEY);
        spClfl.setOnItemSelectedListener(clflChangeListener);
        // boolean isGA = TextUtils.equals("4", kv.getKey());
        // edDabh.setEnabled(isGA);
        // findViewById(R.id.But_query_drv).setEnabled(isGA);
        // spShenFen.setEnabled(isGA);
        // spChenShi.setEnabled(isGA);
        // edZjcx.setEnabled(isGA);
        // if (!isGA) {
        // edDabh.setText("");
        // edZjcx.setText("");
        // }
    }

    /**
     * 隐藏键盘
     *
     * @param edt 控件名称
     */
    private void hideIM(View edt) {
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = edt.getWindowToken();
            if (windowToken != null) {
                // always de-activate IM
                im.hideSoftInputFromWindow(windowToken, 0);
            }
        } catch (Exception e) {
            Log.e("HideInputMethod", "failed:" + e.getMessage());
        }
    }

    /**
     * 隐藏键盘时的监听
     */
    View.OnFocusChangeListener fl = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                hideIM(v);
        }
    };
    View.OnTouchListener tl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideIM(v);
            }
            return false;
        }
    };

    /**
     * 选择了违法行为后,将其详细情况显示到文本框中
     *
     * @param wfdm
     */
    private void queryAndShowWfxw(String wfdm) {
        VioWfdmCode wf = WfdmDao.queryWfxwByWfdm(wfdm, GlobalMethod.getBoxStore(self));
        if (wf != null) {
            textWfxwms.setText(showWfdmDetail(wf));
        } else {
            textWfxwms.setText("");
            //wf = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_WFDD) {
                Bundle b = data.getExtras();
                if (b != null) {
                    String wfdd = b.getString("wfddDm");
                    String wfdz = b.getString("wfddMc");
                    boolean isSelect = GlobalMethod.changeSpinnerSelect(spWfdd,
                            wfdz, GlobalConstant.VALUE);
                    if (!isSelect) {
                        List<KeyValueBean> wfdds = new ArrayList<KeyValueBean>();
                        wfdds.add(new KeyValueBean(wfdd, wfdz));
                        GlobalMethod.changeAdapter(spWfdd, wfdds, this);
                        GlobalMethod.changeSpinnerSelect(spWfdd, wfdd,
                                GlobalConstant.KEY);
                    }
                }
            } else if (requestCode == REQCODE_WFXW) {
                String wfxw = data.getStringExtra("wfxw");
                if (!TextUtils.isEmpty(wfxw)) {
                    edWfxw.setText(wfxw);
                    queryAndShowWfxw(wfxw);
                }
            } else if (requestCode == REQCODE_JTFS) {
                Bundle b = data.getExtras();
                String j = b.getString("jtfsDm");
                GlobalMethod.changeSpinnerSelect(spJtfs, j, GlobalConstant.KEY);
            } else if (requestCode == REQCODE_FZJG) {
                Bundle b = data.getExtras();
                String fzjg = b.getString("fzjg");
                edFzjgms.setText(fzjg);
            } else if (requestCode == REQCODE_OCR) {
                Bundle b = data.getExtras();
                String sfzh = b.getString("txt");
                edJszh.setText(sfzh);
            }
            if (requestCode == CAMER_REQUEST) {
                camerResult();
            }
        } else {
            // edWfdd.setText("");
        }
    }

    /**
     * 获取界面元素的内容的公共内容
     *
     * @return 处罚对象
     */
    protected void getViolationFromView(VioViolation vio) {
        // 人员分类
        vio.setRyfl(GlobalMethod.getKeyFromSpinnerSelected(spRyfl,
                GlobalConstant.KEY));
        // 驾驶证号
        vio.setJszh(edJszh.getText().toString().toUpperCase());
        // 档案编号
        vio.setDabh(edDabh.getText().toString().trim());
        // 发证机关
        vio.setFzjg(edFzjgms.getText().toString().trim());
        // 准驾车型
        vio.setZjcx(edZjcx.getText().toString().trim().toUpperCase());
        // 当事人姓名
        vio.setDsr(edXm.getText().toString().trim());
        // 电话、地址，可空
        vio.setDh(edLxdh.getText().toString().trim());
        vio.setLxfs(edLxdz.getText().toString().trim());
        // 车辆分类
        vio.setClfl(GlobalMethod.getKeyFromSpinnerSelected(spClfl,
                GlobalConstant.KEY));
        // 号牌种类
        vio.setHpzl(GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                GlobalConstant.KEY));
        // 号牌号码
        Editable hp = edHphm.getText();
        String hpql = GlobalMethod.getKeyFromSpinnerSelected(spHpql,
                GlobalConstant.VALUE);
        vio.setHphm(hpql + hp.toString().toUpperCase().trim());
        // 交通方式
        vio.setJtfs(GlobalMethod.getKeyFromSpinnerSelected(spJtfs,
                GlobalConstant.KEY));
        // 违法时间
        vio.setWfsj(edWfsj.getText().toString());
        // 违法地点
        vio.setWfdd(GlobalMethod.getKeyFromSpinnerSelected(spWfdd,
                GlobalConstant.KEY));
        vio.setWfdz(GlobalMethod.getKeyFromSpinnerSelected(spWfdd,
                GlobalConstant.VALUE));
        // 更新时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        vio.setGxsj(sdf.format(new Date()));
        vio.setClsj(sdf.format(new Date()));
        // 违法记分数字段用于存放驾驶员当前累计记分
        vio.setWfjfs(TextUtils.isEmpty(edLjjf.getText()) ? 0 : Integer.valueOf(edLjjf
                .getText().toString()));
        vio.setBzz(edBzz.getText().toString());
        vio.setScz(edScz.getText().toString());
        String zjlx = GlobalMethod.getKeyFromSpinnerSelected(spZjlx, GlobalConstant.KEY);
        vio.setZjlx(zjlx);
        //保存三个字段
        String zzmm = GlobalMethod.getKeyFromSpinnerSelected(spZzmm,
                GlobalConstant.KEY);
        String zyxx = GlobalMethod.getKeyFromSpinnerSelected(spZyxx,
                GlobalConstant.KEY);
        String syxz = GlobalMethod.getKeyFromSpinnerSelected(spSyxz,
                GlobalConstant.KEY);
        String gzxm = ViolationDAO.saveIntoJsonStr("zzmm", zzmm, "zyxx", zyxx, "syxz", syxz);
        vio.setGzxm(gzxm);
    }

    /**
     * 界面中按扭的监听
     *
     * @author lenovo
     */
    //View.OnClickListener butClickListener = new View.OnClickListener() {
    //@Override
    @OnClick({R.id.but_query_drv_sfzh, R.id.But_query_drv})
    public void queryDrv(View v) {
        // 根据档案号查驾驶员信息
        Editable dabh = edDabh.getText();
        Editable sfzh = edJszh.getText();
        Log.e("证件查询", dabh.toString() + "," + sfzh.toString());
        if (TextUtils.isEmpty(dabh) && v.getId() == R.id.But_query_drv) {
            GlobalMethod.showErrorDialog("档案编号不能为空", self);
            return;
        }
        if (TextUtils.isEmpty(sfzh) && v.getId() == R.id.but_query_drv_sfzh) {
            GlobalMethod.showErrorDialog("驾驶证号不能为空", self);
            return;
        }
        if (!GlobalMethod.getKeyFromSpinnerSelected(spRyfl,
                GlobalConstant.KEY).equals("4")) {
            GlobalMethod.showErrorDialog("只有公安驾驶员才能查询", self);
            return;
        }
        // String bd = edFzjgms.getText().toString().trim();
        // GlobalMethod.getKeyFromSpinnerSelected(spChenShi,
        // cityList, GlobalConstant.KEY);
        new CommQueryThread(CommQueryThread.JSON_QUERY_DRV,
                new String[]{dabh.toString().trim(), sfzh.toString().trim()}, self).doStart();
    }

    //获取违法地点
    @OnClick(R.id.But_wfdd)
    public void queryWfdd() {
        // 查询违法地点
        Intent intent = new Intent(self, ConfigWfddActivity.class);
        startActivityForResult(intent, REQCODE_WFDD);
    }

    //查询违法行为
    @OnClick(R.id.But_query_wfxw)
    public void queryWfxw() {
        Intent intent = new Intent(self, ConfigWfdmActivity.class);
        intent.putExtra("comefrom", 1);
        startActivityForResult(intent, REQCODE_WFXW);
    }

    @OnClick(R.id.But_cllx)
    public void queryCllx() {
        // 查询交通方式,如果是非机动车,送一个F,给交通方式进行检索
        Intent intent = new Intent(self, ConfigJtfsActivity.class);
        if ("1".equals(((KeyValueBean) spClfl.getSelectedItem())
                .getKey()))
            intent.putExtra("clfl", "F");
        startActivityForResult(intent, REQCODE_JTFS);
    }

    @OnClick(R.id.btn_fzjg)
    public void queryFzjg() {
        // 选择发证机关
        // 如果编辑框内有文字，根据文字显示
        Intent intent = new Intent(self, JbywVioFzjgActivity.class);
        String fzjgmc = edFzjgms.getText().toString();
        intent.putExtra("fzjg", fzjgmc);
        startActivityForResult(intent, REQCODE_FZJG);
    }

    @OnClick(R.id.But_clbd)
    public void queryClbd() {
        Editable hp = edHphm.getText();
        if (TextUtils.isEmpty(hp)) {
            GlobalMethod.showErrorDialog("号牌号码不能为空", self);
            return;
        }
        String hpzl = GlobalMethod.getKeyFromSpinnerSelected(spHpzl,
                GlobalConstant.KEY);
        String hphm = GlobalMethod.getKeyFromSpinnerSelected(spHpql,
                GlobalConstant.VALUE)
                + hp.toString().trim().toUpperCase();
        new CommQueryThread(CommQueryThread.JSON_QUERY_VEH,
                new String[]{hpzl, hphm}, self).doStart();
    }

    @OnClick(R.id.But_wzrq)
    public void wfrqClick() {
        GlobalMethod.changeDate(edWfsj, self);
    }

    @OnClick(R.id.But_wzsj)
    public void wfsjClick() {
        // 违法时间修改监听
        GlobalMethod.changeTime(edWfsj, self);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btp != null) {
            btp.closeConn();
            btp = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        Log.e("vio", "onStop");
        if (btp != null) {
            btp.closeConn();
            btp = null;
        }
        super.onStop();
    }

    protected void showConVio(final VioViolation violation) {
        final String[] choices = new String[]{"连续处罚", "转警告", "转强制措施",
                "退出"};
        new MaterialDialog.Builder(this)
                .title("请选择一个类型")
                .items(choices)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which != 3) {
                            Intent intent = null;
                            switch (which) {
                                case 0:
                                    intent = new Intent(self, VioJycxActivity.class);
                                    break;
                                case 1:
                                    intent = new Intent(self, VioQwjgActivity.class);
                                    break;
                                case 2:
                                    intent = new Intent(self, VioQzcsActivity.class);
                                    break;
                                //case 3:
                                //    intent = new Intent(self, VioWftzActivity.class);
                                //    break;
                                default:
                                    break;
                            }
                            // intent.putExtra("cfCatalog",
                            // String.valueOf(actives[ch]));
                            if (intent != null) {
                                intent.putExtra("violation", violation);
                                startActivity(intent);
                            } else {
                                GlobalMethod.showErrorDialog("连续处罚出现错误", self);
                            }
                        }
                        finish();
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .show();


    }

    /**
     * 上传处罚决定书的控制操作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadVioHandler(VioUploadEvent event) {
        if (event.scbj == 1) {
            GlobalMethod.showToast("决定书已上传", self);
            ViolationDAO.setVioUploadStatus(event.id, true, GlobalMethod.getBoxStore(self));
        } else {
            GlobalMethod.showToast(event.message, self);
        }
    }

    protected OnClickListener exitVio = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    /**
     * 保存并上传决定书,将决定书加一
     *
     * @return
     */
    protected String saveViolation() {
        violation.setCfzl(cfzl);
        violation.setWslb(wslb);
        if (ViolationDAO.getViolationByJdsbh(violation.getJdsbh(), GlobalMethod.getBoxStore(self)) != null)
            return "决定书出现重号";
        if (ViolationDAO.isViolationDuplicate(violation, GlobalMethod.getBoxStore(self))) {
            return "重复的决定书内容，可能存在误操作";
        }
        if (TextUtils.equals(violation.getWslb(), "6")) {
            return "违法通知书功能已关闭";
        }
        long id = ViolationDAO.saveViolationIntoDB(violation, GlobalMethod.getBoxStore(self));
        if (id > 0) {
            isViolationSaved = true;
            // 更新当前编号
            WsglDAO.saveHmbAddOne(jdsbh, GlobalMethod.getBoxStore(self));
        }
        return "";
    }

    /**
     * 保存确定时的监听
     */
    protected OnClickListener saveVioListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 增加一个验证驾驶员和机动车正确性的异步方法
            //要读取系统参数，确定是否验证和验证的范围
            String ryfl = violation.getRyfl();
            String clfl = violation.getClfl();
            String hpzl = violation.getHpzl();
            boolean drvNeedCheck = (("4".equals(ryfl) && GlobalSystemParam.drvCheckFs > 0)
                    && ((GlobalSystemParam.drvCheckFs == 1 && violation.getDabh().startsWith(
                    "3206")) || GlobalSystemParam.drvCheckFs == 2));
            boolean vehNeedCheck = (("3".equals(clfl) && GlobalSystemParam.vehCheckFs > 0 && "01,02,07,08,15,16"
                    .indexOf(hpzl) > -1)
                    && ((GlobalSystemParam.vehCheckFs == 1 && violation.getHphm().startsWith(
                    "苏F")) || GlobalSystemParam.vehCheckFs == 2));
            if (drvNeedCheck || vehNeedCheck)
                new CheckVehDrvThread(violation).doStart();
            else {
                saveVioAndUpload();
            }

        }
    };

    private void saveVioAndUpload() {
        String err = saveViolation();
        if (TextUtils.isEmpty(err)) {
            GlobalMethod.showDialog("系统信息", violation.getJdsbh()
                    + getViolationTitle() + "文书保存成功!", "确定", self);
            new UploadVioThread(violation, self, false).start();
        } else
            GlobalMethod.showErrorDialog(err, self);
    }

    class CheckVehDrvThread extends Thread {
        private VioViolation v;
        private ProgressDialog progressDialog;

        public CheckVehDrvThread(VioViolation v) {
            this.v = v;
        }

        public void doStart() {
            progressDialog = ProgressDialog.show(self, "提示",
                    "正在验证驾驶员和机动车信息,请稍等...", true);
            progressDialog.setCancelable(true);
            start();
        }

        @Override
        public void run() {
            // 如果三步均通过验证，返回111，否则特定位置为0
            // 分步返回
            int ryCheck = 0;
            int clCheck = 0;
            String wslb = v.getWslb();
            String jdsbh = v.getJdsbh();
            String ryfl = v.getRyfl();
            String clfl = v.getClfl();
            String hpzl = v.getHpzl();
            RestfulDao dao = RestfulDaoFactory.getDao();
            WebQueryResult<String> sre = dao.isDupVio(wslb, jdsbh);
            int checkResult = 0;
            // 验证决定书是否为重复时，不管网络状态，只有结果返回为1时才认为有重复
            if (sre != null && sre.getStatus() == HttpURLConnection.HTTP_OK
                    && TextUtils.equals("1", sre.getResult())) {
                setMsgData(0);
                return;
            }
            checkResult = 1;
            if (("4".equals(ryfl) && GlobalSystemParam.drvCheckFs > 0)
                    && ((GlobalSystemParam.drvCheckFs == 1 && v.getDabh().startsWith(
                    "3206")) || GlobalSystemParam.drvCheckFs == 2)) {
                // 公安驾驶证本地驾驶证，需验证
                WebQueryResult<VioDrvBean> re = dao.queryVioDrv(v.getDabh(),
                        "0");
                String err = GlobalMethod.getErrorMessageFromWeb(re);
                if (TextUtils.isEmpty(err)) {
                    VioDrvBean drv = re.getResult();
                    if (drv != null)
                        ryCheck = 1;
                }
            } else {
                ryCheck = 1;
            }
            if (ryCheck != 1) {
                setMsgData(10);
                return;
            }
            checkResult = 11;
            // 车辆分类为公安号牌且需要验证且号牌种类六
            if (("3".equals(clfl) && GlobalSystemParam.vehCheckFs > 0 && "01,02,07,08,15,16"
                    .indexOf(hpzl) > -1)
                    && ((GlobalSystemParam.vehCheckFs == 1 && v.getHphm().startsWith(
                    "苏F")) || GlobalSystemParam.vehCheckFs == 2)) {
                // 公安机动车
                WebQueryResult<VioVehBean> re = dao.queryVioVeh(v.getHpzl(),
                        v.getHphm());
                String err = GlobalMethod.getErrorMessageFromWeb(re);
                if (TextUtils.isEmpty(err)) {
                    VioVehBean veh = re.getResult();
                    if (veh != null)
                        clCheck = 1;
                }
            } else {
                clCheck = 1;
            }
            checkResult = checkResult * 10 + clCheck;
            setMsgData(checkResult);
        }

        private void setMsgData(int checkResult) {
            JSONObject obj = new JSONObject();
            ParserJson.putJsonVal(obj, "catalog", CommQueryThread.CHECK_DRV_VEH);
            ParserJson.putJsonVal(obj, "checkResult", checkResult);
            EventBus.getDefault().post(obj);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void writeDrvInfoFromyBean(JSONObject drv) {
        edDabh.setText(drv.optString("dabh", ""));
        edXm.setText(drv.optString("xm", ""));
        edJszh.setText(drv.optString("sfzmhm", ""));
        edLxdz.setText(drv.optString("lxzsxxdz", ""));
        edZjcx.setText(drv.optString("zjcx", ""));
        edLxdh.setText(drv.optString("sjhm", ""));
        edLjjf.setText(drv.optString("ljjf", ""));
        edFzjgms.setText(drv.optString("fzjg", ""));
    }

    private void writeVehFromBean(JSONObject veh) {
        String cllx = veh.optString("cllx", "");
        if (!TextUtils.isEmpty(cllx)) {
            GlobalMethod.changeSpinnerSelect(spJtfs, cllx,
                    GlobalConstant.KEY);
        }
        String syxz = veh.optString("syxz", "");
        GlobalMethod.changeSpinnerSelect(spSyxz, syxz,
                GlobalConstant.KEY);
        String hpzl = veh.optString("hpzl", "");
        if (TextUtils.equals("01", hpzl)) {
            String hdzzl = veh.optString("hdzzl", "");
            if (!TextUtils.isEmpty(hdzzl)) {
                edBzz.setText(hdzzl);
            }
        } else if (TextUtils.equals("02", hpzl)) {
            String hdzk = veh.optString("hdzk", "");
            if (!TextUtils.isEmpty(hdzk)) {
                edBzz.setText(hdzk);
            }
        }
    }


    protected void printJdsBySelect(VioViolation vio) {
        if (btp == null && ((btp = GlobalMethod.getBluetoothPrint(this)) == null)) {
            return;
        }
        int status = btp.printJdsByBluetooth(vio, ViolationActivity.this);
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getJtfsData(KeyValueBean obj) {
        Log.e("eventbus", obj.getKey() + "/" + obj.getValue());
        GlobalMethod.changeSpinnerSelect(spJtfs, obj.getKey(), GlobalConstant.KEY);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEventBus(JSONObject obj) {
        //Log.e("eventbus", obj.toString());
        int catalog = obj.optInt("catalog");
        if (catalog == CommQueryThread.JSON_QUERY_DRV || catalog == CommQueryThread.JSON_QUERY_VEH) {
            //查询机动车和驾驶证返回
            Log.e("violation", "查询车证返回：" + obj.toString());
            String err = obj.optString("err");
            if (!TextUtils.isEmpty(err)) {
                GlobalMethod.showErrorDialog(err, self);
                return;
            }
            if (catalog == CommQueryThread.JSON_QUERY_DRV) {
                writeDrvInfoFromyBean(obj);
            } else {
                writeVehFromBean(obj);
            }
            String bdjg = obj.optString("bdjg", "");
            if (!TextUtils.isEmpty(bdjg)) {
                GlobalMethod.showDialog("比对信息", bdjg, "知道了", self);
            }
        } else if (catalog == CommQueryThread.CHECK_DRV_VEH) {
            //保存时验证返回
            int checkResult = obj.optInt("checkResult");
            if (checkResult == 111) {
                saveVioAndUpload();
                return;
            }
            String checkErr = "服务器出现错误";
            if (checkResult == 0) {
                checkErr = "服务器端有相同编号的决定书";
            } else if (checkResult == 10)
                checkErr = "驾驶员信息未通过验证";
            else if (checkResult == 110)
                checkErr = "机动车信息未通过验证";
            GlobalMethod.showErrorDialog(checkErr, self);
        }
    }


    @Override
    public void onBackPressed() {
        // 还没有保存
        if (!isViolationSaved) {
            GlobalMethod.showDialogTwoListener("系统提醒", "本次处罚还没有保存，是否确定退出！",
                    "退出", "返回", exitVio, self);
        } else {
            finish();
        }
    }

    protected String getActivityTitle() {
        // String activityTitle = !TextUtils.isEmpty(printerInfo.getValue()) ?
        // printerInfo
        // .getKey() : "无打印机";
        // activityTitle = "编号: " + jdsbh.getDqhm() + "--" + activityTitle;

        return getViolationTitle();
        // + "--" + activityTitle;
    }

    protected abstract String getViolationTitle();

    protected abstract String saveAndCheckVio();

    protected abstract String showWfdmDetail(VioWfdmCode w);

    protected abstract int getCfzl();

    /**
     * 初始化决定书对象，类中就使用一个对象,不同的处罚加入不同的特定字段
     */
    protected void initViolation() {
        violation = new VioViolation();
        // 一些公共项目
        String fxjg = GlobalData.grxx.get(GlobalConstant.KSBMBH);
        // 发现机关
        violation.setFxjg(fxjg);
        // 多个默认项目
        violation.setZsxzqh("");
        violation.setZsxxdz("");
        violation.setJsjqbj("00");
        violation.setZqmj(GlobalData.grxx.get(GlobalConstant.YHBH));
        // 上传标记和时间
        violation.setScbj("0");
        violation.setCwxx("");
        //violation.setGzxm("");
        //violation.setGzxmmc("");
        violation.setBzz("");
        violation.setScz("");
        if (jdsbh != null) {
            violation.setJdsbh(jdsbh.getDqhm().trim());
            violation.setHdid(jdsbh.getHdid().trim());
        }
    }

    protected boolean menuSaveViolation() {
        if (isViolationSaved) {
            GlobalMethod.showErrorDialog("单据已保存,无需重复保存!", self);
            return true;
        }

        String err = saveAndCheckVio();
        if (!TextUtils.isEmpty(err)) {
            GlobalMethod.showErrorDialog(err, self);
        } else {
            GlobalMethod.showDialogTwoListener("系统提示",
                    "是否保存 " + jdsbh.getDqhm() + getViolationTitle() + " ? 请确认",
                    "保存", "取消", saveVioListener, self);
        }
        return true;
    }

    protected boolean menuPreviewViolation() {
        if (!isViolationSaved) {
            String errInfo = saveAndCheckVio();
            if (!TextUtils.isEmpty(errInfo)) {
                // 保存不成功
                GlobalMethod.showErrorDialog(errInfo, this);
                return true;
            }
        }
        ArrayList<JdsPrintBean> jds = PrintJdsTools.getPrintJdsContent(
                violation, ViolationActivity.this);
        Intent intent = new Intent(self, JdsPreviewActivity.class);
        intent.putExtra("jds", jds);
        startActivityForResult(intent, REQCODE_PREVIEW_JDS);
        return true;
    }

    protected boolean menuPrintViolation() {
        if (isViolationSaved && violation != null) {
            printJdsBySelect(violation);
        } else {
            GlobalMethod.showErrorDialog("决定书还没有保存,请保存后再打印", self);
        }
        return true;
    }

    /**
     * 打开相机拍照片
     *
     * @param photoTo 照片的用处，可能是识别
     */
    protected void startTakePhoto(int photoTo) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = GlobalMethod.createImageFile(this,
                    false);
            if (photoFile != null) {
                photoName = photoFile.getAbsolutePath();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri photoURI = FileProvider.getUriForFile(self, "com.jwt.main.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                Log.e("fxc photo", photoFile.getAbsolutePath());
                startActivityForResult(takePictureIntent, photoTo);
            }
        }
    }

    private void camerResult() {
        if (TextUtils.isEmpty(photoName))
            return;
        File image = new File(photoName);
        Log.e("mCurrentPhotoPath", photoName);
        if (!image.exists()) {
            Toast.makeText(self, "照片拍摄失败", Toast.LENGTH_LONG).show();
            return;
        }
        File dir = image.getParentFile();
        String fn = image.getName();
        dir = new File(dir, "vio");
        if (!dir.exists())
            dir.mkdirs();
        File smallF = new File(dir, fn);
        String text = "拍摄时间："
                + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        Bitmap smallImage = GlobalMethod.compressBitmap(photoName, 800, text);
        if (smallImage == null) {
            Toast.makeText(self, "照片压缩失败", Toast.LENGTH_LONG).show();
            return;
        }
        boolean isSave = GlobalMethod.savePicIntoFile(smallImage, smallF);
        if (!isSave) {
            Toast.makeText(self, "照片保存失败", Toast.LENGTH_LONG).show();
            return;
        }
        //showImageActivity(smallF.getAbsolutePath());
        violation.setPicFile(smallF.getAbsolutePath());
        ViolationDAO.saveViolationIntoDB(violation, GlobalMethod.getBoxStore(self));
        Toast.makeText(self, "照片保存成功", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_SAVE, isViolationSaved);
        if (violation != null)
            outState.putSerializable(STATE_VIOLATION, violation);
        if (jdsbh != null)
            outState.putSerializable(JDSBH, jdsbh);
        if (cfzl != null) {
            outState.putString(CFZL, cfzl);
        }
        if (wslb != null) {
            outState.putString(WSLB, wslb);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle ss) {
        super.onRestoreInstanceState(ss);
        Log.e(TAG, "onRestoreInstanceState");
        if (ss.containsKey(STATE_IS_SAVE))
            isViolationSaved = ss.getBoolean(STATE_IS_SAVE);
        if (ss.containsKey(STATE_VIOLATION))
            violation = (VioViolation) ss.getSerializable(STATE_VIOLATION);
        if (ss.containsKey(JDSBH))
            jdsbh = (THmb) ss.getSerializable(JDSBH);
        if (ss.containsKey(CFZL))
            cfzl = ss.getString(CFZL);
        if (ss.containsKey(WSLB))
            wslb = ss.getString(WSLB);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.violation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.save_quite):
                return menuSaveViolation();
            case (R.id.print_preview):
                // 预览打印
                return menuPreviewViolation();
            case (R.id.pre_print):
                // 单据已保存，打印决定书
                return menuPrintViolation();
            case R.id.con_vio:
                if (violation != null && isViolationSaved)
                    showConVio(violation);
                else
                    GlobalMethod.showToast("请保存当前决定书", self);
                return true;
            case R.id.sys_config:
                Intent intent = new Intent(self, ConfigParamSetting.class);
                intent.putExtra("data", "1");
                intent.putExtra("title", "系统参数设定");
                startActivity(intent);
                return true;
            case R.id.take_photo:
                if (violation != null && isViolationSaved && violation.getId() > 0)
                    startTakePhoto(CAMER_REQUEST);
                else
                    GlobalMethod.showToast("请保存当前决定书", self);
                return true;
            case R.id.show_image:
                if (violation != null && isViolationSaved && violation.getId() > 0 && !TextUtils.isEmpty(violation.getPicFile())) {
                    GlobalMethod.showImageActivity(violation.getPicFile(), self);
                }
                return true;
            case R.id.upload_image:
                new CommUploadThread(CommUploadThread.UPLOAD_VIO_PIC, new Object[]{violation}, self)
                        .doStart();
                return true;
            case R.id.orc_sfzh:
                Intent it = new Intent(self, JbywPhotoOcrActivity.class);
                it.putExtra("ocr", "ocr");
                it.putExtra("catalog", 0);
                startActivityForResult(it, REQCODE_OCR);
                return true;
        }
        return false;
    }

}
