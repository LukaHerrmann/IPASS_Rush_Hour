package com.example.application.algorithm;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.application.objects.Vehicle;

import java.util.Random;
import java.util.*;

// deze class bevat het algoritme om een nieuwe puzzelopstelling te genereren op basis van een
// meegegeven hoeveelheid auto's en trucks
public class GenerateBoard {

    // deze variabele communiceert alleen binnen deze class
    static final char borderchar = '#';

    // deze methode calculeert de benodigde index die de meegegeven coordinaten representeert
    public static int coordinates(int boardSize, int x, int y) {
        return y * boardSize + x;
    }

    // deze methode dient ervoor om het karakter op een gegeven locatie op te halen en het karakter
    // voor buiten het bord terug te geven als deze coordinaten ongeldig zijn
    public static char locateCharacter(String state, int boardSize, int x, int y) {
        return (0 <= x && x < boardSize) && (0 <= y && y < boardSize) ?
                state.charAt(coordinates(boardSize, x, y)) : borderchar;
    }

    // deze methode plaatst een meegegeven karakter op een bepaalde positite en houdt rekening met
    // de meegegeven orientatie en lengte
    public static StringBuilder placeAndRemove(StringBuilder state, int boardSize, int x, int y,
                                               int horizontal, int vertical, int length,
                                               char charact) {
        for (int l=0; l < length; l++) {
            state.setCharAt(coordinates(boardSize, x+l*horizontal, y+l*vertical), charact);
        }
        return state;
    }

    // deze methode controleert of de meegegeven bord toestand een eindtoestand is
    public static boolean checkGoal(String state, int boardSize, char goalCar, int goalX, int goalY) {
        return locateCharacter(state, boardSize, goalX, goalY) == goalCar;
    }

    // deze methode controleert of de gekozen plaats mogelijk is
    public static boolean validPlace(StringBuilder state, int boardSize, int x, int y, int goalY,
                                     int horizontal, int vertical, int length, boolean isGoalCar,
                                     char emptyChar) {
        // kijkt of alle cellen binnen de parameter lege cellen zijn
        for (int l=0;l<length;l++) {
            if (state.charAt(coordinates(boardSize, x+l*horizontal, y+l*vertical)) != emptyChar) {return false;}
        }
        // kijkt of een auto niet horizontaal op dezelfde lijn ligt als de rode auto
        if (y == goalY && horizontal > 0 && !isGoalCar) {return false;}
        int sameOrientation = 0;
        char previousChar = emptyChar;
        char currentChar;
        // kijkt hoeveel karakters op een lijn dezelfde orientatie hebben
        for (int i=0; i < boardSize; i++) {
            currentChar = locateCharacter(state.toString(), boardSize, x*vertical + horizontal*i,
                    y*horizontal + vertical*i);
            if (currentChar != emptyChar) {
                sameOrientation++;
                if (currentChar != previousChar && previousChar != emptyChar) {
                    sameOrientation--;
                }
            }
            previousChar = currentChar;
        }
        // zorgt ervoor dat bijvoorbeeld een auto en een truck niet dezelfde orientatie mogen hebben
        // en op 1 lijn liggen
        return sameOrientation + length < 5;
    }

    // deze methode zal een aantal keer proberen een willekeurig bord te genereren
    // zal een aantal keer proberen een voertuig te plaatsen. Als dit lukt -> door naar volgende
    // als dit niet lukt binnen aangegeven hoeveelheid -> terug naar vorige
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String attemptBoard(Map<Character, Vehicle> allVehicles,
                                      ArrayList<Character> vehiclesToBePlaced, int cars, int trucks,
                                      char emptyChar, int boardSize, char goalCar, int exitX, int exitY,
                                      int maxCarAttempt, int maxPuzzleAttempt, int minSolveMoves,
                                      Map<Character, Integer> countAttempts) {
        // een randomizer
        Random rand = new Random();
        // onthoudt waar de voertuigen geplaatst zijn om er later makkelijker op terug te komen
        Map<Character, ArrayList<Integer>> placedAt = new HashMap<Character, ArrayList<Integer>>();
        // een leeg bord aangemaakt
        String emptyBoard = new String(new char[boardSize * boardSize]).replace('\0', emptyChar);
        StringBuilder newBoard = new StringBuilder(emptyBoard);
        // maximaal aantal pogingen
        for (int attempt = 0; attempt < maxPuzzleAttempt; attempt++) {
            // zet de teller voor pogingen per voertuig op 0
            for (char vehicle : vehiclesToBePlaced) {
                countAttempts.put(vehicle, 0);
            }
            int vehicleIndex = 0;

            // controleert op de totale hoeveelheid pogingen, stop als hier voorbij gegaan wordt
            while (countAttempts.values().stream().mapToInt(Integer::intValue).sum() < (cars + trucks) * maxCarAttempt) {
                char currentVehicle = vehiclesToBePlaced.get(vehicleIndex);
                int x;
                int y;
                int vertical;
                int horizontal;
                boolean isGoalCar = allVehicles.get(currentVehicle).isGoalCar();
                // rode auto wordt bij de uitgang gezet
                if (isGoalCar) {
                    vertical = 0;
                    horizontal = 1;
                    y = exitY;
                    x = exitX-1;
                }
                // andere voertuigen krijgen een willekeurige positie
                else {
                    vertical = rand.nextInt(2);
                    horizontal = Math.abs(vertical - 1);
                    y = rand.nextInt(boardSize - vertical
                            * allVehicles.get(currentVehicle).getLength());
                    x = rand.nextInt(boardSize - horizontal
                            * allVehicles.get(currentVehicle).getLength());
                }
                int length = allVehicles.get(currentVehicle).getLength();
                // als de willekeurige plaatsing wordt goedgekeurd zal deze geplaatst worden
                if (validPlace(newBoard, boardSize, x, y, exitY, horizontal, vertical, length,
                        isGoalCar, emptyChar)) {
                    newBoard = placeAndRemove(newBoard, boardSize, x, y, horizontal, vertical, length,
                            currentVehicle);
                    // locatie wordt opgeslagen voor als dit voertuig later opnieuw moet worden
                    // geplaatst
                    ArrayList<Integer> location = new ArrayList<Integer>();
                    location.add(x);
                    location.add(y);
                    location.add(horizontal);
                    location.add(vertical);
                    placedAt.put(currentVehicle, location);
                    vehicleIndex++;
                }
                // als de plaatsing niet valide is en het maximale aantal pogingen al is bereikt
                // zal de vorige auto opnieuw geplaatst worden
                else if (countAttempts.get(currentVehicle) >= maxCarAttempt) {
                    countAttempts.put(currentVehicle, 0);
                    ArrayList<Integer> previousLocation;
                    if (vehicleIndex > 0) {
                        previousLocation = placedAt.get(vehiclesToBePlaced.get(vehicleIndex - 1));
                        newBoard = placeAndRemove(newBoard, boardSize, previousLocation.get(0),
                                previousLocation.get(1),
                                previousLocation.get(2), previousLocation.get(3),
                                allVehicles.get(vehiclesToBePlaced.get(vehicleIndex - 1)).getLength(),
                                emptyChar);
                    }
                    vehicleIndex--;
                    // als het algoritme verder terug moet gaan dan de rode auto is de poging gefaald
                    // en begint het opnieuw
                    if (vehicleIndex < 0) {break;}
                }
                // als de plaatsing niet valide is en het maximale aantal pogingen is nog niet bereikt
                // zal dat als een poging worden geregistreerd
                else {
                    countAttempts.put(currentVehicle, countAttempts.get(currentVehicle) + 1);
                }

                // als het algoritme voorbij de laatste voortuig komt zijn alle voertuigen
                // succesvol geplaatst en houdt het algoritme op
                if (vehicleIndex >= vehiclesToBePlaced.size()) {
                    break;
                }
            }

            // de willekeurige opstelling wordt gevalideerd door deze te reverse-engineren
            RushHour rushBoard = new RushHour(6, newBoard.toString(), emptyChar, exitX, exitY);
            ArrayList<String> longestPath = rushBoard.getLongestPath();
            // als de willekeurige startpositie niet voldoet aan het quota voor mogelijkheden
            // wordt er een nieuwe poging gedaan
            if (longestPath.size()-1 >= minSolveMoves && !checkGoal(longestPath.get(
                    longestPath.size()-1), boardSize, goalCar, exitX, exitY)) {
                return longestPath.get(longestPath.size()-1);}
        }
        // als het maximaal aantal pogingen zijn bereikt zal dit de return waarde zijn
        return "Failed";
    }

    // deze methode zal een willekeurige puzzel genereren met een meegegeven aantal auto's en trucks
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String generate(Map<Character, Vehicle> allVehicles, int cars, int trucks,
                                  char emptyChar, int boardSize, int exitX,
                                  int exitY, int maxCarAttempt, int maxPuzzleAttempt, int minSolveMoves) {
        Random rand = new Random();
        // bepaalt de rode auto
        char goalCar = allVehicles.values().stream().filter(Vehicle::isGoalCar).findFirst()
                .get().getId();
        ArrayList<Character> vehiclesToBePlaced = new ArrayList<Character>();
        ArrayList<Character> vehicles = new ArrayList<Character>();
        Map<Character, Integer> countAttempts = new HashMap<Character, Integer>();
        Map<Character, ArrayList<Integer>> placedAt = new HashMap<Character, ArrayList<Integer>>();
        // maakt een lijst met alle beschikbare voertuigen
        for (char vehicle : allVehicles.keySet()) {
            vehicles.add(vehicle);
        }
        int carsleft = cars;
        int trucksleft = trucks;
        vehiclesToBePlaced.add(goalCar);
        countAttempts.put(goalCar, 0);
        carsleft--;
        // zal een willekeurige selectie maken van voertuigen om te plaatsen
        while (carsleft > 0 || trucksleft > 0) {
            char randomVehicle = vehicles.get(rand.nextInt(vehicles.size()));
            if (allVehicles.get(randomVehicle).getLength() == 3 && trucksleft > 0 && !vehiclesToBePlaced.contains(randomVehicle)) {
                vehiclesToBePlaced.add(randomVehicle);
                countAttempts.put(randomVehicle, 0);
                trucksleft--;
            } else if (allVehicles.get(randomVehicle).getLength() == 2 && carsleft > 0 && !vehiclesToBePlaced.contains(randomVehicle)) {
                vehiclesToBePlaced.add(randomVehicle);
                countAttempts.put(randomVehicle, 0);
                carsleft--;
            }
        }
        // probeert een willeurige opstelling
        return attemptBoard(allVehicles, vehiclesToBePlaced, cars, trucks, emptyChar, boardSize,
                goalCar, exitX, exitY, maxCarAttempt, maxPuzzleAttempt, minSolveMoves, countAttempts);
    }
}


