package com.example.application.graphic;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.application.R;
import com.example.application.databinding.HomeScreenBinding;

// deze class zal het homescherm laten zien
public class HomeScreen extends Fragment {

    private HomeScreenBinding binding;

    // deze methode zorgt ervoor dat het juiste scherm (het homescherm) wordt laten zien
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // deze methode zal ervoor zorgen dat de knoppen op het homescherm navigeren naar de
    // gepaste volgende scherm
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // deze knop zal navigeren naar het keuze scherm hoe de gebruiker een nieuwe puzzel wil
        // gaan krijgen
        binding.newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeScreen.this)
                        .navigate(R.id.action_home_to_game);
            }
        });

        // deze knop zal navigeren naar het scherm waar de opgeslagen puzzels te zien zullen zijn
        binding.savedGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeScreen.this)
                        .navigate(R.id.go_to_savedgames);
            }
        });

        // deze knop zal navigeren naar het scherm waar wat informatie op te zien is betreffend
        // hoe het spel werkt en de ontwikkelaar van de applicatie
        binding.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeScreen.this)
                        .navigate(R.id.go_to_infoscreen);
            }
        });
    }
}
