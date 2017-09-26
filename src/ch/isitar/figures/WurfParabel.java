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
    private ThrowableFigure f;
    private Point startingPoint;

    public WurfParabel(double m, double v0y, double v0x, double dt, double ax, double ay, ThrowableFigure f) {
        super();
        this.m = m;
        this.vx = v0x;
        this.vy = v0y;
        this.dt = dt;
        this.f = f;
        this.ax = ax;
        this.ay = ay;
        this.startingPoint = new Point(f.getPoint());
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        f.draw(gl, mygl);// , x, y, z);
    }

    @Override
    public void update() {
        f.getPoint().setY((float) (f.getPoint().getY() + vy * dt));
        vy += ay * dt;
        f.getPoint().setX((float) (f.getPoint().getX() + vx * dt));
        vx += ax * dt;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'r') {
            this.f.setPoint(new Point(startingPoint));
            this.vy += 10;
            this.ay = -this.ay;
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

}
