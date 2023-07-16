package com.example.licenta;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flask.colorpicker.ColorPickerPreference;

public class SetariFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener,SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREF_KEY_RINGTONE = "pref_key_ringtone";
    private Uri selectedRingtoneUri;
    private Ringtone selectedRingtone;
    private static final int RINGTONE_DURATION_MS = 3000; // 5 seconds

    private Handler handler = new Handler();
    private Runnable stopRingtoneRunnable = new Runnable() {
        @Override
        public void run() {
            stopRingtone();
        }
    };
    private ColorPreference intalnireColorPreference;
    private ColorPreference sedintaColorPreference;
    private ColorPreference muncaColorPreference;
    private ColorPreference temaColorPreference;
    private ColorPreference gospodaritColorPreference;
    private ColorPreference relaxareColorPreference;
    private ColorPreference sportColorPreference;
    private ColorPreference proiectColorPreference;
    private ColorPreference deadlineColorPreference;
    private ColorPreference alteleColorPreference;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        ListPreference ringtonePreference = findPreference(PREF_KEY_RINGTONE);
        ringtonePreference.setOnPreferenceChangeListener(this);

        // Fetch the available ringtones and update the ListPreference entries and values
        RingtoneManager ringtoneManager = new RingtoneManager(requireContext());
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = ringtoneManager.getCursor();
        CharSequence[] entries = new CharSequence[cursor.getCount()];
        CharSequence[] entryValues = new CharSequence[cursor.getCount()];

        if (cursor.moveToFirst()) {
            int index = 0;
            do {
                String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                Uri uri = ringtoneManager.getRingtoneUri(index);
                entries[index] = title;
                entryValues[index] = uri.toString();
                index++;
            } while (cursor.moveToNext());
        }

        cursor.close();

        ringtonePreference.setEntries(entries);
        ringtonePreference.setEntryValues(entryValues);

//        ColorPickerPreference colorPickerPreference=findPreference();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
//        int intalnireColor = sharedPreferences.getInt("pref_key_category_color_intalnire", ContextCompat.getColor(requireContext(), R.color.calpurple));
//        int sedintaColor = sharedPreferences.getInt("pref_key_category_color_sedinta", ContextCompat.getColor(requireContext(), R.color.caldarkblue));
//        int muncaColor = sharedPreferences.getInt("pref_key_category_color_munca", ContextCompat.getColor(requireContext(), R.color.calblue));
//        int temaColor = sharedPreferences.getInt("pref_key_category_color_tema", ContextCompat.getColor(requireContext(), R.color.calturqouse));
//        int gospodaritColor = sharedPreferences.getInt("pref_key_category_color_gospodarit", ContextCompat.getColor(requireContext(), R.color.calgreen));
//        int relaxareColor = sharedPreferences.getInt("pref_key_category_color_relaxare", ContextCompat.getColor(requireContext(), R.color.calyellow));
//
//        int sportColor = sharedPreferences.getInt("pref_key_category_color_sport", ContextCompat.getColor(requireContext(), R.color.calorange));
//        int proiectColor = sharedPreferences.getInt("pref_key_category_color_proiect", ContextCompat.getColor(requireContext(), R.color.caldarkorange));
//        int deadlineColor = sharedPreferences.getInt("pref_key_category_color_deadline", ContextCompat.getColor(requireContext(), R.color.calred));
//
//        int alteleColor = sharedPreferences.getInt("pref_key_category_color_altele", ContextCompat.getColor(requireContext(), R.color.calpink));
        // Retrieve the rest of the color preferences for other event categories

        // Set the selected colors for each ColorPreference
//        intalnireColorPreference = findPreference("pref_key_category_color_intalnire");
//        intalnireColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_INTALNIRE);
//
//        sedintaColorPreference = findPreference("pref_key_category_color_sedinta");
//        sedintaColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_SEDINTA);
//
//        muncaColorPreference = findPreference("pref_key_category_color_munca");
//        muncaColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_MUNCA);
//
//        temaColorPreference = findPreference("pref_key_category_color_tema");
//        temaColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_TEMA);
//
//        gospodaritColorPreference = findPreference("pref_key_category_color_gospodarit");
//        gospodaritColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_GOSPODARIT);
//
//        relaxareColorPreference = findPreference("pref_key_category_color_relaxare");
//        relaxareColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_RELAXARE);
//
//        sportColorPreference = findPreference("pref_key_category_color_sport");
//        sportColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_SPORT);
//
//        proiectColorPreference = findPreference("pref_key_category_color_proiect");
//        proiectColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_PROIECT);
//
//        deadlineColorPreference = findPreference("pref_key_category_color_deadline");
//        deadlineColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_DEADLINE);
//
//        alteleColorPreference = findPreference("pref_key_category_color_altele");
//        alteleColorPreference.setSelectedColor(MainActivity.COLOR_CATEGORY_ALTELE);
//
//
//        // Register the SharedPreferences change listener
//        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        if (preference.getKey().equals(PREF_KEY_RINGTONE)) {
            String selectedRingtoneUriString = (String) newValue;

            // Update the selected ringtone URI
            selectedRingtoneUri = Uri.parse(selectedRingtoneUriString);

            // Play the selected ringtone
            playRingtone(selectedRingtoneUri);
            handler.postDelayed(stopRingtoneRunnable, RINGTONE_DURATION_MS);

            return true;
        }

        return false;
    }

    private void playRingtone(Uri ringtoneUri) {
        // Stop the currently playing ringtone
        stopRingtone();

        // Play the new ringtone
        selectedRingtone = RingtoneManager.getRingtone(requireContext(), ringtoneUri);
        if (selectedRingtone != null) {
            selectedRingtone.play();
        }
    }

    private void stopRingtone() {
        handler.removeCallbacks(stopRingtoneRunnable); // Cancel any pending stop actions

        if (selectedRingtone != null && selectedRingtone.isPlaying()) {
            selectedRingtone.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the ringtone when the fragment is destroyed
        stopRingtone();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (key) {
            case "pref_key_category_color_intalnire": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calpurple));
                // Perform actions with the new color for the "Sedinta" category
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_INTALNIRE=newColor;
                break;
            }
            case "pref_key_category_color_sedinta": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.caldarkblue));
                // Perform actions with the new color for the "Sedinta" category
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_SEDINTA=newColor;
                break;
            }
            case "pref_key_category_color_munca": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calblue));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_MUNCA=newColor;
                break;
            }
            case "pref_key_category_color_tema": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calturqouse));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_TEMA=newColor;
                break;
            }
            case "pref_key_category_color_gospodarit": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calgreen));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_GOSPODARIT=newColor;
                break;
            }
            case "pref_key_category_color_relaxare": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calyellow));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_RELAXARE=newColor;
                break;
            }
            case "pref_key_category_color_sport": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calorange));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_SPORT=newColor;
                break;
            }
            case "pref_key_category_color_proiect": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.caldarkorange));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_PROIECT=newColor;
                break;
            }
            case "pref_key_category_color_deadline": {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calred));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_DEADLINE=newColor;
                break;
            }
            default: {
                int newColor = sharedPreferences.getInt(key, ContextCompat.getColor(requireContext(), R.color.calpink));
                editor.putInt(key, newColor);
                MainActivity.COLOR_CATEGORY_ALTELE=newColor;
                break;
            }
        }
        editor.apply();
    }

    private void saveColorToSharedPreferences(String categoryKey, int color) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(categoryKey, color);
        editor.apply();
    }
}