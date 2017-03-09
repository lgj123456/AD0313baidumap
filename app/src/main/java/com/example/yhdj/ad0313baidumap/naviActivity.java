package com.example.yhdj.ad0313baidumap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;

public class naviActivity extends AppCompatActivity {

    private boolean useCommonInterface = true;
    private final String TAG = naviActivity.class.getName();
    private BNRoutePlanNode mBNRoutePlanNode = null;
    private BaiduNaviCommonModule mBaiduNaviCommonModule = null;
    private Handler hd = null;
    private static final int MSG_SHOW = 1;
    private static final int MSG_HIDE = 2;
    private static final int MSG_RESET_NODE = 3;
    private BNRouteGuideManager.OnNavigationListener mOnNavigationListener;
    private double latitude;
    private double longtitude;
    private String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        GuideActivity.activityList.add(this);
      createHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        }
        View view = null;
        if (useCommonInterface) {
            //使用通用接口
            mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(
                    NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, this,
                    BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE, mOnNavigationListener);
            if (mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onCreate();
                view = mBaiduNaviCommonModule.getView();
            }

        } else {
            //使用传统接口
            view = BNRouteGuideManager.getInstance().onCreate(this, mOnNavigationListener);
        }


        if (view != null) {
            setContentView(view);
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            city = intent.getStringExtra("city");
            latitude = intent.getDoubleExtra("latitude",0);
            longtitude = intent.getDoubleExtra("longtitude",0);
            if (bundle != null) {
                mBNRoutePlanNode = (BNRoutePlanNode) bundle.getSerializable(GuideActivity.ROUTE_PLAN_NODE);

            }
        }
    }

    private void createHandler() {
        if (hd == null) {
            hd = new Handler(getMainLooper()) {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == MSG_SHOW) {

                    } else if (msg.what == MSG_HIDE) {
                        BNRouteGuideManager.getInstance().showCustomizedLayer(false);
                    } else if (msg.what == MSG_RESET_NODE) {
                        BNRouteGuideManager.getInstance().resetEndNodeInNavi(
                                new BNRoutePlanNode(longtitude, latitude, city, null, BNRoutePlanNode.CoordinateType.GCJ02));
                    }
                }

                ;
            };
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (useCommonInterface) {
            if (mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onResume();
            }
        } else {
            BNRouteGuideManager.getInstance().onResume();
        }


        if (hd != null) {
            hd.sendEmptyMessageAtTime(MSG_SHOW, 2000);
        }
    }

    protected void onPause() {
        super.onPause();

        if (useCommonInterface) {
            if (mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onPause();
            }
        } else {
            BNRouteGuideManager.getInstance().onPause();
        }

    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (useCommonInterface) {
            if (mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onDestroy();
            }
        } else {
            BNRouteGuideManager.getInstance().onDestroy();
        }
        GuideActivity.activityList.remove(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (useCommonInterface) {
            if (mBaiduNaviCommonModule != null) {
                mBaiduNaviCommonModule.onStop();
            }
        } else {
            BNRouteGuideManager.getInstance().onStop();
        }


        mOnNavigationListener = new BNRouteGuideManager.OnNavigationListener() {

            @Override
            public void onNaviGuideEnd() {
                //退出导航
                finish();
            }

            @Override
            public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {

                if (actionType == 0) {
                    //导航到达目的地 自动退出
                    Log.i(TAG, "notifyOtherAction actionType = " + actionType + ",导航到达目的地！");
                }

                Log.i(TAG, "actionType:" + actionType + "arg1:" + arg1 + "arg2:" + arg2 + "obj:" + obj.toString());
            }


        };
    }
}