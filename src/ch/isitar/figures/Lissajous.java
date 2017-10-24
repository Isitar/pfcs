package ch.isitar.figures;

import java.util.Random;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.pfcs.Point;

public class Lissajous implements Figure {

    private float phi = 1f;
    private float amplitudeX = 1;
    private float amplitudeY = 1;
    private float omegaX = 1;
    private float omegaY = 1;
    private int n = 1000;
    private float dt;
    private boolean increase;
    private Point color;

    public Lissajous(float amplitudeX, float amplitudeY, float omegaX, float omegaY, boolean increase) {
        super();
        this.amplitudeX = amplitudeX;
        this.amplitudeY = amplitudeY;
        this.omegaX = omegaX;
        this.omegaY = omegaY;
        this.dt = (float) ((2 * Math.PI) / (omegaX * n));
        this.increase = increase;
        Random rnd = new Random();
        color = new Point(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());

    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        mygl.setColor(color.getX(), color.getY(), color.getZ());
        for (int i = 0; i < n; i++) {
            mygl.putVertex(getX(i * dt, amplitudeX, omegaX), getY(i * dt, amplitudeY, omegaY, phi), 0);
        }

        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_LINE_LOOP);
    }

    @Override
    public void update() {
        if (increase) {
            phi += 0.01f;
        } else {
            phi -= 0.01f;
        }
    }

    private float getX(float t, float amplitudeX, float omegaX) {
        return (float) (amplitudeX * ((float) Math.cos(omegaX * t)));
    }

    private float getY(float t, float amplitudeY, float omegaY, float phi) {
        return (float) (amplitudeY * ((float) Math.sin(omegaY * t - phi)));
    }

}
