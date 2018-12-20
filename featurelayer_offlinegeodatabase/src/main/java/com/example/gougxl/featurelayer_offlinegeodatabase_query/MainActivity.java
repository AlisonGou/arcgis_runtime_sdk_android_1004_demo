package com.example.gougxl.featurelayer_offlinegeodatabase_query;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.StatisticDefinition;
import com.esri.arcgisruntime.data.StatisticRecord;
import com.esri.arcgisruntime.data.StatisticType;
import com.esri.arcgisruntime.data.StatisticsQueryParameters;
import com.esri.arcgisruntime.data.StatisticsQueryResult;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView;
    private ArcGISMap mArcGISMap;

    private static final String TAG = MainActivity.class.getSimpleName();;
    // permission to read external storage
    private final String[] reqPermission = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG,"start to load");
        mMapView=findViewById(R.id.mapView);
        mArcGISMap = new ArcGISMap(Basemap.createOpenStreetMap());
        mMapView.setMap(mArcGISMap);
        //set mapview background to white
        BackgroundGrid backgroundGrid = new BackgroundGrid();
        backgroundGrid.setColor(Color.WHITE);
        mMapView.setBackgroundGrid(backgroundGrid);

        // For API level 23+ request permission at runtime
        if (ContextCompat.checkSelfPermission(MainActivity.this, reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            loadgeodatabase();
        } else {
            // request permission
            int requestCode = 2;
            ActivityCompat.requestPermissions(MainActivity.this, reqPermission, requestCode);
        }

    }

    private void loadgeodatabase (){
        String geodatabasepath = Environment.getExternalStorageDirectory()+"/Android/data/ArcGIS/wgs84_web_mecator.geodatabase";
        System.out.println("externalpath"+geodatabasepath);
        Geodatabase geodatabase = new Geodatabase(geodatabasepath);

        geodatabase.loadAsync();

        geodatabase.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (geodatabase.getLoadStatus()== LoadStatus.LOADED){

                    GeodatabaseFeatureTable geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTableByServiceLayerId(0);
                    geodatabaseFeatureTable.loadAsync();
                    FeatureLayer featureLayer = new FeatureLayer(geodatabaseFeatureTable);

                    featureLayer.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            if (featureLayer.getLoadStatus()==LoadStatus.LOADED){
                                featureLayer.setRenderer(new SimpleRenderer(new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.RED, null)));
                                mMapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));
                                QueryParameters queryParameters  = new QueryParameters();
                                featurelayerquery(featureLayer,queryParameters, FeatureLayer.SelectionMode.ADD);
                                statisticbypolygon(featureLayer);
                            }else{
                                Log.e(TAG, "Feature Layer failed to load!");
                            }

                        }
                    });
                    mMapView.getMap().getOperationalLayers().add(featureLayer);
                }
            }
        });



    }

    //this method takes a feature layer in and query on it
    public void featurelayerquery(FeatureLayer featureLayer, QueryParameters queryParameters, FeatureLayer.SelectionMode mode){

        ListenableFuture<FeatureQueryResult> featurequeryresult= featureLayer.selectFeaturesAsync(queryParameters,mode);
        featurequeryresult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = featurequeryresult.get();
                    Iterator<Feature> iterator = result.iterator();
                    while (iterator.hasNext()){
                        Feature feature = iterator.next();
                        Map<String,Object> map =feature.getAttributes();

                        //System.out.println("要素属性（feature attribute）："+map);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        });



    }

    public void statisticbypolygon(FeatureLayer featureLayer){

        List statisticDefinitions = new ArrayList();
        //if the sum of some certain field is needed later, then statistic method of sum has to be used by OBJECTID

        StatisticDefinition statDefCount = new StatisticDefinition("OBJECTID", StatisticType.SUM,"objectcount");
        statisticDefinitions.add(statDefCount);
        StatisticDefinition mj = new StatisticDefinition("PERIMETER", StatisticType.SUM,"premetersum");
        statisticDefinitions.add(mj);

        StatisticsQueryParameters statisticsQueryParameters = new StatisticsQueryParameters(statisticDefinitions);
        statisticsQueryParameters.getGroupByFieldNames().add("st_area(Shape)");
        ListenableFuture<StatisticsQueryResult> statQueryResultFuture =  featureLayer.getFeatureTable().queryStatisticsAsync(statisticsQueryParameters);

        statQueryResultFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    StatisticsQueryResult statisticsQueryResult =statQueryResultFuture.get();
                    Iterator result=  statisticsQueryResult.iterator();

                    while (result.hasNext()){
                        StatisticRecord statisticRecord= (StatisticRecord) result.next();
                        if (statisticRecord == null) {
                            break;
                        }
                        Map<String, Object> resultmap= statisticRecord.getStatistics();
                        //System.out.println("result map is:"+resultmap);
                        if (resultmap.containsKey("premetersum")){
                            double d = (double) resultmap.get("premetersum");
//                            double objectid = (double)resultmap.get("objectcount");
                            //System.out.println("PERIMETER sum ："+d +"objectid :" +objectid);
                            System.out.println("PERIMETER sum ："+d);
                        }else {
                            System.out.println("PERIMETER sum is not returned" );
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    e.getMessage();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    e.getCause();
                }




            }
        });
    }
    /**
     * Handle the permissions request response
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            System.out.println("on request permission result");
            loadgeodatabase();
        } else {
            // report to user that permission was denied
            Toast.makeText(MainActivity.this, getResources().getString(R.string.write_permission_denied),
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

}


