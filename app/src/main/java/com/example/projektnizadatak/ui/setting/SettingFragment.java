package com.example.projektnizadatak.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.projektnizadatak.MainActivity;
import com.example.projektnizadatak.R;

import java.util.Locale;

public class SettingFragment extends Fragment {

    private RadioGroup languageRadio;
    private Button saveBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        languageRadio = view.findViewById(R.id.language_radio_group);
        saveBtn = view.findViewById(R.id.button_save_language);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = languageRadio.getCheckedRadioButtonId();

                if (selectedId != -1) {
                    RadioButton selectedRadioButton = view.findViewById(selectedId);
                    if (selectedRadioButton != null) {
                        String selectedLanguage = selectedRadioButton.getText().toString();

                        String languageCode = selectedLanguage.equals(getString(R.string.serbian)) ? "sr" : "en";

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("selected_language", languageCode);
                        editor.apply();

                        setLocale(languageCode);

                        if (getActivity() != null) {
                            getActivity().recreate();
                        }
                    }
                }
            }
        });

        return view;
    }


    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        getActivity().getBaseContext().getResources().updateConfiguration(config, resources.getDisplayMetrics());


    }


}
