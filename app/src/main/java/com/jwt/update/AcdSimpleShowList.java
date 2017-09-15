package com.jwt.update;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;

import com.jwt.activity.CommTwoRowSelectAcbarListActivity;
import com.jwt.bean.KeyValueBean;
import com.jwt.bean.TwoColTwoSelectBean;
import com.jwt.dao.AcdSimpleDao;
import com.jwt.dao.WsglDAO;
import com.jwt.jbyw.JdsPreviewActivity;
import com.jwt.pojo.AcdPhotoBean;
import com.jwt.pojo.AcdSimpleBean;
import com.jwt.pojo.AcdSimpleHumanBean;
import com.jwt.printer.BlueToothPrint;
import com.jwt.printer.JdsPrintBean;
import com.jwt.printer.PrintAcdTools;
import com.jwt.thread.CommUploadThread;
import com.jwt.utils.GlobalConstant;
import com.jwt.utils.GlobalData;
import com.jwt.utils.GlobalMethod;
import com.jwt.web.WebQueryResult;
import com.jwt.zapc.ZapcReturn;

public class AcdSimpleShowList extends CommTwoRowSelectAcbarListActivity {

    protected static final int MENU_UPLOAD_ACD = 1;
    protected static final int MENU_MODIFY_ACD = 2;
    protected static final int MENU_DETAIL_ACD = 3;
    protected static final int MENU_PRINT_ACD = 4;
    protected static final int MENU_PREVIEW_ACD = 5;
    private static final int REQ_MODIFY_ACD = 0;
    protected static final int REQ_NEW_CAD = 1;
    private List<AcdSimpleBean> acds;
    private Context self;
    private Button btnTakePic, btnNewAcd, btnShowPhoto, btnDel;
    private KeyValueBean printerInfo;
    private BlueToothPrint btp = null;
    private String zqmj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        if (GlobalData.grxx == null)
            GlobalData.initGlobalData(GlobalMethod.getBoxStore(self));
        zqmj = GlobalData.grxx.get(GlobalConstant.YHBH);
        if (TextUtils.isEmpty(zqmj))
            return;
        setContentView(R.layout.comm_four_btn_show_list);
        setTitle(getIntent().getStringExtra("title"));
        String pn = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_NAME);
        String pd = GlobalData.grxx.get(GlobalConstant.GRXX_PRINTER_ADDRESS);
        if (!TextUtils.isEmpty(pn) && !TextUtils.isEmpty(pd))
            printerInfo = new KeyValueBean(pn, pd);
        // 初始化打印机
        acds = AcdSimpleDao.getAllAcd(GlobalMethod.getBoxStore(self));
        createBeanFromAcd();
        initView();
        btnNewAcd = (Button) findViewById(R.id.btn_one);
        btnNewAcd.setText("新增");
        btnTakePic = (Button) findViewById(R.id.btn_two);
        btnTakePic.setText("拍照片");
        btnShowPhoto = (Button) findViewById(R.id.btn_three);
        btnShowPhoto.setText("看照片");
        btnDel = (Button) findViewById(R.id.btn_four);
        btnDel.setText("删除");
        getListView().setOnCreateContextMenuListener(createMenuListener);

        btnNewAcd.setOnClickListener(btnClick);
        btnTakePic.setOnClickListener(btnClick);
        btnDel.setOnClickListener(btnClick);
        btnShowPhoto.setOnClickListener(btnClick);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == btnNewAcd) {
                int count = WsglDAO.getJdsCount(
                        String.valueOf(GlobalConstant.ACDSIMPLEWS), zqmj,
                        GlobalMethod.getBoxStore(self));
                if (count < 1) {
                    GlobalMethod.showErrorDialog("请首先获取法律文书", self);
                    return;
                }
                Intent intent = new Intent(self, AcdJycxJbqklrActivity.class);
                startActivityForResult(intent, REQ_NEW_CAD);
            } else {
                if (selectedIndex > -1) {
                    final AcdSimpleBean acdSimple = acds.get(selectedIndex);
                    List<AcdPhotoBean> photoList = AcdSimpleDao.getAllAcdPhoto(
                            acdSimple.getWsbh(), GlobalMethod.getBoxStore(self)
                    );
                    if (v == btnTakePic) {
                        if (photoList.isEmpty()) {
                            AcdPhotoBean acd = new AcdPhotoBean();
                            acd.setScbj(0);
                            acd.setSgbh(acdSimple.getWsbh());
                            acd.setSgdd(acdSimple.getSgdd());
                            acd.setSgsj(acdSimple.getSgfssj());
                            Intent intent = new Intent(self,
                                    AcdTakePhotoActivity.class);
                            intent.putExtra(AcdSimpleDao.PHOTO_BEAN, acd);
                            intent.putExtra(AcdSimpleDao.OPER_MOD,
                                    AcdSimpleDao.ACD_MOD_MODITY);
                            startActivity(intent);
                        } else {
                            GlobalMethod.showErrorDialog(
                                    "已拍照片，无需重复拍照，如需重拍，请到列表中删除后再拍", self);
                        }
                    } else if (v == btnShowPhoto) {
                        if (!photoList.isEmpty()) {
                            AcdPhotoBean acd = photoList.get(0);
                            Intent intent = new Intent(self,
                                    AcdTakePhotoActivity.class);
                            //intent.putExtra(AcdSimpleDao.PHOTO_BEAN, acd);
                            intent.putExtra(AcdSimpleDao.OPER_MOD,
                                    AcdSimpleDao.ACD_MOD_SHOW);
                            startActivity(intent);

                        } else {
                            GlobalMethod.showErrorDialog("还没有拍照，无法看照片", self);
                        }
                    } else if (v == btnDel) {
                        if (!TextUtils.equals(acdSimple.getScbj(), "1")) {
                            GlobalMethod.showDialogTwoListener(
                                    getString(R.string.sys_prompt_text),
                                    "事故信息还没有上传，删除将不可恢复，是否确定删除？", "删除", "返回",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            delAcdAndHuman(acdSimple);
                                        }
                                    }, self
                            );
                        } else {
                            delAcdAndHuman(acdSimple);
                        }
                    }
                } else {
                    GlobalMethod.showErrorDialog("请选择一条记录进行操作", self);
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acd_jycx_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (selectedIndex < 0) {
            GlobalMethod.showErrorDialog("请选择一条记录操作", self);
            return false;
        }
        switch (item.getItemId()) {
            case R.id.menu_upload: {
                menuUploadAcd(selectedIndex);
            }
            return true;
            case R.id.menu_print: {
                menuPrintAcd(selectedIndex);
            }
            return true;
            case R.id.menu_preview: {
                menuPreview(selectedIndex);
            }
            return true;
            case R.id.menu_detail: {
                menuShowDetailAcd(selectedIndex);
            }
            return true;
            default:
                break;
        }
        return false;
    }

    private void createBeanFromAcd() {
        if (beanList == null)
            beanList = new ArrayList<TwoColTwoSelectBean>();
        beanList.clear();
        for (AcdSimpleBean acd : acds) {
            long id = acd.getId();
            List<AcdSimpleHumanBean> list = AcdSimpleDao.queryHumanByCond(
                    id, GlobalMethod.getBoxStore(self)
            );
            if (list == null)
                continue;
            Log.e("JYCS_LIST", " " + acd.getSgdd() + acd.getMs());
            String text1 = acd.getWsbh() + "|" + acd.getSgfssj();
            String text2 = "";
            for (AcdSimpleHumanBean h : list) {
                text2 += h.getXm() + "|";
            }
            text2 += acd.getSgdd();
            boolean isSc = TextUtils.equals(acd.getScbj(), "1");
            beanList.add(new TwoColTwoSelectBean(text1, text2, isSc, false));
        }
    }

    private void referListView() {
        acds = AcdSimpleDao.getAllAcd(GlobalMethod.getBoxStore(self));
        createBeanFromAcd();
        getCommAdapter().notifyDataSetChanged();
    }

    private OnCreateContextMenuListener createMenuListener = new OnCreateContextMenuListener() {

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenuInfo menuInfo) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int pos = mi.position;
            if (pos > -1 && acds != null && acds.size() > 0) {
                AcdSimpleBean acd = acds.get(pos);
                menu.add(Menu.NONE, MENU_DETAIL_ACD, Menu.NONE, "显示详细信息");
                menu.add(Menu.NONE, MENU_PREVIEW_ACD, Menu.NONE, "预览打印认定书");
                if (!TextUtils.equals(acd.getScbj(), "1")) {
                    menu.add(Menu.NONE, MENU_MODIFY_ACD, Menu.NONE, "修改该事故");
                    menu.add(Menu.NONE, MENU_UPLOAD_ACD, Menu.NONE, "上传该事故");
                } else {
                    menu.add(Menu.NONE, MENU_PRINT_ACD, Menu.NONE, "打印事故认定书");
                }
            } else {
            }
        }
    };

    private void menuShowDetailAcd(int pos) {
        final AcdSimpleBean acd = acds.get(pos);
        Intent intent = new Intent(self, AcdJycxJbqklrActivity.class);
        intent.putExtra("acd", acd);
        intent.putExtra(AcdSimpleDao.OPER_MOD, AcdSimpleDao.ACD_MOD_SHOW);
        startActivity(intent);
    }

    private void menuModAcd(int pos) {
        final AcdSimpleBean acd = acds.get(pos);
        if (TextUtils.equals(acd.getScbj(), "1")) {
            GlobalMethod.showErrorDialog("记录已上传，不能修改", self);
            return;
        }
        Intent intent = new Intent(self, AcdJycxJbqklrActivity.class);
        intent.putExtra("acd", acd);
        intent.putExtra(AcdSimpleDao.OPER_MOD, AcdSimpleDao.ACD_MOD_MODITY);
        startActivityForResult(intent, REQ_MODIFY_ACD);
    }

    private void menuPreview(int pos) {
        final AcdSimpleBean acd = acds.get(pos);
        final List<AcdSimpleHumanBean> humans = AcdSimpleDao
                .queryHumanByCond(acd.getId(), GlobalMethod.getBoxStore(self));
        ArrayList<JdsPrintBean> jds = PrintAcdTools.getPrintAcdContent(acd, humans);
        Intent intent = new Intent(self, JdsPreviewActivity.class);
        intent.putExtra("jds", jds);
        startActivity(intent);
    }

    private void menuPrintAcd(int pos) {
        final AcdSimpleBean acd = acds.get(pos);
        if (TextUtils.equals(acd.getScbj(), "0")) {
            GlobalMethod.showErrorDialog("上传后才能打印", self);
            return;
        }
        final List<AcdSimpleHumanBean> humans = AcdSimpleDao
                .queryHumanByCond(acd.getId(), GlobalMethod.getBoxStore(self));
        printAcdBySelect(acd, humans);
    }

    private void menuUploadAcd(int pos) {
        final AcdSimpleBean acd = acds.get(pos);
        if (TextUtils.equals(acd.getScbj(), "1")) {
            GlobalMethod.showErrorDialog("记录已上传，无需重复上传", self);
            return;
        }
        final List<AcdSimpleHumanBean> humans = AcdSimpleDao
                .queryHumanByCond(acd.getId(), GlobalMethod.getBoxStore(self));
        UploadAcdHandler handler = new UploadAcdHandler(this);
        CommUploadThread thread = new CommUploadThread(CommUploadThread.UPLOAD_ACD, new Object[]{acd, humans}, self);
        thread.doStart();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        int pos = mi.position;
        if (pos > -1 && acds != null && acds.size() > 0) {
            switch (item.getItemId()) {
                case MENU_DETAIL_ACD: {
                    menuShowDetailAcd(pos);
                }
                break;
                case MENU_MODIFY_ACD: {
                    menuModAcd(pos);
                }
                break;
                case MENU_PREVIEW_ACD: {
                    menuPreview(pos);
                }
                break;
                case MENU_PRINT_ACD:
                    menuPrintAcd(pos);
                    break;
                case MENU_UPLOAD_ACD:
                    menuUploadAcd(pos);
                    break;

                default:
                    break;
            }
        }
        return false;
    }

    static class UploadAcdHandler extends Handler {

        private final WeakReference<AcdSimpleShowList> myActivity;

        public UploadAcdHandler(AcdSimpleShowList activity) {
            myActivity = new WeakReference<AcdSimpleShowList>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AcdSimpleShowList ac = myActivity.get();
            if (ac != null) {
                ac.uploadAcdMsg(msg);
            }
        }
    }

    private void uploadAcdMsg(Message msg) {
        Bundle data = msg.getData();
        if (data == null)
            return;
        AcdSimpleBean acd = (AcdSimpleBean) data
                .getSerializable(CommUploadThread.UPLOAD_ACD_BEAN);
        WebQueryResult<ZapcReturn> re = (WebQueryResult<ZapcReturn>) data
                .getSerializable(CommUploadThread.RESULT_UPLOAD_ACD);
        if (acd == null || re == null)
            return;
        String err = GlobalMethod.getErrorMessageFromWeb(re);
        if (TextUtils.isEmpty(err)) {
            ZapcReturn result = re.getResult();
            if (TextUtils.equals("1", result.getCgbj())) {
                AcdSimpleDao.saveAcdSimpleScbj(acd.getSgbh(), GlobalMethod.getBoxStore(self));
                referListView();
                GlobalMethod.showDialog("系统提示", "简易程序事故上传成功", "确定", self);
            } else {
                GlobalMethod.showErrorDialog("上传失败", self);
            }
        } else {
            GlobalMethod.showErrorDialog(err, self);
        }
    }

    private void delAcdAndHuman(AcdSimpleBean acd) {
        AcdSimpleDao.delAcdAndHuman(acd.getId(), GlobalMethod.getBoxStore(self));
        referListView();
    }

    private void printAcdBySelect(AcdSimpleBean acd,
                                  List<AcdSimpleHumanBean> humans) {
        if (TextUtils.isEmpty(printerInfo.getValue())) {
            GlobalMethod.showErrorDialog("没有配置默认打印机!", self);
            return;
        }
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return;
        }
        if (btp == null)
            btp = new BlueToothPrint(printerInfo.getValue());
        if (btp.getBluetoothStatus() != BlueToothPrint.BLUETOOTH_STREAMED) {
            // 没有建立蓝牙串口流
            int errorStaus = btp.createSocket(btAdapter);
            if (errorStaus != BlueToothPrint.SOCKET_SUCCESS) {
                GlobalMethod.showErrorDialog(
                        btp.getBluetoothCodeMs(errorStaus), self);
                return;
            }
        }
        int status = btp.printAcdByBluetooth(acd, humans);
        // 打印错误描述
        if (status != BlueToothPrint.PRINT_SUCCESS) {
            GlobalMethod.showErrorDialog(btp.getBluetoothCodeMs(status), self);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (btp != null) {
            btp.closeConn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_MODIFY_ACD || requestCode == REQ_NEW_CAD) {
                referListView();
            }
        }
    }

}
