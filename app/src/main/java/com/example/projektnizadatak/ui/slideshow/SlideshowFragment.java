package com.example.projektnizadatak.ui.slideshow;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import com.example.projektnizadatak.MainActivity;
import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.LokacijaAdapter;
import com.example.projektnizadatak.adapter.OsobaAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.dialogFragment.DetaljiLokacije;

import com.example.projektnizadatak.dialogFragment.DetaljiOsobe;
import com.example.projektnizadatak.model.Lokacija;
import com.example.projektnizadatak.model.Osoba;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


public class SlideshowFragment extends Fragment {

    private List<Lokacija> listLokacija = new ArrayList<>();;
    private LokacijaAdapter adapter;
    private List<Lokacija> filteredListByName;
    private List<Lokacija> filteredListById;
    private EditText editTextFilterName;
    private EditText editTextFilterNumber;
    private DataBase database;
    ListView listViewLokacija;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow,container,false);

        Button addbutton = root.findViewById(R.id.addButtonLocation);
        listViewLokacija = root.findViewById(R.id.listViewLocations);


        editTextFilterName = root.findViewById(R.id.editTextLocationCity);
        editTextFilterNumber = root.findViewById(R.id.editTextLocationiId);

        database = DataBase.getInstance(getActivity());
        filteredListByName= new ArrayList<>();
        filteredListById= new ArrayList<>();

        adapter = new LokacijaAdapter(getActivity(), listLokacija);
        listViewLokacija.setAdapter(adapter);

        loadAndRefreshList();




        listViewLokacija.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Lokacija selectedItem = (Lokacija) adapter.getItem(position);

                DetaljiLokacije detaljiDialog = DetaljiLokacije.newInstance(selectedItem);
                detaljiDialog.setAdapter(adapter);
                detaljiDialog.show(getParentFragmentManager(), "ARG_LOKACIJA");

            }
        });

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setTitle(R.string.osoba);
        }


        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.slideshow_to_addLocation);
            }
        });

        editTextFilterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterLista(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextFilterNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterById(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        return root;
    }

    private void loadAndRefreshList() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                listLokacija.clear();

                listLokacija.addAll(database.lokacijaDao().getAllLokacije());
                filteredListByName.clear();
                filteredListById.clear();
                filteredListById.addAll(listLokacija);
                filteredListByName.addAll(listLokacija);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                            adapter.notifyDataSetChanged();

                    }
                });
            }
        });
    }




    private void filterLista(String imeFilter) {


        filteredListByName.clear();


        for (Lokacija l : listLokacija) {
            if(l.getGrad().toLowerCase().startsWith(imeFilter.toLowerCase()))
                filteredListByName.add(l);
        }

        adapter.setLista(filteredListByName);
        adapter.notifyDataSetChanged();
    }

    private void filterById(String number){


        filteredListById.clear();


            for(Lokacija l : listLokacija){
                if(String.valueOf(l.getPostanskiBroj()).startsWith(number))
                    filteredListById.add(l);
            }

        adapter.setLista(filteredListById);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}