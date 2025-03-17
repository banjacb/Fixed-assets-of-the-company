package com.example.projektnizadatak.addFragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.maps.MapsActivity;
import com.example.projektnizadatak.model.Lokacija;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class AddLokacija extends Fragment {

    private DataBase database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_add_lokacija, container, false);
        database=DataBase.getInstance(getActivity());

        Button buttonAdd = view.findViewById(R.id.addLokacija);
        Spinner spinnerGrad= view.findViewById(R.id.spinnerGrad);
        EditText id= view.findViewById(R.id.editTextId);

        List<String> gradoviList = new ArrayList<>(MapsActivity.GRADOVI_MAP.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, gradoviList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrad.setAdapter(adapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String izabraniGrad = spinnerGrad.getSelectedItem().toString();

                if (!MapsActivity.GRADOVI_MAP.containsKey(izabraniGrad)) {
                    Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }

                LatLng koordinate = MapsActivity.GRADOVI_MAP.get(izabraniGrad);


                Lokacija lokacija = new Lokacija();
                lokacija.setPostanskiBroj(Integer.parseInt(id.getText().toString()));
                lokacija.setGrad(izabraniGrad);
                lokacija.setX(koordinate.latitude);
                lokacija.setY(koordinate.longitude);
                new InsertTask(AddLokacija.this,lokacija).execute();
                Toast.makeText(getActivity(),getString(R.string.add_true),Toast.LENGTH_SHORT).show();

            }
        });


        return  view;
    }


    private static class InsertTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<AddLokacija> activityReference;
        private Lokacija location;
        InsertTask(AddLokacija context, Lokacija location) {
            activityReference = new WeakReference<>(context);
            this.location = location;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            activityReference.get().database.lokacijaDao().insert(location);
            return null;
        }
    }
}