package com.example.bas.app3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

            mMap.clear();
            nameAdd.setText("");
            descriptionAdd.setText("");

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

        @Override
        protected void onPostExecute(String resultat) {
            Toast.makeText(getContext(), resultat, Toast.LENGTH_LONG).show();

        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng norway = new LatLng(59.911491,10.757933);
        CameraUpdate minPosition = CameraUpdateFactory.newLatLngZoom(norway, 12);
        //mMap.animateCamera(minPosition);
        mMap.moveCamera(minPosition);
        mMap.setOnMapClickListener(this);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Clicked"));
        //Toast.makeText(getContext(), String.valueOf(latLng), Toast.LENGTH_SHORT).show();
        selectedPos = latLng;
    }
}
