package ch.fhnw.pfcs;

//  -------------   JOGL 2D-Programm  -------------------
import java.awt.*;
import java.awt.event.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.*;
import ch.fhnw.util.math.*;

public class MyFirst2D implements WindowListener, GLEventListener {

    // --------- globale Daten ---------------------------

    String windowTitle = "JOGL-Application";
    int windowWidth = 1920;
    int windowHeight = 1080;
    String vShader = MyShaders.vShader1; // Vertex-Shader
    String fShader = MyShaders.fShader0; // Fragment-Shader
    int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
    GLCanvas canvas; // OpenGL Window
    MyGLBase1 mygl; // eigene OpenGL-Basisfunktionen
    private float triangleSize = 0.5f;
    private float growthFactor = 0.01f;
    private boolean growing = true;
    private Mat4 transformationMatrix = Mat4.ID;
    private float angle = 1f;

    private float r = 0.01f;
    private float g = 0.01f;
    private float b = 0.01f;
    private int GrowingColor = 0;

    // --------- Methoden ----------------------------------

    public MyFirst2D() // Konstruktor
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
    };

    public void zeichneDreieck(GL3 gl, float x1, float y1, float x2, float y2, float x3, float y3) {
        mygl.rewindBuffer(gl); // Vertex-Buffer zuruecksetzen
        mygl.putVertex(x1, y1, 0); // Eckpunkte in VertexArray speichern
        mygl.putVertex(x2, y2, 0);
        mygl.putVertex(x3, y3, 0);
        mygl.copyBuffer(gl);
        mygl.drawArrays(gl, GL3.GL_TRIANGLES);
    }

    // ---------- OpenGL-Events ---------------------------

    @Override
    public void init(GLAutoDrawable drawable) // Initialisierung
    {
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("OpenGl Version: " + gl.glGetString(gl.GL_VERSION));
        System.out.println("Shading Language: " + gl.glGetString(gl.GL_SHADING_LANGUAGE_VERSION));
        System.out.println();
        gl.glClearColor(0, 0, 1, 1); // Hintergrundfarbe

        int programId = MyShaders.initShaders(gl, vShader, fShader); // Compile/Link Shader-Programme
        mygl = new MyGLBase1(gl, programId, maxVerts); // OpenGL Basis-Funktionen

        FPSAnimator anim = new FPSAnimator(canvas, 60, true);
        anim.start();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT); // Bildschirm loeschen
        mygl.setColor(r, g, b); // Farbe der Vertices
        // float a = 0.5f;
        // triangleSize += acceleration;
        // acceleration += 0.0005f;
        transformationMatrix = Mat4.rotate(angle, 0, 0, 1);

        angle = (angle + 1) % 360;
        mygl.setM(gl, transformationMatrix);

        if (growing) {
            growthFactor += 0.01f;
            if (growthFactor >= 0.98f) {
                growing = false;
            }
        } else {
            growthFactor -= 0.01f;
            if (growthFactor <= 0.01f) {
                growing = true;
            }
        }
        increaseColor();
        float calculatedTriangleSize = triangleSize * growthFactor;

        zeichneDreieck(gl, -calculatedTriangleSize, -calculatedTriangleSize, calculatedTriangleSize,
                -calculatedTriangleSize, 0, calculatedTriangleSize);

    }

    private void increaseColor() {
        System.out.println("GrowingColor: " + GrowingColor);
        System.out.println("rgb: " + r + "/" + g +"/"+ b);
        float incr = 0.01f;
        float max = 1f;
        float min = 0.01f;
        switch (GrowingColor) {
        case 0: {
            r += incr;
            if (r >= max) {
                GrowingColor++;
            }
            break;
        }

        case 1: {
            g += incr;
            if (g >= max) {
                GrowingColor++;
            }
            break;
        }
        case 2: {
            b += incr;
            if (b >= max) {
                GrowingColor++;
            }
            break;
        }
        case 3: {
            r -= incr;
            if (r <= min) {
                GrowingColor++;
            }
            break;
        }
        case 4: {
            g -= incr;
            if (g <= min) {
                GrowingColor++;
            }
            break;
        }
        case 5: {
            b -= incr;
            if (b <= min) {
                GrowingColor++;
            }
            break;
        }
        }
        GrowingColor = GrowingColor % 6;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        // Set the viewport to be the entire window
        gl.glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    } // not needed

    // ----------- main-Methode ---------------------------

    public static void main(String[] args) {
        new MyFirst2D();
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

}
