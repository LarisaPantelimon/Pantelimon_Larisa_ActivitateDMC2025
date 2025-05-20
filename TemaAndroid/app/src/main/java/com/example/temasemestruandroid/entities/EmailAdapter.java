package com.example.temasemestruandroid.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.temasemestruandroid.R;

import java.util.List;

public class EmailAdapter extends ArrayAdapter<Email> {
    public EmailAdapter(Context context, List<Email> emails) {
        super(context, 0, emails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Email email = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_email, parent, false);
        }

        TextView tvSender = convertView.findViewById(R.id.tvSender);
        TextView tvReceiver = convertView.findViewById(R.id.tvReceiver);
        TextView tvSubject = convertView.findViewById(R.id.tvSubject);

        tvSender.setText("De la: " + email.getSender());
        tvReceiver.setText("Către: " + email.getReceiver());
        tvSubject.setText("Subiect: " + email.getSubject());

        return convertView;
    }
}