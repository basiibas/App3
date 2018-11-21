package com.example.bas.app3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<Booking> {

    private Context context;
    private List<Booking> bookingList;

    public CustomListAdapter(Context context, List<Booking> bookings){
        super(context, R.layout.adapter_custom, bookings);
        this.context = context;
        this.bookingList = bookings;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.adapter_custom, parent, false);

        TextView dateAdapter = view.findViewById(R.id.dateAdapter);
        TextView startAdapter = view.findViewById(R.id.startAdapter);
        TextView endAdapter = view.findViewById(R.id.endAdapter);

            dateAdapter.setText(bookingList.get(position).getDate());
            startAdapter.setText(bookingList.get(position).getStart());
            endAdapter.setText(bookingList.get(position).getEnd());





        return view;
    }
}
