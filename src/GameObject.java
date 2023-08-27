/*
 * Author: Oleksiy Zhytnetsky
 * Project: Lab2-Zhytnetskyi
 * File: GameObject.java
 * ---------------------
 * This file implements the GameObject class, which is the parent class for both Ball and
 * Platform. This class mostly includes generic fields and methods that are often used by the
 * objects of both of its children.
 */

import acm.graphics.*;
import java.awt.Color;

/**
 * Implements the GameObject class, which is the parent class for both Ball and Platform.
 * This class mostly includes generic fields and methods that are often used by the objects
 * of both of its children.
 */
public class GameObject extends GCompound {
    /* Constructors */

    /**
     * A complete constructor for GameObject objects.
     *
     * @param centreX The X coordinate of the centre of the object.
     * @param centreY The Y coordinate of the centre of the object.
     * @param dx      The speed of movement of the object along the X-Axis.
     * @param dy      The speed of movement of the object along the Y-Axis.
     * @param colour  The colour of the object.
     */
    public GameObject(double centreX, double centreY, double dx, double dy, Color colour) {
        this.centreX = centreX;
        this.centreY = centreY;
        this.dx = dx;
        this.dy = dy;
        this.colour = colour;
    }

    /* Public Methods */

    /**
     * Increments this.centreX and this.centreY fields of the object by this.dx and this.dy
     * respectively.
     * This method should to be invoked right after the object has moved.
     */
    public void updateLocation() {
        this.centreX += this.dx;
        this.centreY += this.dy;
    }

    /* Getters and Setters */

    /**
     * The getter for this.centreX
     *
     * @return this.centreX; The X coordinate of the centre of the object
     */
    public double getCentreX() {
        return this.centreX;
    }

    /**
     * The setter for this.centreX
     *
     * @param centreX The X coordinate of the centre of the object
     */
    public void setCentreX(double centreX) {
        this.centreX = centreX;
    }

    /**
     * The getter for this.centreY
     *
     * @return this.centreY; The Y coordinate of the centre of the object
     */
    public double getCentreY() {
        return centreY;
    }

    /**
     * The setter for this.centreY
     *
     * @param centreY The Y coordinate of the centre of the object
     */
    public void setCentreY(double centreY) {
        this.centreY = centreY;
    }

    /**
     * The getter for this.dx
     *
     * @return this.dx; The speed of movement of the object along the X-Axis.
     */
    public double getDx() {
        return dx;
    }

    /**
     * The setter for this.dx
     *
     * @param dx The speed of movement of the object along the X-Axis
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * The getter for this.dy
     *
     * @return this.dy; The speed of movement of the object along the Y-Axis
     */
    public double getDy() {
        return dy;
    }

    /**
     * The setter for this.dy
     *
     * @param dy The speed of movement of the object along the Y-Axis
     */
    public void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * The getter for this.colour
     *
     * @return this.colour; The colour of the object
     */
    public Color getColour() {
        return colour;
    }

    /* Private Instance Variables */

    /** The X coordinate of the centre of the object */
    private double centreX;

    /** The Y coordinate of the centre of the object */
    private double centreY;

    /** The speed of movement of the object along the X-Axis */
    private double dx;

    /** The speed of movement of the object along the Y-Axis */
    private double dy;

    /** The colour of the object */
    private final Color colour;
}
