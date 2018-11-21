package com.example.bas.app3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private List<MapPoint> pointList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,false);

        pointList = new ArrayList();

        getJSON task = new getJSON();
        task.execute(new
                String[]{"http://student.cs.hioa.no/~s315572/jsonout.php"});


        return view;
    }

    private void syncMap(){
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //Toast.makeText(getActivity(), marker.getSnippet(), Toast.LENGTH_SHORT).show();
        HomeDialog dialog = new HomeDialog();
        Bundle args = new Bundle();
        args.putString("id",marker.getSnippet());
        dialog.setArguments(args);
        dialog.show(getChildFragmentManager(),"home dialog");

        return true;
    }



    private  class getJSON extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection)
                            urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept","application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }


                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    System.out.println("Output fra Server .... \n");
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray mat = new JSONArray(output);
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject = mat.getJSONObject(i);
                            String id = jsonobject.getString("id");
                            String name = jsonobject.getString("name");
                            String description = jsonobject.getString("description");
                            String latitude = jsonobject.getString("latitude");
                            String longitude = jsonobject.getString("longitude");
                            MapPoint point = new MapPoint(Integer.valueOf(id),name,description,Double.parseDouble(latitude),
                                    Double.parseDouble(longitude));
                            pointList.add(point);
                            retur = retur + id + "," +name + ","+description+","+latitude+","+ longitude  + "\n";
                        }
                        return retur;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return retur;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String s) {
            syncMap();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng firstPosition = new LatLng( 59.92027, 10.734576);

        for (MapPoint point: pointList
             ) {
            LatLng position = new LatLng(point.getLatitube(), point.getLongitude());
            /*
            mMap.addMarker(new MarkerOptions().position(position).title(
                    point.getName()).snippet(String.valueOf(point.getId())+","+point.getName()+","+point.getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(
                            new Random().nextInt(360))));
            */
            mMap.addMarker(new MarkerOptions().position(position).title(
                    point.getName()).snippet(String.valueOf(point.getId())+","+point.getName()+","
                            +point.getDescription()));

        }



        mMap.setOnMarkerClickListener(this);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .tilt(50)
                .zoom(16)
                .target(firstPosition)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMinZoomPreference(15);
    }


}
