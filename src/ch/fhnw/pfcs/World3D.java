package ch.fhnw.pfcs;

//  -------------   JOGL 3D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

import com.jogamp.opengl.*;
import ch.fhnw.util.math.*;
import ch.isitar.figures.Circle;

import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;

public class World3D implements WindowListener, GLEventListener, KeyListener {

    // --------- globale Daten ---------------------------

    String windowTitle = "World with moon";
    int windowWidth = 800;
    int windowHeight = 600;
    String vShader = MyShaders.vShader2; // Vertex-Shader mit Transformations-Matrizen
    String fShader = MyShaders.fShader0; // Fragment-Shader
    int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas; // OpenGL Window
    MyGLBase1 mygl; // OpenGL Basis-Funktionen¨

    float elevation = 10;
    float azimut = 40;
    float phi = 0;
    Quader quad;
    RotKoerper rotk;

    Mat4 M; // ModelView-Matrix
    Mat4 P; // Projektions-Matrix

    Stack<Mat4> matrixStack = new Stack<>();

    // LookAt-Parameter fuer Kamera-System
    Vec3 A = new Vec3(0, 0, 50); // Kamera-Pos. (Auge)
    Vec3 B = new Vec3(0, 0, 0); // Zielpunkt
    Vec3 up = new Vec3(0, 50, 0); // up-Richtung

    boolean cameraIsLight = false;

    // in this context, we are in 1/1000
    // private double x0 = 385; //moon avg distance
    private double x0 = 40; // moon avg distance
    private double v0x = 0;
    private double v0y = Math.sqrt(GM / x0);
    // private double v0y = 0.001022;
    private double vx = v0x, vy = v0y;
    private final double dt = 1200;

    private final static double g = 9.81e-6;
    private final static double earthRadius = 6.378;
    private final static double GM = g * earthRadius * earthRadius;
    private final static double moonRadius = 1.737;

    private Point SatelitePoint = new Point(x0, 0, 0);

    // -------- Viewing-Volume ---------------
    float left = (float) (-x0 - 10), right = (float) (x0 + 10);
    float bottom, top;
    float near = (float) (-x0 - 10), far = 1000;

    // --------- Methoden ----------------------------------

    public World3D() // Konstruktor
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
        gl.glClearColor(0, 0, 0, 1);
        int programId = MyShaders.initShaders(gl, vShader, fShader);
        mygl = new MyGLBase1(gl, programId, maxVerts);
        quad = new Quader(mygl);
        rotk = new RotKoerper(mygl);
    }

    Circle lightBulb = new Circle(0.4f, new Point(0, 0, 0), new Point(1, 1, 1));

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        Mat4 R1 = Mat4.rotate(-elevation, 1, 0, 0);
        Mat4 R2 = Mat4.rotate(azimut, 0, 1, 0);
        Mat4 R = R1.preMultiply(R2);

        M = Mat4.lookAt(R.transform(A), B, R.transform(up));
        Mat4 origM = M;
        matrixStack.push(M);
        mygl.setM(gl, M); // Blickrichtung A --> B
        mygl.setColor(1, 1, 1);
        mygl.setShadingLevel(gl, 0);

        if (cameraIsLight) {
            M = Mat4.ID;
            matrixStack.push(M);
            mygl.setM(gl, matrixStack.pop());
        }
        mygl.setLightPosition(gl, 50, 4, 4); // changed
        // matrixStack.push(M.postMultiply(Mat4.translate(mygl.getLightPosition()[0],
        // mygl.getLightPosition()[1], mygl.getLightPosition()[2])));
        // mygl.setM(gl, matrixStack.pop());
        // lightBulb.draw(gl, mygl);
        mygl.setShadingParam(gl, 0.2f, 0.8f);
        mygl.setShadingLevel(gl, 1);
        mygl.setColor(0, 0.5f, 1);

        matrixStack.push(M.postMultiply(Mat4.rotate(phi, 0, 1, 0)));
        phi++;
        M = matrixStack.pop();
        mygl.setM(gl, M);

        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(1, 1);
        rotk.zeichneKugel(gl, (float) earthRadius, 30, 30, true);
        mygl.setColor(0, 1, 1);
        rotk.zeichneKugel(gl, (float) earthRadius, 30, 30, false);

        SatelitePoint.setX(SatelitePoint.getXDouble() + vx * dt);
        SatelitePoint.setY(SatelitePoint.getYDouble() + vy * dt);

        double ax = -GM * SatelitePoint.getXDouble() / Math.pow(SatelitePoint.getLength(), 3);
        vx += ax * dt;
        double ay = -GM * SatelitePoint.getYDouble() / Math.pow(SatelitePoint.getLength(), 3);
        vy += ay * dt;

        Mat4 translated = Mat4.translate(new Vec3(SatelitePoint.getX(), SatelitePoint.getY(), SatelitePoint.getZ()));
        M = origM;
        M = M.postMultiply(Mat4.rotate(-90, 1, 0, 0));
        M = M.postMultiply(Mat4.rotate(-90, 0, 0, 1));
        M = M.postMultiply(translated);


        mygl.setM(gl, M);
        mygl.setColor(1, 1, 1);
        mygl.drawAxis(gl, 50, 50, 50); // Koordinatenachsen
        rotk.zeichneKugel(gl, (float) moonRadius, 30, 30, true);

        M = matrixStack.pop();
        mygl.setM(gl, M);

        matrixStack.push(M);
        mygl.drawAxis(gl, 50, 50, 50); // Koordinatenachsen
        mygl.setM(gl, matrixStack.pop());

        // Circle c = new Circle(moonRadius, new Point(SatelitePoint), new Point(1, 1, 1));
        // mygl.rewindBuffer(gl);
        // c.draw(gl, mygl);

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
        new World3D();
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
