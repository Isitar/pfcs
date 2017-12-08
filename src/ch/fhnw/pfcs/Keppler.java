package ch.fhnw.pfcs;

//  -------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;
import ch.isitar.figures.Circle;
import ch.isitar.figures.Figure;
import ch.isitar.figures.KeyFigure;
import ch.isitar.figures.Spear;

public class Keppler implements WindowListener, GLEventListener, KeyListener {

    // --------- globale Daten ---------------------------

    String windowTitle = getClass().getName();
    int windowWidth = 800;
    int windowHeight = 800;
    String vShader = MyShaders.vShader1; // Vertex-Shader
    String fShader = MyShaders.fShader0; // Fragment-Shader
    int maxVerts = 10 * 1024; // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas; // OpenGL Window
    MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen

    private ArrayList<Figure> figures;
    private float boundaryY = 60;
    private float boundaryX = 60;
    private float boundaryZ;

    private double x0 = 42;
    private double v0x = 0, v0y = Math.sqrt(GM/x0);
    private double vx = v0x, vy = v0y;
    private final double dt = 5000;
    private int counter = 0;

    // in this context, we are in 1/1000
    private final static double g = 9.81e-6;
    private final static double earthRadius = 6.378;
    private final static double GM = g * earthRadius * earthRadius;
    private final static double moonRadius = 1.737;
    private final static int speedFactor = 10;

    private ArrayList<Point> lineArr = new ArrayList<>();

    private Point SatelitePoint = new Point(x0, 0, 0);

    // --------- Methoden ----------------------------------

    public Keppler() // Konstruktor
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

        figures = new ArrayList<>();
        figures.add(new Circle(6.378f, new Point(0, 0, 0), new Point(0.1f, 0.8f, 1)));

        FPSAnimator anim = new FPSAnimator(canvas, speedFactor * 60, true);
        anim.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        counter++;
        System.out.println("" + counter + ": (" + SatelitePoint.getX() + ";" + SatelitePoint.getY() + ")");

        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Bildschirm loeschen
        mygl.setColor(0, 1, 0); // Farbe der Vertices

        figures.forEach(f -> f.update());

        SatelitePoint.setX(SatelitePoint.getXDouble() + vx * dt);
        SatelitePoint.setY(SatelitePoint.getYDouble() + vy * dt);

        double ax = -GM * SatelitePoint.getXDouble() / Math.pow(SatelitePoint.getLength(), 3);
        vx += ax * dt;
        double ay = -GM * SatelitePoint.getYDouble() / Math.pow(SatelitePoint.getLength(), 3);
        vy += ay * dt;

        figures.forEach(f -> {
            mygl.rewindBuffer(gl); // Vertex-Buffer zuruecksetzen
            f.draw(gl, mygl);
        });

        lineArr.add(new Point(SatelitePoint));
        drawLineArr(gl);

        Circle c = new Circle(moonRadius, new Point(SatelitePoint), new Point(1, 1, 1));
        mygl.rewindBuffer(gl);
        c.draw(gl, mygl);

    }

    private void drawLineArr(GL3 gl) {
        mygl.setColor(0, 1, 0); // Farbe der Vertices
        mygl.rewindBuffer(gl);
        lineArr.forEach(p -> {
            mygl.putVertex(p.getX(), p.getY(), p.getZ());
        });
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL.GL_LINE_STRIP);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);

        float zP = 100;
        float aspect = (float) height / width;
        float yP = boundaryX * aspect;
        Mat4 P = Mat4.ortho(-boundaryX, boundaryX, -yP, yP, -zP, zP);
        mygl.setP(gl, P);

        this.boundaryY = yP;
        this.boundaryZ = zP;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    } // not needed

    // ----------- main-Methode ---------------------------

    public static void main(String[] args) {
        new Keppler();
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
        figures.stream().filter(f -> f instanceof KeyFigure).forEach(f -> ((KeyFigure) f).keyTyped(e));

        if (e.getKeyChar() == 'z') {
            boundaryX += 10;
        }

    }

}
