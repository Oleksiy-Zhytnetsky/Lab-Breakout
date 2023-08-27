/*
 * Author: Oleksiy Zhytnetsky
 * Project: Lab2-Zhytnetskyi
 * File: Shapes.java
 * -----------------
 * This file implements the Shapes class, which exclusively contains Public Static methods.
 * This class is utilised as a collection of useful drawing methods and is meant to streamline
 * the process of creating graphics objects using the acm library.
 */

import acm.graphics.*;
import java.awt.Color;

/**
 * Implements the Shapes class, which exclusively contains Public Static methods. This class is
 * utilised as a collection of useful drawing methods and is meant to streamline the process of
 * creating graphics objects using the acm library.
 */
public class Shapes {
    /* Public Static Methods */

    /**
     * Creates a GRect object, which serves as a visual representation of GameObject objects,
     * such as "paddle" and "brick".
     *
     * @param centreX The X coordinate of the centre of the object.
     * @param centreY The Y coordinate of the centre of the object.
     * @param width   The width of the object in px.
     * @param height  The height of the object in px.
     * @param colour  The colour of the object.
     * @return rect; The GRect (visual) representation of a GameObject.
     */
    public static GRect createRect(double centreX, double centreY, double width,
                                   double height, Color colour) {
        GRect rect = new GRect(centreX - width / 2.0, centreY - height / 2.0,
                width, height);

        rect.setColor(colour);
        rect.setFillColor(colour);
        rect.setFilled(true);

        return rect;
    }

    /**
     * Creates a GOval object, which serves as visual representation of GameObject objects,
     * such as "ball".
     *
     * @param centreX The X coordinate of the centre of the object.
     * @param centreY The Y coordinate of the centre of the object.
     * @param radius  The radius of the object in px.
     * @param colour  The colour of the object.
     * @return circle; The GOval (visual) representation of a GameObject object.
     */
    public static GOval createCircle(double centreX, double centreY, double radius,
                                     Color colour) {
        GOval circle = new GOval(centreX - radius / 2.0, centreY - radius / 2.0,
                radius * 2, radius * 2.0);

        circle.setColor(colour);
        circle.setFillColor(colour);
        circle.setFilled(true);

        return circle;
    }
}
