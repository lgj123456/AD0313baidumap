package com.example.yhdj.ad0313baidumap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;

public class GuideActivity extends AppCompatActivity {
    private EditText mEdt_staring;
    private EditText mEdt_end;
    private Button mBtn_guide;
    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "AD0313baidumap";
    private String authinfo;
    private CoordinateType mCoordinateType;
    private boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";

    private final static String authBaseArr[] =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
    private final static String authComArr[] = {Manifest.permission.READ_PHONE_STATE};
    private final static int authBaseRequestCode = 1;
    private final static int authComRequestCode = 2;
    public static List<Activity> activityList = new LinkedList<Activity>();
    private Button mWgsNaviBtn;
    private double begin_latitude;
    private double begin_lontitude;
    private double end_latitude;
    private double end_lontitude;
    private GeoCoder mSearch;
    private EditText mEdt_begin_address;
    private EditText mEdi_end_address;
    private String begin_city;
    private String end_city;
    private String begin_address;
    private String end_address;
    private boolean isGetEndResult = false;
    private boolean isGetBeginResult = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mSearch = GeoCoder.newInstance();
        initViews();
        activityList.add(this);
        initListener();
        mSearch.setOnGetGeoCodeResultListener(listener);
        if (initDirs()) {
            initNavi();
        }
    }

    private void initListener() {
//        if (mWgsNaviBtn != null) {
//            mWgsNaviBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    if (BaiduNaviManager.isNaviInited()) {
//                        routeplanToNavi(CoordinateType.WGS84);
//                    }
//                }
//
//            });
//        }

    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    private void routeplanToNavi(CoordinateType coType) {
        mCoordinateType = coType;

        if (!hasInitSuccess) {
            Toast.makeText(GuideActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        // 权限申请
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    this.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    Toast.makeText(GuideActivity.this, "没有完备的权限!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case GCJ02: {
                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
                break;
            }
            case WGS84: {
                sNode = new BNRoutePlanNode(begin_lontitude, begin_latitude, begin_address, null, coType);
                eNode = new BNRoutePlanNode(end_lontitude, end_latitude, end_address, null, coType);
                break;
            }
            case BD09_MC: {
                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
                break;
            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.40386525193937, 39.915160800132085, "北京天安门", null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }

    private boolean hasCompletePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public class DemoRoutePlanListener implements RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
        /*
         * 设置途径点以及resetEndNode会回调该接口
         */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("naviActivity")) {

                    return;
                }
            }
            Intent intent = new Intent(GuideActivity.this, naviActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            intent.putExtra("latitude",begin_latitude);
            intent.putExtra("longtitude",begin_lontitude);
            intent.putExtra("city",begin_address);
            startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(GuideActivity.this, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }


    private void initNavi() {
        BNOuterTTSPlayerCallback ttsCallback = null;

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {

                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;

            }
        }

        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        GuideActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(GuideActivity.this, authinfo, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    public void initSuccess() {
                        Toast.makeText(GuideActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;
                        initSetting();
                    }

                    public void initStart() {
                        Toast.makeText(GuideActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    public void initFailed() {
                        Toast.makeText(GuideActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }
                }, null /*mTTSCallback*/);
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();

        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initViews() {
        mEdt_begin_address = (EditText) findViewById(R.id.edt_begin_address);
        mEdi_end_address = (EditText) findViewById(R.id.edt_end_address);
        //  mWgsNaviBtn = (Button) findViewById(R.id.wgsNaviBtn);
        mEdt_staring = (EditText) findViewById(R.id.edt_staring);
        mEdt_end = (EditText) findViewById(R.id.edt_end);
        mBtn_guide = (Button) findViewById(R.id.btn_guide);
        mBtn_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                begin_city = mEdt_staring.getText().toString().trim();
                end_city = mEdt_end.getText().toString();
                begin_address = mEdt_begin_address.getText().toString().trim();
                end_address = mEdi_end_address.getText().toString().trim();
                mSearch.geocode(new GeoCodeOption()
                        .city(begin_city)
                        .address(begin_address));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            isGetEndResult = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mSearch.geocode(new GeoCodeOption()
                                .city(end_city)
                                .address(end_address));

                    }
                }).start();


            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(GuideActivity.this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }
            routeplanToNavi(mCoordinateType);
        }

    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };


    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
//            showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
//            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    public void showToastMsg(final String msg) {
        GuideActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(GuideActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mSearch.destroy();
    }


    //  创建地理编码检索监听者；
    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                Toast.makeText(GuideActivity.this, "没有检索到结果", Toast.LENGTH_SHORT).show();
            }
            //获取地理编码结果
            // Toast.makeText(GuideActivity.this, "" + result.getLocation().latitude , Toast.LENGTH_SHORT).show();
            if (isGetBeginResult) {
                begin_latitude = result.getLocation().latitude;
                begin_lontitude = result.getLocation().longitude;
                isGetBeginResult = false;
            }

            if (isGetEndResult) {
                end_latitude = result.getLocation().latitude;
                end_lontitude = result.getLocation().longitude;
                isGetEndResult = false;
            }
            if (BaiduNaviManager.isNaviInited() && end_lontitude != 0) {
                routeplanToNavi(CoordinateType.WGS84);
            }
            Log.i("eeeeeeeeeeeeeee", "onGetGeoCodeResult: " + begin_latitude + " " + begin_lontitude + " " + end_latitude + " " + end_lontitude);
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
                Toast.makeText(GuideActivity.this, "没有检索到结果+1", Toast.LENGTH_SHORT).show();

            }
            //获取反向地理编码结果
            Toast.makeText(GuideActivity.this, "+1" + result.getLocation().latitude, Toast.LENGTH_SHORT).show();

        }
    };

}
