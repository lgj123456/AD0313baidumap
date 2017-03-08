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
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.pano.platform.plugin.indooralbum.IndoorAlbumPlugin;

import static android.R.attr.level;

/**
 * Created by yhdj on 2017/3/9.
 */
public class PanoramaDemoActivityMain extends AppCompatActivity {
    private PanoramaView mPanoramaView;
    private double latitude;
    private double longitude;
    private String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        getLocation();
        initViews();
        initManager();

    }

    private void getLocation() {
        Intent intent = getIntent();
        if(intent != null){
            latitude = intent.getDoubleExtra("latitude",22.995318800316);
            longitude = intent.getDoubleExtra("longitude",113.2767829097);
            uid = intent.getStringExtra("uid");
            Toast.makeText(this, "" + uid, Toast.LENGTH_SHORT).show();
            Log.i("uid", "getLocation: " + uid);
        }
    }

    private void initViews() {
        mPanoramaView = (PanoramaView) findViewById(R.id.panorama);
        IndoorAlbumPlugin.getInstance().init();

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
        mPanoramaView. setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);

//根据bitmap设置箭头的纹理(2.0.0新增)
        Resources res=getResources();
        Drawable drawable = res.getDrawable(R.drawable.b);
//实际上这是一个BitmapDrawable对象
        BitmapDrawable bitmapDrawable=(BitmapDrawable)drawable;
//可以在调用getBitmap方法，得到这个位图
        Bitmap bitmap=bitmapDrawable.getBitmap();
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
        if(longitude != 0 && latitude != 0){
           // mPanoramaView.setPanorama(longitude,latitude);
            Log.i("ddddddd", "initManager: ddddddddd");
            try{
                Log.i("ddddddd", "initManager: ddddddddd");
                mPanoramaView.setPanoramaByUid(uid, PanoramaView.PANOTYPE_INTERIOR);
                Log.i("ddddddd", "initManager: ddddddddd");
            }catch (Exception e){
                Log.i("ddddddd", "initManager: ddddddddd");
                Toast.makeText(app, "您所选的地点没有内景图！！！", Toast.LENGTH_SHORT).show();
                Log.i("ddddddd", "initManager: ddddddddd");
            }finally {
                mPanoramaView.setPanoramaByUid("28e700f15aae5418085cb3a7", PanoramaView.PANOTYPE_INTERIOR);
            }



          //  mPanoramaView.setPanoramaByUid("28e700f15aae5418085cb3a7", PanoramaView.PANOTYPE_INTERIOR);
        }

      //  mPanoramaView.setPanorama("0100220000130817164838355J5");
    }
}
