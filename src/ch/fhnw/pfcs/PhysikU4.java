package ch.fhnw.pfcs;

//  -------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;

import ch.fhnw.pfcs.helpers.Dynamics;
import ch.fhnw.util.math.*;
import ch.isitar.figures.Circle;
import ch.isitar.figures.Figure;
import ch.isitar.figures.KeyFigure;
import ch.isitar.figures.FigureHolder;

public class PhysikU4 implements WindowListener, GLEventListener, KeyListener, FigureHolder {

    // --------- globale Daten ---------------------------

    String windowTitle = "Physik U4";
    int windowWidth = 800;
    int windowHeight = 800;
    String vShader = MyShaders.vShader1; // Vertex-Shader
    String fShader = MyShaders.fShader0; // Fragment-Shader
    int maxVerts = 10 * 1024; // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas; // OpenGL Window
    MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen

    private List<Figure> figures = new CopyOnWriteArrayList<Figure>();
    private float boundaryX = 10;
    private float boundaryY = 10;
    @SuppressWarnings("unused")
    private float boundaryZ;
    private String pauseKey = " ";
    private boolean pause = false;

    private double cilinderRadius = 2;
    private int currTime = 0; 
    private int modulLength = 8;

    private Consumer<double[]> vectorFieldCilinder = (double[] x) -> {
        double x2 = Math.pow(x[0], 2);
        double y2 = Math.pow(x[1], 2);
        double r2 = Math.pow(cilinderRadius, 2);
        double newX = 1 + (r2 / (x2 + y2)) - (2 * r2 * x2) / Math.pow(x2 + y2, 2);
        double newY = -(2 * r2 * x[0] * x[1]) / Math.pow(x2 + y2, 2);
        x[0] = newX;
        x[1] = newY;
    };
    // --------- Methoden ----------------------------------

    public PhysikU4() {
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
        canvas.addKeyListener(this);
        f.add(canvas);
        f.setVisible(true);
        f.addKeyListener(this);
    };

    // ---------- OpenGL-Events ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) // Initialisierung
    {
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("OpenGl Version: " + gl.glGetString(GL.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION));
        System.out.println();
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1); // Hintergrundfarbe

        int programId = MyShaders.initShaders(gl, vShader, fShader); // Compile/Link Shader-Programme
        mygl = new MyGLBase1(gl, programId, maxVerts); // OpenGL Basis-Funktionen

        FPSAnimator anim = new FPSAnimator(canvas, 60, true);
        anim.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Bildschirm loeschen
        mygl.setColor(0, 1, 0); // Farbe der Vertices
        mygl.rewindBuffer(gl);
        new Circle(cilinderRadius, new Point(0, 0, 0), new Point(1, 0, 0)).draw(gl, mygl);
        mygl.rewindBuffer(gl);
        mygl.setColor(0, 1, 0);
        double deltaAbstand = 0.1;
        for (int i = 0; i < 100; ++i) {
            drawLine(gl, mygl, -boundaryX, deltaAbstand* i, 600, 0.1);
            drawLine(gl, mygl, -boundaryX, -deltaAbstand * i, 600, 0.1);
        }
        currTime++;
    }

    private void drawLine(GL3 gl, MyGLBase1 mygl, double startX, double startY, int steps, double dt) {
        mygl.rewindBuffer(gl);

        boolean draw = currTime == 0;
        if (draw) {
            mygl.setColor(0, 0, 0);
        } else {
            mygl.setColor(1, 0, 1);
        }
        mygl.putVertex((float) startX, (float) startY, 0);

        double[] x = { startX, startY };

        for (int i = 1; i <= steps; ++i) {
            x = Dynamics.rungeKutta(x, dt, vectorFieldCilinder);

            draw = (currTime - i) % modulLength > (modulLength / 2);

            if (draw) {
                mygl.setColor(0, 1, 0);
            } else {
                mygl.setColor(0, 0, 0);
            }

            mygl.putVertex((float) x[0], (float) x[1], 0);
        }
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_LINE_STRIP);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float xP = this.boundaryX;
        float zP = 100;
        float aspect = (float) height / width;
        float yP = xP * aspect;
        Mat4 P = Mat4.ortho(-xP, xP, -yP, yP, -zP, zP);
        mygl.setP(gl, P);

        this.boundaryY = yP;
        this.boundaryZ = zP;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    } // not needed

    // ----------- main-Methode ---------------------------

    public static void main(String[] args) {
        new PhysikU4();
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

    @Override
    public void keyPressed(KeyEvent e) {
        figures.stream().filter(f -> f instanceof KeyFigure).forEach(f -> ((KeyFigure) f).keyPressed(e));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        figures.stream().filter(f -> f instanceof KeyFigure).forEach(f -> ((KeyFigure) f).keyReleased(e));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        try {
            if (String.valueOf(e.getKeyChar()).toUpperCase().equals(pauseKey)) {
                pause = !pause;
            }
            if (e.getKeyChar() == 'i') {
                currTime++;
            }
            if (e.getKeyChar() == 'r') {
                cilinderRadius += 0.5;
            }
            if (e.getKeyChar() == 'R') {
                cilinderRadius -= 0.5;
            }
            figures.stream().filter(f -> f instanceof KeyFigure).forEach(f -> ((KeyFigure) f).keyTyped(e));
        } catch (ConcurrentModificationException ex) {
        }
    }

    @Override
    public void addFigure(Figure f) {
        if (this.figures.contains(f)) {
            return;
        }
        this.figures.add(f);
    }

    @Override
    public void removeFigure(Figure f) {
        if (!this.figures.contains(f)) {
            return;
        }
        this.figures.remove(f);
        System.out.println("executed");
    }

    @Override
    public List<Figure> getFigures() {

        return this.figures;

    }
}
