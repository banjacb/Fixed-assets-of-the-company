package com.example.projektnizadatak.maps;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.database.DatabaseTaskLokacija;
import com.example.projektnizadatak.model.Lokacija;
import com.example.projektnizadatak.model.OsnovnoSredstvo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.projektnizadatak.databinding.ActivityMapsBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private DataBase dataBase;

    public static HashMap<String, LatLng> GRADOVI_MAP = new HashMap<>();

    static {
        GRADOVI_MAP.put("Banja Luka", new LatLng(44.7725, 17.1910));
        GRADOVI_MAP.put("Gradiška", new LatLng(45.1450, 17.2500));
        GRADOVI_MAP.put("Prijedor", new LatLng(44.9811, 16.7147));
        GRADOVI_MAP.put("Bijeljina", new LatLng(44.7511, 19.2144));
        GRADOVI_MAP.put("Doboj", new LatLng(44.7316, 18.0833));
        GRADOVI_MAP.put("Trebinje", new LatLng(42.7110, 18.3443));
        GRADOVI_MAP.put("Foča", new LatLng(43.5069, 18.7764));
        GRADOVI_MAP.put("Zvornik", new LatLng(44.3861, 19.1033));
        GRADOVI_MAP.put("Mrkonić Grad", new LatLng(44.4195, 17.0866));
        GRADOVI_MAP.put("Petrovac", new LatLng(44.5558, 16.3769));
    }



    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dataBase = DataBase.getInstance(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bosnaIHercegovina = new LatLng(44.1688, 17.7850);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bosnaIHercegovina, 7.0f));
        new LoadLocationsTask().execute();
    }

    private class LoadLocationsTask extends AsyncTask<Void, Void, List<Lokacija>> {

        private List<OsnovnoSredstvo> osnovnaSredstva; // Додајемо основна средства

        @Override
        protected List<Lokacija> doInBackground(Void... voids) {
            // Учитавање локација
            List<Lokacija> lokacije = dataBase.lokacijaDao().getAllLokacije();

            // Учитавање основних средстава
            osnovnaSredstva = dataBase.osnovnoSredstvoDao().getAllOsnovnoSredstvo(); // Претпостављам да имаш ову методу у DAO

            return lokacije;
        }

        @Override
        protected void onPostExecute(List<Lokacija> lokacije) {
            super.onPostExecute(lokacije);

            if (lokacije != null) {

                HashMap<Integer, StringBuilder> lokacijaSredstvaMap = new HashMap<>();

                for (OsnovnoSredstvo os : osnovnaSredstva) {
                    if (!lokacijaSredstvaMap.containsKey(os.getFrom_lokacija_id())) {
                        lokacijaSredstvaMap.put(os.getFrom_lokacija_id(), new StringBuilder());
                    }
                    lokacijaSredstvaMap.get(os.getFrom_lokacija_id()).append(os.naziv).append(", "); // Додајемо име основног средства
                }

                for (Lokacija lokacija : lokacije) {
                    LatLng pozicija = new LatLng(lokacija.x, lokacija.y);

                    String sredstvaText = "";
                    if (lokacijaSredstvaMap.containsKey(lokacija.id)) {
                        sredstvaText = lokacijaSredstvaMap.get(lokacija.id).toString();
                        if (!sredstvaText.isEmpty()) {
                            sredstvaText = sredstvaText.substring(0, sredstvaText.length() - 2);
                        }
                    }


                    mMap.addMarker(new MarkerOptions()
                            .position(pozicija)
                            .title(lokacija.grad)
                            .snippet(sredstvaText)
                    );
                }
            }
        }
    }
}