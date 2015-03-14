package com.example.beccaris.osmexample;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.graphics.AndroidResourceBitmap;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import com.example.beccaris.osmexample.R;

public class MapFragment extends Fragment {

    private final String TAG = "MapFragment";
    private static final String MAP_FILE = "torino.map";
    private static final String MAP_CACHE = "cachemap";
    private final LatLong centerLatLong = new LatLong(45.064109, 7.658855);
    private LocationManager locationManager;

    private MapView mapView;
    private TileRendererLayer tileRendererLayer;
    private TileCache tileCache;
    private TappableMarker positionMarker;

    public static MapFragment newInstance() {
        return new MapFragment();

    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getActivity().getApplication());
        if(getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);
        }

        mapView = new MapView(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener2 = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG,"onLocationChanged");
                Log.d(TAG,location.getLongitude()+" "+location.getLatitude());
                Toast toast = Toast.makeText(getActivity(),"onLocationChanged",Toast.LENGTH_SHORT);
                toast.show();
                if (positionMarker==null)
                {
                   positionMarker = new TappableMarker(R.drawable.marker, new LatLong(location.getLatitude(),
                           location.getLongitude()),getActivity());
                }
                else{
                    mapView.getLayerManager().getLayers().remove(positionMarker);
                    positionMarker =  new TappableMarker(R.drawable.marker, new LatLong(location.getLatitude(),
                            location.getLongitude()),getActivity());
                }
                mapView.getLayerManager().getLayers().add(positionMarker);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG,"onStatusChanged from "+provider);
                Toast toast = Toast.makeText(getActivity(),"onProviderEnabled",Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG,"onProviderEnabled");
                Toast toast = Toast.makeText(getActivity(),"onProviderEnabled",Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG,"onProviderDisabled "+provider);
                Toast toast = Toast.makeText(getActivity(),"onProviderDisabled",Toast.LENGTH_SHORT);
                toast.show();

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener2);

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                Log.d(TAG,"onLocationChanged");
//                Log.d(TAG,location.getLongitude()+" "+location.getLatitude());
//                mapView.invalidate();
//                //mapView.getLayerManager().getLayers().
//                TappableMarker positionMarker = new TappableMarker(R.drawable.marker, new LatLong(location.getLatitude(), location.getLongitude()),getActivity());
//                mapView.getLayerManager().getLayers().add(positionMarker);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                Log.d(TAG,"onStatusChanged");
//                Toast toast = Toast.makeText(getActivity(),"onProviderEnabled",Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Log.d(TAG,"onProviderEnabled");
//                Toast toast = Toast.makeText(getActivity(),"onProviderEnabled",Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Log.d(TAG,"onProviderDisabled");
//                Toast toast = Toast.makeText(getActivity(),"onProviderDisabled",Toast.LENGTH_SHORT);
//                toast.show();
//            }
//
//        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            AndroidResourceBitmap.clearResourceBitmaps();
            this.mapView.getLayerManager().getLayers().remove(this.tileRendererLayer);
            this.tileRendererLayer.onDestroy();
            this.tileCache.destroy();
            this.mapView.getModel().mapViewPosition.destroy();
            this.mapView.destroy();
            Log.d(TAG, "Delete fragment");
        }catch(IllegalArgumentException iae){
            Log.e(TAG, "Error: "+iae.getMessage());
        }catch(Exception e){
            Log.e(TAG, "Error: "+e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try{
            ReadFileTask mapTask = new ReadFileTask();
            mapTask.execute();
        }catch(Exception e){
            Log.e(TAG, "Error :" + e.getMessage());
        }


        return mapView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    //Task for getting file of map
    private class ReadFileTask extends AsyncTask<Void, Void, File> {

        @Override
        protected File doInBackground(Void... UnUsed) {
            // File file = new File(Environment.getExternalStorageDirectory(), MAP_FILE);
            AssetManager assetManager = getActivity().getAssets();


            File file = new File(getActivity().getCacheDir(), MAP_FILE);
            Log.d(TAG,"ABS PATH"+file.getAbsolutePath());
            try {
                InputStream inputStream = assetManager.open(MAP_FILE);

                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    try {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                    } finally {
                        outputStream.close();
                    }
                } finally {
                    inputStream.close();
                    Log.d(TAG,"ABS PATH"+file.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return file;

        }

        @Override
        protected void onPostExecute(File result) {

            try {
                mapView.setClickable(true);
                mapView.getMapScaleBar().setVisible(true);
                mapView.setBuiltInZoomControls(true);
                mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
                mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

                mapView.getModel().mapViewPosition.setCenter(centerLatLong);
                //TappableMarker positionMarker = new TappableMarker(R.drawable.marker, centerLatLong,getActivity());
                mapView.getModel().mapViewPosition.setZoomLevel((byte) 14);



                //To avoid redrawing all the tiles all the time,a tile cache is used
                tileCache = AndroidUtil.createTileCache(getActivity(), MAP_CACHE,
                        mapView.getModel().displayModel.getTileSize(), 1f,
                        mapView.getModel().frameBufferModel.getOverdrawFactor());

                // tile renderer layer using internal render theme
                tileRendererLayer = new TileRendererLayer(tileCache,
                        mapView.getModel().mapViewPosition, false, true, AndroidGraphicFactory.INSTANCE);
                tileRendererLayer.setMapFile(result);
                tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

                // only once a layer is associated with a mapView the rendering starts
                mapView.getLayerManager().getLayers().add(tileRendererLayer);
                //mapView.getLayerManager().getLayers().add(positionMarker);
            }catch(Exception e){
                Log.i(TAG,e.getMessage());
            }

        }

    }



}
