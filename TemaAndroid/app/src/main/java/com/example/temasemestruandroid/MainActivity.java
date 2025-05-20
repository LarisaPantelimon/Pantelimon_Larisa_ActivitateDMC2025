package com.example.temasemestruandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private TextView tvWelcome;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        tvWelcome = findViewById(R.id.tvWelcome);

        // Retrieve username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EmailAppPrefs", MODE_PRIVATE);
        user = prefs.getString("loggedInUser", "Utilizator");
        tvWelcome.setText("Bun venit, " + user + "!");

        // Set up the ActionBarDrawerToggle for hamburger menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_compose) {
                startActivity(new Intent(MainActivity.this, ComposeActivity.class));
            } else if (id == R.id.nav_inbox) {
                Intent intent = new Intent(MainActivity.this, InboxActivity.class);
                intent.putExtra("username", user);
                startActivity(intent);
            } else if (id == R.id.nav_location) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
            } else if (id == R.id.nav_user_details) {
                startActivity(new Intent(MainActivity.this, UserDetailsActivity.class));
            } else if (id == R.id.nav_logout) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("loggedInUser");
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}