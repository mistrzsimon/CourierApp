package com.example.szymon.courier;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AllParcelOnMaps extends FragmentActivity  implements OnMapReadyCallback{

    private GoogleMap mMap;
    private FirebaseAuth auth;
    private ArrayList<Parcel> pracelList = new ArrayList<Parcel>();
    private ArrayList<Parcel> pracelList1 = new ArrayList<Parcel>();
    private ArrayList<Parcel> pracelList2 = new ArrayList<Parcel>();
    private ArrayList<LatLng> adressList = new ArrayList<LatLng>();
    private ArrayList<String> adressListString = new ArrayList<String>();
    Parcel paczka;
    String ID;
    String email = null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    String adres;
    LatLng Adresik;
    LatLngBounds.Builder builder;
    CameraUpdate cu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_parcel_on_maps);
        try {
            auth = FirebaseAuth.getInstance();
            myRef = database.getReference("parcels");
            Log.d(" --- MAPA WSZYSTKO --- ", " JESTES W KALSIE GDZIE POKAZUJE WSZSYTKIE PUNKTY NA MAPIE");
            setToolbar();
            if (Objects.equals(auth.getCurrentUser(), null)) {
                Toast.makeText(this, "Nie jestes zalogowany" , Toast.LENGTH_SHORT).show();
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
            else{
                email = auth.getCurrentUser().getEmail();
                ID=email;
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        }
            catch(Exception e) {
                Toast.makeText(this, "ERROR: " + e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Query query = myRef.orderByChild("parcelStatus").equalTo("dodoreczenia");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                for (DataSnapshot aSnapshotIterator : snapshotIterator) {
                    Parcel value = aSnapshotIterator.getValue(Parcel.class);
                    pracelList.add(value);
                }
                for(int i = 0; i < pracelList.size(); i++) {
                    Parcel paczka = pracelList.get(i);
                    if( Objects.equals(paczka.getparcelKurier(), ID)){
                        pracelList1.add(paczka);
                    }
                }
                Log.d(" --- SIZE --- ", "SIZE ReadFirstList " + pracelList1.size() );
                Log.d(" --- LIST --- ", "TUTAJ NASTOPILO SPRWADENIE I WCZYTANIE LISTVIEW DODORECZENIA ReadFirstList ------");
                for(int i = 0; i <(pracelList1.size()); i++) {
                    paczka = pracelList1.get(i);
                    adres = paczka.getParcelAdres1()+ " " +paczka.getParcelAdres2();
                    Adresik = getLocationFromAddress(getApplicationContext(), adres);

                    if( Objects.equals(Adresik, null)){
                        Toast.makeText(AllParcelOnMaps.this, " Podany adres nie istnieje: \n" + adres, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        adressList.add(Adresik);
                        adressListString.add(adres);
                        pracelList2.add(paczka);
                    }
                    Log.d(" ---onMapReady--- ", " Zapisalem dane do Listy" );
                }
                if(Objects.equals(adressList.size(), 0)){
                    Toast.makeText(AllParcelOnMaps.this, "Nie ma żadnych mozliwych paczek do wyswietlenia", Toast.LENGTH_SHORT).show();
                }else{
                    List<Marker> markersList = new ArrayList<Marker>();
                    for(int j = 0; j < adressList.size(); j++) {
                        Log.d(" ---onMapReady--- ", " Dodaje punkty na mapie " );
                        markersList.add(mMap.addMarker(new MarkerOptions().position(adressList.get(j)).title(adressListString.get(j))));
                       
                    }

                    builder = new LatLngBounds.Builder();
                    for (Marker m : markersList) {
                        builder.include(m.getPosition());
                    }
                    int padding = 250;
                    LatLngBounds bounds = builder.build();
                    cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            mMap.animateCamera(cu);
                        }
                    });

                }

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker arg0) {
                        Parcel paczuneszka;
                        String nr = arg0.getId().substring(1,arg0.getId().length());
                        int liczba;
                        View v=null;
                        try{
                            liczba =  Integer.valueOf(nr);
                            paczuneszka = pracelList2.get(liczba);
                            Log.d(" ---getInfoContents--- ", " Tutaj parceList.Size() "+ pracelList2.size() + " Paczka nr: " + paczuneszka.getParcelID());
                            //Toast.makeText( getApplicationContext() , "Tutaj Marker : getID " + arg0.getId() + " nr w liscie: " + liczba, Toast.LENGTH_SHORT).show();
                            v = getLayoutInflater().inflate(R.layout.windowlayout, null);
                            TextView tvLat = v.findViewById(R.id.tv_lat);
                            TextView tvLng = v.findViewById(R.id.tv_lng);
                            tvLat.setText("ul. "+paczuneszka.getParcelAdres1()+"\n"+paczuneszka.getParcelAdres2());
                            tvLng.setText("Adresat: " + paczuneszka.getParcelName() + "\nNumer tel: " + paczuneszka.getParcelNumertel());
                        }
                        catch (Exception en){
                            Toast.makeText( getApplicationContext() , "Bład przy próbie tworzenia InfoWindowAdapter ", Toast.LENGTH_SHORT).show();
                        }
                        return v;
                    }
                });

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(AllParcelOnMaps.this, ParcelDeliver.class);
                        intent.putExtra("parcel", new Gson().toJson(paczka) );
                        startActivity(intent);
                        finish();
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Blad", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
                address = coder.getFromLocationName(strAddress, 3);
                if (Objects.equals(address.size(), 0)) {
                    return null;
                }
                Address location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
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
