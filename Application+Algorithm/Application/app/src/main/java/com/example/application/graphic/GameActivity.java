package com.example.application.graphic;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.math.MathUtils;

import com.example.application.R;
import com.example.application.algorithm.GenerateBoard;
import com.example.application.algorithm.RushHour;
import com.example.application.custom.CustomDropdown;
import com.example.application.objects.Vehicle;
import com.example.application.objects.Vehicles;

import java.util.ArrayList;
import java.util.Map;

// deze class zal het scherm verzorgen voor het spelen van puzzels en het maken van puzzels
public class GameActivity extends AppCompatActivity {

    // speelbord eigenschappen
    private final int boardSize = 6;
    private final char borderchar = '#';
    private final char emptychar = '.';
    private char goalCar;
    private final int goalx = 5;
    private final int goaly = 2;
    private final int margin = 5;

    // algemene voertuig eigenschappen
    private String mode;
    private Map<Character, Vehicle> allVehicles;
    private String startState;
    private String currentState = startState;
    private final ArrayList<ImageView> placedVehicles = new ArrayList<ImageView>();
    private final ArrayList<Character> placedVehiclesList = new ArrayList<Character>();

    // algemene grafische elementen
    private ConstraintLayout layout;
    private int screenwidth;

    // elementen van de "gamescreen"
    private TextView moveCounter;
    private TextView hintCounter;
    private Dialog winWindow;
    private Dialog saveWindow;
    private int counter = 0;
    private int hintCount = 0;

    // elementen van het scherm om nieuw bord te maken
    private Dialog selectNewVehicle;
    private Dialog errorWindow;

    // deze methode zal het juiste scherm laten zien op basis van de meegegeven modus
    // dit kan het scherm zijn om een puzzel te maken of het scherm om een puzzel te spelen
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // leest de modus in
        mode = getIntent().getStringExtra("mode");
        // leest de startopstelling in
        startState = getIntent().getStringExtra("board");

        // als de modus "play" is zal het speelscherm worden laten zien
        if (mode.equals("play")) {
            createPlayScreen();
        }

        // als de modus "create" is zal het maakscherm worden laten zien
        else if (mode.equals("create")) {
            createMakeScreen();
        }

        // algemene parameters worden gevormd
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenwidth = display.widthPixels;
        allVehicles = Vehicles.getVehicles();
        goalCar = allVehicles.values().stream().filter(Vehicle::isGoalCar)
                .findFirst().get().getId();
        setUpVehicles(startState, layout, 0, true);
    }

    public void createPlayScreen() {
        // het speelscherm wordt laten zien
        setContentView(R.layout.play_game_screen);
        layout = findViewById(R.id.playGameScreen);

        // de scherm elementen worden geinitialiseerd
        moveCounter = findViewById(R.id.moves);
        hintCounter = findViewById(R.id.hints);
        ImageButton saveGame = findViewById(R.id.saveGameIcon);
        Button resetButton = findViewById(R.id.resetButton);
        Button hintButton = findViewById(R.id.hintButton);
        winWindow = new Dialog(this);
        saveWindow = new Dialog(this);

        // de saveknop zal een pop-up scherm maken waarin de gebruiker een naam voor de puzzel
        // kan meegeven als id
        saveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGamePopup();
            }
        });

        // de resetknop zal de opstellen op het speelscherm terugzetten naar de startopstelling
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset ook de tellers voor aantal moves en aantal hints gebruikt
                counter = 0;
                hintCount = 0;
                hintCounter.setText("Hints used: 0");
                resetVehicles(layout, placedVehicles, placedVehiclesList);
                setUpVehicles(startState, layout, 0, true);
            }
        });

        // de hintknop zal een schaduw laten zien van de auto die volgens het algoritme het best
        // verplaatst kan worden
        hintButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                resetVehicles(layout, placedVehicles, placedVehiclesList);
                displayHint(currentState, layout);
                setUpVehicles(currentState, layout, 0, true);
                hintCount++;
                hintCounter.setText("Hints used: " + hintCount);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createMakeScreen() {
        // het maakscherm wordt laten zien
        setContentView(R.layout.make_game_screen);
        layout = findViewById(R.id.makeGameScreen);

        // alle schermelementen worden geinitialiseerd
        Button newVehicleButton = findViewById(R.id.newVehicleButton);
        Button playGameButton = findViewById(R.id.playButton);
        Button resetBut = findViewById(R.id.resetBut);
        selectNewVehicle = new Dialog(this);
        errorWindow = new Dialog(this);

        // deze knop zal een pop-up scherm laten opkomen waarin de gebruiker een nieuw speelstuk
        // kan kiezen
        newVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVehiclesPopup();
            }
        });

        // de resetknop zal alle geplaatste elementen weer van het bord halen en de rode auto overlaten
        resetBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetVehicles(layout, placedVehicles, placedVehiclesList);
                setUpVehicles(startState, layout, 0, true);
            }
        });

        // de speelknop zal de gemaakte opstelling controleren en doorsturen naar het speelscherm
        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // de opstelling wordt gecontroleerd door het oplosalgoritme
                RushHour rushBoard = new RushHour(boardSize, currentState, emptychar, goalx, goaly);
                ArrayList<String> solvingMoves = rushBoard.getSolvingMoves();
                // als er geen oplossing gevonden is voor de gemaakt puzzels wordt een pup-up laten
                // zien die de gebruiker dit laat weten
                if (solvingMoves.size() < 2) {
                    unSolvableError();
                }

                // als de gemaakte puzzel een oplossing heeft zal er worden genavigeerd naar het
                // speelscherm
                else {
                    Intent i = new Intent(GameActivity.this, GameActivity.class);
                    // de gemaakte opstelling wordt meegegeven
                    i.putExtra("board", currentState);
                    // de modus zal "play" zijn
                    i.putExtra("mode", "play");
                    startActivity(i);
                }
            }
        });
    }

    // deze methode zal de homeicon toevoegen aan de toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // deze methode zorgt ervoor dat als je op de homeicon klikt, je terug gaat naar de homescreen
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

    // deze methode zal alle grafisch geplaatste voertuigen verwijderen van het scherm
    // wordt gebruikt als de opstelling verandert en opnieuw geplaatst moet worden
    public void resetVehicles(ConstraintLayout layout, ArrayList<ImageView> vehicles,
                              ArrayList<Character> vehiclesCharacter) {
        while (!vehicles.isEmpty()) {
            layout.removeView(vehicles.remove(0));
        }
        while (!vehiclesCharacter.isEmpty()) {
            vehiclesCharacter.remove(0);
        }
    }

    // deze methode zal een meegegeven grafisch element beweegbaar maken op een wijze die bepaald
    // wordt door de meegegeven parameters
    @SuppressLint("ClickableViewAccessibility")
    public void makeMovable(Vehicle vehicle, ImageView image, String state, int vertical,
                            int horizontal, float cellwidth, float cellwidthvertical,
                            int minlimit, int maxlimit, int bidirectional) {
        // de startpositie wordt hier onthouden
        float originalX = image.getX();
        float originalY = image.getY();
        // de parameters van het voertuig worden gereset
        vehicle.setUp();

        // deze methode bepaalt de actie die plaatsvindt als je het grafische element aanraakt
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float movedX = 0, movedY = 0;
                float absX = 0, absY = 0;
                switch (event.getActionMasked()) {
                    // bij de eerste aanraking
                    case MotionEvent.ACTION_DOWN:
                        vehicle.setxDown(event.getX());
                        vehicle.setyDown(event.getY());
                        break;

                    // bij het slepen
                    case MotionEvent.ACTION_MOVE:
                        // de nieuwe positie wordt onthouden
                        movedX = event.getX();
                        movedY = event.getY();

                        // de afgelegde afstand wordt onthouden
                        float distanceX = movedX - vehicle.getxDown();
                        float distanceY = movedY - vehicle.getyDown();

                        // de nieuwe positie in wordt onthouden
                        absX = image.getX() + distanceX;
                        absY = image.getY() + distanceY;

                        // de relatieve verplaatsing ten opzichte van de beginstand wordt bijgehouden
                        vehicle.addRelX(distanceX);
                        vehicle.addRelY(distanceY);

                        // de relatieve verplaatsing wordt opgehaald om te gebruiken bij het limiteren
                        // van de beweging van het grafische element
                        float vergelijkX = vehicle.getRelX();
                        float vergelijkY = vehicle.getRelY();

                        // als de relatieve horizontal verplaatsing binnen de toegestane parameters valt
                        if (vergelijkX < maxlimit * cellwidth && vergelijkX > minlimit * cellwidth &&
                                horizontal + bidirectional > 0) {
                            // het grafische element wordt verplaatst
                            image.setX(absX);
                        } else {
                            // het grafische element wordt niet verplaatst en de relatieve verplaatsing
                            // wordt teruggedraaid
                            vehicle.addRelX(-distanceX);
                        }

                        // als de relatieve verticale verplaatsing binnen de toegestane paramters valt
                        // ** bij het maken van een puzzel kan de rode auto alleen horizontaal bewegen
                        if (vergelijkY < maxlimit * cellwidthvertical && vergelijkY > minlimit * cellwidthvertical &&
                                vertical + bidirectional > 0 && vehicle.getId() != goalCar) {
                            // het grafische element wordt verplaatst
                            image.setY(absY);
                        } else {
                            // het grafische element wordt niet verplaatst en de relatieve verplaatsing
                            // wordt teruggedraaid
                            vehicle.addRelY(-distanceY);
                        }
                        break;

                    // bij het loslaten
                    case MotionEvent.ACTION_UP:
                        // de bewogen afstand wordt afgerond om het grafische element altijd in een
                        // valide positie te laten vallen
                        movedX = cellwidth*(Math.round(vehicle.getRelX()/cellwidth));
                        movedY = cellwidthvertical*(Math.round(vehicle.getRelY()/cellwidthvertical));
                        // de bewogen afstand in hoeveelheid x en y
                        int relMoveX =  - Math.round(movedX/cellwidth);
                        int relMoveY =  - Math.round(movedY/cellwidthvertical);
                        // de absolute x en y positie
                        int xAbsolute = Math.round(originalX/cellwidth);
                        int yAbsolute = Math.round(originalY/cellwidthvertical)+2+vertical*
                                (2-vehicle.getLength());
                        // zet het grafische element op de juiste positie
                        image.setTranslationX(originalX+movedX);
                        image.setTranslationY(originalY+movedY);
                        // hoeft alleen iets te doen als het grafische element daadwerkelijk verplaatst is
                        if (relMoveX != 0 || relMoveY != 0) {
                            counter++;
                            // verwijdert het voertuig van de opstelling
                            String newState = removeVehicle(currentState, vehicle);
                            // bij een valide nieuwe bestemming zal dit voertuig weer geplaatst worden
                            if (validPlacement(newState, vehicle, xAbsolute - relMoveX,
                                    yAbsolute - relMoveY, vertical, horizontal)) {
                                // een nieuwe opstelling wordt gemaakt met de nieuwe locatie van
                                // het voertuig
                                newState = GenerateBoard.placeAndRemove(new StringBuilder(newState),
                                        boardSize,xAbsolute - relMoveX, yAbsolute - relMoveY,
                                        horizontal, vertical, vehicle.getLength(),
                                        vehicle.getId()).toString();
                            }
                            resetVehicles(layout, placedVehicles, placedVehiclesList);
                            // als de de modus "play" is bewegen de voertuigen in 1 lijn en binnen limieten
                            if (mode.equals("play")) {setUpVehicles(newState, layout, 0, true);}
                            // als de modus "create" is bewegen de voertuigen in alle richting zonder limieten
                            else if (mode.equals("create")) {setUpVehicles(newState, layout, 1, false);}
                        }
                        break;
                }
                return true;
            }
        });
    }

    // deze methode zal een nieuw grafisch element genereren die geplaatst wordt op een bepaalde locatie
    // die een voertuig representeert
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "UseCompatLoadingForDrawables"})
    public void generateCar(String state, Vehicle vehicle, ConstraintLayout layout, boolean moveable,
                            float alpha, int vertical, int horizontal,  int x,
                                   int y, int minlimit, int maxlimit, int bidirectional) {
        // de verhoudingen worden berekent
        int cellwidth = (screenwidth - 2*margin) / 6;
        float cellwidthvertical = 0.95f * cellwidth;
        double vehiclewidthx = 0.97 * cellwidth;
        double vehiclewidthy = 0.97 * cellwidthvertical;

        // het grafische element voor het voertuig wordt gemaakt en geplaatst
        ImageView img = new ImageView(getApplicationContext());
        img.setId(View.generateViewId());
        img.setImageDrawable(getDrawable(R.drawable.car_temp));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 100);
        img.setLayoutParams(params);
        layout.addView(img);
        ConstraintSet constraintset = new ConstraintSet();
        constraintset.clone(layout);
        constraintset.connect(img.getId(), ConstraintSet.TOP, R.id.rushHourBoard, ConstraintSet.TOP, 0);
        constraintset.connect(img.getId(), ConstraintSet.BOTTOM, R.id.rushHourBoard, ConstraintSet.BOTTOM, 0);
        constraintset.connect(img.getId(), ConstraintSet.START, R.id.rushHourBoard, ConstraintSet.START, 0);
        constraintset.applyTo(layout);
        img.getLayoutParams().height = (int)Math.round(vehiclewidthy + vertical * (vehicle.getLength() - 1) * vehiclewidthy);
        img.getLayoutParams().width = (int)Math.round(vehiclewidthx + horizontal * (vehicle.getLength() - 1) * vehiclewidthx);
        if (vertical == 1) {img.setTranslationY(-cellwidthvertical * (3-vehicle.getLength()*0.5f));}
        else {img.setTranslationY(-cellwidthvertical * 2.5f);}
        img.setTranslationX(img.getX() + 2*margin + x * cellwidth);
        img.setTranslationY(img.getY() + 0.75f*margin + y * cellwidthvertical);
        img.setColorFilter(getResources().getColor(vehicle.getColor()));
        img.setAlpha(alpha);
        img.setLongClickable(true);

        // kijkt of het voertuig beweegbaar moet zijn (bij hint bijvoorbeeld niet) en of deze al
        // niet eerder geplaatst is
        if (moveable && !placedVehiclesList.contains(vehicle.getId())) {
            // deze methode maakt voertuig beweegbaar
            makeMovable(vehicle, img, state, vertical, horizontal, cellwidth, cellwidthvertical,
                    minlimit, maxlimit, bidirectional);}

        // registreert welke voertuigen geplaatst zijn en zal alle grafische elementen verzamelen
        if (alpha >= 1) {placedVehiclesList.add(vehicle.getId());}
        placedVehicles.add(img);
    }

    // deze methode zal op een bepaalde locatie in de puzzel kijken welk voertuig hier staat en welke
    // orientatie deze heeft
    private char getOrientation(String state, int x, int y) {
        if (GenerateBoard.locateCharacter(state, boardSize,x-1, y) ==
                GenerateBoard.locateCharacter(state, boardSize, x, y) ||
                GenerateBoard.locateCharacter(state, boardSize, x+1, y) ==
                        GenerateBoard.locateCharacter(state, boardSize, x, y)) {
            return 'h';
        }
        else {return 'v';}
    }

    // deze methode kijkt hoeveel beschikbare beweegruimte er is in een bepaalde richting
    private int checkSpaces(String state, int x, int y, int updown, int leftright) {
        char originalchar = GenerateBoard.locateCharacter(state, boardSize, x, y);
        int spaces = 0;
        // zolang de karakters in een bepaalde richting gelijk zijn aan een leeg vak of
        // het voertuig zelf
        while (GenerateBoard.locateCharacter(state, boardSize, x, y) == emptychar ||
                GenerateBoard.locateCharacter(state, boardSize, x, y) == originalchar) {
            if (GenerateBoard.locateCharacter(state, boardSize, x, y) == emptychar) {spaces ++;}
            if (GenerateBoard.locateCharacter(state, boardSize, x, y) == borderchar) {break;}
            x -= leftright;
            y -= updown;
        }
        // de gevonden beweegruimte wordt teruggegeven
        return spaces;
    }

    // deze methode conroleert of de het meegegeven voertuig op een bepaalde locatie met een bepaalde
    // orientatie geplaatst kan worden
    public boolean validPlacement(String state, Vehicle vehicle, int x, int y, int vertical,
                                  int horizontal) {
        // controleert elk vakje waar het voertuig in kan vallen op basis van de lengte van het voertuig
        for (int l=0; l < vehicle.getLength(); l++) {
            if (GenerateBoard.locateCharacter(state, boardSize,x+l*horizontal, y+l*vertical) !=
                    emptychar) {return false;}
        }
        return true;
    }

    // deze methode haalt een voertuig weg uit een puzzel
    public String removeVehicle(String state, Vehicle vehicle) {
        StringBuilder mutableString = new StringBuilder(state);
        // loopt door de gehele puzzel heen
        for (int y=0; y < boardSize; y++) {
            for (int x=0; x < boardSize; x++) {
                // haalt karakter weg als deze overeenkomt met het karakter van het meegegeven voertuig
                if (GenerateBoard.locateCharacter(state, boardSize, x, y) == vehicle.getId()) {
                    mutableString.setCharAt(GenerateBoard.coordinates(boardSize, x, y), emptychar);}
            }
        }
        return mutableString.toString();
    }

    // deze methode zal vanuit een bepaalde opstelling alle grafische elementen juist plaatsen en
    // configuren
    @SuppressLint("SetTextI18n")
    public void setUpVehicles(String state, ConstraintLayout layout, int bidrectional, boolean limited) {
        // update de currentstate
        currentState = state;

        // update de movecounter alleen als deze class in de speelmodus staat
        if (mode.equals("play")) {moveCounter.setText("Moves: " + counter);}

        // houdt lijst van geplaaste voertuigen bij om duplicaten te voorkomen
        ArrayList<Character> vehiclesPlaced = new ArrayList<Character>();
        char currentChar;
        // gaat door alle cellen van de puzzel heen
        for (int y=0; y < boardSize; y++) {
            for (int x=0; x < boardSize; x++) {
                currentChar = GenerateBoard.locateCharacter(state, boardSize, x, y);
                // als er iets staat
                if (currentChar != emptychar) {
                    // als dit voertuig al niet eerder geplaatst is
                    if (!vehiclesPlaced.contains(currentChar)) {
                        // de parameters voor het grafische element worden bepaald
                        char orientation = getOrientation(state, x, y);
                        int minlimit;
                        int maxlimit;
                        if (limited || currentChar == 'x') {
                            minlimit = (orientation == 'v') ?
                                    -checkSpaces(state, x, y, 1, 0) :
                                    -checkSpaces(state, x, y, 0, 1);
                            maxlimit = (orientation == 'v') ? checkSpaces(state, x, y, -1, 0) :
                                    checkSpaces(state, x, y, 0, -1);
                        }
                        else {
                            minlimit = -100;
                            maxlimit = 100;
                        }
                        // het grafische element wordt gemaakt
                        generateCar (state, allVehicles.get(currentChar), layout,
                                ((!GenerateBoard.checkGoal(state, boardSize, goalCar, goalx, goaly))
                                        || !mode.equals("play")), 1,
                                (orientation == 'v') ? 1 : 0, (orientation == 'h') ? 1 : 0, x, y, minlimit,
                                maxlimit, bidrectional);
                        if (GenerateBoard.checkGoal(state, boardSize, goalCar, goalx, goaly)
                                && mode.equals("play")) {createWinMessage();}
                        vehiclesPlaced.add(currentChar);
                    }
                }
            }
        }
    }

    // deze methode zal een schaduw van een voertuig laten zien om zo een hint te representeren
    public void displayHint(String currentState, ConstraintLayout layout) {
        RushHour rushBoard = new RushHour(boardSize, currentState, emptychar, goalx, goaly);
        int xVehicle = -1;
        int yVehicle = -1;
        char movedVehicle = '~';
        // krijgt de volgende bord toestand van het algoritme
        String nextState = rushBoard.getHint();
        // gaat door alle cellen van het bord heen
        for (int y=0; y < boardSize; y++) {
            for (int x=0; x < boardSize; x++) {
                // als een cel van de huidige staat en de hintstaat ongelijk is
                if (GenerateBoard.locateCharacter(nextState, boardSize, x, y) != emptychar &&
                        GenerateBoard.locateCharacter(currentState, boardSize, x, y) == emptychar) {
                    // voertuig wordt onthouden
                    movedVehicle = GenerateBoard.locateCharacter(nextState, boardSize, x, y);
                    xVehicle = x;
                    yVehicle = y;
                    break;
                }
            }
        }
        // begincoordinaten van het hintvoertuig worden gezocht
        while (GenerateBoard.locateCharacter(nextState, boardSize, xVehicle, yVehicle) == movedVehicle) {
            xVehicle--;
        }
        xVehicle ++;
        while (GenerateBoard.locateCharacter(nextState, boardSize, xVehicle, yVehicle) == movedVehicle) {
            yVehicle--;
        }
        yVehicle++;
        // orientatie wordt bepaald
        char orientation = getOrientation(nextState, xVehicle, yVehicle);
        // hintvoertuig wordt geplaatst
        if (movedVehicle != '~') {generateCar(nextState, allVehicles.get(movedVehicle), layout,false, 0.55f,
                (orientation == 'v') ? 1 : 0, (orientation == 'h') ? 1 : 0, xVehicle, yVehicle, 0, 0,
                0);}
    }

    // deze methode zal een pop-up scherm maken met daarin een bericht dat de gebruiker heeft gewonnen
    @SuppressLint("SetTextI18n")
    public void createWinMessage() {
        TextView totalMoves;
        TextView totalHints;
        Button OKButton;
        winWindow.setContentView(R.layout.win_message);
//        winMessage = (TextView) winWindow.findViewById(R.id.winMessage);
        totalMoves = (TextView) winWindow.findViewById(R.id.moveDisplay);
        totalMoves.setText("in " + counter + " moves");
        totalHints = (TextView) winWindow.findViewById(R.id.hintDisplay);
        totalHints.setText("using " + hintCount + " hints");
        OKButton = (Button) winWindow.findViewById(R.id.confirmButton);
        // deze knop zal het scherm weer sluiten
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                winWindow.dismiss();
            }
        });
        winWindow.show();
    }

    // deze methode zal een pop-up maken met daarin de mogelijkheid een naam in te voeren
    // ter identificatie van een opgeslagen bord opstelling
    public void saveGamePopup() {
        EditText saveName;
        TextView errorMessage;
        Button cancelButton;
        Button saveButton;

        // de opgeslagen puzzels worden opgehaald
        SharedPreferences savedGames = getSharedPreferences("savedGames",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savedGames.edit();
        saveWindow.setContentView(R.layout.input_save_game);

        // de scherm elementen worden geinitialiseerd
        saveName = (EditText) saveWindow.findViewById(R.id.enterSaveName);
        errorMessage = (TextView) saveWindow.findViewById(R.id.nameExistError);
        cancelButton = (Button) saveWindow.findViewById(R.id.saveCancelButton);
        saveButton = (Button) saveWindow.findViewById(R.id.saveButton);

        // deze knop zal de puzzel opslaan
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savedName = saveName.getText().toString();
                // als de ingevoerde naam leeg is zal de puzzel niet opgeslagen worden en een foutmelding
                // wordt geven
                if (savedName.equals("")) {
                    errorMessage.setText("No name entered");
                    errorMessage.setVisibility(View.VISIBLE);
                }
                // als de ingevoerde naam al bestaat tussen de opgeslagen puzzels zal de puzzel niet
                // worden opgeslagen en een foutmeldingen wordt geven
                else if (savedGames.contains(savedName)) {
                    errorMessage.setText("Name already exists");
                    errorMessage.setVisibility(View.VISIBLE);
                }
                // de puzzel wordt onder de ingevoerde naam opgeslagen
                else {
                    editor.putString(savedName, startState);
                    editor.apply();
                    saveWindow.dismiss();
                }
            }
        });

        // deze knop zal het pop-up scherm weer weghalen
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWindow.dismiss();
            }
        });
        saveWindow.show();
    }

    // deze methode zal een pop-up maken die de gebruiker een nieuw voertuig laat kiezen om te plaatsen
    // voor een zelfgemaakte puzzel
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void chooseVehiclesPopup() {
        Spinner vehicleType;
        Spinner vehicleColor;
        Spinner vehicleOrientation;
        Button OKButton;
        // een lijst met alle voertuigen wordt gemaakt om 1 van deze elementen dan uiteindelijk
        // mee te geven voor een nieuw voertuig
        ArrayList<Vehicle> tempVehicles = new ArrayList<Vehicle>(allVehicles.values());

        // het nieuwe scherm wordt gemaakt
        selectNewVehicle.setContentView(R.layout.vehicle_parameters);

        // de dropdown waardes worden bepaald
        String[] cars = new String[] {"Car", "Truck"};
        String[] orientations = new String[] {"Vertical", "Horizontal"};
        ArrayAdapter<String> vehicleTypes = new ArrayAdapter<String>(GameActivity.this,
                R.layout.drop_down_item, cars);
        ArrayAdapter<String> vehicleOrientations = new ArrayAdapter<String>(GameActivity.this,
                R.layout.drop_down_item, orientations);

        // de schermelementen worden geinitialiseerd
        OKButton = (Button) selectNewVehicle.findViewById(R.id.confirmVehicles);
        vehicleType = selectNewVehicle.findViewById(R.id.dropDownType);
        vehicleColor = selectNewVehicle.findViewById(R.id.dropDownColor);
        vehicleOrientation = selectNewVehicle.findViewById(R.id.dropDownOrientation);

        // de dropdown waardes worden toegepast
        vehicleType.setAdapter(vehicleTypes);
        vehicleOrientation.setAdapter(vehicleOrientations);
        // bij het selecteren van een auto of een truck worden de beschikbare kleuren automatisch aangepast
        vehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currentSelect = vehicleType.getSelectedItem().toString();
                if (currentSelect.equals("Car")) {
                    vehicleColor.setAdapter(new CustomDropdown(parent.getContext(), R.array.carcolors));
                }
                else {vehicleColor.setAdapter(new CustomDropdown(parent.getContext(), R.array.truckcolors));}
            }

            // default dropdown is de autokleuren
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vehicleColor.setAdapter(new CustomDropdown(parent.getContext(), R.array.carcolors));
            }
        });

        // bij het drukken op deze knop wordt de informatie verwerkt en een nieuw voertuig aangemaakt
        OKButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // voertuig parameters worden bepaald
                String orientation = vehicleOrientation.getSelectedItem().toString();
                int vertical = (orientation.equals("Vertical")) ? 1 : 0;
                int horizontal = (orientation.equals("Horizontal")) ? 1 : 0;
                int vehicleIndex = vehicleColor.getSelectedItemPosition();
                if (vehicleType.getSelectedItem().toString().equals("Truck")) {vehicleIndex += 11;}
                // nieuw voertuig wordt geplaatst
                resetVehicles(layout, placedVehicles, placedVehiclesList);
                setUpVehicles(currentState, layout, 1, false);
                generateCar(currentState, tempVehicles.get(vehicleIndex), layout, true, 1, vertical,
                        horizontal, 3, 6, -1000, 1000, 1);
                selectNewVehicle.dismiss();
            }
        });
        selectNewVehicle.show();
    }

    // deze methode verzorgt een pop-up die laten zien wordt wanneer de gebruiker een puzzel heeft
    // gemaakt die onoplosbaar is
    @SuppressLint("SetTextI18n")
    public void unSolvableError() {
        TextView errorMSG;
        Button OKButton;
        errorWindow.setContentView(R.layout.board_create_error);
        errorMSG = errorWindow.findViewById(R.id.FailedText);
        OKButton = errorWindow.findViewById(R.id.confirmErrorButton);
        errorMSG.setText("This puzzle can't be solved");
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorWindow.dismiss();
            }
        });
        errorWindow.show();
    }
}
