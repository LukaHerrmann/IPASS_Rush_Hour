package com.example.application.objects;

public class Vehicle {

    private final char id;
    private final int color;
    private final int length;
    private final boolean isGoalCar;
    private float xDown = 0, yDown = 0;
    private float relX = 0, relY = 0;

    public Vehicle(char id, int color, int length, boolean isGoalCar) {
        this.id = id;
        this.color = color;
        this.length = length;
        this.isGoalCar = isGoalCar;
    }

    public void setUp() {
        xDown = 0;
        yDown = 0;
        relX = 0;
        relY = 0;
    }

    public char getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public int getLength() {
        return length;
    }

    public boolean isGoalCar() {
        return isGoalCar;
    }

    public float getxDown() {
        return xDown;
    }

    public void setxDown(float xDown) {
        this.xDown = xDown;
    }

    public void addxDown(float number) {
        xDown += number;
    }

    public float getyDown() {
        return yDown;
    }

    public void setyDown(float yDown) {
        this.yDown = yDown;
    }

    public void addyDown(float number) {
        yDown += number;
    }

    public float getRelX() {
        return relX;
    }

    public void setRelX(float relX) {
        this.relX = relX;
    }

    public void addRelX(float number) {
        relX += number;
    }

    public float getRelY() {
        return relY;
    }

    public void setRelY(float relY) {
        this.relY = relY;
    }

    public void addRelY(float number) {
        relY += number;
    }
}
