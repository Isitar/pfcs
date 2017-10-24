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
    double v0 = 15;
    final double dt = 500;
    double x = 0;
    double y = 0;
    double x0 = 42;
    double y0 = 0;
    double ax = 0;
    double ay = 0;
    double v0x = 0;   // Anfangsgeschw.
    double v0y = 0.003;   // Anfangsgeschw.
    double vx = v0x;
    double vy = v0y;

    double g = 9.81e-6; // erdbeschleudnigung
    double ybottom, ytop;
    double rB = 1; // radius Bumerang
    double rC = 7; // radius Flugbahn
    double phi = 20;

    int counter = 0;

    public Bumerang() {
        this.x = x0;
        this.y = y0;
    }

    @Override
    public void update() {
        counter++;
        System.out.println("" + counter + "(" + x + "," + y + ")");

        //double r = Math.sqrt(x * x + y * y);
        //double r3 = r * r * r;
        //ax = -GM * x / r3;
        //ay = -GM * y / r3;
        phi += 1;
        phi = phi % 360;

        /*vx += ax * dt;
        vy += ay * dt;

        x = x + vx * dt;
        y = y + vy * dt;*/
    }

    @Override
    public void draw(GL3 gl, MyGLBase1 mygl) {
        Mat4 mSave = mygl.getM();

        Mat4 M = Mat4.ID;
                
        // rotation für kreisbewegung
        M = M.postMultiply(Mat4.rotate((float) phi, 0, 1, 0));
        
        // versetzen um Radius der Kreisbahn
        M = M.postMultiply(Mat4.translate((float) rC, 0, 0));
        
         // rotation, damit der Bumerang nicht genau zum Bahnmittelpunkt schaut
        M = M.postMultiply(Mat4.rotate((float) -20, 0, 0, 1));
        
        // Senkrecht stellen des Bumerang
        M = M.postMultiply(Mat4.rotate((float) 90, 0, 1, 0));
        
        // Drehung der waagrechten Umlaufbahn
        M = M.preMultiply(Mat4.rotate((float) -20, 0, 0, 1));

        mygl.setM(gl, M);

        mygl.setColor(0.8f, 0.8f, 0.8f);
        /*for (float[] shot : shots) {
            drawSpear(gl, mygl, shot[0], shot[1], 1f);
        }*/

        //drawSpear(gl, mygl, 1.2f, 0.2f, 0.1f);
        drawCircle(gl, mygl, (float) rB, 0, 0, 0);

        //drawGun(gl, mygl);
        mygl.setM(gl, mSave);
    }

    public void drawCircle(GL3 gl, MyGLBase1 mygl, float r, float xm, float ym, float z) {
        int nPkte = 100;
        mygl.rewindBuffer(gl);
        double phi = 2 * Math.PI / nPkte;
        double x;
        double y;
        mygl.putVertex(xm, ym, 0);
        for (int i = 0; i <= nPkte; i++) {
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
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                //directionSpear += Math.PI / 90;
                break;
            case KeyEvent.VK_DOWN:
                //directionSpear -= Math.PI / 90;
                break;
            case KeyEvent.VK_SUBTRACT:
                v0 -= 1;
                v0 = Math.max(v0, 0);
                break;
            case KeyEvent.VK_ADD:
                v0 += 1;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
