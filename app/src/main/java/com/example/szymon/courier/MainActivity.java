package com.example.szymon.courier;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    String ID_Kuriera_po_emailu;
    String IDKuriera;
    String email = null;
    private String myString;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("--- MainActivity  ---", " START " );

        if(isNetworkAvailable(getApplicationContext())){
        try {
            auth = FirebaseAuth.getInstance();
            Intent myLocalIntent = getIntent();
            Bundle myBundle = myLocalIntent.getExtras();
            try {
                email = myBundle.getString("email");
            } catch (Exception en) {
                //do nothing
            }
            ID_Kuriera_po_emailu = email;

            if (auth.getCurrentUser() != null) {
                email = auth.getCurrentUser().getEmail();
                myString=String.valueOf(email);
                IDKuriera = email;
            }

            setNotification();
            auth = FirebaseAuth.getInstance();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            };

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.addTab(tabLayout.newTab().setText("Paczki"));
            tabLayout.addTab(tabLayout.newTab().setText("Doreczone"));
            tabLayout.addTab(tabLayout.newTab().setText("Inne"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            final PagerAdapter adapter = new PagerAdapter
                    (getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.setPageTransformer(true, new RotateUpTransformer());
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            }
            catch(Exception e) {
                Toast.makeText(MainActivity.this, "ERROR: " + e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }else{
            auth = FirebaseAuth.getInstance();
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }


    public void setNotification(){
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        //Toast.makeText(MainActivity.this, "Action Add cliced", Toast.LENGTH_LONG).show();
                        Intent NewParcel = new Intent(getApplicationContext() , AddParcel.class);
                        Bundle myDataBundle1 = new Bundle();
                        myDataBundle1.putString("ID", IDKuriera);
                        NewParcel.putExtras(myDataBundle1);
                        startActivity(NewParcel);
                        break;
                    case R.id.action_delivery:
                        //Toast.makeText(MainActivity.this, "Delivery", Toast.LENGTH_LONG).show();
                        Intent DeliveryParcel = new Intent(getApplicationContext() , ParcelDeliveryFast.class);
                        Bundle myDataBundle3 = new Bundle();
                        myDataBundle3.putString("ID",IDKuriera);
                        DeliveryParcel.putExtras(myDataBundle3);
                        startActivity(DeliveryParcel);
                        break;
                    case R.id.action_delete:
                        //Toast.makeText(MainActivity.this, "Action Delete cliced", Toast.LENGTH_LONG).show();
                        Intent DeleteParcel = new Intent(getApplicationContext(), DeleteParcel.class);
                        Bundle myDataBundle2 = new Bundle();
                        myDataBundle2.putString("ID", IDKuriera);
                        DeleteParcel.putExtras(myDataBundle2);
                        startActivity(DeleteParcel);
                        break;
                    case R.id.action_awizo:
                       // Toast.makeText(MainActivity.this, "Action Awizo cliced", Toast.LENGTH_LONG).show();
                        if(isNetworkAvailable(getApplicationContext())){
                            Intent AwizoParcel = new Intent(getApplicationContext(), ListOfAwizo.class);
                            Bundle myDataBundle = new Bundle();
                            myDataBundle.putString("ID", IDKuriera);
                            AwizoParcel.putExtras(myDataBundle);
                            startActivity(AwizoParcel);
                        }
                        else{
                            Snackbar.make(findViewById(android.R.id.content) , "Brak połączenia z internetem", Snackbar.LENGTH_LONG).show();
                        }
                        break;
                }
                return true;
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

    public String getMyData() {
        return myString;
    }
}
