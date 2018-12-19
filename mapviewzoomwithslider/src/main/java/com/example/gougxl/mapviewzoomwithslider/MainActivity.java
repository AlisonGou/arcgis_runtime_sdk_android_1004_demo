package com.example.gougxl.mapviewzoomwithslider;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.security.AuthenticationManager;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private MapView mMapView;
    private ArcGISMap mGISMap;
    AuthenticationManager mAuthenticationManager;
    private SeekBar mSeekBar;
    private static final String TAG = "seekbarstatus";
    double maxscale = 1000;
    double minscale = 3.777303373333333E8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textforseekbar);
        textView.setText(R.string.text);


        mMapView = findViewById(R.id.mapView);
        //trust all certificates which is not recommended in product environment
        mAuthenticationManager.setTrustAllSigners(true);

        mGISMap = new ArcGISMap();



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

        //set background for mapview
        BackgroundGrid backgroundGrid = new BackgroundGrid();
        int color = Color.WHITE;
        backgroundGrid.setColor(color);
        mMapView.setBackgroundGrid(backgroundGrid);


        //set max and minscale for users to get through clicking buttons

        System.out.println("maxscale is:"+maxscale + "and minscale is : "+minscale);


        //initialize sliderbar
        mSeekBar=(SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);





    }

    @Override
    protected void onPause(){
        super.onPause();
        //mMapView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mMapView.dispose();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        double zoomtoscale = minscale-(minscale-maxscale)/100*progress;
        double currentmapscale=mMapView.getMapScale();
        if(currentmapscale>0&currentmapscale<minscale){
            mMapView.setViewpointScaleAsync(zoomtoscale);
            System.out.println("zoom to scale"+zoomtoscale);
        }



    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
