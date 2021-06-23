package com.example.application.graphic;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.algorithm.GenerateBoard;
import com.example.application.databinding.ChooseVehiclesScreenBinding;
import com.example.application.objects.Vehicles;

import java.util.stream.IntStream;

// deze class laat het scherm zien waarbij de gebruiker de parameters kan kiezen waarmee zij een
// nieuwe puzzel opstelling mee willen genereren
public class ChooseVehiclesScreen extends Fragment{

    private ChooseVehiclesScreenBinding binding;
    // dit element zal een foutmelding laten zien
    private Dialog errorWindow;

    // deze methode zorgt ervoor dat het juiste scherm (het kiezen van de parameters) wordt laten zien
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ChooseVehiclesScreenBinding.inflate(inflater, container, false);
        errorWindow = new Dialog(getActivity());
        return binding.getRoot();
    }

    // deze methode zal de elementen op dit scherm op de juiste wijze configureren
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // in het drop-down element kan gekozen worden voor een aantal van 1 tot 11 (1 t/m 10) cars
        // dit is minder dan de volle 12 omdat met zoveel auto's het bord vol en onspeelbaar is
        String[] allCars = IntStream.range(1, 11).mapToObj(String::valueOf)
                .toArray(String[]::new);
        ArrayAdapter<String> carAdapt = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, allCars);
        binding.dropDownCars.setAdapter(carAdapt);

        // in het drop-down element kan gekozen worden voor een aantal van 1 tot 5 (1 t/m 4) trucks
        String[] allTrucks = IntStream.range(1, 5).mapToObj(String::valueOf)
                .toArray(String[]::new);
        ArrayAdapter<String> truckAdapt = new ArrayAdapter<String>(getActivity(),
                R.layout.drop_down_item, allTrucks);
        binding.dropDownTrucks.setAdapter(truckAdapt);

        // deze knop zal ingevoerde waardes inlezen en ze beoordelen
        binding.confirmVehicles.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Spinner carSpin = (Spinner) view.findViewById(R.id.dropDownCars);
                Spinner truckSpin = (Spinner) view.findViewById(R.id.dropDownTrucks);
                String generatedBoard = GenerateBoard.generate(Vehicles.getVehicles(),
                        Integer.parseInt(carSpin.getSelectedItem().toString()),
                        Integer.parseInt(truckSpin.getSelectedItem().toString()), '.',
                        6, 5, 2,
                        4, 10, 5);

                // voorgaande methode geeft "Failed" terug als het niet gelukt is binnen de gegeven
                // tijd een geldige puzzel te vinden en zal dit aangeven bij de gebruiker
                if (generatedBoard.equals("Failed")) {
                    Button OkButton;
                    // een pop-up scherm wordt laten zien met daarin een foutmelding en een knop
                    // om deze weer weg te klikken
                    errorWindow.setContentView(R.layout.board_create_error);
                    OkButton = (Button) errorWindow.findViewById(R.id.confirmErrorButton);
                    OkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            errorWindow.dismiss();
                        }
                    });
                    errorWindow.show();
                }

                // als het algoritme in staat is geweest een geldige puzzel te maken zal de
                // gebruiker doorgestuurd worden naar het speelscherm
                else {
                    Intent i = new Intent(getActivity(), GameActivity.class);
                    // de startopstellen wordt meegegeven
                    i.putExtra("board", generatedBoard);
                    // met deze modus zal de gebruiker in staat zijn om te gaan spelen
                    i.putExtra("mode", "play");
                    startActivity(i);
                }
            }
        });
    }
}
