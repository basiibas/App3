package com.example.bas.app3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeDialog extends AppCompatDialogFragment {

    TextView nameCustom, descriptionCustom;
    ListView list;
    RelativeLayout topDiv;
    String snippet;
    String[] markerInfo;
    String id, name, description;
    List<Booking> bookingList;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        bookingList = new ArrayList<>();

        snippet = getArguments().getString("id");
        markerInfo = snippet.split(",");
        id = markerInfo[0];
        name = markerInfo[1];
        description = markerInfo[2];

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom,null);

        topDiv = (RelativeLayout) view.findViewById(R.id.topDiv);
        nameCustom = (TextView) view.findViewById(R.id.nameCustom);
        descriptionCustom = (TextView) view.findViewById(R.id.descriptionCustom);
        list = (ListView) view.findViewById(R.id.listCustom);

        builder.setView(view)
                .setNegativeButton("close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        nameCustom.setText(name);
        descriptionCustom.setText(description);

        GetBestiilingTask getBestiilingTask = new GetBestiilingTask();
        getBestiilingTask.execute();

        return builder.create();
    }


    private class GetBestiilingTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String query = "http://student.cs.hioa.no/~s315572/bestillingout.php?Id="+ id;
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
                        String date = jsonobject.getString("date");
                        String start = jsonobject.getString("start");
                        String end = jsonobject.getString("end");
                        Booking booking = new Booking(date,start,end);

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
            if(resultat.equals("Ingen"))
                Toast.makeText(getContext(), "Ingen bestillinger", Toast.LENGTH_SHORT).show();
            list.setAdapter(new CustomListAdapter(getContext(),bookingList));

        }

    }







}
