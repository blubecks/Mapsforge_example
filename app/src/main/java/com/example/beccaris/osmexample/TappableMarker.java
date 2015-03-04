package com.example.beccaris.osmexample;

import android.app.Activity;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;

/**
 * Created by beccaris on 04/03/15.
 */
public class TappableMarker extends Marker{
    /**
     * @param latLong          the initial geographical coordinates of this marker (may be null).
     * @param bitmap           the initial {@code Bitmap} of this marker (may be null).
     * @param horizontalOffset the horizontal marker offset.
     * @param verticalOffset
     */
    public TappableMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);
    }
    public TappableMarker(int icon, LatLong localLatLong, Activity activity) {
        super(localLatLong,AndroidGraphicFactory.convertToBitmap(activity.getApplicationContext().getResources().getDrawable(icon)),
                1*(AndroidGraphicFactory.convertToBitmap(activity.getApplicationContext().getResources().getDrawable(icon)).getWidth())/2,
                -1*(AndroidGraphicFactory.convertToBitmap(activity.getApplicationContext().getResources().getDrawable(icon)).getHeight())/2);
    }
}
