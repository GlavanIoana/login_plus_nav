package com.example.licenta.Eisenhower;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.licenta.R;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<String> tvTitles,tvDescriptions;
    private List<Integer> images;
    private List<Integer> colors;

    LayoutInflater inflater;

    public GridAdapter(Context context, List<String> tvTitles, List<String> tvDescriptions, List<Integer> images, List<Integer> colors) {
        this.context = context;
        this.tvTitles = tvTitles;
        this.tvDescriptions = tvDescriptions;
        this.images = images;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return tvTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater==null){
            inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView==null){
            convertView=inflater.inflate(R.layout.grid_item,null);
        }
        ImageView imageView=convertView.findViewById(R.id.imgGrid);
        TextView tvTitle=convertView.findViewById(R.id.tvGridTitle);
        TextView tvDescription=convertView.findViewById(R.id.tvGridDescription);
        LinearLayout llGrid=convertView.findViewById(R.id.llGrid);

        imageView.setImageResource(images.get(position));
        tvTitle.setText(tvTitles.get(position));
        tvDescription.setText(tvDescriptions.get(position));
        llGrid.setBackgroundColor(ContextCompat.getColor(context, colors.get(position)));

        return convertView;
    }
}
