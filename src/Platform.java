/*
 * Author: Oleksiy Zhytnetsky
 * Project: Lab2-Zhytnetskyi
 * File: Platform.java
 * -------------------
 * This file implements the Platform class, which is used to create objects such as bricks
 * and the paddle.
 */

import acm.graphics.*;
import java.awt.Color;

/**
 * Implements the Platform class, which is used to create objects such as bricks and the paddle.
 */
public class Platform extends GameObject {
    /* Constructors */

    /**
     * A complete constructor for Platform objects.
     *
     * @param centreX The X coordinate of the centre of the object.
     * @param centreY The Y coordinate of the centre of the object.
     * @param width   The width of the object in px.
     * @param height  The height of the object in px.
     * @param dx      The speed of movement of the object along the X-Axis.
     * @param dy      The speed of movement of the object along the Y-Axis.
     * @param colour  The colour of the object.
     */
    public Platform(double centreX, double centreY, double width, double height, double dx,
                    double dy, Color colour) {
        super(centreX, centreY, dx, dy, colour);

        this.width = width;
        this.height = height;
        drawPlatform();
    }

    /* Private Methods */

    /**
     * Creates an image of the Platform object using private/inherited fields and adds it
     * onto the canvas
     */
    private void drawPlatform() {
        /* The visual representation (image) of the brick/paddle */
        GRect image = Shapes.createRect(this.getCentreX(), this.getCentreY(), this.width,
                this.height, this.getColour());
        add(image);
    }

    /* Getters and Setters */

    /**
     * The getter for this.width
     *
     * @return this.width; The width of the object in px
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * The getter for this.height
     *
     * @return this.height; The height of the object in px
     */
    @Override
    public double getHeight() {
        return height;
    }

    /* Private Instance Variables */

    /** The width of the object in px */
    private final double width;

    /** The height of the object in px */
    private final double height;
}
