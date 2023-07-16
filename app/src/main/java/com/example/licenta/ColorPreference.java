package com.example.licenta;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

public class ColorPreference extends Preference {

    private int selectedColor;
    private ImageView colorPreview;
    private AlertDialog dialog;
    private String preferenceKey;

//    private View color1,color2,color3,color4,color5,color6,color7,color8,color9,color10;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.preference_color);
        preferenceKey = attrs.getAttributeValue(null, "key");

    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        colorPreview = (ImageView) holder.findViewById(R.id.color_preview);
        Drawable circleBackground = ContextCompat.getDrawable(getContext(), R.drawable.circle_background);
        if (circleBackground != null) {
            circleBackground.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
            colorPreview.setBackground(circleBackground);
        }

        colorPreview.setOnClickListener(v -> {
            // Open color picker or any other UI component to choose a new color
            showColorPickerDialog();
        });
    }

    private void showColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.color_picker_dialog, null);

        // Find color views in the dialog layout
//        initialiseColorViews(dialogView);

        View.OnClickListener onClickListener= v -> {
            int backgroundTint = v.getBackgroundTintList().getDefaultColor();
            setSelectedColor(backgroundTint);
//            saveSelectedColorToPreferences();
            updateColorPreview();
            dialog.dismiss();
        };

        ConstraintLayout parentLayout = dialogView.findViewById(R.id.clColorPickerDialog);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);
            if (!(childView instanceof TextView)) {
                childView.setOnClickListener(onClickListener);
            }
        }

       // Create the AlertDialog
        builder.setView(dialogView);

        dialog = builder.create();

        // Show the dialog
        dialog.show();

    }

    private void saveSelectedColorToPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceKey, selectedColor);
        editor.apply();
    }

    private void updateColorPreview() {
        if (colorPreview != null) {
            Drawable circleBackground = ContextCompat.getDrawable(getContext(), R.drawable.circle_background);
            if (circleBackground != null) {
                circleBackground.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
                colorPreview.setBackground(circleBackground);
            }
        }
    }

    public void setSelectedColor(int color) {
        selectedColor = color;
        switch (preferenceKey) {
            case "pref_key_category_color_intalnire": {
                MainActivity.COLOR_CATEGORY_INTALNIRE=color;
                break;
            }
            case "pref_key_category_color_sedinta": {
                MainActivity.COLOR_CATEGORY_SEDINTA=color;
                break;
            }
            case "pref_key_category_color_munca": {
                MainActivity.COLOR_CATEGORY_MUNCA=color;
                break;
            }
            case "pref_key_category_color_tema": {
                MainActivity.COLOR_CATEGORY_TEMA=color;
                break;
            }
            case "pref_key_category_color_gospodarit": {
                MainActivity.COLOR_CATEGORY_GOSPODARIT=color;
                break;
            }
            case "pref_key_category_color_relaxare": {
                MainActivity.COLOR_CATEGORY_RELAXARE=color;
                break;
            }
            case "pref_key_category_color_sport": {
                MainActivity.COLOR_CATEGORY_SPORT=color;
                break;
            }
            case "pref_key_category_color_proiect": {
                MainActivity.COLOR_CATEGORY_PROIECT=color;
                break;
            }
            case "pref_key_category_color_deadline": {
                MainActivity.COLOR_CATEGORY_DEADLINE=color;
                break;
            }
            default: {
                MainActivity.COLOR_CATEGORY_ALTELE=color;
                break;
            }
        }
        updateColorPreview();
    }
}
