package ch.isitar.figures;

import com.jogamp.opengl.GL3;

import ch.fhnw.pfcs.MyGLBase1;

public interface Figure {
    public void draw(GL3 gl, MyGLBase1 mygl);

    public void update();
}
