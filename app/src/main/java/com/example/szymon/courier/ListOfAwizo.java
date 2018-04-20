package com.example.szymon.courier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ListOfAwizo extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private ListView mListView;
    private TextView text;
    private ArrayList<Parcel> pracelList;
    private ProgressBar progressBar;
    private ArrayList<Parcel> pracelList1;
    String ID_Kuriera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_awizo);
        setToolbar();
        Intent myLocalIntent = getIntent();
        ID_Kuriera = myLocalIntent.getExtras().getString("ID");
        // tutaj nie przechodzi puto jest
        myRef = database.getReference("parcels");
        mListView = (ListView) findViewById(R.id.list);
        text = (TextView) findViewById(R.id.textView12);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ReadFirstList(ID_Kuriera);
        readDataAfterAdd(ID_Kuriera);
    }

    private void ReadFirstList(final String ID) {
        text.setVisibility(View.GONE);
        Query query = myRef.orderByChild("parcelStatus").equalTo("awizo");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                text.setVisibility(View.GONE);
                pracelList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    Parcel value = iterator.next().getValue(Parcel.class);
                    pracelList.add(value);
                }
                pracelList1 = new ArrayList<Parcel>();
                for(int i = 0; i < pracelList.size(); i++) {

                    Parcel paczka = pracelList.get(i);
                    if( Objects.equals(paczka.getparcelKurier(), ID)){
                        pracelList1.add(paczka);
                    }
                }
                AdapterList myCustomAdapter=null;
                myCustomAdapter = new AdapterList( getApplicationContext() , android.R.layout.simple_list_item_1, pracelList1);
                mListView.setAdapter(myCustomAdapter);
                progressBar.setVisibility(View.GONE);
                if( Objects.equals(pracelList1.size(), 0)){
                    text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Blad", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    private void readDataAfterAdd(final String ID){
        text.setVisibility(View.GONE);
        Query query = myRef.orderByChild("parcelStatus").equalTo("awizo");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pracelList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    Parcel value = iterator.next().getValue(Parcel.class);
                    pracelList.add(value);
                }

                pracelList1 = new ArrayList<Parcel>();
                for(int i = 0; i < pracelList.size(); i++) {

                    Parcel paczka = pracelList.get(i);
                    if( Objects.equals(paczka.getparcelKurier(), ID)){
                        pracelList1.add(paczka);
                    }
                }
                if( Objects.equals(pracelList1.size(), 0)){
                    text.setVisibility(View.VISIBLE);
                }
                AdapterList myCustomAdapter=null;
                myCustomAdapter = new AdapterList(getApplicationContext(), android.R.layout.simple_list_item_1, pracelList1);
                mListView.setAdapter(myCustomAdapter);
                Log.d(" --- LIST --- ", "TUTAJ NASTOPILO SPRWADENIE I WCZYTANIE LISTVIEW AWIZO ------");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
