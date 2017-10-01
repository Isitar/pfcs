package ch.isitar.figures;

import java.awt.event.KeyEvent;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.Point;

public class U2Blech implements Figure, KeyFigure, ThrowableFigure {

    private Point location;
    private Point startingLocation;
    private float width;
    private float height;

    public U2Blech(Point location, float width, float height) {
        super();
        this.location = location;
        this.startingLocation = new Point(location);
        this.width = width;
        this.height = height;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
            this.location = new Point(startingLocation);
        }

    }

    @Override
    public Point getPoint() {
        // TODO Auto-generated method stub
        return this.location;
    }

    @Override
    public void setPoint(Point p) {
        this.location = new Point(p);

    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        float x = this.getPoint().getX();
        float y = this.getPoint().getY();
        float z = this.getPoint().getZ();
        mygl.putVertex(x, y, z);
        mygl.putVertex(x + width, y, z);
        mygl.putVertex(x + width, y - height, z);
        mygl.putVertex(x, y - height, z);
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_LINE_LOOP);
    }

    @Override
    public void update() {

    }

}
