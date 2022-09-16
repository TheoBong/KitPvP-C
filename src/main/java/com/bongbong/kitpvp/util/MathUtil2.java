package com.bongbong.kitpvp.util;

import org.bukkit.util.Vector;

public class MathUtil2 {
    public static Vector rotateVector(Vector vector, double whatAngle) {
        double sin = Math.sin(Math.toRadians(whatAngle));
        double cos = Math.cos(Math.toRadians(whatAngle));
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;
     
        return vector.setX(x).setZ(z);
    }
}
