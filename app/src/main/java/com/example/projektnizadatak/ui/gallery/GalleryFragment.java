package com.example.projektnizadatak.ui.gallery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.projektnizadatak.MainActivity;
import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.OsobaAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.databinding.FragmentGalleryBinding;
import com.example.projektnizadatak.model.Osoba;
import com.example.projektnizadatak.dialogFragment.DetaljiOsobe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private List<Osoba> listOsoba;

    private EditText editTextFilterNamePerson;
    private EditText editTextFilterSurnamePerson;

    private List<Osoba> listFiltered;

    private DataBase dataBase;
    private OsobaAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listViewOsoba = root.findViewById(R.id.listViewOsoba);
        dataBase = DataBase.getInstance(getActivity());
        listOsoba = new ArrayList<>();
        listFiltered = new ArrayList<>();
        adapter = new OsobaAdapter(getActivity(), listOsoba);
        listViewOsoba.setAdapter(adapter);

        loadAndRefreshList();

        editTextFilterNamePerson = root.findViewById(R.id.editTextFilterNamePerson);
        editTextFilterSurnamePerson = root.findViewById(R.id.editTextFilterSurnamePerson);
        Button addButtonPerson = root.findViewById(R.id.addButtonOsoba);



        listViewOsoba.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Osoba selectedItem = (Osoba) adapter.getItem(position);

                DetaljiOsobe detaljiDialog = DetaljiOsobe.newInstance(selectedItem);
                detaljiDialog.setAdapter(adapter);
                detaljiDialog.show(getParentFragmentManager(), "arg_osoba");

            }
        });

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setTitle(R.string.osoba);
        }

        addButtonPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.gallery_to_addPerson);
            }
        });

        editTextFilterNamePerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterLista();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextFilterSurnamePerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterLista();
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        return root;
    }

    private void filterLista() {
        String imeFilter = editTextFilterNamePerson.getText().toString().toLowerCase();
        String prezimeFilter = editTextFilterSurnamePerson.getText().toString().toLowerCase();

        listFiltered.clear();

        for (Osoba o : listOsoba) {
            boolean matchesIme = o.getIme().toLowerCase().startsWith(imeFilter);
            boolean matchesPrezime = o.getPrezime().toLowerCase().startsWith(prezimeFilter);

            if (matchesIme && matchesPrezime) {
                listFiltered.add(o);
            }
        }

        adapter.setLista(listFiltered);
        adapter.notifyDataSetChanged();
    }
    private void loadAndRefreshList() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                listOsoba.clear();
                listOsoba.addAll(dataBase.osobaDao().getAllOsobe());
                listFiltered.clear();
                listFiltered.addAll(listOsoba);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}