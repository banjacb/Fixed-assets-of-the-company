package com.example.projektnizadatak.dialogFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.OsnovnoSredstvoAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.database.DatabaseTaskOs;
import com.example.projektnizadatak.model.OsnovnoSredstvo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class DetaljiOsLista extends DialogFragment {

    private static final  String ARG_OS="arg_os";
    private OsnovnoSredstvo selectedOs;
    private DataBase database;

    private List<OsnovnoSredstvo> lista;

    private OsnovnoSredstvoAdapter adapter;


    public static DetaljiOsLista newInstance(OsnovnoSredstvo selectedOs) {
        DetaljiOsLista fragment = new DetaljiOsLista();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OS, selectedOs);
        fragment.setArguments(args);
        return fragment;
    }

    public List<OsnovnoSredstvo> getLista() {
        return lista;
    }

    public void setLista(List<OsnovnoSredstvo> lista) {
        this.lista = lista;
    }
    public void setAdapter(OsnovnoSredstvoAdapter adapter) {
        this.adapter = adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detalji_os_lista, container, false);
        database= DataBase.getInstance(getActivity());
        if(getArguments() != null){
            selectedOs = (OsnovnoSredstvo) getArguments().getSerializable(ARG_OS);
        }


        EditText naziv = root.findViewById(R.id.editTextNaziv);
        EditText trenutniZaduzeni= root.findViewById(R.id.edit_trenutna_osoba);
        EditText sledeciZduzeni=root.findViewById(R.id.edit_sledeca_osoba);
        EditText trenutnaLokacija= root.findViewById(R.id.edit_trenutna_lokacija);
        EditText sledecaLokacija=root.findViewById(R.id.edit_sledeca_lokacija);
        Button buttonDeleteOs = root.findViewById(R.id.buttonDeleteOs);
        Button buttonUpdateOs = root.findViewById(R.id.buttonUpdateOs);
        Button buttonShowOsInMapp = root.findViewById(R.id.buttonShowOs);

        naziv.setText(selectedOs.getNaizv());
        if (selectedOs.getFrom_osoba_id() != 0) {
            trenutniZaduzeni.setText(String.valueOf(selectedOs.getFrom_osoba_id()));
        } else {
            trenutniZaduzeni.setText("");
        }
        if (selectedOs.getFrom_osoba_id() != 0) {
            sledeciZduzeni.setText(String.valueOf(selectedOs.getTo_osoba_id()));
        } else {
            sledeciZduzeni.setText("");
        }
        if (selectedOs.getFrom_osoba_id() != 0) {
            trenutnaLokacija.setText(String.valueOf(selectedOs.getFrom_lokacija_id()));
        } else {
            trenutnaLokacija.setText("");
        }
        if (selectedOs.getFrom_osoba_id() != 0) {
            sledecaLokacija.setText(String.valueOf(selectedOs.getTo_lokacija_id()));
        } else {
            sledecaLokacija.setText("");
        }

        buttonUpdateOs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trenutniZaduzeniText = trenutniZaduzeni.getText().toString();
                String sledeciZaduzeniText = sledeciZduzeni.getText().toString();
                String trenutnaLokacijaText = trenutnaLokacija.getText().toString();
                String sledecaLokacijaText = sledecaLokacija.getText().toString();

                int trenutnaLokacijaId = Integer.parseInt(trenutnaLokacijaText);
                int novaLokacijaId = Integer.parseInt(sledecaLokacijaText);
                int trenutniZaduzeniId = Integer.parseInt(trenutniZaduzeniText);
                int noviZaduzeniId = Integer.parseInt(sledeciZaduzeniText);

                // Proveri da li osoba i lokacija postoje pre nego što nastaviš
                doesOsobaExistAsync(trenutniZaduzeniId, exists -> {
                    if (!exists) {
                        Toast.makeText(getActivity(), getString(R.string.error_osoba_not_found), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    doesOsobaExistAsync(noviZaduzeniId, exists2 -> {
                        if (!exists2) {
                            Toast.makeText(getActivity(), getString(R.string.error_osoba_not_found), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        doesLokacijaExistAsync(trenutnaLokacijaId, exists3 -> {
                            if (!exists3) {
                                Toast.makeText(getActivity(), getString(R.string.error_lokacija_not_found), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            doesLokacijaExistAsync(novaLokacijaId, exists4 -> {
                                if (!exists4) {
                                    Toast.makeText(getActivity(), getString(R.string.error_lokacija_not_found), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Ako svi postoje, postavi vrednosti za selectedOs
                                selectedOs.setFrom_lokacija_id(trenutnaLokacijaId);
                                selectedOs.setTo_osoba_id(trenutniZaduzeniId);
                                selectedOs.setFrom_lokacija_id(trenutnaLokacijaId);
                                selectedOs.setTo_lokacija_id(novaLokacijaId);


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
                            });
                        });
                    });
                });
            }
        });




        buttonDeleteOs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    selectedOs.setLista_id(null);

                new DatabaseTaskOs(new DatabaseTaskOs.DatabaseTaskListener() {
                    @Override
                    public void onTaskCompleted(boolean success) {
                        if (success) {
                            lista.remove(selectedOs);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getString(R.string.delete_true), Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, database, selectedOs, 1).execute();
                dismiss();
            }



        });



        return  root;
    }

    private void doesOsobaExistAsync(int osobaId, Consumer<Boolean> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            boolean exists = database.osobaDao().getOsobaById(osobaId) != null;


            requireActivity().runOnUiThread(() -> callback.accept(exists));
        });
    }

    private void doesLokacijaExistAsync(int lokacijaId, Consumer<Boolean> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            boolean exists = database.lokacijaDao().getLokacijaById(lokacijaId) != null;


            requireActivity().runOnUiThread(() -> callback.accept(exists));
        });
    }
}