package com.example.application.graphic;

import com.example.application.R;
import com.example.application.databinding.NewGameScreenBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

// deze class maakt een scherm met twee knoppen zodat de gebruiker een keuze kan maken
// en nieuwe puzzel te maken of te laten generen
public class NewGameScreen extends Fragment {

    private NewGameScreenBinding binding;

    // deze methode zorgt ervoor dat het juiste scherm (dit keuzescherm) wordt laten zien
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NewGameScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // deze methode zal zorgt ervoor dat de knoppen op dit scherm navigeren naar het juiste
    // volgende scherm
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // deze knop gaat naar het scherm om de parameters te kiezen voor het genereren van een
        // nieuwe puzzel
        binding.generateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(NewGameScreen.this)
                        .navigate(R.id.go_to_choice);
            }
        });

        // deze knop gaat naar het scherm om zelf een nieuwe puzzel samen te stellen
        binding.createBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GameActivity.class);
                // geeft de startopstelling mee waar het spel mee moet beginnen
                i.putExtra("board", ".............xx.....................");
                // in deze modus zal de applicatie de gebruiker de mogelijkheid geven elementen
                // op het bord te plaatsen
                i.putExtra("mode", "create");
                startActivity(i);
            }
        });
    }
}
