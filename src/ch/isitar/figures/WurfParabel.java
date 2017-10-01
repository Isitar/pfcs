package ch.isitar.figures;

import java.awt.event.KeyEvent;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.PhysicStatics;
import ch.fhnw.pfcs.Point;

public class WurfParabel implements Figure, KeyFigure {
    private double m;
    private double vx, vy;
    private double dt = 0.01; // Zeitschritt
    private double ax, ay;
    private double v0y, v0x;
    private ThrowableFigure f;
    private Point startingPoint;
    private String resetKey = "r";
    private String startKey = "s";
    private boolean running = false;

    public WurfParabel(double m, double v0y, double v0x, double dt, double ax, double ay, ThrowableFigure f,
            String resetKey, String startKey) {
        super();
        this.m = m;
        this.vx = v0x;
        this.vy = v0y;
        this.v0y = v0y;
        this.v0x = v0x;
        this.dt = dt;
        this.f = f;
        this.ax = ax;
        this.ay = ay;
        this.startingPoint = new Point(f.getPoint());
        this.resetKey = resetKey;
        this.startKey = startKey;
        if (startKey.equals("")) {
            this.running = true;
        }
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        f.draw(gl, mygl);// , x, y, z);
    }

    @Override
    public void update() {
        if (!running) {
            return;
        }
        f.getPoint().setY((float) (f.getPoint().getY() + vy * dt));
        vy += ay * dt;
        f.getPoint().setX((float) (f.getPoint().getX() + vx * dt));
        vx += ax * dt;
        // System.out.println("updated to " + f.getPoint().getX() + " " + f.getPoint().getY());
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (String.valueOf(e.getKeyChar()).toUpperCase().equals(resetKey.toUpperCase())) {
            this.f.setPoint(new Point(startingPoint));
            this.vy = v0y;
            this.vx = v0x;
            this.running = this.startKey.equals("");
        }

        if (String.valueOf(e.getKeyChar()).toUpperCase().equals(startKey.toUpperCase())) {
            this.running = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getAx() {
        return ax;
    }

    public void setAx(double ax) {
        this.ax = ax;
    }

    public double getAy() {
        return ay;
    }

    public void setAy(double ay) {
        this.ay = ay;
    }

}
