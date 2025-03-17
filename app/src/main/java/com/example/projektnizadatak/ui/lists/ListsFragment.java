package com.example.projektnizadatak.ui.lists;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.projektnizadatak.MainActivity;
import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.OsnovnoSredstvoAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.databinding.FragmentListsBinding;
import com.example.projektnizadatak.dialogFragment.DetaljiOsLista;
import com.example.projektnizadatak.dialogFragment.DetaljiOsnovnoSredstvo;
import com.example.projektnizadatak.model.Lista;
import com.example.projektnizadatak.model.OsnovnoSredstvo;
import com.example.projektnizadatak.model.Osoba;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListsFragment extends Fragment {

    private FragmentListsBinding binding;
    private OsnovnoSredstvoAdapter adapter;
    private DataBase database;
    private List<OsnovnoSredstvo> lists;

    private EditText editTextfilterByName, editTextfilterByBarcod;
    private List<OsnovnoSredstvo> listFiltered;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_lists, container, false);
        ListsViewModel listsViewModel= new ViewModelProvider(ListsFragment.this).get(ListsViewModel.class);
        binding=FragmentListsBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        database = DataBase.getInstance(getActivity());


        ListView listView = view.findViewById(R.id.listViewOsnovnaSredstva);

        lists = new ArrayList<>();
        adapter= new OsnovnoSredstvoAdapter(getActivity(),lists);
        listView.setAdapter(adapter);
        loadOsnovnaSredstva();

        listFiltered = new ArrayList<>(lists);
        Button button = view.findViewById(R.id.button_add);
        button.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_lists_to_addOsLists));
        editTextfilterByName=view.findViewById(R.id.editTextFilterListName);
        editTextfilterByBarcod=view.findViewById(R.id.editTextFilterBarcod);





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OsnovnoSredstvo selectedItem = (OsnovnoSredstvo) adapter.getItem(i);
                DetaljiOsLista detalji = DetaljiOsLista.newInstance(selectedItem);
                detalji.setLista(lists);
                detalji.setAdapter(adapter);
                detalji.show(getParentFragmentManager(), "item_detail");
            }
        });

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setTitle(R.string.lists);
        }


        editTextfilterByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterNaziv(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextfilterByBarcod.addTextChangedListener(new TextWatcher() {
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


        return view;
    }


    private void loadOsnovnaSredstva() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {


                List<OsnovnoSredstvo> tempList = new ArrayList<>();

                List<OsnovnoSredstvo> os = database.osnovnoSredstvoDao().getAllOsnovnoSredstvo();

                if (os != null) {
                    for (OsnovnoSredstvo osnovnoSredstvo : os) {

                        if (osnovnoSredstvo.getLista_id() != null)

                        {

                            byte[] imageBytes = database.osnovnoSredstvoDao().getImageForOS(osnovnoSredstvo.getId());
                            if (imageBytes != null) {
                                osnovnoSredstvo.setSlika(imageBytes);
                            }
                            tempList.add(osnovnoSredstvo);

                        }

                    }
                   // adapter.notifyDataSetChanged();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lists.clear();
                        lists.addAll(tempList);
                        adapter.setLista(lists);


                    }
                });
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void filterNaziv(String text){
        listFiltered.clear();
        if(text.isEmpty() || text.equals("")){
            listFiltered.addAll(lists);
        }
        else{
            for(OsnovnoSredstvo os : lists){
                if(os.getNaizv().toLowerCase().contains(text.toLowerCase()))
                    listFiltered.add(os);
            }
        }
        adapter.setLista(listFiltered);
        adapter.notifyDataSetChanged();
    }

    private void filterLista() {
        String nazivFilter = editTextfilterByName.getText().toString().toLowerCase();
        String barkodFilter = editTextfilterByBarcod.getText().toString().toLowerCase();

        listFiltered.clear();



            for (OsnovnoSredstvo o : lists) {
                boolean matchesIme =  o.getNaizv().toLowerCase().startsWith(nazivFilter);
                boolean matchesBarkod = o.getBarkod().toLowerCase().startsWith(barkodFilter);

                if (matchesIme && matchesBarkod) {
                    listFiltered.add(o);
                }
            }


        adapter.setLista(listFiltered);
        adapter.notifyDataSetChanged();
    }

}
