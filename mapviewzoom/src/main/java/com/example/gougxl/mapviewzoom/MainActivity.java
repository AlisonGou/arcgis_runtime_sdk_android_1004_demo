package com.example.gougxl.mapviewzoom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.security.AuthenticationManager;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private ArcGISMap mGISMap;
    AuthenticationManager mAuthenticationManager;
    private Button mzoomoutButton;
    private Button mzoominButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        mGISMap = new ArcGISMap();

        //trust all certificates which is not recommended in product environment
        mAuthenticationManager.setTrustAllSigners(true);

        //initialize butons
        mzoominButton = findViewById(R.id.zoomin);
        mzoomoutButton = findViewById(R.id.zoomout);

        //replace with your own dynamic map service
        final ArcGISMapImageLayer arcGISMapImageLayer = new ArcGISMapImageLayer("https://linux111.esrichina.com/server/rest/services/SampleWorldCities/MapServer");
        //monitor layer load status
        arcGISMapImageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (arcGISMapImageLayer.getLoadStatus()== LoadStatus.LOADED){
                    System.out.println("arcgismaploadstatus is :"+arcGISMapImageLayer.getLoadStatus());

                }else if(arcGISMapImageLayer.getLoadStatus()== LoadStatus.FAILED_TO_LOAD){
                    System.out.println("arcgismaploadstatus failed to load as :"+arcGISMapImageLayer.getLoadError().getCause());

                }
            }
        });
        mGISMap.getOperationalLayers().add(arcGISMapImageLayer);
        mMapView.setMap(mGISMap);

        //set max and minscale for users to get through clicking buttons
        final double maxscale = 1000;
        final double minscale = 3.777303373333333E8;
        System.out.println("maxscale is:"+maxscale + "and minscale is : "+minscale);

        //to avoid doing too much on main UI thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                mzoominButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        Double scale = mMapView.getMapScale();
                        if (scale>maxscale&scale<minscale){
                            double zoomtoscale = scale/2;
                            System.out.println("zoom to scale:"+" "+zoomtoscale);
                            mMapView.setViewpointScaleAsync(zoomtoscale);
                        }
                        return false;
                    }
                });
                mzoomoutButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        Double scale = mMapView.getMapScale();
                        System.out.println(scale);
                        if (scale>0&scale<minscale){
                            double zoomtoscale = scale*2;
                            System.out.println("zoom to scale:"+" "+zoomtoscale);
                            mMapView.setViewpointScaleAsync(zoomtoscale);
                        }
                        return false;
                    }
                });

            }
        }).start();




    }

    @Override
    protected void onPause(){
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}
