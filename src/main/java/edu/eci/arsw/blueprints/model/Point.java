
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.model;

/**
 * Represents a point in a 2D coordinate system.
 * @author hcadavid
 */
public class Point {

    private int x;
    private int y;

    /**
     * Constructor for creating a point with given coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Default constructor.
     */
    public Point() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }



}
