package com.example.application.algorithm;

import java.util.*;

public class RushHour {
    // hier wordt uitgegaan dat het bord altijd vierkant is
    private final int size;
    // de startstate is geformat als een string om het programma efficienter te maken
    private final String startstate;
    private final char emptychar;
    private final int goalx;
    private final int goaly;

    // deze variabelen verdelen de voertuigen onder in verschillende types
    private String vertical = "";
    private String horizontal = "";
    private String cars = "";
    private String trucks = "";
    private char goalcar;

    // deze variabelen vormen onderdeel van het algoritme
    private final HashSet<String> next = new HashSet<String>();
    private final Queue<String> searchTree = new LinkedList<String>();
    private final Map<String, String> previousStates = new HashMap<String, String>();
    private final ArrayList<String> moves = new ArrayList<String>();

    public RushHour (int size, String startstate, char emptychar, int goalx, int goaly) {
        this.size = size;
        this.startstate = startstate;
        this.emptychar = emptychar;
        this.goalx = goalx;
        this.goaly = goaly;
        determinetypes(startstate);
    }

    // deze methode geeft aan wat de lengte van een bepaald voertuig is
    public int getLength(char car) {
        return cars.contains(Character.toString(car)) ? 2 : 3;
    }

    // deze methode bepaald welke auto's horizontaal, verticaal, kort en lang zijn
    public void determinetypes(String state) {
        // houdt de frequentie van karakters bij voor de lengtes
        HashMap<Character, Integer> frequency = new HashMap<Character, Integer>();
        // gaat door alle cellen van het bord
        for (int y=0; y < size; y++) {
            // houdt de frequentie van karakters bij op 1 lijn om horizontale voertuigen na te gaan
            HashMap<Character, Integer> frequencyline = new HashMap<Character, Integer>();
            for (int x=0; x < size; x++) {
                // de frequentiemappen worden bijgehouden
                if (!frequency.containsKey(GenerateBoard.locateCharacter(state, size, x, y))) {
                    frequency.put(GenerateBoard.locateCharacter(state, size, x, y), 1);}
                else {frequency.put(GenerateBoard.locateCharacter(state, size, x, y),
                        frequency.get(GenerateBoard.locateCharacter(state, size, x, y)) + 1);}
                if (!frequencyline.containsKey(GenerateBoard.locateCharacter(state, size, x, y))) {
                    frequencyline.put(GenerateBoard.locateCharacter(state, size, x, y), 1);}
                else {frequencyline.put(GenerateBoard.locateCharacter(state, size, x, y),
                        frequencyline.get(GenerateBoard.locateCharacter(state, size, x, y)) + 1);}
            }
            // het karakter wat een lege cel representeerd wordt verwijderd
            frequencyline.remove(emptychar);
            for (char vehicle : frequencyline.keySet()) {
                // de types voor orientatie worden toegeschreven
                if (frequencyline.get(vehicle) > 1) {
                    if (!horizontal.contains(Character.toString(vehicle))) {
                        horizontal = horizontal.concat(Character.toString(vehicle));}
                    // als een voertuig horizontaal zich op het zelfde y als het doel is het de
                    // rode auto
                    if (y == goaly) {goalcar = vehicle;}
                }
                else {
                    if (!vertical.contains(Character.toString(vehicle))) {
                        vertical = vertical.concat(Character.toString(vehicle));}
                }
            }
        }
        frequency.remove(emptychar);
        // de types voor lengte worden toegeschreven
        for (char vehicle : frequency.keySet()) {
            if (frequency.get(vehicle).equals(2)) {
                cars = cars.concat(Character.toString(vehicle));
            }
            else {trucks = trucks.concat(Character.toString(vehicle));}
        }
    }

    // deze methode kijkt of de meegegeven staat van het bord al is doorgenomen en als dit niet het geval
    // is zal deze worden toegeveogd aan de queue voor het doornemen van bord states
    public void addToQueue(String currentState, String previousState) {
        if (!previousStates.containsKey(currentState)) {
            previousStates.put(currentState, previousState);
            searchTree.add(currentState);
        }
    }

    // deze methode gaat vanaf de eindstand door de voorgaande bord toestanden totdat de eerste toestand
    // is gevonden en zo een lijst met de gemaakte moves gemaakt kan worden
    public void traceBack(String current) {
        String prev = previousStates.get(current);
        if (prev != null) {traceBack(prev);}
        moves.add(current);
    }

    // deze methode kijkt hoeveel beschikbare plekken zijn in de aangegeven richting
    // updown: + is omhoog, - omlaag
    // leftright: + is naar links, - naar rechts
    public int checkSpaces(String state, int x, int y, int updown, int leftright) {
        x -= leftright;
        y -= updown;
        int spaces = 0;
        while (GenerateBoard.locateCharacter(state, size, x, y) == emptychar) {
            x -= leftright;
            y -= updown;
            spaces ++;
        }
        return spaces;
    }

    // deze methode verplaatst een auto en voegt de nieuwe mogelijke opstellingen toe aan de queue
    public void moveVehicle(String state, int x, int y, int updown, int leftright, int iterations,
                                    boolean addEachMove) {
        StringBuilder mutableState = new StringBuilder(state);
        char carToMove = GenerateBoard.locateCharacter(state, size, x, y);
        int length = getLength(carToMove);
        for (int i=0; i < iterations; i++) {
            x -= leftright;
            y -= updown;
            mutableState.setCharAt(GenerateBoard.coordinates(size, x, y), carToMove);
            mutableState.setCharAt(GenerateBoard.coordinates(size, x + length * leftright,
                    y + length * updown), emptychar);
            addToQueue(mutableState.toString(), state);
            if (addEachMove) {state = mutableState.toString();}
        }
    }

    // deze methode verkent de mogelijk volgende toestanden van een bord
    // ook de mogelijkheid om elke verplaatsing bij te houden in plaats van elke beweging
    public void possibleStates(String currentState, boolean addEachMove) {
        for (int y=0; y<size; y++) {
            for (int x=0; x<size; x++) {
                if (GenerateBoard.locateCharacter(currentState, size, x, y) != emptychar) {
                    if (vertical.contains(Character.toString(GenerateBoard.
                            locateCharacter(currentState, size, x, y)))) {
                        int upspaces = checkSpaces(currentState, x, y, 1, 0);
                        int downspaces = checkSpaces(currentState, x, y, -1, 0);
                        moveVehicle(currentState, x, y, 1, 0, upspaces, addEachMove);
                        moveVehicle(currentState, x, y, -1, 0, downspaces, addEachMove);
                    }
                    else {
                        int leftspaces = checkSpaces(currentState, x, y, 0, 1);
                        int rightspaces = checkSpaces(currentState, x, y, 0, -1);
                        moveVehicle(currentState, x, y, 0, 1, leftspaces, addEachMove);
                        moveVehicle(currentState, x, y, 0, -1, rightspaces, addEachMove);
                    }
                }
            }
        }
    }

    // deze getter is gemaakt ten doeleinde van de tests
    public Queue<String> getSearchTree() {
        return searchTree;
    }

    // deze functie begint meestal bij de eindstand en gaat door alle mogelijke bord toestanden
    // om uiteindelijk bij de laatste weer te beginnen
    public ArrayList<String> getLongestPath() {
        addToQueue(startstate, null);
        String lastMove = startstate;
        while (!searchTree.isEmpty()) {
            String currentstate = searchTree.remove();
            possibleStates(currentstate, false);
            lastMove = currentstate;
        }
        traceBack(lastMove);
        return moves;
    }

    // deze methode kijkt wat de kortste route is naar de oplossingen en geeft een lijst terug
    // met de toestanden die daar tussen komen
    public ArrayList<String> getSolvingMoves() {
        addToQueue(startstate, null);
        boolean solved = false;
        while(!searchTree.isEmpty()) {
            String currentState = searchTree.remove();
            if (GenerateBoard.checkGoal(currentState, size, goalcar, goalx, goaly)) {
                solved = true;
                traceBack(currentState);
                break;
            }
            possibleStates(currentState, false);
        }
        if (solved) {return moves;}
        else {return new ArrayList<String>();}
    }

    // deze methode geeft vanuit een bord opstelling de volgende opstelling die
    // dichterbij het doel komt
    public String getHint() {
        ArrayList<String> allMoves = getSolvingMoves();
        return (allMoves.size() > 1) ? allMoves.get(1) : allMoves.get(0);
    }

}
