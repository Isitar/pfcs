package ch.isitar.figures;

import java.util.Random;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.PhysicStatics;
import ch.fhnw.pfcs.Point;

public class Circle implements ThrowableFigure {
    private double radius = 0;
    private Point p;
    private Point color;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Circle(double radius, float x, float y, float z) {
        this(radius, new Point(x, y, z));
    }

    public Circle(double radius, Point p) {
        super();
        this.radius = radius;
        this.p = p;

        Random rnd = new Random();
        color = new Point(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        mygl.setColor(color.getX(), color.getY(), color.getZ());
        // draw(gl, mygl, x, y, z);
        int numberOfTriangles = 100;

        mygl.putVertex(p.getX(), p.getY(), p.getZ());
        double phi = 2 * Math.PI / numberOfTriangles;
        for (int i = 0; i <= numberOfTriangles; i++) {
            mygl.putVertex((float) (p.getX() + radius * Math.cos(i * phi)),
                    (float) (p.getY() + radius * Math.sin(i * phi)), p.getZ());
        }

        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public Point getPoint() {
        return p;
    }

    @Override
    public void setPoint(Point p) {
        this.p = p;
    }

    @Override
    public double getC() {
        return PhysicStatics.airDensity / 2 * PhysicStatics.bulletC * Math.pow(radius, 2) * Math.PI;
    }
}
