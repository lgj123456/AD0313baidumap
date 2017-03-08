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

import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        initViews();
    }


    OnGetRoutePlanResultListener routeListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
//            Toast.makeText(SearchActivity.this, "walkingRouteResult " + walkingRouteResult.getRouteLines().get(0).getAllStep(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            list = transitRouteResult.getRouteLines();
            Toast.makeText(SearchActivity.this, "TransitRouteResult===========" + transitRouteResult.getRouteLines(), Toast.LENGTH_SHORT).show();
            tv_content.setText("distance:" + list.get(0).getDistance() + "duration:" + list.get(0).getDuration()
                    + "" + list.get(0).getAllStep().get(0).getInstructions()+ list.get(0).getAllStep().get(2).getInstructions()+ "getVehicleInfo  "+ list.get(0).getAllStep().get(0).getVehicleInfo() +
                  "getEntrance" + list.get(0).getAllStep().get(0).getEntrance() + "getStepType"+ list.get(0).getAllStep().get(0).getStepType() +
                  "getExit"  + list.get(0).getAllStep().get(0).getExit() + list.get(1).getAllStep().get(0).getInstructions()
            );
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
            //获取跨城综合公共交通线路规划结果
            Toast.makeText(SearchActivity.this, "MassTransitRouteResult:::::" + result.getSuggestAddrInfo(), Toast.LENGTH_SHORT).show();
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
        mPoiSearch = PoiSearch.newInstance();
        mRoutePlanSearch = RoutePlanSearch.newInstance();
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
                linearLayout_routePlan.setVisibility(View.GONE);
            }
        });



        //城市检索
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        btn_routePlan_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_routePlan.setVisibility(View.VISIBLE);
                linearLayout_city.setVisibility(View.GONE);
            }
        });
        btn_search3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String begin_city = edt_begin_city.getText().toString().trim();
                String end_city = edt_end_city.getText().toString().trim();
                String starting_point = edt_staring_point.getText().toString().trim();
                String destination = edt_destination.getText().toString().trim();
                PlanNode stMassNode = PlanNode.withCityNameAndPlaceName(begin_city, starting_point);
                PlanNode enMassNode = PlanNode.withCityNameAndPlaceName(end_city, destination);

                //  mRoutePlanSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stMassNode).to(enMassNode));
                // mRoutePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(stMassNode).to(enMassNode));
                mRoutePlanSearch.transitSearch(new TransitRoutePlanOption().from(stMassNode).to(enMassNode).city("广州"));
            }
        });

    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果


            Log.i("bbbbbbbbbbbbb", "onGetPoiResult: " + result.getAllPoi().get(0).address);

            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            getlocation(result);
            intent.putExtra("location", location);
            startActivity(intent);
            mPoiSearch.destroy();
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
