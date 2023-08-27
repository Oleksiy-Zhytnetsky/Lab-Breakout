/*
 * Author: Oleksiy Zhytnetsky
 * Project: Lab2-Zhytnetskyi
 * File: Ball.java
 * ---------------
 * This file implements the Ball class, which is used to create the ball.
 */

import acm.graphics.*;
import java.awt.Color;

/**
 * Implements the Ball class, which is used to create the ball
 */
public class Ball extends GameObject {
    /* Constructors */

    /**
     * A complete constructor for Ball objects.
     *
     * @param centreX The X coordinate of the centre of the object.
     * @param centreY The Y coordinate of the centre of the object.
     * @param radius  The radius of the ball in px.
     * @param dx      The speed of movement of the object along the X-Axis.
     * @param dy      The speed of movement of the object along the Y-Axis.
     * @param colour  The colour of the object.
     */
    public Ball(double centreX, double centreY, double radius, double dx, double dy,
                Color colour) {
        super(centreX, centreY, dx, dy, colour);

        this.radius = radius;
        drawBall();
    }

    /* Private Methods */

    /**
     * Creates an image of the Ball object using private/inherited fields and adds it onto
     * the canvas.
     */
    private void drawBall() {
        /* The visual representation (image) of the ball */
        GOval image = Shapes.createCircle(this.getCentreX(), this.getCentreY(), this.radius,
                this.getColour());
        add(image);
    }

    /* Getters and Setters */

    /**
     * The getter for this.radius
     *
     * @return this.radius; The radius of the ball in px
     */
    public double getRadius() {
        return radius;
    }

    /* Private Instance Variables */

    /** The radius of the ball in px */
    private final double radius;
}
