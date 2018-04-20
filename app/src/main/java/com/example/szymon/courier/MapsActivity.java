package com.example.szymon.courier;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String jsonMyObject;
    Parcel paczka;
    String Adres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
        setToolbar();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("parcel");
        }
        paczka = new Gson().fromJson(jsonMyObject, Parcel.class);
        Adres= paczka.getParcelAdres1()+" " +paczka.getParcelAdres2();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        }
        catch(Exception e) {
            Toast.makeText(this, "ERROR: " + e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng proba = getLocationFromAddress(this, Adres);
        if(Objects.equals(proba, null)){
            Toast.makeText(getApplicationContext(), " Nie mozemy wyświetlić punktu na mapie, gdyż taki nie istnieje.", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            mMap.addMarker(new MarkerOptions().position(proba).title(Adres));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(proba));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(proba).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, ParcelDeliver.class);
                intent.putExtra("parcel", new Gson().toJson(paczka) );
                startActivity(intent);
                finish();
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.windowlayout, null);
                TextView tvLat = v.findViewById(R.id.tv_lat);
                TextView tvLng = v.findViewById(R.id.tv_lng);
                tvLat.setText(paczka.getParcelAdres1()+" "+paczka.getParcelAdres2());
                tvLng.setText("Adresat: " + paczka.getParcelName() + "\nNumer tel: " + paczka.getParcelNumertel() + " \n");
                return v;

            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 3);
            if (Objects.equals(address.size(),0 )){
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Mapa z paczkami");
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
