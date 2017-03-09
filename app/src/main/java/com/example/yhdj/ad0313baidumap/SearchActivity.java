package com.example.yhdj.ad0313baidumap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private Button btn_city_search;

    private Button btn_routePlan_search;
    private Button btn_search;

    private Button btn_search3;
    private EditText edt_city;
    private EditText edt_keyboard;

    private EditText edt_begin_city;
    private EditText edt_staring_point;
    private EditText edt_end_city;
    private EditText edt_destination;
    private LinearLayout linearLayout_city;
    private TextView tv_content;
    private LinearLayout linearLayout_routePlan;
    private PoiSearch mPoiSearch;
    private ArrayList<String> location = new ArrayList<>();
    private RoutePlanSearch mRoutePlanSearch;
    private List<TransitRouteLine> list = new ArrayList<>();
    private StringBuffer buffer = new StringBuffer();
    private MapView mMapView;
    LatLng begin;
    LatLng end;
    private BaiduMap mBaiduMap;
    private String uid;
    private LinearLayout linearLayout_guide;
    private Button guide_bus;
    private Button guide_walk;
    private Button guide_drive;
    private String begin_city;
    private String end_city;
    private String starting_point;
    private String destination;
    private boolean isWalk = false;
    private boolean isBus = false;
    private Button btn_panorama;
    private boolean isCity_search = false;
    private boolean isPanorama = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        initViews();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
    }


    OnGetRoutePlanResultListener routeListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            Toast.makeText(SearchActivity.this, "walkingRouteResult " + walkingRouteResult.getRouteLines(), Toast.LENGTH_SHORT).show();
       for(int i = 0; i < walkingRouteResult.getRouteLines().size(); i++){
           for(int y = 0 ; y < walkingRouteResult.getRouteLines().get(i).getAllStep().size(); y++){
               buffer.append(walkingRouteResult.getRouteLines().get(i).getAllStep().get(y).getInstructions());
           }
       }
       tv_content.setText(buffer.toString());

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                //未找到结果
                return;
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                //result.getSuggestAddrInfo()
                return;
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                TransitRouteLine route = transitRouteResult.getRouteLines().get(0);
                //创建公交路线规划线路覆盖物


            }


            list = transitRouteResult.getRouteLines();
            Toast.makeText(SearchActivity.this, "TransitRouteResult===========" + transitRouteResult.getRouteLines(), Toast.LENGTH_SHORT).show();
            tv_content.setText("distance:" + list.get(0).getDistance() + "duration:" + list.get(0).getDuration()
                    + "" + list.get(0).getAllStep().get(0).getInstructions() + list.get(0).getAllStep().get(2).getInstructions() + "getVehicleInfo  " + list.get(0).getAllStep().get(0).getVehicleInfo() +
                    "getEntrance" + list.get(0).getAllStep().get(0).getEntrance() + "getStepType" + list.get(0).getAllStep().get(0).getStepType() +
                    "getExit" + list.get(0).getAllStep().get(0).getExit() + list.get(1).getAllStep().get(0).getInstructions()
            );
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
            //获取跨城综合公共交通线路规划结果
            begin = result.getRouteLines().get(0).getNewSteps().get(0).get(0).getStartLocation();

            end = result.getRouteLines().get(0).getNewSteps().get(0).get(0).getEndLocation();
            for (int i = 0; i < result.getRouteLines().size(); i++) {
                float duration = ((float) result.getRouteLines().get(i).getDuration() / 3600);
                String dur = String.valueOf(((float) result.getRouteLines().get(i).getDuration() / 3600)).substring(0, 4);
                buffer.append("第" + (i + 1) + "种方案："); // + "用时：" +dur +"小时" + "距离" + (double)result.getRouteLines().get(i).getDistance()/1000 + "千米" + "\n");
                for (int y = 0; y < result.getRouteLines().get(i).getNewSteps().size(); y++) {
                    for (int z = 0; z < result.getRouteLines().get(i).getNewSteps().get(y).size(); z++) {
                        buffer.append(result.getRouteLines().get(i).getNewSteps().get(y).get(z).getInstructions() + "\n"


                        );
                    }
                }
            }
            Log.i("ccccccccccccccccc", "latitude: " + begin.latitude + "longitude" + begin.longitude);

//            MyLocationData data = new MyLocationData.Builder()
//
//
//                    .direction(100).latitude(begin.latitude).longitude(begin.longitude).build();
//            mBaiduMap.setMyLocationData(data);
//            MapStatus.Builder buider = new MapStatus.Builder();
//            buider.target(begin).zoom(18.0f);
//            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(buider.build()));


            tv_content.setText(buffer.toString());

            Toast.makeText(SearchActivity.this, "MassTransitRouteResult:::::" + result.getRouteLines().get(0).getNewSteps().get(0).get(0).getInstructions(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };


    private void initViews() {
        linearLayout_guide = (LinearLayout) findViewById(R.id.linearLayout_guide);
        guide_bus = (Button) findViewById(R.id.guide_bus);
        guide_walk = (Button) findViewById(R.id.guide_walk);
        guide_drive = (Button) findViewById(R.id.guide_drive);
        btn_panorama = (Button) findViewById(R.id.btn_panorama);
        btn_panorama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearLayout_city.setVisibility(View.VISIBLE);
                linearLayout_guide.setVisibility(View.GONE);
                linearLayout_routePlan.setVisibility(View.GONE);
                isPanorama = true;
            }
        });

        mPoiSearch = PoiSearch.newInstance();
        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mMapView = (MapView) findViewById(R.id.mMapView);
        tv_content = (TextView) findViewById(R.id.tv_content);
        mRoutePlanSearch.setOnGetRoutePlanResultListener(routeListener);
        linearLayout_city = (LinearLayout) findViewById(R.id.linearLayout_city);
        linearLayout_routePlan = (LinearLayout) findViewById(R.id.linearLayout_routePlan);
        btn_city_search = (Button) findViewById(R.id.btn_city_search);
        btn_routePlan_search = (Button) findViewById(R.id.btn_routePlanSearch);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_keyboard = (EditText) findViewById(R.id.edr_keyboard);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search3 = (Button) findViewById(R.id.btn_search3);
        edt_begin_city = (EditText) findViewById(R.id.edt_begin_city);
        edt_end_city = (EditText) findViewById(R.id.edt_end_city);
        edt_staring_point = (EditText) findViewById(R.id.edt_starting_point);
        edt_destination = (EditText) findViewById(R.id.edt_destination);
        btn_city_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_city.setVisibility(View.VISIBLE);
                linearLayout_guide.setVisibility(View.GONE);
                linearLayout_routePlan.setVisibility(View.GONE);
            }
        });
        guide_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_routePlan.setVisibility(View.VISIBLE);
                isBus = true;
                tv_content.setText("");
            }
        });

        guide_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_routePlan.setVisibility(View.VISIBLE);
                isWalk = true;
                tv_content.setText("");
            }
        });

        guide_drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this,GuideActivity.class);
                startActivity(intent);
            }
        });

        //城市检索
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCity_search = true;
                citySearch();



            }
        });

        btn_routePlan_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_guide.setVisibility(View.VISIBLE);
                linearLayout_city.setVisibility(View.GONE);
            }
        });
        btn_search3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_content.setText("");
                getcontent();

                PlanNode stMassNode = PlanNode.withCityNameAndPlaceName(begin_city, starting_point);
                PlanNode enMassNode = PlanNode.withCityNameAndPlaceName(end_city, destination);

                if (isBus) {
                    mRoutePlanSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stMassNode).to(enMassNode));
                    isBus = false;
                }


                if (isWalk) {
                    Toast.makeText(SearchActivity.this, "" + isWalk, Toast.LENGTH_SHORT).show();
                    mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(stMassNode).to(enMassNode));
                    isWalk = false;
                }
                //  mRoutePlanSearch.transitSearch(new TransitRoutePlanOption().from(stMassNode).to(enMassNode).city("广州"));
            }
        });

    }

    private void citySearch() {
        String city = edt_city.getText().toString().trim();
        String keyboard = edt_keyboard.getText().toString().trim();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        Log.i("aaaaaaaaaaaaaaa", "onClick: " + city + keyboard);
        mPoiSearch.searchInCity((new PoiCitySearchOption()
                .city(city)
                .keyword(keyboard)
                .pageCapacity(500)

        ));
    }

    private void getcontent() {
        begin_city = edt_begin_city.getText().toString().trim();
        end_city = edt_end_city.getText().toString().trim();
        starting_point = edt_staring_point.getText().toString().trim();
        destination = edt_destination.getText().toString().trim();
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果

if(isCity_search){
    Log.i("bbbbbbbbbbbbb", "onGetPoiResult: " + result.getAllPoi().get(0).address);

    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
    getlocation(result);
    intent.putExtra("location", location);
    startActivity(intent);
    isCity_search = false;
}
          if(isPanorama){
              //内景
            Intent intent = new Intent(SearchActivity.this, PanoramaDemoActivityMain.class);
            intent.putExtra("latitude", result.getAllPoi().get(0).location.latitude);
            intent.putExtra("longitude", result.getAllPoi().get(0).location.longitude);
            intent.putExtra("uid",result.getAllPoi().get(0).uid);
            startActivity(intent);
            mPoiSearch.destroy();
              isPanorama = false;
          }

        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果


        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private void getlocation(PoiResult result) {
        for (int i = 0; i < result.getAllPoi().size(); i++) {
            location.add(String.valueOf(result.getAllPoi().get(i).location.latitude));
            location.add(String.valueOf(result.getAllPoi().get(i).location.longitude));
        }
    }
}
