package com.example.licenta;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.dynamite.DynamiteModule;

public class SettingsFragment extends Fragment {
    private LinearLayout llColorCategoryIntalnire,llColorCategorySedinta,llColorCategoryMunca,llColorCategoryTema,llColorCategoryGospodarit,llColorCategoryRelaxare,llColorCategorySport,llColorCategoryProiect,llColorCategoryDeadline,llColorCategoryAltele;
    private ImageView ivColorIntalnire, ivColorSedinta, ivColorMunca, ivColorTema, ivColorGospodarit, ivColorRelaxare, ivColorSport, ivColorProiect, ivColorDeadline, ivColorAltele;
    private AlertDialog dialog;
    private int selectedColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setari, container, false);
        initialiseViews(view);

        View.OnClickListener categoryClickListener= this::showColorPickerDialog;

        ConstraintLayout parentLayout = view.findViewById(R.id.clParentLayoutSetari);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);
            if (childView instanceof LinearLayout) {
                String layoutIdString = getResources().getResourceEntryName(childView.getId());
                if (layoutIdString.contains("Color")){
                    childView.setOnClickListener(categoryClickListener);
                }
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivColorIntalnire.setColorFilter(MainActivity.COLOR_CATEGORY_INTALNIRE);
        ivColorSedinta.setColorFilter(MainActivity.COLOR_CATEGORY_SEDINTA);
        ivColorMunca.setColorFilter(MainActivity.COLOR_CATEGORY_MUNCA);
        ivColorTema.setColorFilter(MainActivity.COLOR_CATEGORY_TEMA);
        ivColorGospodarit.setColorFilter(MainActivity.COLOR_CATEGORY_GOSPODARIT);
        ivColorRelaxare.setColorFilter(MainActivity.COLOR_CATEGORY_RELAXARE);
        ivColorSport.setColorFilter(MainActivity.COLOR_CATEGORY_SPORT);
        ivColorProiect.setColorFilter(MainActivity.COLOR_CATEGORY_PROIECT);
        ivColorDeadline.setColorFilter(MainActivity.COLOR_CATEGORY_DEADLINE);
        ivColorAltele.setColorFilter(MainActivity.COLOR_CATEGORY_ALTELE);

    }

    private void showColorPickerDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.color_picker_dialog, null);

        // Find color views in the dialog layout
//        initialiseColorViews(dialogView);

        View.OnClickListener onClickListener= v -> {
            int backgroundTint = v.getBackgroundTintList().getDefaultColor();
            setSelectedColor(view,backgroundTint);
//            saveSelectedColorToPreferences();
//            updateColorPreview(view);
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

    private void updateColorPreview(View view) {
        LinearLayout layout= (LinearLayout) view;
        for (int i = 0; i < layout.getChildCount(); i++) {
            View childView = layout.getChildAt(i);
            if (childView instanceof ImageView) {
                Drawable circleBackground = ContextCompat.getDrawable(requireContext(), R.drawable.circle_background);
                if (circleBackground != null) {
                    circleBackground.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
                    childView.setBackground(circleBackground);
                }
            }
        }
    }

    private void setSelectedColor(View view, int color) {
        selectedColor=color;
        String layoutIdString = getResources().getResourceEntryName(view.getId());
        if (layoutIdString.contains("Intalnire")){
            MainActivity.COLOR_CATEGORY_INTALNIRE=color;
        }else if (layoutIdString.contains("Sedinta")){
            MainActivity.COLOR_CATEGORY_SEDINTA=color;
        }else if (layoutIdString.contains("Munca")){
            MainActivity.COLOR_CATEGORY_MUNCA=color;
        }else if (layoutIdString.contains("Tema")){
            MainActivity.COLOR_CATEGORY_TEMA=color;
        }else if (layoutIdString.contains("Gospodarit")){
            MainActivity.COLOR_CATEGORY_GOSPODARIT=color;
        }else if (layoutIdString.contains("Relaxare")){
            MainActivity.COLOR_CATEGORY_RELAXARE=color;
        }else if (layoutIdString.contains("Sport")){
            MainActivity.COLOR_CATEGORY_SPORT=color;
        }else if (layoutIdString.contains("Proiect")){
            MainActivity.COLOR_CATEGORY_PROIECT=color;
        }else if (layoutIdString.contains("Deadline")){
            MainActivity.COLOR_CATEGORY_DEADLINE=color;
        }else{// if (layoutIdString.contains("Altele")){
            MainActivity.COLOR_CATEGORY_ALTELE=color;
        }
        updateColorPreview(view);
    }

    private void initialiseViews(View view) {
        llColorCategoryIntalnire=view.findViewById(R.id.llColorCategoryIntalnire);
        llColorCategorySedinta=view.findViewById(R.id.llColorCategorySedinta);
        llColorCategoryMunca=view.findViewById(R.id.llColorCategoryMunca);
        llColorCategoryTema=view.findViewById(R.id.llColorCategoryTema);
        llColorCategoryGospodarit=view.findViewById(R.id.llColorCategoryGospodarit);
        llColorCategoryRelaxare=view.findViewById(R.id.llColorCategoryRelaxare);
        llColorCategorySport=view.findViewById(R.id.llColorCategorySport);
        llColorCategoryProiect=view.findViewById(R.id.llColorCategoryProiect);
        llColorCategoryDeadline=view.findViewById(R.id.llColorCategoryDeadline);
        llColorCategoryAltele=view.findViewById(R.id.llColorCategoryAltele);

        ivColorIntalnire =view.findViewById(R.id.viewColorIntalnire);
        ivColorSedinta =view.findViewById(R.id.viewColorSedinta);
        ivColorMunca =view.findViewById(R.id.viewColorMunca);
        ivColorTema =view.findViewById(R.id.viewColorTema);
        ivColorGospodarit =view.findViewById(R.id.viewColorGospodarit);
        ivColorRelaxare =view.findViewById(R.id.viewColorRelaxare);
        ivColorSport =view.findViewById(R.id.viewColorSport);
        ivColorProiect =view.findViewById(R.id.viewColorProiect);
        ivColorDeadline =view.findViewById(R.id.viewColorDeadline);
        ivColorAltele =view.findViewById(R.id.viewColorAltele);

    }
}