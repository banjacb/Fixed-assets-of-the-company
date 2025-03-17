package com.example.projektnizadatak.ui.home;

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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.projektnizadatak.MainActivity;
import com.example.projektnizadatak.R;
import com.example.projektnizadatak.adapter.OsnovnoSredstvoAdapter;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.databinding.FragmentHomeBinding;
import com.example.projektnizadatak.dialogFragment.DetaljiOsnovnoSredstvo;
import com.example.projektnizadatak.model.OsnovnoSredstvo;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<OsnovnoSredstvo> listOsnovnoSredstvo;
    private  List<OsnovnoSredstvo> listFilterPrice;
    private List<OsnovnoSredstvo> listFilterName;
   private EditText editTextFilterName;
   private EditText editTextFilterPrice;

   private DataBase dataBase;
   private OsnovnoSredstvoAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataBase=DataBase.getInstance(getActivity());

        listOsnovnoSredstvo = new ArrayList<>();
        adapter = new OsnovnoSredstvoAdapter(getActivity(), listOsnovnoSredstvo);
        ListView listViewOs = root.findViewById(R.id.listViewOs);
        listViewOs.setAdapter(adapter);
        loadAndRefreshList();


        listFilterName = new ArrayList<>();
        listFilterPrice = new ArrayList<>();


        editTextFilterName = root.findViewById(R.id.editTextFilterName);
        editTextFilterPrice = root.findViewById(R.id.editTextFilterPrice);
        Button addButtonOs = root.findViewById(R.id.addButtonOs);

        addButtonOs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(root).navigate(R.id.home_to_addOS);
            }
        });


        listViewOs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OsnovnoSredstvo selectedItem = (OsnovnoSredstvo) adapter.getItem(position);
                DetaljiOsnovnoSredstvo detaljiDialog = DetaljiOsnovnoSredstvo.newInstance(selectedItem);
                detaljiDialog.setAdapter(adapter);
                detaljiDialog.show(getParentFragmentManager(), "arg_os");

            }
        });
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setTitle(R.string.osnovnaSredstva);
        }

        editTextFilterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterNaziv();

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextFilterPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().equals(""))
                    filterePrice(Integer.valueOf(charSequence.toString()));
                else{
                    adapter.setLista(listOsnovnoSredstvo);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return root;
    }

    private void loadAndRefreshList() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<OsnovnoSredstvo> resultList = dataBase.osnovnoSredstvoDao().getAllOsnovnoSredstvo();

                for (OsnovnoSredstvo os : resultList) {

                    byte[] imageBytes = dataBase.osnovnoSredstvoDao().getImageForOS(os.getId());
                    if (imageBytes != null) {
                        os.setSlika(imageBytes);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listOsnovnoSredstvo.clear();
                        listOsnovnoSredstvo.addAll(resultList);

                        listFilterName.clear();
                        listFilterName.addAll(listOsnovnoSredstvo);

                        adapter.setLista(listOsnovnoSredstvo);
                    }
                });
            }
        });
    }




    private void filterNaziv(){
       String nazivFilter= editTextFilterName.getText().toString().toLowerCase();
        listFilterName.clear();

            for(OsnovnoSredstvo os : listOsnovnoSredstvo){
                boolean matchesNaziv= os.getNaziv().toLowerCase().startsWith(nazivFilter);

                if (matchesNaziv)
                {
                    listFilterName.add(os);
                }
            }

        adapter.setLista(listFilterName);
        adapter.notifyDataSetChanged();
    }

    private void filterePrice(int number){
        listFilterPrice.clear();
        if(number <= 1)
            listFilterPrice.addAll(listOsnovnoSredstvo);
        else {
            for(OsnovnoSredstvo os : listOsnovnoSredstvo){
                if(os.getCijena() < number)
                    listFilterPrice.add(os);
            }
        }
        adapter.setLista(listFilterPrice);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}