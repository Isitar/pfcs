package ch.fhnw.pfcs;

//  -------------   JOGL 3D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;
import ch.isitar.figures.Bumerang;
import ch.isitar.figures.Figure;

import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;

public class StartBumerang implements WindowListener, GLEventListener, KeyListener {

    // --------- globale Daten ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = MyShaders.vShader1; // Vertex-Shader mit Transformations-Matrizen
    String fShader = MyShaders.fShader0; // Fragment-Shader
    int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas; // OpenGL Window
    MyGLBase1 mygl; // OpenGL Basis-Funktionen¨

    float elevation = 10;
    float azimut = 40;

    Quader quad;
    RotKoerper rotk;

    Mat4 M; // ModelView-Matrix
    Mat4 P; // Projektions-Matrix

    // -------- Viewing-Volume ---------------
    float left = -10f, right = 10f;
    float bottom, top;
    float near = -10, far = 1000;

    // LookAt-Parameter fuer Kamera-System
    Vec3 A = new Vec3(0, 0, 4); // Kamera-Pos. (Auge)
    Vec3 B = new Vec3(0, 0, 0); // Zielpunkt
    Vec3 up = new Vec3(0, 1, 0); // up-Richtung

    ArrayList<Figure> figures = new ArrayList<Figure>();

    // --------- Methoden ----------------------------------

    public StartBumerang() // Konstruktor
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

        canvas.addKeyListener(this);
        f.addKeyListener(this);
        FPSAnimator anim = new FPSAnimator(canvas, 60, true);
        anim.start();
    };

    public void zeichneDreieck(GL3 gl, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3,
            float z3) {
        Vec3 u = new Vec3(x2 - x1, y2 - y1, z2 - z1);
        Vec3 v = new Vec3(x3 - x1, y3 - y1, z3 - z1);
        Vec3 normale = u.cross(v); // Normalenvektor
        mygl.setNormal(normale.x, normale.y, normale.z);
        mygl.rewindBuffer(gl);
        mygl.putVertex(x1, y1, z1); // Eckpunkte in VertexArray speichern
        mygl.putVertex(x2, y2, z2);
        mygl.putVertex(x3, y3, z3);
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLES);
    }

    // ---------- OpenGL-Events ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) // Initialisierung
    {
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("OpenGl Version: " + gl.glGetString(GL.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(GL2ES2.GL_SHADING_LANGUAGE_VERSION));
        System.out.println();
        gl.glEnable(GL3.GL_DEPTH_TEST);
        gl.glClearColor(0, 0, 1, 1);
        int programId = MyShaders.initShaders(gl, vShader, fShader);
        mygl = new MyGLBase1(gl, programId, maxVerts);
        quad = new Quader(mygl);
        rotk = new RotKoerper(mygl);
        figures.add(new Bumerang());
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        Mat4 R1 = Mat4.rotate(-elevation, 1, 0, 0);
        Mat4 R2 = Mat4.rotate(azimut, 0, 1, 0);
        Mat4 R = R1.preMultiply(R2);
        Mat4 V = Mat4.lookAt(R.transform(A), B, R.transform(up));

        mygl.setM(gl, V); // Blickrichtung A --> B
        mygl.setColor(1, 1, 1);
        mygl.drawAxis(gl, 2, 2, 2); // Koordinatenachsen
        mygl.setLightPosition(gl, -2, 2, 2);
        mygl.setColor(1, 0, 0);
        zeichneDreieck(gl, -1, 0.3f, 0.5f, 2.8f, 0, -1, 1f, 1.5F, -1);
        mygl.setColor(1, 1, 0);
        zeichneDreieck(gl, -0.4f, 0.2f, -1, 3, 2, 3, -1.8f, 1f, -1);

        for (Figure figure : figures) {
            figure.update();
            figure.draw(gl, mygl);
        }
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
        new StartBumerang();
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if ((e.getKeyChar() == 'W') || (e.getKeyChar() == 'w')) {
            elevation++;
        }
        if ((e.getKeyChar() == 'S') || (e.getKeyChar() == 's')) {
            elevation--;
        }

        if ((e.getKeyChar() == 'A') || (e.getKeyChar() == 'a')) {
            azimut--;
        }
        if ((e.getKeyChar() == 'D') || (e.getKeyChar() == 'd')) {
            azimut++;
        }
    }

}
