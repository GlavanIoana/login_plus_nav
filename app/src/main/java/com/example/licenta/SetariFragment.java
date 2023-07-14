package com.example.licenta;

import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SetariFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private static final String PREF_KEY_RINGTONE = "pref_key_ringtone";
    private Uri selectedRingtoneUri;
    private Ringtone selectedRingtone;

    private Button saveButton;

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

//        saveButton = getView().findViewById(R.id.saveButton);
//        saveButton.setOnClickListener(v -> {
//            // Save the selected ringtone URI
//            if (selectedRingtoneUri != null) {
//                // Perform necessary actions with the selected ringtone URI
//            }
//        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_setari, container, false);
//    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        if (preference.getKey().equals(PREF_KEY_RINGTONE)) {
            String selectedRingtoneUriString = (String) newValue;

            // Update the selected ringtone URI
            selectedRingtoneUri = Uri.parse(selectedRingtoneUriString);

            // Play the selected ringtone
            playRingtone(selectedRingtoneUri);

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
        if (selectedRingtone != null && selectedRingtone.isPlaying()) {
            selectedRingtone.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the ringtone when the fragment is destroyed
        stopRingtone();
    }
}