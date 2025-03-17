package com.example.projektnizadatak.addFragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.database.DataBase;
import com.example.projektnizadatak.model.OsnovnoSredstvo;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AddOsnovnoSredstvo extends Fragment {


    private static final int CAPTURE_IMAGE = 2;
    private static final int CAMERA_PERMISSION = 100;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;
    private ImageView imageView;
    private EditText etNaziv, etOpis, etBarkod, etCijena, etDatum, etOsobaId, etLokacijaId;
    private DataBase dataBase;
    List<OsnovnoSredstvo> list;

    byte[] imageBytes;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                }
            }
    );

    private final ActivityResultLauncher<Intent> barcodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                IntentResult resultScan = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                if (resultScan != null) {
                    if (resultScan.getContents() == null) {
                        Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
                    } else {
                        String barcode = resultScan.getContents();
                        etBarkod.setText(barcode);
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            }
    );


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_osnovno_sredstvo, container, false);

        Button btnCamera = view.findViewById(R.id.buttonCamera);
        Button btnSubmit = view.findViewById(R.id.buttonAdd);
        Button btnSelectImage = view.findViewById(R.id.buttonSelectImage);
        Button btnScan = view.findViewById(R.id.buttonScan);

        etNaziv = view.findViewById(R.id.editTextNaziv);
        etOpis = view.findViewById(R.id.editTextOpis);
        etBarkod = view.findViewById(R.id.editTextBarkod);
        etCijena = view.findViewById(R.id.editTextCijena);
        etDatum = view.findViewById(R.id.editTextDatum);
        etOsobaId = view.findViewById(R.id.editTextOsobaId);
        etLokacijaId = view.findViewById(R.id.editTextLokacijaId);
        imageView = view.findViewById(R.id.imageViewSlika);

        dataBase = DataBase.getInstance(getActivity());


        btnSelectImage.setOnClickListener(v -> chooseImage());

        btnCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            }
        });
        btnScan.setOnClickListener(v -> scanBarcode());
        etDatum.setOnClickListener(v -> pickDate());




        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            list = dataBase.osnovnoSredstvoDao().getAllOsnovnoSredstvoWithoutImages();
            for (OsnovnoSredstvo os : list) {
                imageBytes = null;
                if (os.getId() != 1)
                    imageBytes = dataBase.osnovnoSredstvoDao().getImageForOS(os.getId());
                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    os.setSlika(imageBytes);
                }
            }


            requireActivity().runOnUiThread(() -> {

                Log.d("DB_FETCH", "Dohvaceno " + list.size() + " osnovnih sredstava");
            });
        });




        btnSubmit.setOnClickListener(v -> {
            saveDataAsync(osnovnoSredstvo -> {
                if (osnovnoSredstvo != null) {
                    new InsertTask(AddOsnovnoSredstvo.this, osnovnoSredstvo).execute();
                    Toast.makeText(getActivity(), getString(R.string.add_true), Toast.LENGTH_SHORT).show();
                }
            });
        });




        return view;
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.search)), PICK_IMAGE_REQUEST);
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(takePictureIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImage();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }




    private void scanBarcode() {



        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);

       // integrator.setPrompt(" Skeniraj");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);

        Intent intent = integrator.createScanIntent();
        barcodeLauncher.launch(intent);
    }






    private void pickDate() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, year, month, day) ->
                etDatum.setText(day + "/" + (month + 1) + "/" + year), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveDataAsync(Consumer<OsnovnoSredstvo> callback) {
        String naziv = etNaziv.getText().toString();
        String opis = etOpis.getText().toString();
        String barkod = etBarkod.getText().toString();
        String cijenaText = etCijena.getText().toString();
        String datumText = etDatum.getText().toString();
        String osobaIdText = etOsobaId.getText().toString();
        String lokacijaIdText = etLokacijaId.getText().toString();

        if (naziv.isEmpty() || opis.isEmpty() || barkod.isEmpty() || cijenaText.isEmpty() || datumText.isEmpty() || osobaIdText.isEmpty() || lokacijaIdText.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            callback.accept(null);
            return;
        }

        int osobaId = Integer.parseInt(osobaIdText);
        int lokacijaId = Integer.parseInt(lokacijaIdText);


        doesOsobaExistAsync(osobaId, osobaExists -> {
            if (!osobaExists) {
                Toast.makeText(getActivity(), getString(R.string.error_osoba_not_found), Toast.LENGTH_SHORT).show();
                callback.accept(null);
                return;
            }

            doesLokacijaExistAsync(lokacijaId, lokacijaExists -> {
                if (!lokacijaExists) {
                    Toast.makeText(getActivity(), getString(R.string.error_lokacija_not_found), Toast.LENGTH_SHORT).show();
                    callback.accept(null);
                    return;
                }


                OsnovnoSredstvo os = new OsnovnoSredstvo();
                os.setNaizv(naziv);
                os.setOpis(opis);
                os.setBarkod(barkod);
                os.setCijena(Double.parseDouble(cijenaText));
                os.setDatum(parseDate(datumText));
                os.setFrom_osoba_id(osobaId);
                os.setFrom_lokacija_id(lokacijaId);

                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                os.setSlika(this.getResizedAndCompressedBitmapBytes(bitmap, 800, 80));


                callback.accept(os);
            });
        });
    }

    private void doesOsobaExistAsync(int osobaId, Consumer<Boolean> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean exists = dataBase.osobaDao().getOsobaById(osobaId) != null;
            requireActivity().runOnUiThread(() -> callback.accept(exists));
        });
    }

    private void doesLokacijaExistAsync(int lokacijaId, Consumer<Boolean> callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean exists = dataBase.lokacijaDao().getLokacijaById(lokacijaId) != null;
            requireActivity().runOnUiThread(() -> callback.accept(exists));
        });
    }




    public byte[] getResizedAndCompressedBitmapBytes(Bitmap bitmap, int maxSize, int quality) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private static class InsertTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<AddOsnovnoSredstvo> fragmentRef;
        private final OsnovnoSredstvo os;
        InsertTask(AddOsnovnoSredstvo fragment, OsnovnoSredstvo os) {
            fragmentRef = new WeakReference<>(fragment);
            this.os = os;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            fragmentRef.get().dataBase.osnovnoSredstvoDao().insert(os);
            return null;
        }
    }

    private Date parseDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap, int maxSize, int quality) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } else {
            return null;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {

                  Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }
    }





}
