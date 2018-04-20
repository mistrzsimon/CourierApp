package com.example.szymon.courier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ListOfAll extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private ListView mListView;
    private ArrayList pracelList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_all);
        setToolbar();
        myRef = database.getReference("parcels");
        mListView = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ReadFirstList();
        readDataAfterAdd();
    }

    private void ReadFirstList() {
        Query query = myRef.orderByChild("parcelID");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pracelList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    Parcel value = iterator.next().getValue(Parcel.class);
                    pracelList.add(value);
                }
                AdapterList myCustomAdapter=null;
                myCustomAdapter = new AdapterList( getApplicationContext() , android.R.layout.simple_list_item_1, pracelList);
                mListView.setAdapter(myCustomAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Blad", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    private void readDataAfterAdd(){
        Query query = myRef.orderByChild("parcelID");
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
                AdapterList myCustomAdapter=null;
                myCustomAdapter = new AdapterList(getApplicationContext(), android.R.layout.simple_list_item_1, pracelList);
                mListView.setAdapter(myCustomAdapter);
                Log.d(" --- LIST --- ", "TUTAJ NASTOPILO SPRWADENIE I WCZYTANIE LISTVIEW ALL ------");
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
