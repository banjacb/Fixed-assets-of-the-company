package com.example.projektnizadatak.dialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.OsnovnoSredstvoAdapter;
import com.example.projektnizadatak.adapter.OsobaAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.database.DatabaseTaskOs;
import com.example.projektnizadatak.maps.MapsActivity;
import com.example.projektnizadatak.model.OsnovnoSredstvo;
import com.example.projektnizadatak.model.Osoba;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DetaljiOsnovnoSredstvo extends DialogFragment {


    private static final  String ARG_OS="arg_os";
    private OsnovnoSredstvo selectedOs;
    private DataBase database;

    private OsnovnoSredstvoAdapter adapter;
    public static DetaljiOsnovnoSredstvo newInstance(OsnovnoSredstvo selectedOs) {
        DetaljiOsnovnoSredstvo fragment = new DetaljiOsnovnoSredstvo();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OS, selectedOs);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAdapter(OsnovnoSredstvoAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root= inflater.inflate(R.layout.fragment_detalji_osnovno_sredstvo, container, false);
        database= DataBase.getInstance(getActivity());
        if(getArguments() != null){
            selectedOs = (OsnovnoSredstvo) getArguments().getSerializable(ARG_OS);
        }

        EditText naziv = root.findViewById(R.id.editTextNaziv);
        EditText opis = root.findViewById(R.id.editTextOpis);
        EditText barkod = root.findViewById(R.id.editTextBarkod);
        EditText cijena = root.findViewById(R.id.editTextCijena);
        EditText datum = root.findViewById(R.id.editTextDatum);

        ImageView imageViewPicture = root.findViewById(R.id.imageViewPicture);

        Button buttonDeleteOs = root.findViewById(R.id.buttonDeleteOs);
        Button buttonUpdateOs = root.findViewById(R.id.buttonUpdateOs);
        Button buttonShowOsInMapp = root.findViewById(R.id.buttonShowOs);


        naziv.setText(selectedOs.getNaizv());
        opis.setText(selectedOs.getOpis());
        barkod.setText(selectedOs.getBarkod());
        cijena.setText(String.valueOf(selectedOs.getCijena()));
        datum.setText(selectedOs.getDatum() != null ? selectedOs.getDatum().toString() : "");

        if (selectedOs.getSlika() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(selectedOs.getSlika(), 0, selectedOs.getSlika().length);
            imageViewPicture.setImageBitmap(bitmap);
        } else {

            imageViewPicture.setImageResource(R.drawable.ic_launcher_foreground);
        }

        buttonUpdateOs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedOs.setNaizv(naziv.getText().toString());
                selectedOs.setOpis(opis.getText().toString());
                selectedOs.setBarkod(barkod.getText().toString());
                selectedOs.setCijena(Double.parseDouble(cijena.getText().toString()));


                String datumText = datum.getText().toString();
                if (!datumText.isEmpty()) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date datumValue = dateFormat.parse(datumText);
                        selectedOs.setDatum(datumValue);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                new DatabaseTaskOs(new DatabaseTaskOs.DatabaseTaskListener() {
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
                }, database, selectedOs, 1).execute();
                dismiss();
            }
        });

        buttonDeleteOs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatabaseTaskOs(new DatabaseTaskOs.DatabaseTaskListener() {
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
                }, database, selectedOs, 2).execute();  // 2 je za delete
            }
        });

        buttonShowOsInMapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
        }
    }

}