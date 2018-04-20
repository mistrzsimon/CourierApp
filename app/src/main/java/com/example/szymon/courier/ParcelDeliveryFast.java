package com.example.szymon.courier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.client.android.CaptureActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ParcelDeliveryFast extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    String ID_Kuriera;
    private Spinner status;
    private Button aktualizuj_paczke_do_bazy, scaner;
    private ArrayList<String> pracelList;
    private ArrayList<Parcel>  parceListToEqual;
    private Spinner nrPaczki;
    private TextView Dane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_delivery_fast);
        Intent myLocalIntent = getIntent();
        ID_Kuriera = myLocalIntent.getExtras().getString("ID");
        myRef = database.getReference("parcels");
        nrPaczki = (Spinner) findViewById(R.id.spinnerID);
        Dane = (TextView)findViewById(R.id.textView8);
        scaner = (Button)findViewById(R.id.scaner);
        status = (Spinner) findViewById(R.id.spinner);
        ReadAllListToDelivery(ID_Kuriera);
        setToolbar();
        updateParcelToBase();
        scaner.setOnClickListener(this);

        nrPaczki.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            boolean flaga = false;
            @Override
            public void onItemSelected(AdapterView parent, View v, int pos, long id) {
                Dane.setText("    ");
                if(Objects.equals(parent.getItemAtPosition(pos).toString(), "")){
                    //Toast.makeText(getApplicationContext(), " Jestem w spinnerze i pole jest puste ", Toast.LENGTH_SHORT).show();
                    Dane.setText("    ");
                }else{
                    // Toast.makeText(getApplicationContext(), " Numer na spinerze: "+ parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
                    if( Objects.equals(parceListToEqual.size(), 0)){
                        Toast.makeText(getApplicationContext(), "parcelListEqual = 0", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String IDporownianie;
                        for(int i = 0; i < parceListToEqual.size(); i++) {

                            IDporownianie = parceListToEqual.get(i).getParcelID();

                            if( Objects.equals( parent.getItemAtPosition(pos).toString(), IDporownianie)){
                               // Toast.makeText(getApplicationContext(), "Tak to sa te dane do wyswietlenia", Toast.LENGTH_SHORT).show();
                                Parcel paczka = parceListToEqual.get(i);
                                Dane.setText("Odbiorca: "+paczka.getParcelName()+"\nUl.: " + paczka.getParcelAdres1()+"\nMiasto: " + paczka.getParcelAdres2()+ "\nNumer tel: " + paczka.getParcelNumertel());
                                flaga = true;
                                //break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView arg0) {
            }});
    }


    private void updateParcelToBase() {
        aktualizuj_paczke_do_bazy = (Button) findViewById(R.id.aktualizuj_paczke);
        aktualizuj_paczke_do_bazy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status1 = String.valueOf(status.getSelectedItem());
                String nrID = String.valueOf(nrPaczki.getSelectedItem());
                try {
                    if( Objects.equals(status1, "") ||  Objects.equals(nrID, "")) {
                        Toast.makeText(getApplicationContext(), " Proszę podać wszystkie dane ", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if(isNetworkAvailable(getApplicationContext())){
                            myRef.child(nrID).child("parcelStatus").setValue(status1);
                            Log.d("Update: ", "Updating ..");
                            Toast.makeText(getApplicationContext(), " Dostarczono ", Toast.LENGTH_LONG).show();
                            //Intent GoToMain = new Intent( getApplicationContext() , MainActivity.class);
                            // startActivity(GoToMain);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), " Sprawdź połączenie z interentem ", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception en) {
                    Toast.makeText(getApplicationContext(), " ERROR " + en.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Error: ", "Blad o nazwie: " + en.getMessage());
                }
            }
        });
    }


    public void onClick(View v){
        if(v.getId()==R.id.scaner){

            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(),CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN");
                intent.putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);
            }else
            {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 213);
                return;
            }

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //Toast.makeText(getApplicationContext(), " CONTENT: " + contents, Toast.LENGTH_LONG).show();
                setSpinText(nrPaczki,contents);

            } else if (resultCode == RESULT_CANCELED) {
                setSpinText(nrPaczki,"");
                Toast.makeText(getApplicationContext(), " BLAD - Nie nie zeskanowales", Toast.LENGTH_LONG).show();

            }
        }
    }

    public void setSpinText(Spinner spin, String text)
    {
        boolean nieMaTakiegoId = false;
        for(int i= 0; i < spin.getAdapter().getCount(); i++)
        {
            if( Objects.equals(spin.getAdapter().getItem(i).toString(), text) )
            {
                spin.setSelection(i);
                nieMaTakiegoId=true;
            }
        }
        if(nieMaTakiegoId==false){
            Toast.makeText(getApplicationContext(), " Nie ma paczki o takim ID ", Toast.LENGTH_SHORT).show();
            spin.setSelection(0);
        }

    }


    private void ReadAllListToDelivery(String ID) {
        Query query = myRef.orderByChild("parcelKurier").equalTo(ID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pracelList = new ArrayList<>();
                parceListToEqual = new ArrayList<Parcel>();
                pracelList.add("");
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    Parcel value = iterator.next().getValue(Parcel.class);
                    String ID = value.getParcelID();
                    if( Objects.equals(value.getparcelStatus(), "dodoreczenia") ||  Objects.equals(value.getparcelStatus(), "awizo") ){
                        pracelList.add(ID);
                        parceListToEqual.add(value);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(  getApplicationContext() , android.R.layout.simple_spinner_item, pracelList);
                nrPaczki.setAdapter(adapter);
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

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
