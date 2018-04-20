package com.example.szymon.courier;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class DeleteParcel extends AppCompatActivity {

    private Button usunPaczke;
    private ArrayList<String> pracelList;
    private Spinner nrPaczki;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    boolean czynet;
    String ID_Kuriera;
    Spinner s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_parcel);
        Intent myLocalIntent = getIntent();
        ID_Kuriera = myLocalIntent.getExtras().getString("ID");
        myRef = database.getReference("parcels");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nrPaczki = (Spinner) findViewById(R.id.spinner);
        ReadFirstList(ID_Kuriera);
        DeleteParcel();
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void DeleteParcel() {
        usunPaczke = (Button) findViewById(R.id.usun_paczke);
        usunPaczke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(isNetworkAvailable(getApplicationContext())){
                    String nrPaczkiString = String.valueOf(nrPaczki.getSelectedItem());
                    myRef.child(nrPaczkiString).removeValue();
                    Log.d("Delete: ", "Deleting ..");
                    Toast.makeText(getApplicationContext(), " Usunieto paczke ID: " + nrPaczkiString, Toast.LENGTH_SHORT).show();
                    finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), " Sprawdź połączenie z interentem ", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception en) {
                    Toast.makeText(getApplicationContext(), " ERROR " + en.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Error: ", "Blad o nazwie: " + en.getMessage());
                }
            }
        });
    }

    private void ReadFirstList(String ID) {
        // Query query = myRef;
        Query query = myRef.orderByChild("parcelKurier").equalTo(ID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pracelList = new ArrayList<>();
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    Parcel value = iterator.next().getValue(Parcel.class);
                    String ID = value.getParcelID();
                    pracelList.add(ID);
                }
                s = (Spinner) findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(  getApplicationContext() , android.R.layout.simple_spinner_item, pracelList);
                s.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Blad", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
