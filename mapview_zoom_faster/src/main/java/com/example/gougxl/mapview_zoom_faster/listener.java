package com.example.gougxl.mapview_zoom_faster;

import android.content.Context;
import android.view.MotionEvent;

import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;

public class listener extends DefaultMapViewOnTouchListener {
    public listener(Context context, MapView mapView) {
        super(context, mapView);
        }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        double currentscale=mMapView.getMapScale();
        double maxscale=mMapView.getMap().getMaxScale();
        double minscale=mMapView.getMap().getMinScale();

        System.out.println("maxscale is:"+maxscale);
        System.out.println("minscale is:"+minscale);
        System.out.println("currentscale is:"+currentscale);

        mMapView.setViewpointScaleAsync((currentscale)/3);
        return super.onDoubleTap(e);

    }

}
