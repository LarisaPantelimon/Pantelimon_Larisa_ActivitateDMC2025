package com.example.lab9;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<ListItem> {
    private final Activity context;
    private final ArrayList<ListItem> items;

    public CustomAdapter(Activity context, ArrayList<ListItem> items) {
        super(context, R.layout.list_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);

        TextView title = rowView.findViewById(R.id.itemTitle);
        TextView description = rowView.findViewById(R.id.itemDescription);
        ImageView image = rowView.findViewById(R.id.itemImage);

        ListItem currentItem = items.get(position);

        title.setText(currentItem.getTitle());
        description.setText(currentItem.getDescription());
        image.setImageResource(currentItem.getImageResId());

        return rowView;
    }
}
