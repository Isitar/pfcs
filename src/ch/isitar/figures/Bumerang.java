/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package ch.isitar.figures;

import ch.fhnw.pfcs.MyGLBase1;
import ch.fhnw.util.math.Mat4;

import com.jogamp.opengl.GL3;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Janik
 */
public class Bumerang implements Figure, KeyListener {

    final double m = 1;
    double v0 = 60;
    final double dt = 500;
    double x = 0;
    double y = 0;
    double x0 = 42;
    double y0 = 0;
    double ax = 0;
    double ay = 0;
    double v0x = 0; // Anfangsgeschw.
    double v0y = 0.003; // Anfangsgeschw.
    double vx = v0x;
    double vy = v0y;

    double g = 9.81e-6; // erdbeschleudnigung
    double ybottom, ytop;
    double rB = 1; // radius Bumerang
    double rC = 7; // radius Flugbahn
    double phi = 20;
    double deltaPhi = 1;

    int counter = 0;

    public Bumerang() {
        this.x = x0;
        this.y = y0;
    }

    @Override
    public void update() {
        counter++;
        // System.out.println(counter + "(" + x + "," + y + ")");

        phi += deltaPhi;
        phi = phi % 360;
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        Mat4 mSave = mygl.getM();

        Mat4 M = Mat4.ID;

        M = M.postMultiply(Mat4.rotate((float) phi, 0, 1, 0));
        M = M.postMultiply(Mat4.translate((float) rC, 0, 0));
        M = M.postMultiply(Mat4.rotate((float) -20, 0, 0, 1));
        M = M.postMultiply(Mat4.rotate((float) 90, 0, 1, 0));
        M = M.preMultiply(Mat4.rotate((float) -20, 0, 0, 1));
        M = M.postMultiply(Mat4.rotate((float) (10 * phi + 10 * deltaPhi), 0, 0, 1));
        mygl.setM(gl, M);

        drawCircle(gl, mygl, (float) rB, 0, 0, 0);
        mygl.setM(gl, mSave);
    }

    public void drawCircle(GL3 gl, MyGLBase1 mygl, float r, float xm, float ym, float z) {
        int nPkte = 100;
        mygl.rewindBuffer(gl);
        double phi = 2 * Math.PI / nPkte;
        double x;
        double y;
        mygl.setColor(1f, 0.9f, 0f);
        mygl.putVertex(xm, ym, 0);

        for (int i = 0; i <= nPkte; i++) {
            mygl.setColor(1f, 0.9f - (i * 0.01f), 0f);
            x = xm + r * Math.cos(i * phi);
            y = ym + r * Math.sin(i * phi);
            mygl.putVertex((float) x, (float) y, z);
        }

        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLE_FAN);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyChar()) {
        case 'd':
            deltaPhi -= 1;
            deltaPhi = Math.max(deltaPhi, 0);
            System.out.println("new deltaPhi " + deltaPhi);
            break;
        case 'u':
            System.out.println("new deltaPhi " + deltaPhi);
            deltaPhi += 1;
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
