package com.example.application.tests;

import com.example.application.algorithm.RushHour;

import junit.framework.TestCase;

import java.util.ArrayList;

public class RushHourTest extends TestCase {

    int boardSize;
    int goalX;
    int goalY;
    char emptyChar;
    String state = "333acdb22acdb.xxcd55e....fe.44.f1166";
    String moveState = "333acdb22acdbxx.cd55e....fe.44.f1166";
    RushHour rushBoard;


    public void setUp() throws Exception {
        super.setUp();
        boardSize = 6;
        goalX = 5;
        goalY = 2;
        emptyChar = '.';
        rushBoard = new RushHour(boardSize, state, emptyChar, goalX, goalY);
    }

    public void tearDown() throws Exception {
        rushBoard = null;
        state = null;
    }

    public void testGetLength() {
        char car = '3';
        assertEquals(3, rushBoard.getLength(car));
    }

    public void testCheckSpaces() {
        assertEquals(1, rushBoard.checkSpaces(state, 2, 2, 0, 1));
    }

    public void testMoveVehicle() {
        rushBoard.moveVehicle(state, 2, 2, 0, 1, 1, false);
        assertEquals(moveState, rushBoard.getSearchTree().remove());
    }

    public void testPossibleStates() {
        rushBoard.possibleStates(state, false);
        assertTrue(rushBoard.getSearchTree().contains(moveState));
    }

    public void testGetSolvingMoves() {
        ArrayList<String> solvingMoves = rushBoard.getSolvingMoves();
        assertEquals(50, solvingMoves.size());
    }

    public void testGetHint() {
        System.out.println(rushBoard.getHint());
        assertEquals(moveState, rushBoard.getHint());
    }
}