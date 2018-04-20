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
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class ParcelDeliver extends AppCompatActivity {

    String jsonMyObject;
    Parcel paczka;
    private EditText Nrpaczki;
    private TextView Dane;
    private Spinner status;
    private Button aktualizuj_paczke_do_bazy;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_deliver);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("parcel");
            paczka = new Gson().fromJson(jsonMyObject, Parcel.class);
        }
        setToolbar();

        Nrpaczki = (EditText)findViewById(R.id.Nr_paczki);
        Dane = (TextView)findViewById(R.id.textView8);
        status = (Spinner) findViewById(R.id.spinner);
        myRef = database.getReference("parcels");
        Nrpaczki.setText(paczka.getParcelID());
        Dane.setText("Odbiorca: "+paczka.getParcelName()+"\nUl.: " + paczka.getParcelAdres1()+"\nMiasto: " + paczka.getParcelAdres2()+ "\nNumer tel: " + paczka.getParcelNumertel());

        updateParcelToBase();
    }

    private void updateParcelToBase() {
        aktualizuj_paczke_do_bazy = (Button) findViewById(R.id.aktualizuj_paczke_do_bazy);
        aktualizuj_paczke_do_bazy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status1 = String.valueOf(status.getSelectedItem());
                try {
                if(isNetworkAvailable(getApplicationContext())){
                    myRef.child(paczka.getParcelID()).child("parcelStatus").setValue(status1);
                    Log.d("Update: ", "Updating ..");
                    Toast.makeText(getApplicationContext(), " Dostarczono ", Toast.LENGTH_LONG).show();
                    //Intent GoToMain = new Intent( getApplicationContext() , MainActivity.class);
                    //startActivity(GoToMain);
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
                //Intent intent = new Intent(ParcelDeliver.this, MainActivity.class);
                //startActivity(intent);
                finish();
            }
        });
    }


}
