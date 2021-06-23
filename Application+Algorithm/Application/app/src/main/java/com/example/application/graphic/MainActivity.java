package com.example.application.graphic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.application.R;

// Deze class verzorgt de algemene layout van de fragments voorafgaand aan het de andere activity
// die het spelen van het spel zelf verzorgt
public class MainActivity extends AppCompatActivity {

    // deze methode zal bij het opstarten van de applicatie de toolbar opzetten voor de volgende
    // fragmenten
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    // deze methode zal een eigen menu implementeren met daarin een knop om terug te gaan naar
    // het startscherm
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // deze methode controleert wat er gebeurt bij het klikken van het home icon in de toolbar
    // in dit geval zal je dan terug gaan naar het hoofdscherm
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Intent goHome = new Intent(this, MainActivity.class);
            this.startActivity(goHome);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}