package com.example.gougxl.tdt_wmtslayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.esri.arcgisruntime.layers.WmtsLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.ogc.wmts.WmtsService;
import com.esri.arcgisruntime.ogc.wmts.WmtsServiceInfo;

public class MainActivity extends AppCompatActivity {
    MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView=(MapView) findViewById(R.id.mapView);
        String url = "http://tdtmap.mas.gov.cn/WMTS/kvp/services/MASELE/MapServer/TDTWMTSServer?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetCapabilities";
        final WmtsLayer wmtsLayer = new WmtsLayer(url, "MASELE");
        final WmtsService wmtsService = new WmtsService("http://tdtmap.mas.gov.cn/WMTS/kvp/services/MASELE/MapServer/TDTWMTSServer?SERVICE=WMTS&VERSION=1.0.0&REQUEST=GetCapabilities");
        wmtsService.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (wmtsService.getLoadStatus()==LoadStatus.LOADED){
                    WmtsServiceInfo wmtsServiceInfo = wmtsService.getServiceInfo();
                    System.out.println("wmtsserviceinfo is ：" + wmtsServiceInfo);

                }else if (wmtsService.getLoadStatus() == LoadStatus.FAILED_TO_LOAD){
                    System.out.println("failed to load wmtsserviceinfo "+wmtsService.getLoadError().getCause());

                }
            }
        });
        wmtsLayer.addDoneLoadingListener(new Runnable() {
            public void run() {
                if (wmtsLayer.getLoadStatus() == LoadStatus.LOADED) {

                    // work with WMTS layer here
                }else if (wmtsLayer.getLoadStatus() == LoadStatus.FAILED_TO_LOAD){
                    System.out.println("failed to load wmtslayer is"+wmtsLayer.getLoadError().getCause());

                }
            }
        });


        Basemap tdtBasemap = new Basemap(wmtsLayer);
        ArcGISMap map = new ArcGISMap(tdtBasemap);
        mMapView.setMap(map);

    }
}
