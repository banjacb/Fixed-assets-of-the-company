package com.example.projektnizadatak.addFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.database.DatabaseTaskOsoba;
import com.example.projektnizadatak.model.Osoba;


public class AddPerson extends Fragment {

    private DataBase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_person, container, false);
        database = DataBase.getInstance(getActivity());

        Button buttonAdd = view.findViewById(R.id.buttonSubmitOsoba);
        EditText name = view.findViewById(R.id.editTextIme);
        EditText surname = view.findViewById(R.id.editTextPrezime);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Osoba o = new Osoba();
                o.setIme(name.getText().toString());
                o.setPrezime(surname.getText().toString());


                new DatabaseTaskOsoba(new DatabaseTaskOsoba.DatabaseTaskListener() {
                    @Override
                    public void onTaskCompleted(boolean success) {
                        if (success) {

                            Toast.makeText(getActivity(), getString(R.string.add_true), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, database, o, 0).execute();
            }
        });


        return view;
    }




}