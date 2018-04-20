package com.example.szymon.courier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Objects;

public class TabFragment1 extends Fragment{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private ListView mListView;
    private TextView text, text2;
    private ArrayList<Parcel> pracelList;
    private ArrayList<Parcel> pracelList1;
    private ProgressBar progressBar;
    public String myIDKURIER;
    private Parcel paczuszka;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity activity = (MainActivity)getActivity();
        String myDataFromActivity = activity.getMyData();
        Log.d("--- TabFragment1 ", "ID KURIERA PRZESLANE  w TAB 1: " + myDataFromActivity);
        myIDKURIER = myDataFromActivity;
        View rootView = inflater.inflate(R.layout.tab_fragment_1, container, false);
        myRef = database.getReference("parcels");
        mListView = rootView.findViewById(R.id.list);
        text = rootView.findViewById(R.id.textView12);
        text2= rootView.findViewById(R.id.textView13);
        text2.setVisibility(View.GONE);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        if(isNetworkAvailable(getActivity())){
            Log.d("--- TAB1  ---", " Czytamy dane z ReadFirstList " );
            ReadFirstList(myIDKURIER);
            Log.d("--- TAB1  ---", " Czytamy dane z readDataAfterAdd " );
            readDataAfterAdd(myIDKURIER);
            Log.d("--- TAB1  ---", " Po czytaniu" );
        }
        else{
            Snackbar.make(container, "Nie masz połączenia z internetem", Snackbar.LENGTH_LONG).show();
            text2.setVisibility(View.VISIBLE);
        }

        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(TabFragment1.this).attach(TabFragment1.this).commit();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                if(isNetworkAvailable(getActivity())){
                    paczuszka= pracelList1.get(position);
                    makeDialog(paczuszka);
                }
                else{
                    Snackbar.make(getView(), "Nie masz połączenia z internetem", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    private void makeDialog( final Parcel paczka){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Czy chcesz pokazać na mapie:");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Tak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent GoToMaps = new Intent(getActivity(), MapsActivity.class);
                        GoToMaps.putExtra("parcel", new Gson().toJson(paczka) );
                        startActivity(GoToMaps);
                    }
                });

        builder1.setNeutralButton(
                "Przejdz do dostarczenia",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent Delivery=new Intent(getActivity(), ParcelDeliver.class);
                        Delivery.putExtra("parcel", new Gson().toJson(paczka) );
                        startActivity(Delivery);
                    }
                });
        builder1.setNegativeButton(
                "Anuluj",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void ReadFirstList(final String ID) {
        text.setVisibility(View.GONE);
        Query query = myRef.orderByChild("parcelStatus").equalTo("dodoreczenia");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("---ReadFirstList TAB1-", " Jestem w onDataChange " );

                pracelList = new ArrayList<Parcel>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                for (DataSnapshot aSnapshotIterator : snapshotIterator) {
                    Parcel value = aSnapshotIterator.getValue(Parcel.class);
                    pracelList.add(value);
                    Log.d("---ReadFirstList TAB1-", " Query: paczka ID " + value.getParcelID() + " dla kuriera " + value.getparcelKurier() );
                }
                pracelList1 = new ArrayList<Parcel>();
                for(int i = 0; i < pracelList.size(); i++) {

                    Parcel paczka = pracelList.get(i);
                    if( Objects.equals(paczka.getparcelKurier(), ID)){
                        pracelList1.add(paczka);
                    }
                }
                AdapterList myCustomAdapter=null;
                myCustomAdapter = new AdapterList(getActivity(), android.R.layout.simple_list_item_1, pracelList1);
                mListView.setAdapter(myCustomAdapter);
                progressBar.setVisibility(View.GONE);
                if( Objects.equals(pracelList1.size(), 0)){
                    text.setVisibility(View.VISIBLE);
                }
                Log.d(" --- SIZE --- ", "SIZE ReadFirstList " + pracelList1.size() );
                Log.d(" --- LIST --- ", "TUTAJ NASTOPILO SPRWADENIE I WCZYTANIE LISTVIEW DODORECZENIA ReadFirstList ------");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Blad", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    private void readDataAfterAdd(final String ID){
        text.setVisibility(View.GONE);
        Query query = myRef.orderByChild("parcelStatus").equalTo("dodoreczenia");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pracelList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                for (DataSnapshot aSnapshotIterator : snapshotIterator) {
                    Parcel value = aSnapshotIterator.getValue(Parcel.class);
                    pracelList.add(value);
                    Log.d("--readDataAfterAdd TAB1", " Query: paczka ID " + value.getParcelID() + " dla kuriera " + value.getparcelKurier() );
                }
                pracelList1 = new ArrayList<Parcel>();
                for(int i = 0; i < pracelList.size(); i++) {

                    Parcel paczka = pracelList.get(i);
                    if( Objects.equals(paczka.getparcelKurier(), ID)){
                        pracelList1.add(paczka);
                    }
                }
                AdapterList myCustomAdapter=null;
                myCustomAdapter = new AdapterList(getActivity(), android.R.layout.simple_list_item_1, pracelList1);
                mListView.setAdapter(myCustomAdapter);
                text.setVisibility(View.GONE);
                if( Objects.equals(pracelList1.size(), 0)){
                    text.setVisibility(View.VISIBLE);
                }
                Log.d(" --- SIZE --- ", "SIZE readDataAfterAdd " + pracelList1.size() );
                Log.d(" --- LIST --- ", "TUTAJ NASTOPILO SPRWADENIE I WCZYTANIE LISTVIEW DODORECZENIA readDataAfterAdd ------");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Blad", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

}


