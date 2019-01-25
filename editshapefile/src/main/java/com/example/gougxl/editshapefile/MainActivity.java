package com.example.gougxl.editshapefile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;

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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/arcgis/dltb.shp";
        shapefileFeatureTable = new ShapefileFeatureTable(path);
        featureLayer = new FeatureLayer(shapefileFeatureTable);
        ArcGISMap arcGISMap = new ArcGISMap();
        arcGISMap.getOperationalLayers().add(featureLayer);
        mMapView.setMap(arcGISMap);
        //queryfeature(featureLayer);
        addfeatture();
        System.out.println("editable "+shapefileFeatureTable.isEditable());

    }
   private void addfeatture(){
       final Feature featuretoadd = shapefileFeatureTable.createFeature();
       featuretoadd.getAttributes().put("BGLX","test");
       Geometry geometry = new Envelope(new Point(38674266.711,3174196.329),new Point(38674344.934,3174162.723));
       featuretoadd.setGeometry(geometry);
       final ListenableFuture<Void> update =featureLayer.getFeatureTable().updateFeatureAsync(featuretoadd);
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
    /*private void queryfeature(final FeatureLayer featureLayer){
        FeatureLayer featureLayer1 = featureLayer;
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setWhereClause("FID=0");
        final ListenableFuture<FeatureQueryResult> queryresult = featureLayer1.selectFeaturesAsync(queryParameters,FeatureLayer.SelectionMode.ADD);
        queryresult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult queryResult = queryresult.get();
                    Iterator<Feature> iterator = queryResult.iterator();
                    while(iterator.hasNext()){
                        final Feature feature = iterator.next();
                        feature.getAttributes().put("BGLX","test");
                        Geometry geometry = new Envelope(new Point(38674266.711,3174196.329),new Point(38674344.934,3174162.723));
                        feature.setGeometry(geometry);
                        final ListenableFuture<Void> update =featureLayer.getFeatureTable().updateFeatureAsync(feature);
                        update.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                if (update.isDone()){
                                    System.out.println("feature attribute is "+ feature.getAttributes());
                                    System.out.println("feature geometry is" + feature.getGeometry());
                                }
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }*/


    //request read permission for device
    private void requestReadPermission() {
        // define permission to request
        String[] reqPermission = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE };
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
