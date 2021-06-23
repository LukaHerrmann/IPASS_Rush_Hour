package com.example.application.tests;

import com.example.application.algorithm.GenerateBoard;

import junit.framework.TestCase;

import java.time.temporal.Temporal;

public class GenerateBoardTest extends TestCase {

    String state;
    char goalCar;
    int boardSize;
    int goalX;
    int goalY;
    char borderChar;
    char emptyChar;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        state = "...333.............................";
        goalCar = 'x';
        boardSize = 6;
        goalX = 5;
        goalY = 2;
        borderChar = '#';
        emptyChar = '.';
    }

    public void testCoordinates() {
        int stringIndex = GenerateBoard.coordinates(boardSize, 0, 3);
        assertEquals(18, stringIndex);
    }

    public void testLocateCharacter() {
        char locatedChar = GenerateBoard.locateCharacter(state, boardSize, 4, 0);
        assertEquals('3', locatedChar);
    }

    public void testLocateOutofBound() {
        char locatedChar = GenerateBoard.locateCharacter(state, boardSize, 8, 9);
        assertEquals(borderChar, locatedChar);
    }

    public void testPlaceAndRemove() {
        int x = 3;
        int y = 2;
        int length = 3;
        char vehicle = 'a';
        String newState = GenerateBoard.placeAndRemove(new StringBuilder(state), 6, x, y,
                0, 1, length, vehicle).toString();
        assertEquals(vehicle, GenerateBoard.locateCharacter(newState, boardSize, x, y));
        assertEquals(vehicle, GenerateBoard.locateCharacter(newState, boardSize, x, y+1));
        assertEquals(vehicle, GenerateBoard.locateCharacter(newState, boardSize, x, y+2));
    }

    public void testCheckGoal() {
        String goalState = "...333..........xx.................";
        assertTrue(GenerateBoard.checkGoal(goalState, boardSize, goalCar, goalX, goalY));
        assertFalse(GenerateBoard.checkGoal(state, boardSize, goalCar, goalX, goalY));
    }

    public void testValidPlace() {
        assertTrue(GenerateBoard.validPlace(new StringBuilder(state), boardSize, 3, 4, 2, 1,
                0, 2, false, emptyChar));
        assertFalse(GenerateBoard.validPlace(new StringBuilder(state), boardSize, 4, 0, 2, 0,
                1, 3, false, emptyChar));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        state = null;
        assertNull(state);
    }
}