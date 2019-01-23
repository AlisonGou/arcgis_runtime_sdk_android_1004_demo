package com.example.gougxl.addscenelayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.SceneView;

public class MainActivity extends AppCompatActivity {
    SceneView mSceneView;
    ArcGISScene mArcGISScene;
    ArcGISSceneLayer arcGISSceneLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArcGISScene= new ArcGISScene();
        mArcGISScene.setBasemap(Basemap.createOpenStreetMap());
        mSceneView = (SceneView)findViewById(R.id.sceneview);
        mSceneView.setScene(mArcGISScene);

        arcGISSceneLayer = new ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer");
        arcGISSceneLayer.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                if (arcGISSceneLayer.getLoadStatus()== LoadStatus.LOADED){
                    mArcGISScene.getOperationalLayers().add(arcGISSceneLayer);
                    System.out.println("arcgisscenelayer is alreay "+ LoadStatus.LOADED);
                }else if(arcGISSceneLayer.getLoadStatus()==LoadStatus.FAILED_TO_LOAD){
                    System.out.println("arcgisscenelayer failed to load as :"+arcGISSceneLayer.getLoadError().getCause());
                }else if(arcGISSceneLayer.getLoadStatus()==LoadStatus.LOADING){
                    System.out.println("arcgisscenelayer is "+ LoadStatus.LOADING);
                }
            }
        });
        arcGISSceneLayer.addLoadStatusChangedListener(new LoadStatusChangedListener() {
            @Override
            public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
                loadStatusChangedEvent.getNewLoadStatus();
            }
        });



        // add a camera and initial camera position
        Camera camera = new Camera(48.378, -4.494, 200, 345, 65, 0);
        mSceneView.setViewpointCamera(camera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSceneView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSceneView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSceneView.dispose();
    }
}
