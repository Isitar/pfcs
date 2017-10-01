package ch.isitar.figures;

import java.awt.event.KeyEvent;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.PhysicStatics;
import ch.fhnw.pfcs.Point;

public class U2Riffle implements Figure, KeyFigure {
    private Point location;
    private double alpha;
    private double size;
    private FigureHolder figureHolder;
    private double speed = 10;

    public U2Riffle(Point location, double alpha, double size, FigureHolder figureHolder) {
        this.location = location;
        this.alpha = alpha;
        this.size = size;
        this.figureHolder = figureHolder;
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

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'S' || e.getKeyChar() == 's') {

            double v0y = speed * Math.sin(alpha);
            double v0x = speed * Math.cos(alpha);
            this.figureHolder.AddFigure(new WurfParabel(1, v0y, v0x, 0.01, 0, -PhysicStatics.g,
                    new Circle(0.1, new Point(this.location)), "", ""));
        }

        if (e.getKeyChar() == 'v') {
            speed += 5;

        }
        if (e.getKeyChar() == 'b') {
            speed -= 5;
        }
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
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
        mygl.drawArrays(gl, GL3.GL_LINE_LOOP);
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
    }
}
