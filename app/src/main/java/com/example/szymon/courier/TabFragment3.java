package com.example.szymon.courier;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class TabFragment3 extends Fragment{

    private FirebaseAuth auth;
    public String myIDKURIER;
    private Button button_wyloguj,button_poakz_wszystko, button_poakz_mape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflater.inflate(R.layout.tab_fragment_3, container, false);
        MainActivity activity = (MainActivity)getActivity();
        String myDataFromActivity = activity.getMyData();
        Log.d("---TabFragment3 ", "ID KURIERA PRZESLANE  w TAB 3: " + myDataFromActivity);
        myIDKURIER = myDataFromActivity;
        View rootView = inflater.inflate(R.layout.tab_fragment_3, container, false);
        button_poakz_wszystko = rootView.findViewById(R.id.button_poakz_wszystko);

        if (Objects.equals(myIDKURIER, "szymon@gmail.com")) {
            button_poakz_wszystko.setVisibility(View.VISIBLE);
        }else{
            button_poakz_wszystko.setVisibility(View.GONE);
        }


        button_wyloguj = rootView.findViewById(R.id.button_wyloguj);
        button_wyloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Wylogowano", Toast.LENGTH_LONG).show();
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();

            }
        });

        button_poakz_wszystko = rootView.findViewById(R.id.button_poakz_wszystko);
        button_poakz_wszystko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetworkAvailable(getActivity())){
                    startActivity(new Intent(getActivity(), ListOfAll.class));
                    //getActivity().finish();
                }
                else{
                    Snackbar.make(getView(), "Brak połączenia z internetem", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        button_poakz_mape = rootView.findViewById(R.id.button_poakz_mape);
        button_poakz_mape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable(getActivity())){
                    startActivity(new Intent(getActivity(), AllParcelOnMaps.class));
                    //getActivity().finish();
                }
                 else{
                    Snackbar.make(getView(), "Brak połączenia z internetem", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
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