package com.example.projektnizadatak.addFragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.model.Lista;
import com.example.projektnizadatak.model.OsnovnoSredstvo;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AddOsList extends Fragment {

    private DataBase database;
    private EditText naziv, trenutniZaduzeni, noviZaduzeni, trenutnaLokacija, novaLokacija, barkod, nazivListe;

    private Lista pl = new Lista();
    private OsnovnoSredstvo osnovnoSredstvo = new OsnovnoSredstvo();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_os_list, container, false);

        naziv = root.findViewById(R.id.naziv);
        trenutniZaduzeni = root.findViewById(R.id.trenutna_osoba);
        noviZaduzeni = root.findViewById(R.id.sledeca_osoba);
        trenutnaLokacija = root.findViewById(R.id.trenutna_lokacija);
        novaLokacija = root.findViewById(R.id.sledeca_lokacija);
        barkod = root.findViewById(R.id.barkod);

        database = DataBase.getInstance(getActivity());

        Button manualButton = root.findViewById(R.id.manual_button);
        Button submitButton = root.findViewById(R.id.submit_button);
        Button scanButton = root.findViewById(R.id.scan_button);

        manualButton.setOnClickListener(view -> {
            String barcodeValue = barkod.getText().toString();

            if (!barcodeValue.isEmpty()) {
                new FetchOsnovnoSredstvoTask(this, barcodeValue).execute();
            } else {
                Toast.makeText(getActivity(), getString(R.string.search), Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(view -> {
            String trenutnaLokacijaText = trenutnaLokacija.getText().toString();
            String novaLokacijaText = novaLokacija.getText().toString();
            String trenutniZaduzeniText = trenutniZaduzeni.getText().toString();
            String noviZaduzeniText = noviZaduzeni.getText().toString();

            int trenutnaLokacijaId = Integer.parseInt(trenutnaLokacijaText);
            int novaLokacijaId = Integer.parseInt(novaLokacijaText);
            int trenutniZaduzeniId = Integer.parseInt(trenutniZaduzeniText);
            int noviZaduzeniId = Integer.parseInt(noviZaduzeniText);


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


                            osnovnoSredstvo.setFrom_lokacija_id(trenutnaLokacijaId);
                            osnovnoSredstvo.setTo_osoba_id(trenutniZaduzeniId);
                            osnovnoSredstvo.setTo_lokacija_id(novaLokacijaId);
                            osnovnoSredstvo.setTo_osoba_id(noviZaduzeniId);

                            new UpdateOsnovnoSredstvoTask(AddOsList.this, osnovnoSredstvo).execute();
                        });
                    });
                });
            });
        });

        scanButton.setOnClickListener(view -> {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(AddOsList.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
          //  integrator.setPrompt("Skenirajte");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        });

        return root;
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


    private static class FetchOsnovnoSredstvoTask extends AsyncTask<Void, Void, OsnovnoSredstvo> {
        private WeakReference<AddOsList> fragmentReference;
        private String barcode;

        FetchOsnovnoSredstvoTask(AddOsList fragment, String barcode) {
            this.fragmentReference = new WeakReference<>(fragment);
            this.barcode = barcode;
        }

        @Override
        protected OsnovnoSredstvo doInBackground(Void... voids) {
            AddOsList fragment = fragmentReference.get();
            if (fragment == null) return null;
            return fragment.database.osnovnoSredstvoDao().getOsnovnoSredstvoByBarkod(barcode);
        }

        @Override
        protected void onPostExecute(OsnovnoSredstvo os) {
            AddOsList fragment = fragmentReference.get();

            if (fragment != null && os != null) {

                fragment.osnovnoSredstvo = os;
                fragment.naziv.setText(os.getNaizv());
                fragment.trenutniZaduzeni.setText(String.valueOf(os.getFrom_osoba_id()));
                fragment.noviZaduzeni.setText(String.valueOf(os.getTo_osoba_id()));
                fragment.trenutnaLokacija.setText(String.valueOf(os.getFrom_lokacija_id()));
                fragment.novaLokacija.setText(String.valueOf(os.getTo_lokacija_id()));


            } else {
                Toast.makeText(fragment.getActivity(), fragment.getString(R.string.error), Toast.LENGTH_SHORT).show();}
        }
    }

    private static class UpdateOsnovnoSredstvoTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddOsList> fragmentReference;
        private OsnovnoSredstvo osnovnoSredstvo;

        UpdateOsnovnoSredstvoTask(AddOsList fragment, OsnovnoSredstvo osnovnoSredstvo) {
            this.fragmentReference = new WeakReference<>(fragment);
            this.osnovnoSredstvo = osnovnoSredstvo;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            AddOsList fragment = fragmentReference.get();
            if (fragment != null) {
                try {
                    fragment.database.osnovnoSredstvoDao().update(osnovnoSredstvo);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            AddOsList fragment = fragmentReference.get();
            if (fragment != null) {
                if (success) {
                    new InsertTask(fragment, fragment.pl).execute();
                } else {
                    Toast.makeText(fragment.getActivity(), fragment.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {
        private WeakReference<AddOsList> fragmentReference;
        private Lista pl;

        InsertTask(AddOsList context, Lista pl) {
            this.fragmentReference = new WeakReference<>(context);
            this.pl = pl;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            AddOsList fragment = fragmentReference.get();
            if (fragment != null) {
                try {
                    fragment.database.listaDao().insert(pl);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            AddOsList fragment = fragmentReference.get();
            if (fragment != null) {
                if (success) {
                    Toast.makeText(fragment.getActivity(), fragment.getString(R.string.add_true), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(fragment.getActivity(), fragment.getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
            } else {
                String scannedBarcode = result.getContents();
                barkod.setText(scannedBarcode);

                new FetchOsnovnoSredstvoTask(this, scannedBarcode).execute();
            }
        }
    }
}
