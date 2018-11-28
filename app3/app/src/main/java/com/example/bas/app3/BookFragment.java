package com.example.bas.app3;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookFragment extends Fragment {

    CalendarView calendarView;
    Spinner startSpinnerBook, endSpinnerBook;
    Button avaiableRoom, btnBook;
    ArrayAdapter<CharSequence> startAdapter, endAdapter;
    String selectedDate, selectedRoomId;
    String startTime, endTime;
    List<Booking> bookingList;
    List<MapPoint> allRoomPointList;
    List<String>allRoomList;
    List<String> busyRoomList;
    ListView listView = null;
    boolean confirm;
    int checkStartDate, checkEndDate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);

        bookingList = new ArrayList<>();
        busyRoomList = new ArrayList<>();
        allRoomList = new ArrayList<>();
        allRoomPointList = new ArrayList<>();
        confirm = false;

        calendarView = (CalendarView) view.findViewById(R.id.calenderBook);
        startSpinnerBook = (Spinner) view.findViewById(R.id.startSpinnerBook);
        endSpinnerBook = (Spinner) view.findViewById(R.id.endSpinnerBook);
        avaiableRoom = (Button) view.findViewById(R.id.avaiableRoom);
        btnBook = (Button) view.findViewById(R.id.btnBook);

        generateLayout();
        //final GetBestillingTask getBestillingTask = new GetBestillingTask();
        //getBestillingTask.execute();

        avaiableRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sjekker om End er større enn Start.
                if(checkStartDate>=checkEndDate){
                    Toast.makeText(getContext(), "Ugyldig tid! Vennglist velg et annet tidspunkt", Toast.LENGTH_SHORT).show();
                }else{
                    bookingList.clear();
                    allRoomList.clear();
                    busyRoomList.clear();
                    GetBestillingTask getBestillingTask = new GetBestillingTask();
                    getBestillingTask.execute();
                }

            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!confirm) {
                    Toast.makeText(getContext(), " Please fill all informations!", Toast.LENGTH_SHORT).show();
                    String resultat = selectedDate + " , " + startTime + " , " + endTime + " , " + selectedRoomId;
                    //Toast.makeText(getContext(), resultat, Toast.LENGTH_SHORT).show();
                } else {
                    String resultat = selectedDate + " , " + startTime + " , " + endTime + " , " + selectedRoomId;
                    //Toast.makeText(getContext(), resultat, Toast.LENGTH_SHORT).show();

                    PostBestillingTask saveNewOrder = new
                            PostBestillingTask(selectedRoomId,selectedDate, startTime, endTime);
                    saveNewOrder.execute();

                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragment_layout,
                            homeFragment).commit();

                    BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);

                    bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);

                }

            }
        });


        return view;
    }

    public void generateLayout() {
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        selectedDate = sdf.format(calendarView.getDate());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                avaiableRoom.setText("Avaiable Room");
                confirm = false;
                //Toast.makeText(getContext(), selectedDate, Toast.LENGTH_SHORT).show();
            }
        });


        startAdapter = ArrayAdapter.createFromResource(getContext(), R.array.startTime, android.R.layout.simple_spinner_dropdown_item);
        startSpinnerBook.setAdapter(startAdapter);

        startSpinnerBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = String.valueOf(parent.getItemAtPosition(position));
                String[] dateString = date.split(":");
                checkStartDate = Integer.parseInt(dateString[0]);

                startTime = date;
                avaiableRoom.setText("Avaiable Room");
                confirm = false;
                //startTime = Integer.parseInt(dateString[0]);
                //Toast.makeText(getContext(), startTime, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                startTime = "07:00";
            }
        });

        endAdapter = ArrayAdapter.createFromResource(getContext(), R.array.endTime, android.R.layout.simple_spinner_dropdown_item);
        endSpinnerBook.setAdapter(endAdapter);
        endSpinnerBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = String.valueOf(parent.getItemAtPosition(position));
                String[] dateString = date.split(":");
                checkEndDate = Integer.parseInt(dateString[0]);

                /*
                if(checkDate == 10 || checkDate == 20){
                    //checkDate.replace("0","");
                    Toast.makeText(getContext(), String.valueOf(checkDate), Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getContext(), String.valueOf(checkDate), Toast.LENGTH_SHORT).show();
                }
                */




                //Toast.makeText(getContext(), checkDate, Toast.LENGTH_SHORT).show();

                avaiableRoom.setText("Avaiable Room");
                confirm = false;
                endTime = date;
                //Toast.makeText(getContext(), endTime, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                endTime = "08:00";
            }
        });


    }

    private class GetBestillingTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            busyRoomList.clear();

            StringBuilder startPlusEnMinutt = new StringBuilder(startTime);
            startPlusEnMinutt.setCharAt(4, '1');

            // String query = "http://student.cs.hioa.no/~s315572/bestillinglistout.php?Date=" + selectedDate;
            String query = "http://student.cs.hioa.no/~s315572/bestillinglistout.php?Date=" + selectedDate + "&Start=" + startTime + "&Startinclude=" + startPlusEnMinutt.toString() + "&End=" + endTime + "";
            String s = "";
            String output = "";
            String retur = "";


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
                        String roomName = jsonobject.getString("name");
                        String date = jsonobject.getString("date");
                        String start = jsonobject.getString("start");
                        String end = jsonobject.getString("end");

                        Booking booking = new Booking(Integer.parseInt(id), roomName, date, start, end);
                        bookingList.add(booking);
                        //busyRoomList.add(roomName);

                        retur = retur + roomName + "," + date + "," + start + "," + end + "\n";
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
        protected void onPostExecute(String s) {
            //Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();

            GetAllRoomTask getAllRoomTask = new GetAllRoomTask();
            getAllRoomTask.execute();

        }
    }

    private class GetAllRoomTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            allRoomList.clear();

            String query = "http://student.cs.hioa.no/~s315572/bestillingallout.php";
            String s = "";
            String output = "";
            String retur = "";


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
                        String roomid = jsonobject.getString("id");
                        String roomName = jsonobject.getString("name");


                        MapPoint mapPoint = new MapPoint(Integer.valueOf(roomid),roomName);
                        allRoomPointList.add(mapPoint);
                        allRoomList.add(roomName);

                        retur = retur + roomName + "\n";
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
        protected void onPostExecute(String s) {
            //Toast.makeText(getContext(), "Alle rom kommer her: " +s, Toast.LENGTH_LONG).show();


            if (bookingList.isEmpty()) {
                Toast.makeText(getContext(), "Ingen rom er booket, alle er tilgjennelig.", Toast.LENGTH_SHORT).show();
                listView = new ListView(getContext());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dialog_listcustom, R.id.textDialogCustom, allRoomList);
                listView.setAdapter(adapter);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setView(listView);
                final AlertDialog dialog = builder.create();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ViewGroup vg = (ViewGroup) view;
                        TextView txt = (TextView) vg.findViewById(R.id.textDialogCustom);
                        //Toast.makeText(getContext(), txt.getText().toString(), Toast.LENGTH_SHORT).show();
                        for (MapPoint mapPoint:allRoomPointList
                             ) {
                            if(txt.getText().toString().equals(mapPoint.name)){
                                //selectedRoomId = String.valueOf(booking.getId());
                                selectedRoomId = String.valueOf(mapPoint.id);
                            }
                        }
                        avaiableRoom.setText(txt.getText().toString());
                        dialog.dismiss();
                        //Toast.makeText(getContext(), selectedRoomId, Toast.LENGTH_SHORT).show();
                        confirm = true;

                    }
                });

                dialog.show();
            } else {

                //Toast.makeText(getContext(), String.valueOf(allRoomList.size()), Toast.LENGTH_SHORT).show();

                for (int i = 0; i < bookingList.size(); i++) {



                    for (int j = 0; j < allRoomList.size(); j++) {

                        //Toast.makeText(getContext(), String.valueOf(bookingList.get(i).name), Toast.LENGTH_SHORT).show();
                        if (bookingList.get(i).name.equals(allRoomList.get(j))) {
                            //Toast.makeText(getContext(), String.valueOf("Rommet er fjernet: " + allRoomList.get(j)), Toast.LENGTH_SHORT).show();
                            allRoomList.remove(j);
                        }

                    }
                }
                //Toast.makeText(getContext(), String.valueOf(allRoomList.size()), Toast.LENGTH_SHORT).show();


                if (allRoomList.size() < 1) {
                    Toast.makeText(getContext(), "Ingen rom er ledig! Velg et annet tidspunkt.", Toast.LENGTH_SHORT).show();
                } else {
                    listView = new ListView(getContext());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.dialog_listcustom, R.id.textDialogCustom, allRoomList);
                    listView.setAdapter(adapter);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setView(listView);
                    final AlertDialog dialog = builder.create();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ViewGroup vg = (ViewGroup) view;
                            TextView txt = (TextView) vg.findViewById(R.id.textDialogCustom);
                            for (MapPoint mapPoint:allRoomPointList
                                    ) {
                                if(txt.getText().toString().equals(mapPoint.name)){
                                    //selectedRoomId = String.valueOf(booking.getId());
                                    selectedRoomId = String.valueOf(mapPoint.id);
                                }
                            }
                            //Toast.makeText(getContext(), txt.getText().toString(), Toast.LENGTH_SHORT).show();
                            avaiableRoom.setText(txt.getText().toString());
                            dialog.dismiss();
                            //Toast.makeText(getContext(), selectedRoomId, Toast.LENGTH_SHORT).show();
                            confirm = true;
                            
                        }
                    });

                    dialog.show();
                }


            }


        }
    }


    private class PostBestillingTask extends AsyncTask<Void, Void, String> {

        String selectedDate, selectedRoomId;
        String startTime, endTime;

        public PostBestillingTask(String selectedRoomId, String selectedDate, String startTime, String endTime) {
            this.selectedDate = selectedDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.selectedRoomId = selectedRoomId;
        }

        @Override
        protected String doInBackground(Void... params) {

            String query = "http://student.cs.hioa.no/~s315572/bestillingin.php?Roomid="+this.selectedRoomId+"&Date="+this.selectedDate+"&Start="+this.startTime+"&End="+endTime+"";

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

                return "Saved!";
            } catch (Exception e) {
                return "Noen gikk feil";
            }
        }

        @Override
        protected void onPostExecute(String resultat) {
            Toast.makeText(getContext(), resultat, Toast.LENGTH_LONG).show();

        }

    }

}



