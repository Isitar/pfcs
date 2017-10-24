package ch.fhnw.pfcs;

public class Point implements Cloneable {

    private double x, y, z;

    public Point(float x, float y, float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(double x, double y, double z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return (float) x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public float getY() {
        return (float) y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public float getZ() {
        return (float) z;
    }

    public double getXDouble() {
        return x;
    }

    public double getYDouble() {
        return y;
    }

    public double getZDouble() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Point(Point p) {
        this.x = p.getX();
        this.y = p.getY();
        this.z = p.getZ();
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
