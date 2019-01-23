package com.example.gougxl.mapview_zoom_faster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MainActivity extends AppCompatActivity {
    customerizeddoubletaplistener mCustomerizeddoubletaplistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapView mapView=findViewById(R.id.mapView);
        ArcGISMap arcGISMap=new ArcGISMap(Basemap.createOpenStreetMap());
        mapView.setMap(arcGISMap);
        mapView.setOnTouchListener(new customerizeddoubletaplistener(MainActivity.this,mapView));

    }




}



