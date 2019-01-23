package com.example.gougxl.changesublayeropacity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.layers.ArcGISMapImageSublayer;
import com.esri.arcgisruntime.layers.ArcGISSublayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.util.ListenableList;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private ArcGISMap mArcGISMap;
    private ArcGISMapImageLayer mArcGISMapImageLayer;
    private ArcGISMapImageSublayer mArcGISMapImageSublayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = findViewById(R.id.mapview);
        mArcGISMap = new ArcGISMap();
        mArcGISMapImageLayer = new ArcGISMapImageLayer("http://sampleserver6.arcgisonline.com/arcgis/rest/services/USA/MapServer");

        mArcGISMapImageLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (mArcGISMapImageLayer.getLoadStatus()== LoadStatus.LOADED){
                    ListenableList<ArcGISSublayer> sublayerlist = mArcGISMapImageLayer.getSublayers();

                    mArcGISMapImageSublayer =(ArcGISMapImageSublayer) sublayerlist.get(0);

                    //mArcGISMapImageSublayer.setOpacity(0.01f);
                }else if (mArcGISMapImageSublayer.getLoadStatus()==LoadStatus.FAILED_TO_LOAD){

                    System.out.println(mArcGISMapImageLayer.getLoadError().getCause());

                }
            }
        });
        mArcGISMapImageLayer.loadAsync();
        mArcGISMap.getOperationalLayers().add(mArcGISMapImageLayer);
        mMapView.setMap(mArcGISMap);


    }
}
