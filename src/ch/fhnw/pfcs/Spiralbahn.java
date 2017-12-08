package ch.fhnw.pfcs;

//-------------   JOGL 3D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;

import ch.fhnw.pfcs.helpers.Dynamics;
import ch.fhnw.util.math.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;
import java.util.*;
import java.util.function.Consumer;

import javax.management.RuntimeErrorException;

public class Spiralbahn implements WindowListener, GLEventListener, KeyListener {

    // --------- globale Daten ---------------------------

    String windowTitle = this.getClass().getName();
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = MyShaders.vShader1; // Vertex-Shader mit Transformations-Matrizen
    String fShader = MyShaders.fShader0; // Fragment-Shader
    int maxVerts = 100 * 2048; // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas; // OpenGL Window
    MyGLBase1 mygl; // OpenGL Basis-Funktionen

    Stack<Mat4> matrixStack = new Stack<>();
    float elevation = 10;
    float azimut = 30;

    Mat4 M; // ModelView-Matrix
    Mat4 P; // Projektions-Matrix

    // -------- Viewing-Volume ---------------
    float left = -60, right = 60;
    float bottom, top;
    float near = -10, far = 1000;

    // LookAt-Parameter fuer Kamera-System
    Vec3 A = new Vec3(0, 0, 100); // Kamera-Pos. (Auge)
    Vec3 B = new Vec3(0, 0, 0); // Zielpunkt
    Vec3 up = new Vec3(0, 1, 0); // up-Richtung

    Vec3 bFeld = new Vec3(1, 1, -1);
    double q = -1; // e-ladung des teilchens
    double m = 1; // masse des teilchens

    private Consumer<double[]> lorenzF = (double[] x) -> {
        // Vec3 pos = new Vec3(x[0], x[1], x[2]);

        Vec3 v = new Vec3(x[3], x[4], x[5]);
        Vec3 F = v.cross(bFeld).scale((float) q); // (v x B) * q

        x[0] = v.x;
        x[1] = v.y;
        x[2] = v.z;

        x[3] = F.x / m;
        x[4] = F.y / m;
        x[5] = F.z / m;

    };

    // --------- Methoden ----------------------------------

    public Spiralbahn() // Konstruktor
    {
        createFrame();
    }

    void createFrame() // Fenster erzeugen
    {
        Frame f = new Frame(windowTitle);
        f.setSize(windowWidth, windowHeight);
        f.addWindowListener(this);
        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCaps = new GLCapabilities(glp);
        canvas = new GLCanvas(glCaps);
        canvas.addGLEventListener(this);
        f.add(canvas);
        f.setVisible(true);
        f.addKeyListener(this);
        canvas.addKeyListener(this);
    };

    // ------- Klothoide (Cornu'sche Spirale) ------------
    public void zeichneBahn(GL3 gl, double xStart, double yStart, double zStart, double phi, double ds,
            double kruemmung, double dKruemmung, int nPunkte) {
        double x = xStart, y = yStart, z = zStart;
        mygl.rewindBuffer(gl);
        mygl.putVertex((float) x, (float) y, (float) z);
        for (int i = 1; i < nPunkte; i++) {
            x += Math.cos(phi) * ds;
            y += Math.sin(phi) * ds;
            mygl.putVertex((float) x, (float) y, (float) z);
            phi += kruemmung * ds;
            kruemmung += dKruemmung * ds;
        }
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_LINE_STRIP);
    }

    // ---------- OpenGL-Events ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) // Initialisierung
    {
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
        System.out.println();
        gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glClearColor(0.2f, 0.2f, 1.0f, 1);
        int programId = MyShaders.initShaders(gl, vShader, fShader);
        mygl = new MyGLBase1(gl, programId, maxVerts);
        FPSAnimator anim = new FPSAnimator(canvas, 60, true); // Animations-Thread, 200 Frames/sek
        anim.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        M = Mat4.ID;
        mygl.setM(gl, M);
        Mat4 R1 = Mat4.rotate(-elevation, 1, 0, 0);
        Mat4 R2 = Mat4.rotate(azimut, 0, 1, 0);
        Mat4 R = R1.preMultiply(R2);
        M = Mat4.lookAt(R.transform(A), B, R.transform(up));
        mygl.setM(gl, M);
        mygl.setColor(1, 1, 1);
        mygl.drawAxis(gl, 100, 100, 100); // Koordinatenachsen
        mygl.setColor(0, 1, 1);
        // zeichneBahn(gl, -20, -20, 0, 0, 1, 0, 0.0005f, 500); // Spirale
        // zeichneBahn(mygl, gl, 100, 100, 10, 0.01, 5000); //

        // mygl.setColor(1, 0, 0);
        // zeichneKugelAufBahn(mygl, gl, 0.01, kugel1X); //

        mygl.setM(gl, M);
        mygl.setColor(0, 1, 0);
        zeichneBahn(mygl, gl, new Vec3(0, 0, 0), new Vec3(12, 4, 10), 0.1, 500);
        mygl.setColor(1, 0, 1);
        zeichneKugelAufBahn(mygl, gl, 0.1, Kugel1);
    }

    double[] Kugel1 = { 0, 0, 0, 12, 4, 10 };

    public void zeichneBahn(MyGLBase1 mygl, GL3 gl, Vec3 startP, Vec3 startV, double dt, int schritte) {
        double[] x = { startP.x, startP.y, startP.z, startV.x, startV.y, startV.z };
        mygl.rewindBuffer(gl);

        for (int i = 0; i <= schritte; ++i) {
            mygl.putVertex((float) x[0], (float) x[1], (float) x[2]);
            x = Dynamics.rungeKutta(x, dt, lorenzF);
        }
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_LINE_STRIP);
    }

    public void zeichneKugelAufBahn(MyGLBase1 mygl, GL3 gl, double dt, double[] kugelV) {
        mygl.rewindBuffer(gl);
        double[] temp = Dynamics.rungeKutta(kugelV, dt, lorenzF);
        kugelV[0] = temp[0];
        kugelV[1] = temp[1];
        kugelV[2] = temp[2];
        kugelV[3] = temp[3];
        kugelV[4] = temp[4];
        kugelV[5] = temp[5];
        Mat4 translated = Mat4.translate(new Vec3(kugelV[0], kugelV[1], kugelV[2]));
        mygl.setM(gl, M.postMultiply(translated));
        new RotKoerper(mygl).zeichneKugel(gl, 1f, 100, 100, true);
        mygl.setM(gl, M);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float aspect = (float) height / width;
        bottom = aspect * left;
        top = aspect * right;
        mygl.setP(gl, Mat4.ortho(left, right, bottom, top, near, far));
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    } // not needed

    // ----------- main-Methode ---------------------------

    public static void main(String[] args) {
        new Spiralbahn();
    }

    // --------- Window-Events --------------------

    public void windowClosing(WindowEvent e) {
        System.out.println("closing window");
        System.exit(0);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
        case KeyEvent.VK_UP:

            elevation++;
            break;
        case KeyEvent.VK_DOWN:

            elevation--;
            break;
        case KeyEvent.VK_LEFT:

            azimut--;
            break;
        case KeyEvent.VK_RIGHT:

            azimut++;
            break;

        }

        if (e.getKeyChar() == 'r') {
            Kugel1 = new double[] { 0, 0, 0, 12, 4, 10 };

        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
