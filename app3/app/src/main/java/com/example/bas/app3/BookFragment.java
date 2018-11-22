package com.example.bas.app3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment {

    CalendarView calendarView;
    Spinner startSpinnerBook, endSpinnerBook;
    Button avaiableRoom,btnBook;
    ArrayAdapter<CharSequence> startAdapter,endAdapter;
    String selectedDate;
    int startTime, endTime;
    List<Booking> bookingList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book,container, false);

        bookingList = new ArrayList<>();

        calendarView = (CalendarView) view.findViewById(R.id.calenderBook);
        startSpinnerBook = (Spinner) view.findViewById(R.id.startSpinnerBook);
        endSpinnerBook = (Spinner) view.findViewById(R.id.endSpinnerBook);
        avaiableRoom = (Button) view.findViewById(R.id.avaiableRoom);
        btnBook = (Button) view.findViewById(R.id.btnBook);
        generateLayout();
        GetBestillingTask getBestillingTask = new GetBestillingTask();
        getBestillingTask.execute();

        avaiableRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetBestillingTask getBestillingTask = new GetBestillingTask();
                getBestillingTask.execute();
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (Booking booking:bookingList
                     ) {
                    Toast.makeText(getContext(), String.valueOf(booking.getId()), Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    public void generateLayout(){
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        selectedDate = sdf.format(calendarView.getDate());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year+"-"+(month+1)+"-"+dayOfMonth;
                //Toast.makeText(getContext(), selectedDate, Toast.LENGTH_SHORT).show();
            }
        });


        startAdapter = ArrayAdapter.createFromResource(getContext(),R.array.startTime,android.R.layout.simple_spinner_dropdown_item);
        startSpinnerBook.setAdapter(startAdapter);

        startSpinnerBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = String.valueOf(parent.getItemAtPosition(position));
                String[] dateString = date.split(":");
                startTime = Integer.parseInt(dateString[0]);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                startTime = 0;
            }
        });

        endAdapter = ArrayAdapter.createFromResource(getContext(),R.array.endTime,android.R.layout.simple_spinner_dropdown_item);
        endSpinnerBook.setAdapter(endAdapter);
        endSpinnerBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = String.valueOf(parent.getItemAtPosition(position));
                String[] dateString = date.split(":");

                if(startTime>=Integer.parseInt(dateString[0])){
                    Toast.makeText(getContext(), "slutt tiden kan ikke være før start tiden!", Toast.LENGTH_SHORT).show();
                }else{
                    endTime = Integer.parseInt(dateString[0]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                endTime = 0;
            }
        });


    }


    private class GetBestillingTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String query = "http://student.cs.hioa.no/~s315572/bestillinglistout.php?Date="+selectedDate;
            String s = "";
            String output = "";
            String retur = "";
            bookingList.clear();

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
                        String id = jsonobject.getString("romId");
                        String date = jsonobject.getString("date");
                        String start = jsonobject.getString("start");
                        String end = jsonobject.getString("end");
                        Booking booking = new Booking(Integer.parseInt(id),date,start,end);

                        bookingList.add(booking);

                        retur = retur + id + "," +date + ","+start+","+end+ "\n";
                    }
                    return retur;
                } catch (JSONException e) {
                    e.printStackTrace();
                    retur = "Ingen";
                }
                return retur;
            } catch (Exception e) {
                return "Noe gikk feil";
            }
        }

        @Override
        protected void onPostExecute(String resultat) {
                Toast.makeText(getContext(), resultat, Toast.LENGTH_SHORT).show();
        }

    }

}
