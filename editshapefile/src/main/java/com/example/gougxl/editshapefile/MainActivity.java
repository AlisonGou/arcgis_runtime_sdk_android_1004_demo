package com.example.gougxl.editshapefile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    MapView mMapView;
    ShapefileFeatureTable shapefileFeatureTable;
    FeatureLayer featureLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView)findViewById(R.id.mapview);
        requestReadPermission();




    }

    private  void featureLayerShapefile(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/arcgis/shp/poly.shp";
        if (path.isEmpty()){
            System.out.println("path is null");
        }
        shapefileFeatureTable = new ShapefileFeatureTable(path);
        featureLayer = new FeatureLayer(shapefileFeatureTable);
        ArcGISMap arcGISMap = new ArcGISMap();
        arcGISMap.getOperationalLayers().add(featureLayer);
        mMapView.setMap(arcGISMap);
        //queryfeature(featureLayer);
        //addfeatture();

        Button button = (Button)findViewById(R.id.edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked");
                queryfeature();
            }
        });
        System.out.println("editable "+shapefileFeatureTable.isEditable());

    }
  private void addfeatture(){
       final Feature featuretoadd = shapefileFeatureTable.createFeature();
       Feature featureadd1=shapefileFeatureTable.createFeature();
       //新建要素的时候一定要添加Id值
       featuretoadd.getAttributes().put("Id",0);
       Geometry geometry = new Point(38674266.711,3174196.329);
       featuretoadd.setGeometry(geometry);


      final ListenableFuture<Void> update =featureLayer.getFeatureTable().addFeatureAsync(featuretoadd);
       update.addDoneListener(new Runnable() {
           @Override
           public void run() {
               if (update.isDone()){
                   System.out.println("feature attribute is "+ featuretoadd.getAttributes());
                   System.out.println("feature geometry is" + featuretoadd.getGeometry());
               }
           }
       });
   }

    private void queryfeature(){
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause("FID=0");
        final ListenableFuture<FeatureQueryResult> queryresult = featureLayer.selectFeaturesAsync(queryParameters,FeatureLayer.SelectionMode.ADD);
        queryresult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult queryResult = null;
                    try {
                        queryResult = queryresult.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Iterator<Feature> iterator = queryResult.iterator();
                    while(iterator.hasNext()){
                        final Feature feature = iterator.next();
                        System.out.println("查询遍历结果："+feature.getAttributes());
                        feature.getAttributes().put("new","zoo");
                        Point point = new Point(38674344.934,3174162.723);
                        Point point1 =  new Point(38674266.711,3174196.329);
                        Point point2 = new Point(38675266.711,3174296.329);
                        SpatialReference spatialReference = mMapView.getSpatialReference();
                        PointCollection pointCollection = new PointCollection(spatialReference);
                        pointCollection.add(point);
                        pointCollection.add(point1);
                        Polygon polygon = new Polygon(pointCollection);


                        feature.setGeometry(polygon);
                        final ListenableFuture<Void> update =featureLayer.getFeatureTable().updateFeatureAsync(feature);
                        update.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                if (update.isDone()){
                                    System.out.println("update feature attribute is "+ feature.getAttributes());
                                    System.out.println("update feature geometry is" + feature.getGeometry());
                                }else if(!update.isDone()) {

                                    System.out.println("failed to update");
                                }
                            }
                        });

                    }
                } catch (InterruptedException e) {
                   System.out.println("update feature exception because"+e.getCause());

                }
            }
        });

    }


    //request read permission for device
    private void requestReadPermission() {
        // define permission to request
        String[] reqPermission = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        int requestCode = 2;
        // For API level 23+ request permission at runtime
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            featureLayerShapefile();
        } else {
            // request permission
            ActivityCompat.requestPermissions(MainActivity.this, reqPermission, requestCode);
        }
    }

    /**
     * Handle the permissions request response.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            featureLayerShapefile();
        } else {
            // report to user that permission was denied
            Toast.makeText(MainActivity.this, getResources().getString(R.string.read_permission_denied),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
