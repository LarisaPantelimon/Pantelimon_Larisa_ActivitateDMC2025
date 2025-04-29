package com.example.lab9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<ListItem> listItems;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        listItems = new ArrayList<>();
        listItems.add(new ListItem("Titlu 1", "Descriere 1", R.drawable.img, "https://www.istockphoto.com/ro/fotografie/fundal-de-ciocolat%C4%83-gm522735736-91749353"));
        listItems.add(new ListItem("Titlu 2", "Descriere 2", R.drawable.img_1, "https://www.istockphoto.com/ro/fotografie/sortiment-de-bomboane-de-ciocolat%C4%83-fin%C4%83-alb-%C3%AEntuneric-%C8%99i-ciocolat%C4%83-cu-lapte-gm923430892-253476751"));
        listItems.add(new ListItem("Titlu 3", "Descriere 3", R.drawable.img_2, "https://www.istockphoto.com/ro/fotografie/ciocolat%C4%83-gm488182109-39105824"));
        listItems.add(new ListItem("Titlu 4", "Descriere 4", R.drawable.img_3, "https://www.istockphoto.com/ro/fotografie/cofet%C4%83rie-dulce-%C8%99i-bomboane-indulgen%C8%9B%C4%83-concept-tem%C4%83-cu-aproape-pe-o-praline-de-gm1182505978-332081188"));
        listItems.add(new ListItem("Titlu 5", "Descriere 5", R.drawable.img_4, "https://www.istockphoto.com/ro/fotografie/trufe-de-ciocolat%C4%83-mixte-brigadeiros-gm691726064-127607235"));

        adapter = new CustomAdapter(this, listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", listItems.get(position).getUrl());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  // << Adăugat
            startActivity(intent);
        });
    }
}
