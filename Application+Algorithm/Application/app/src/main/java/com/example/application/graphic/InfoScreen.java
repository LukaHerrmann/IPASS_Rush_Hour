package com.example.application.graphic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.databinding.InfoScreenBinding;

// deze class zal een nieuw scherm maken waarin informatie wordt weergegeven over hoe het spel werk
// en de ontwikkelaar van de applicatie
public class InfoScreen extends Fragment {

    private InfoScreenBinding binding;

    // deze methode zorgt ervoor dat het juiste scherm (het informatiescherm) wordt laten zien
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = InfoScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
