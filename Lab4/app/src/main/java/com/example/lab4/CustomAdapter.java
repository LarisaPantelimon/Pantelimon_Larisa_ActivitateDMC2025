package com.example.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private final Context context;
    private final List<Palton> listaPaltoane;

    public CustomAdapter(Context context, List<Palton> listaPaltoane) {
        this.context = context;
        this.listaPaltoane = listaPaltoane;
    }

    @Override
    public int getCount() {
        return listaPaltoane.size();
    }

    @Override
    public Object getItem(int position) {
        return listaPaltoane.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_palton, parent, false);
        }

        Palton palton = listaPaltoane.get(position);

        TextView textViewCuloare = convertView.findViewById(R.id.textViewCuloare);
        TextView textViewPret = convertView.findViewById(R.id.textViewPret);

        textViewCuloare.setText(palton.getCuloare());
//        //textViewPret.setText();
        String text = String.format("Culoare: %s, Preț: %s", palton.getCuloare(), palton.getPret(),palton.getMaterial(),palton.getMarime(),palton.getDataAdaugare());

        return convertView;
    }
}
