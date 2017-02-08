package com.ceanwu.gpstrackdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements BackTrackDialogFragment.ItemClickListener{

    private BaiduMap map;
    private LocationClient client;
    private double currentLat;
    private double currentLng;
    private String cuurentAddr;
    private DatabaseAdapter dbAdapter;
    private LinkedList<LatLng> locations = new LinkedList<>();
    private boolean isTracking;
    private boolean isLocating;
    private int tid;
    private GeoCoder coder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = ((MapView) findViewById(R.id.map_view)).getMap();
        initMap();
        dbAdapter = new DatabaseAdapter(this);

        //reverse GeoCode when track stoped
        coder = GeoCoder.newInstance();
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                cuurentAddr = geoCodeResult.getAddress();
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
    }

    private void initMap() {
        client = new LocationClient(this);
        client.registerLocationListener(listener);

        LocationClientOption clientOption = new LocationClientOption();
        clientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        clientOption.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        clientOption.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        clientOption.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        clientOption.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向

        client.setLocOption(clientOption);
        client.start(); // 启动SDK定位
        client.requestLocation(); // 发起定位请求
    }

    private BDLocationListener listener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null && !isTracking){ //当模拟移动开始后，屏蔽掉系统定位
                currentLat = bdLocation.getLatitude();
                currentLng = bdLocation.getLongitude();
                cuurentAddr = bdLocation.getAddrStr();

                //construct my current location information
                MyLocationData.Builder builder = new MyLocationData.Builder();
                builder.latitude(bdLocation.getLatitude());
                builder.longitude(bdLocation.getLongitude());
                builder.accuracy(bdLocation.getRadius());
                builder.direction(bdLocation.getDirection());
                builder.speed(bdLocation.getSpeed());
                MyLocationData locationData = builder.build();

                //set my location information onto baidu map
                map.setMyLocationData(locationData);
                LatLng latLng = new LatLng(currentLat, currentLng);
                map.setMyLocationConfigeration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, null));
                if (isLocating){
                    map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 15));
                    isLocating = false;
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_locate:
                locateMe();
                break;
            case R.id.menu_start:
                startTrack();
                break;
            case R.id.menu_stop:
                stopTrack();
                break;
            case R.id.menu_back:
                trackBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void locateMe() {
        Toast.makeText(this, "Locating me on map", Toast.LENGTH_SHORT).show();
        map.clear();
        map.setMyLocationEnabled(true);

        isLocating = true;
    }

    private void startTrack() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_start_track, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start track")
                .setCancelable(true)
                .setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et_name = (EditText) view.findViewById(R.id.et_name);
                        createTrack(et_name.getText().toString().trim()); //start track
                        Toast.makeText(MainActivity.this, "Start tracking", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void createTrack(String name) {
        Track track = new Track();
        track.setTrack_name(name);
        track.setCreate_date(DateUtils.toDate(new Date()));
        track.setStart_loc(cuurentAddr);
        System.out.println("current address ------>" + cuurentAddr);
        tid = dbAdapter.addTrack(track);
        System.out.println("tid ---------->" + tid);
        dbAdapter.addTrackDetail(tid, currentLat, currentLng);

        map.clear();
        addMarker();
        locations.add(new LatLng(currentLat, currentLng)); //record two points of line
        isTracking = true;

        new Thread(new TrackThread()).start(); //track location
        Toast.makeText(this, "Start simulate move track", Toast.LENGTH_SHORT).show();
    }

    private void addMarker() {
        map.setMyLocationEnabled(false); //close map location ?
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.map_marker);
        LatLng latLng = new LatLng(currentLat, currentLng);
        OverlayOptions options = new MarkerOptions()
                .position(latLng)
                .icon(bitmap);
        map.addOverlay(options);
    }

    /**
     * Track location Thread
     */
    private class TrackThread implements Runnable {

        @Override
        public void run() {
            while (isTracking){
                simulateMove();
                dbAdapter.addTrackDetail(tid, currentLat, currentLng); //add point to database
                addMarker(); //draw a new marker on map
                locations.add(new LatLng(currentLat, currentLng));
                System.out.println("0locations.size() = " + locations.size());
                drawLine(); //draw a new line
                System.out.println("1locations.size() = " + locations.size());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * simulate location change randomly
     */
    private void simulateMove() {
        currentLat = currentLat + Math.random()/1000;
        currentLng = currentLng + Math.random()/1000;
    }

    /**
     * draw a line between two points and delete the former point
     */
    private void drawLine() {
        OverlayOptions options = new PolylineOptions()
                .points(locations)
                .color(0xFFFF00FF);
        map.addOverlay(options);
//        locations.remove(0); //keep the latest point as start point
        locations.removeFirst();
    }

    private void stopTrack() {
        isTracking = false;

        Toast.makeText(this, "Stop track", Toast.LENGTH_SHORT).show();
        //reverse end point's GeoCode and update end point in database
        coder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(new LatLng(currentLat, currentLng)));
        dbAdapter.updateEndLoc(cuurentAddr, tid);
    }

    private void trackBack() {
        map.clear();
        DialogFragment dialogFragment = new BackTrackDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "backTrack");
    }

    @Override
    public void onDialogItemClick(int id) {
        new Thread(new TrackPlaybackThread(id)).start();
    }

    private class TrackPlaybackThread implements Runnable {

        private int id;
        public TrackPlaybackThread(int id) {
            this.id = id;
            System.out.println("track back id ----->" + id);
        }

        @Override
        public void run() {
            //查询id 对应的路线的所有坐标点
            ArrayList<TrackDetail> trackDetails = dbAdapter.getTrackDetails(id);
            TrackDetail td = null;
            locations.clear();
            currentLat = trackDetails.get(0).getLat();
            currentLng = trackDetails.get(0).getLng();
            locations.add(new LatLng(currentLat, currentLng));
            addMarker();
            int size = trackDetails.size();
            for (int i = 1; i < size; i++) {
                td = trackDetails.get(i);
                currentLat = td.getLat();
                currentLng = td.getLng();
                locations.add(new LatLng(currentLat, currentLng));
                addMarker();
                drawLine();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Back Track finished", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }
}
