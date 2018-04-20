package com.example.szymon.courier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterList extends ArrayAdapter{

    private Context context;
    private ArrayList<Parcel> paczka;

    public AdapterList(Context context, int textViewResourceId, ArrayList objects) {
        super(context,textViewResourceId, objects);
        this.context= context;
        paczka=objects;
    }

    private class ViewHolder
    {
        TextView paczkaName;
        TextView paczkaAdres1;
        TextView paczkaID;
        TextView paczkaNumerTel;
        TextView paczkaStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder=null;
        if (convertView == null)
        {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.paczka_details, null);

            holder = new ViewHolder();
            holder.paczkaName = (TextView)convertView.findViewById(R.id.paczkaName);
            holder.paczkaAdres1 = (TextView)convertView.findViewById(R.id.paczkaAdres1);
            holder.paczkaID = (TextView)convertView.findViewById(R.id.paczkaID);
            holder.paczkaNumerTel = (TextView)convertView.findViewById(R.id.paczkaNumerTel);
            holder.paczkaStatus = (TextView)convertView.findViewById(R.id.paczkaStatus);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Parcel individualParcel= paczka.get(position);
        holder.paczkaID.setText(" Numer ID: " +  individualParcel.getParcelID() + "");
        holder.paczkaName.setText(" Odbiorca: " +  individualParcel.getParcelName() + "");
        holder.paczkaAdres1.setText(" Adres: "+ individualParcel.getParcelAdres1()+ " " +individualParcel.getParcelAdres2());
        holder.paczkaNumerTel.setText(" Numer tel: "+ individualParcel.getParcelNumertel()+ " ");
        holder.paczkaStatus.setText(" "+ individualParcel.getparcelStatus()+ " ");

        return convertView;
    }
}
