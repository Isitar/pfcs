package ch.fhnw.pfcs;

public class Point implements Cloneable {
    private float x, y, z;

    public Point(float x, float y, float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Point(Point p) {
        this.x = p.getX();
        this.y = p.getY();
        this.z = p.getZ();
    }
}
