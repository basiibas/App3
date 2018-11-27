package com.example.bas.app3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddFragment extends Fragment implements OnMapReadyCallback , GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    EditText nameAdd, descriptionAdd;
    LatLng selectedPos;
    MapPoint newPoint;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add,container, false);

        nameAdd = view.findViewById(R.id.nameAdd);
        descriptionAdd = view.findViewById(R.id.descriptionAdd);
        Button button = view.findViewById(R.id.btnAdd);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.mapAdd);
        mapFragment.getMapAsync(this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertPoint();
            }
        });

        return view;
    }


    public void insertPoint(){

        if(nameAdd.getText().toString().matches("") || descriptionAdd.getText().toString().matches("")){
            Toast.makeText(getContext(), "Vennligst fyll ut name eller beskrivelse!", Toast.LENGTH_LONG).show();
        }else if(selectedPos == null){
            Toast.makeText(getContext(), "Vennligst velg et position!", Toast.LENGTH_LONG).show();
        }else{

            newPoint = new MapPoint(nameAdd.getText().toString(),descriptionAdd.getText().toString(),
                    selectedPos.latitude, selectedPos.longitude);


            GetPointTask saveNewPoint = new
                    GetPointTask(newPoint.getName(), newPoint.getDescription(), String.valueOf(newPoint.getLatitube()),
                    String.valueOf(newPoint.getLongitude()));
            saveNewPoint.execute();

            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.fragment_layout,
                    homeFragment).commit();

            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);

            bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);

            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_LONG).show();

        }


    }

    private class GetPointTask extends AsyncTask<Void, Void, String> {
        JSONObject jsonObject;
        String name;
        String description;
        String latitude;
        String logitude;

        public GetPointTask(String name, String description, String latitude, String logitude) {
            this.name = name;
            this.description = description;
            this.latitude = latitude;
            this.logitude = logitude;
        }

        @Override
        protected String doInBackground(Void... params) {

            String query = "http://student.cs.hioa.no/~s315572/jsonin.php/?Name="
                    + this.name.replaceAll(" ", "%20") + "&Description="+
                    this.description.replaceAll(" ","%20")+"&Latitude="+
                    this.latitude+"&Longitude="+this.logitude;

            try {
                URL urlen = new URL(query);
                HttpURLConnection conn =
                        (HttpURLConnection) urlen.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept",
                        "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                return "Lagret!";
            } catch (Exception e) {
                return "Noen gikk feil";
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng firstPosition = new LatLng( 59.92027, 10.734576);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .tilt(50)
                .zoom(16)
                .target(firstPosition)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMinZoomPreference(15);

        mMap.setOnMapClickListener(this);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        selectedPos = latLng;
    }
}
