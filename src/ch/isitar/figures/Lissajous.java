package ch.isitar.figures;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;

public class Lissajous implements Figure {

    private float phi = 1f;
    private float amplitudeX = 1;
    private float amplitudeY = 1;
    private float omegaX = 1;
    private float omegaY = 1;
    private int n = 100;
    private float t;
    private boolean increase;

    public Lissajous(float amplitudeX, float amplitudeY, float omegaX, float omegaY, boolean increase) {
        super();
        this.amplitudeX = amplitudeX;
        this.amplitudeY = amplitudeY;
        this.omegaX = omegaX;
        this.omegaY = omegaY;
        this.t = (float) ((2 * Math.PI) / (omegaX * n));
        this.increase = increase;
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {

        for (int i = 0; i < n; i++) {
            mygl.putVertex(getX(i * t, amplitudeX, omegaX), getY(i * t, amplitudeY, omegaY, phi), 0);
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