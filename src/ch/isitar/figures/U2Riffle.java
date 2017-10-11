package ch.isitar.figures;

import java.awt.event.KeyEvent;
import java.util.Random;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.PhysicStatics;
import ch.fhnw.pfcs.Point;

public class U2Riffle implements Figure, KeyFigure {
    private Point location;
    private double alpha;
    private double size;
    private FigureHolder figureHolder;
    private double speed = 20;
    private Point shootingPoint;
    private Point color;

    private boolean includeAirResistance = true;

    public U2Riffle(Point location, double alpha, double size, FigureHolder figureHolder) {
        this.location = location;
        this.alpha = alpha;
        this.size = size;
        this.figureHolder = figureHolder;

        Random rnd = new Random();
        color = new Point(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
        updateShootingPoint();
    }

    private void updateShootingPoint() {
        float shootingX = (float) (location.getX() + size * Math.cos(alpha));
        float shootingY = (float) (location.getY() + size * Math.sin(alpha));
        this.shootingPoint = new Point(shootingX, shootingY, location.getZ());
    }

    private String keyPressedString = "";

    @Override
    public void keyPressed(KeyEvent arg0) {
        this.keyPressedString = KeyEvent.getKeyText(arg0.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        this.keyPressedString = "";

    }

    private void setSpeed(double newSpeed) {
        if (newSpeed <= 0)
            return;
        this.speed = newSpeed;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'S' || e.getKeyChar() == 's') {
            double v0x = speed * Math.cos(alpha);
            double v0y = speed * Math.sin(alpha);

            this.figureHolder.addFigure(new WurfParabel(1, v0y, v0x, 0.01, 0, -PhysicStatics.g,
                    new Circle(0.2, new Point(this.shootingPoint)), "", "", includeAirResistance));
        }

        if (e.getKeyChar() == 'T' || e.getKeyChar() == 't') {

            double delta = 0.174533; // 10°
            double beta = alpha - 2 * delta;
            for (int i = 0; i < 5; ++i) {
                double v0x = speed * Math.cos(beta + i * delta);
                double v0y = speed * Math.sin(beta + i * delta);

                this.figureHolder.addFigure(new WurfParabel(1, v0y, v0x, 0.01, 0, -PhysicStatics.g,
                        new Circle(0.1, new Point(this.shootingPoint)), "", "", includeAirResistance));

            }
        }

        if (e.getKeyChar() == 'v') {
            setSpeed(speed + 5);
        }

        if (e.getKeyChar() == 'b') {
            setSpeed(speed - 5);
        }
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        mygl.setColor(color.getX(), color.getY(), color.getZ());

        double width = size / 8;
        double beta = alpha - Math.PI / 2;
        float bottomLeftX = (float) (location.getX() + width * Math.cos(beta));
        float bottomLeftY = (float) (location.getY() + width * Math.sin(beta));

        // bottom left
        mygl.putVertex(bottomLeftX, bottomLeftY, location.getZ());
        // bottom right
        float bottomRightX = (float) (bottomLeftX + size * Math.cos(alpha));
        float bottomRightY = (float) (bottomLeftY + size * Math.sin(alpha));
        mygl.putVertex(bottomRightX, bottomRightY, location.getZ());

        beta += Math.PI;

        float topLeftX = (float) (this.location.getX() + width * Math.cos(beta));
        float topLeftY = (float) (this.location.getY() + width * Math.sin(beta));

        // top right

        float topRightX = (float) (topLeftX + size * Math.cos(alpha));
        float topRightY = (float) (topLeftY + size * Math.sin(alpha));

        mygl.putVertex(topRightX, topRightY, location.getZ());

        mygl.putVertex(topLeftX, topLeftY, location.getZ());
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);
    }

    private void setAlpha(double alpha) {
        if (alpha >= 0 && alpha <= Math.PI / 2) {
            this.alpha = alpha;
        }
    }

    @Override
    public void update() {
        double delta = 0.05;
        switch (this.keyPressedString) {
        case "Oben":
            setAlpha(alpha + delta);
            break;
        case "Unten":
            setAlpha(alpha - delta);
            break;
        default:
            // System.out.println(this.keyPressedString);
            break;
        }

        updateShootingPoint();
    }
}
