package com.example.projektnizadatak.dialogFragment;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.OsobaAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.database.DatabaseTaskOsoba;
import com.example.projektnizadatak.model.Osoba;


public class DetaljiOsobe extends DialogFragment {

    private static final  String ARG_OSOBA="osoba";
    private Osoba selectedPerson;
    private DataBase database;

    private OsobaAdapter adapter;

    public static DetaljiOsobe newInstance(Osoba selektovanaOsoba) {
        DetaljiOsobe fragment = new DetaljiOsobe();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OSOBA, selektovanaOsoba);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAdapter(OsobaAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_detalji_osobe, container, false);
        database= DataBase.getInstance(getActivity());
        if(getArguments() != null){
            selectedPerson = (Osoba) getArguments().getSerializable(ARG_OSOBA);
        }

        EditText ime = view.findViewById(R.id.editTextImeView);
        EditText prezime = view.findViewById(R.id.editTextPrezimeView);
        Button buttonUpdate = view.findViewById(R.id.buttonUpdatePerson);
        Button buttonDelete = view.findViewById(R.id.buttonDeletePerson);

        ime.setText(selectedPerson.getIme());
        prezime.setText(selectedPerson.getPrezime());

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPerson.setIme(ime.getText().toString());
                selectedPerson.setPrezime(prezime.getText().toString());
                new DatabaseTaskOsoba(new DatabaseTaskOsoba.DatabaseTaskListener() {
                    @Override
                    public void onTaskCompleted(boolean success) {
                        if (success) {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.update_true), Toast.LENGTH_SHORT).show();

                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, database, selectedPerson, 1).execute();
                dismiss();
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new DatabaseTaskOsoba(new DatabaseTaskOsoba.DatabaseTaskListener() {
                    @Override
                    public void onTaskCompleted(boolean success) {
                        if (success) {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.delete_true), Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, database, selectedPerson, 2).execute();


            }
        });

        return  view;

    }
}