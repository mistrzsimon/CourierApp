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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class AddParcel extends AppCompatActivity {

    private Button dodaj_paczke;
    private EditText ID_kurier, name, adres1, adres2, numertel;
    private Spinner status;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    String key;
    int keyi;
    String ID_Kuriera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parcel);
        Intent myLocalIntent = getIntent();
        ID_Kuriera = myLocalIntent.getExtras().getString("ID");
        myRef = database.getReference("parcels");
        getLastId();
        setToolbar();
        ID_kurier = (EditText)findViewById(R.id.ID_kuriera);
        ID_kurier.setText(ID_Kuriera);
        name = (EditText)findViewById(R.id.Name);
        adres1 = (EditText)findViewById(R.id.Adres1);
        adres2 = (EditText)findViewById(R.id.Adres2);
        numertel = (EditText)findViewById(R.id.NumerTel);
        status = (Spinner) findViewById(R.id.spinner);
        addParcelToBase();
    }


    private void addParcelToBase() {
        dodaj_paczke = (Button) findViewById(R.id.dodaj_paczke_do_bazy);
        dodaj_paczke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastId();
                String name1 = name.getText().toString();
                String addres1 = adres1.getText().toString();
                String addres2 = adres2.getText().toString();
                String numtel1 = numertel.getText().toString();
                String kurier1 = ID_kurier.getText().toString();
                String status1 = String.valueOf(status.getSelectedItem());

                if( Objects.equals(name1, "") ||  Objects.equals(addres1, "") || Objects.equals(addres2, "") || Objects.equals(kurier1, "") || Objects.equals(status1, "") || Objects.equals(numtel1, "")   ) {
                    Toast.makeText(getApplicationContext(), " Proszę podać wszystkie dane ", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        if(Objects.equals(key, null)){
                            key="0";
                        }
                        keyi = Integer.parseInt(key);
                        keyi=keyi+1;
                        Parcel paczkadododania = new Parcel();
                        paczkadododania.setParcelID(String.valueOf(keyi));
                        paczkadododania.setparcelName(name1);
                        paczkadododania.setparcelAdres1(addres1);
                        paczkadododania.setparcelAdres2(addres2);
                        paczkadododania.setparcelNumertel(numtel1);
                        paczkadododania.setparcelKurier(kurier1);
                        paczkadododania.setparcelStatus(status1);

                        if(isNetworkAvailable(getApplicationContext())){
                        myRef.child(Integer.toString(keyi)).setValue(paczkadododania);
                        Toast.makeText(getApplicationContext(), " Dodano paczke ", Toast.LENGTH_LONG).show();
                        finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), " Sprawdź połączenie z interentem ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception en) {
                        Toast.makeText(getApplicationContext(), " ERROR " + en.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void getLastId(){
        Query query = myRef.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Log.d("User key", child.getKey());
                    Log.d("User val", child.getValue().toString());
                    key=child.getKey();
                }
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
