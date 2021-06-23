package com.example.application.graphic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.example.application.databinding.SavedGamesScreenBinding;

// deze class laat alle opgeslagen puzzels zien op een scherm met de mogelijkheid deze opnieuw
// te spelen of te verwijderen
public class SavedGamesScreen extends Fragment {

    private SavedGamesScreenBinding binding;

    // deze methode zorgt ervoor dat het juiste scherm (de opgeslagen spellen) wordt laten zien
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SavedGamesScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // deze methode zal nadat het scherm is gemaakt de opgeslagen puzzels ophalen en deze als map
    // doorgeven aan de methode die dit afhandelt
    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences savedGames = getActivity().getSharedPreferences("savedGames", Context.MODE_PRIVATE);
        checkGames(view, savedGames);
    }

    // deze methode zal kijken of er spellen zijn opgeslagen
    public void checkGames(View view, SharedPreferences savedGames) {
        // als er geen puzzels zijn opgeslagen zal er een stukje tekst worden laten zien die dit
        // aantoont
        if (savedGames.getAll().isEmpty()) {
            LinearLayout scrollGames = (LinearLayout) view.findViewById(R.id.savedGamesList);
            TextView noSavedGames = new TextView(getContext());
            noSavedGames.setText("No Puzzles Saved");
            noSavedGames.setTextSize(16);
            noSavedGames.setTypeface(Typeface.DEFAULT_BOLD);
            scrollGames.addView(noSavedGames);
        }
        // als er puzzels zijn opgeslagen zal de volgende methode worden aangeroepen
        else {
            displaySavedGames(view, savedGames);
        }
    }

    // deze methode zal de opgeslagen puzzels laten zien op het scherm
    public void displaySavedGames(View view, SharedPreferences savedGames) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = savedGames.edit();
        // deze layout zorgt ervoor dat als er meer puzzels zijn opgeslagen dan kunnen worden
        // laten zien op het scherm je kan scrollen
        LinearLayout scrollGames = (LinearLayout) view.findViewById(R.id.savedGamesList);
        LinearLayout allSavedGames = (LinearLayout) view.findViewById(R.id.savedGamesList);
        // algemene layout marges voor de scherm elementen
        LinearLayout.LayoutParams expand = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        expand.setMargins(0, 0, 0, 40);
        LinearLayout.LayoutParams expandfirst = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        expandfirst.weight = 1;
        LinearLayout.LayoutParams expandsecond = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        expandsecond.weight = 2;
        // onderstaande loop loopt door de keys heen
        for (String name : savedGames.getAll().keySet()) {
            TextView puzzle = new TextView(getContext());
            Button playButton = new Button(new ContextThemeWrapper(getContext(), R.style.Theme_Application_AppBarOverlay));
            Button deleteButton = new Button(new ContextThemeWrapper(getContext(), R.style.Theme_Application_AppBarOverlay));
            LinearLayout horizontalLayout = new LinearLayout(getContext());
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(expand);
            int maxLength = 10;
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            // de key, ofwel de meegegeven naam, wordt laten zien als identificatie van de puzzel
            puzzle.setText(name);
            puzzle.setTextSize(18);
            puzzle.setMaxLines(1);
            puzzle.setEllipsize(TextUtils.TruncateAt.END);
            puzzle.setTypeface(null, Typeface.BOLD);
            playButton.setText("PLAY");
            playButton.setLayoutParams(expandfirst);
            playButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            // deze knop zal een nieuw spel starten
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), GameActivity.class);
                    // de opgeslagen puzzel opstelling wordt meegegeven om opgezet te worden op het
                    // spelscherm
                    i.putExtra("board", savedGames.getString(name, ".............xx....................."));
                    // in deze modus kan de gebruiker het spel gaan spelen
                    i.putExtra("mode", "play");
                    startActivity(i);
                }
            });
            deleteButton.setText("DELETE");
            deleteButton.setLayoutParams(expandsecond);
            deleteButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            // deze knop zal ervoor zorgen dat de aangeklikte puzzel verwijderd zal worden
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.remove(name);
                    editor.apply();
                    scrollGames.removeAllViewsInLayout();
                    checkGames(view, savedGames);
                }
            });
            // alle elementen worden laten zien op het scherm
            allSavedGames.addView(puzzle);
            allSavedGames.addView(horizontalLayout);
            horizontalLayout.addView(playButton);
            horizontalLayout.addView(deleteButton);
        }
    }

}
