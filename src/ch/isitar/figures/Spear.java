package ch.isitar.figures;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.PhysicStatics;
import ch.fhnw.pfcs.Point;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public class Spear implements ThrowableFigure {
    private double length;
    private double width;
    private Point location;

    private double m;
    private double vx, vy;
    private double dt = 0.01; // Zeitschritt
    private double ax, ay;
    private double v0y, v0x;

    public Spear(double length, double width, Point location) {
        super();
        this.length = length;
        this.width = width;
        this.location = location;
        m = 1;
        vy = 20;
        vx = 10;
        ax = 0;
        ay = -PhysicStatics.g;
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        Mat4 M = Mat4.ID;
        M = Mat4.translate(new Vec3(location.getX(), location.getY(), 0));
        float alpha = (float) Math.toDegrees(Math.atan((vy / vx)));
        M = M.postMultiply(Mat4.rotate(alpha, 0, 0, 1));
        mygl.setM(gl, M);
        float halfLength = (float) (length / 2 - length / 20);
        float halfWidth = (float) (width / 2);
        mygl.putVertex(location.getX() - halfLength, location.getY() - halfWidth, location.getZ());
        mygl.putVertex(location.getX() + halfLength, location.getY() - halfWidth, location.getZ());
        mygl.putVertex(location.getX() + (float) (length / 2), location.getY(), location.getZ());
        mygl.putVertex(location.getX() + halfLength, location.getY() + halfWidth, location.getZ());
        mygl.putVertex(location.getX() - halfLength, location.getY() + halfWidth, location.getZ());

        mygl.putVertex(location.getX() - (float) (length / 2), location.getY(), location.getZ());
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);

    }

    @Override
    public void update() {
        getPoint().setY((float) (getPoint().getY() + vy * dt));
        vy += ay * dt;
        getPoint().setX((float) (getPoint().getX() + vx * dt));
        vx += ax * dt;
    }

    @Override
    public Point getPoint() {
        return location;
    }

    @Override
    public void setPoint(Point p) {
        location = new Point(p);

    }

}
