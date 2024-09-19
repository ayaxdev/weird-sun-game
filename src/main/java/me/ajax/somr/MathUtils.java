package me.ajax.somr;

public class MathUtils {

    // Before there's actually decent logic for checking intersections between textures in Dackel
    public static boolean intersects(double circle1X, double circle1Y, double circle1Radius,
                                     double circle2X, double circle2Y, double circle2Radius) {

        double distanceX = Math.abs(circle1X - circle2X);
        double distanceY = Math.abs(circle1Y - circle2Y);
        double distance = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        return distance <= circle1Radius + circle2Radius;
    }

}
