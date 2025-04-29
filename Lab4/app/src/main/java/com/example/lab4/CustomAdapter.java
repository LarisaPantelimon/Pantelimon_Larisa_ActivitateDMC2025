package com.example.lab4;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lab4.Palton;
import com.example.lab4.R;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private final Context context;
    private final List<Palton> listaPaltoane;
    private float fontSize;  // Dimensiunea fontului
    private String textColor;  // Culoarea textului

    // Modificăm constructorul pentru a primi dimensiunea fontului și culoarea
    public CustomAdapter(Context context, List<Palton> listaPaltoane, float fontSize, String textColor) {
        this.context = context;
        this.listaPaltoane = listaPaltoane;
        this.fontSize = fontSize;
        this.textColor = textColor;
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

        // Găsește elementele TextView
        TextView textViewCuloare = convertView.findViewById(R.id.textViewCuloare);
        TextView textViewPret = convertView.findViewById(R.id.textViewPret);

        // Setează textul pentru fiecare TextView
        textViewCuloare.setText(palton.getCuloare());
        textViewPret.setText(String.format("Culoare: %s, Preț: %s", palton.getCuloare(), palton.getPret()));

        // Setează dimensiunea fontului
        textViewCuloare.setTextSize(fontSize);
        textViewPret.setTextSize(fontSize);

        // Setează culoarea textului
        switch (textColor) {
            case "Rosu":
                textViewCuloare.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                textViewPret.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "Verde":
                textViewCuloare.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
                textViewPret.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
            case "Albastru":
                textViewCuloare.setTextColor(context.getResources().getColor(android.R.color.holo_blue_light));
                textViewPret.setTextColor(context.getResources().getColor(android.R.color.holo_blue_light));
                break;
            default:
                textViewCuloare.setTextColor(context.getResources().getColor(android.R.color.black));
                textViewPret.setTextColor(context.getResources().getColor(android.R.color.black));
                break;
        }

        return convertView;
    }
}
