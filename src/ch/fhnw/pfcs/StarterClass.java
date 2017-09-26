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
import ch.isitar.figures.Lissajous;
import ch.isitar.figures.ThrowableFigure;
import ch.isitar.figures.WurfParabel;

public class StarterClass implements WindowListener, GLEventListener, KeyListener {

    // --------- globale Daten ---------------------------

    String windowTitle = "Lissajous";
    int windowWidth = 800;
    int windowHeight = 800;
    String vShader = MyShaders.vShader1; // Vertex-Shader
    String fShader = MyShaders.fShader0; // Fragment-Shader
    int maxVerts = 10 * 1024; // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas; // OpenGL Window
    MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen

    private ArrayList<Figure> figures;
    private float boundaryY;
    private float boundaryX;
    private float boundaryZ;
    // --------- Methoden ----------------------------------

    public StarterClass() // Konstruktor
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
        System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
        System.out.println();
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1); // Hintergrundfarbe

        int programId = MyShaders.initShaders(gl, vShader, fShader); // Compile/Link Shader-Programme
        mygl = new MyGLBase1(gl, programId, maxVerts); // OpenGL Basis-Funktionen

        figures = new ArrayList<>();
        // for (int i = 0; i < 40; ++i) {
        // figures.add(new Lissajous(1f, 1 + i * 0.1f, 1 + i * 0.1f, 1f, true));
        // }

        figures.add(new Lissajous(1f, 1f, 1f, 1f, true));
        // figures.add(new Circle(0.5f, 0, 0, 0));

        figures.add(new WurfParabel(0.1f, 3, 5, 0.04, 0, -PhysicStatics.g, new Circle(0.3, -8f, -6f, 0)));
        figures.add(new WurfParabel(0.1f, 4, 6, 0.04, 0, -PhysicStatics.g, new Circle(0.3, -8f, -6f, 0)));
        figures.add(new WurfParabel(0.1f, 5, 7, 0.04, 0, -PhysicStatics.g, new Circle(0.3, -8f, -6f, 0)));
        figures.add(new WurfParabel(0.1f, 6, 8, 0.04, 0, -PhysicStatics.g, new Circle(0.3, -8f, -6f, 0)));
        FPSAnimator anim = new FPSAnimator(canvas, 60, true);
        anim.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Bildschirm loeschen
        mygl.setColor(0, 1, 0); // Farbe der Vertices

        figures.forEach(f -> f.update());
        figures.forEach(f -> {
            mygl.rewindBuffer(gl); // Vertex-Buffer zuruecksetzen
            f.draw(gl, mygl);
        });

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
        float xP = 10;
        float zP = 100;
        float aspect = (float) height / width;
        float yP = xP * aspect;
        Mat4 P = Mat4.ortho(-xP, xP, -yP, yP, -zP, zP);
        mygl.setP(gl, P);

        this.boundaryX = xP;
        this.boundaryY = yP;
        this.boundaryZ = zP;
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    } // not needed

    // ----------- main-Methode ---------------------------

    public static void main(String[] args) {
        new StarterClass();
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
    }

}
