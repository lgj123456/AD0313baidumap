package com.example.yhdj.ad0313baidumap;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.model.BaiduPoiPanoData;
import com.baidu.lbsapi.panoramaview.*;
import com.baidu.pano.platform.plugin.indooralbum.IndoorAlbumPlugin;

import static android.R.attr.level;

/**
 * Created by yhdj on 2017/3/9.
 */
public class PanoramaDemoActivityMain extends AppCompatActivity implements com.baidu.lbsapi.panoramaview.PanoramaViewListener{
    private PanoramaView mPanoramaView;
    private double latitude;
    private double longitude;
    private String uid;
    private Button btn_streetPano;
    private Button btn_innerPano;
    private boolean isStreetPano = false;
    private boolean isInnerPano = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);
        getLocation();
        initViews();
     //   initManager();

    }

    private void getLocation() {
        Intent intent = getIntent();
        if (intent != null) {
            latitude = intent.getDoubleExtra("latitude", 22.995318800316);
            longitude = intent.getDoubleExtra("longitude", 113.2767829097);
            uid = intent.getStringExtra("uid");
            Toast.makeText(this, "" + uid, Toast.LENGTH_SHORT).show();
            Log.i("uid", "getLocation: " + uid);
        }
    }

    private void initViews() {
        mPanoramaView = (PanoramaView) findViewById(R.id.panorama);
        IndoorAlbumPlugin.getInstance().init();

        btn_innerPano = (Button) findViewById(R.id.innerPano);
        btn_streetPano = (Button) findViewById(R.id.streetPano);
        btn_streetPano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStreetPano = true;
                initManager();
            }
        });

        btn_innerPano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInnerPano = true;
                initManager();
            }
        });

        //113.359554,23.189111
//设置全景图的俯仰角
        // mPanoramaView.setPanoramaPitch(pitch);
//获取当前全景图的俯仰角
//更新俯仰角的取值范围：室外景[-15, 90], 室内景[-25, 90],
//90为垂直朝上方向，0为水平方向
        // mPanoramaView.getPanoramaPitch();
//设置全景图的偏航角
        //  mPanoramaView.setPanoramaHeading(heading);
//获取当前全景图的偏航角
        //  mPanoramaView.getPanoramaHeading();
//设置全景图的缩放级别
//level分为1-5级
        mPanoramaView.setPanoramaLevel(level);
//获取当前全景图的缩放级别
        mPanoramaView.getPanoramaLevel();
//是否显示邻接街景箭头（有邻接全景的时候）
        mPanoramaView.setShowTopoLink(true);

//设置全景图片的显示级别
//根据枚举类ImageDefinition来设置清晰级别
//较低清晰度 ImageDefinationLow
//中等清晰度 ImageDefinationMiddle
//较高清晰度 ImageDefinationHigh
        mPanoramaView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);

//根据bitmap设置箭头的纹理(2.0.0新增)
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.b);
//实际上这是一个BitmapDrawable对象
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//可以在调用getBitmap方法，得到这个位图
        Bitmap bitmap = bitmapDrawable.getBitmap();
        mPanoramaView.setArrowTextureByBitmap(bitmap);
//根据url设置箭头的纹理(2.0.0新增)
        //   mPanoramaView.setArrowTextureByUrl(url);
    }

    private void initManager() {
        Log.i("ddddddd", "initManager: ddddddddd");
        PanoDemoApplication app = (PanoDemoApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);

            app.mBMapManager.init(new PanoDemoApplication.MyGeneralListener());
        }
        PanoramaRequest request = PanoramaRequest.getInstance(PanoramaDemoActivityMain.this);
        BaiduPoiPanoData poiPanoData = request.getPanoramaInfoByUid(uid);
        //判断是否有外景(街景)及内景
        //   poiPanoData.hasStreetPano();
        if (longitude != 0 && latitude != 0) {

            if(isStreetPano){
                if (poiPanoData.hasStreetPano()){
                    mPanoramaView.setPanorama(longitude,latitude);
                }else{
                    Toast.makeText(app, "您所选的地点没有外景图！！！", Toast.LENGTH_SHORT).show();
                }

                isStreetPano = false;
            }
            if(isInnerPano){
                isInnerPano = false;
                if (poiPanoData.hasInnerPano()) {
                    mPanoramaView.setPanoramaByUid(uid, PanoramaView.PANOTYPE_INTERIOR);
                } else {
                    Toast.makeText(app, "您所选的地点没有内景图！！！", Toast.LENGTH_SHORT).show();
                }
            }




            //  mPanoramaView.setPanoramaByUid("28e700f15aae5418085cb3a7", PanoramaView.PANOTYPE_INTERIOR);
        }

        //  mPanoramaView.setPanorama("0100220000130817164838355J5");
    }

    /**
     * 监听全景视图加载的各种状态
     */


    @Override
    public void onDescriptionLoadEnd(String s) {
        Toast.makeText(this, "全景图加载完成1", Toast.LENGTH_SHORT).show();
    }
    /**
     * 全景改变开始触发
     */
    @Override
    public void onLoadPanoramaBegin() {
        Toast.makeText(this, "全景图加载完成2", Toast.LENGTH_SHORT).show();
    }
    /**
     * 全景图加载完成,在描述信息加载完之后
     */
    @Override
    public void onLoadPanoramaEnd(String s) {
        Toast.makeText(this, "全景图加载完成", Toast.LENGTH_SHORT).show();
    }
    /**
     * 全景加载异常
     */
    @Override
    public void onLoadPanoramaError(String s) {
        Toast.makeText(this, "全景加载异常", Toast.LENGTH_SHORT).show();
    }
    /**
     * 全景操作的回调(旋转点击等)
     */
    @Override
    public void onMessage(String s, int i) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    /**
     * 自定义marker点击事件(深度定制可以使用,自定义marker点击事件参见demo的简	 * 单使用)
     */
    @Override
    public void onCustomMarkerClick(String s) {

    }
}
