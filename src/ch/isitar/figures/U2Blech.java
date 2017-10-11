package ch.isitar.figures;

import java.awt.event.KeyEvent;
import java.util.Random;
import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.PhysicStatics;
import ch.fhnw.pfcs.Point;

public class U2Blech implements Figure, KeyFigure, ThrowableFigure {

    private Point location;
    private Point startingLocation;
    private float width;
    private float height;
    private FigureHolder figureHolder;
    private Point color;
    private boolean hit = false;

    public U2Blech(Point location, float width, float height, FigureHolder fh) {
        super();
        this.location = location;
        this.startingLocation = new Point(location);
        this.width = width;
        this.height = height;
        this.figureHolder = fh;

        Random rnd = new Random();
        color = new Point(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
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
            this.hit = false;
        }
        System.out.println("keytyped");
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
        if (hit)
            return;
        mygl.setColor(color.getX(), color.getY(), color.getZ());
        float x = this.getPoint().getX();
        float y = this.getPoint().getY();
        float z = this.getPoint().getZ();
        mygl.putVertex(x, y, z);
        mygl.putVertex(x + width, y, z);
        mygl.putVertex(x, y - height, z);
        mygl.putVertex(x + width, y - height, z);

        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLE_STRIP);
    }

    @Override
    public void update() {

        checkCollisionWithCircle();

    }

    private void checkCollisionWithCircle() {
        if (hit) {
            return;
        }
        if (figureHolder.getFigures().stream().filter(f -> (f instanceof WurfParabel)).map(f -> ((WurfParabel) f))
                .filter(wp -> wp.getFigureClass().getSimpleName().equals("Circle"))
                .map(wp -> (Circle) wp.getInternalFigure()).filter(c -> {
                    double circleRightX = c.getPoint().getX() + c.getRadius();
                    double circleBottomY = c.getPoint().getY() - c.getRadius();
                    double circleTopY = c.getPoint().getY() + c.getRadius();
                    if ((circleRightX >= getPoint().getX()) // && circleRightX <= getPoint().getX() + width
                            && circleBottomY <= getPoint().getY() && circleTopY >= getPoint().getY() - height) {
                        return true;
                    }
                    // System.out.println(
                    // "circle xRight: " + circleRightX + " circle ytop: " + circleTopY + " circle bottom y: "
                    // + circleBottomY + "rectangle leftx: " + getPoint().getX() + " rectangle topy: "
                    // + getPoint().getY() + " rectangle bottom y " + (getPoint().getY() - height));
                    return false;
                }).count() > 0) {
            figureHolder.removeFigure(this);
            if (!hit) {
                figureHolder.addFigure(new WurfParabel(0.5, 2, -4, 0.01, 0, -PhysicStatics.g,
                        new Circle(height / 2, new Point(getPoint())), "", ""));
                figureHolder.addFigure(new WurfParabel(0.5, 0, -4, 0.01, 0, -PhysicStatics.g,
                        new Circle(height / 2, new Point(getPoint())), "", ""));
                hit = true;
            }

        }

    }

    @Override
    public double getC() {
        return PhysicStatics.airDensity / 2 * PhysicStatics.blechC * width;
    }

}
