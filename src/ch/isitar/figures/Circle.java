package ch.isitar.figures;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;

public class Circle implements Figure {
    private double radius = 0;
    private float x;
    private float y;
    private float z;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
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

    public Circle(double radius, float x, float y, float z) {
        super();
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {

        int numberOfTriangles = 100;
        mygl.putVertex(x, y, z);
        double phi = 2 * Math.PI / numberOfTriangles;
        for (int i = 0; i <= numberOfTriangles; i++) {
            mygl.putVertex((float) (x + radius * Math.cos(i * phi)), (float) (y + radius * Math.sin(i * phi)), z);
        }

        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

}
