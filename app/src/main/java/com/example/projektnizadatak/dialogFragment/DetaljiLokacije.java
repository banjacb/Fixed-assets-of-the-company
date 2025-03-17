package com.example.projektnizadatak.dialogFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.LokacijaAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.model.Lokacija;
import java.lang.ref.WeakReference;


public class DetaljiLokacije extends DialogFragment {

    private static final  String ARG_LOKACIJA= "lokacija";
    private DataBase dataBase;
    private Lokacija selectedLokacija;
    LokacijaAdapter adapter;

    public void setAdapter(LokacijaAdapter adapter) {
        this.adapter = adapter;
    }

    public static DetaljiLokacije newInstance(Lokacija selektovanaLokacija) {
        DetaljiLokacije fragment = new DetaljiLokacije();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOKACIJA, selektovanaLokacija);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_detalji_lokacije, container, false);
        dataBase = DataBase.getInstance(getActivity());
        if(getArguments() != null){
            selectedLokacija = (Lokacija) getArguments().getSerializable(ARG_LOKACIJA);
        }
        dataBase=DataBase.getInstance(getActivity());

        EditText postanskiBroj = view.findViewById(R.id.editTextId);
        Button buttonUpdate = view.findViewById(R.id.buttonUpdateLocation);
        Button buttonDelete = view.findViewById(R.id.buttonDeleteLocation);
        postanskiBroj.setText(String.valueOf(selectedLokacija.getPostanskiBroj()));

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedLokacija.setPostanskiBroj(Integer.valueOf(postanskiBroj.getText().toString()));
                new InsertTask(DetaljiLokacije.this, selectedLokacija, true).execute();
             adapter.notifyDataSetChanged();

                Toast.makeText(getActivity(),getString(R.string.update_true),Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });




        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InsertTask(DetaljiLokacije.this, selectedLokacija, false).execute();
                Toast.makeText(getActivity(),getString(R.string.delete_true),Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        return view;
    }




    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<DetaljiLokacije> activityReference;
        private Lokacija location;
        private boolean updateOrDelete;
        InsertTask(DetaljiLokacije context, Lokacija location, boolean updateOrDelete) {
            activityReference = new WeakReference<>(context);
            this.location = location;
            this.updateOrDelete = updateOrDelete;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            if(updateOrDelete)
                this.activityReference.get().dataBase.lokacijaDao().update(location);
            else
                this.activityReference.get().dataBase.lokacijaDao().delete(location);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){

        }
    }
}



