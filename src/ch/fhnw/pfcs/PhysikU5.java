package ch.fhnw.pfcs;

//  -------------   JOGL 3D-Programm  -------------------
import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.*;
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.jogamp.opengl.*;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import ch.fhnw.pfcs.helpers.GyroDynamics;
import ch.fhnw.util.math.*;
import ch.isitar.figures.Circle;

import com.jogamp.opengl.awt.*;
import com.jogamp.opengl.util.FPSAnimator;

public class PhysikU5 implements WindowListener, GLEventListener, KeyListener {

	// --------- globale Daten ---------------------------

	String windowTitle = "JOGL-Application";
	int windowWidth = 800;
	int windowHeight = 600;
	String vShader = MyShaders.vShader2; // Vertex-Shader mit Transformations-Matrizen
	String fShader = MyShaders.fShader0; // Fragment-Shader
	int maxVerts = 2048; // max. Anzahl Vertices im Vertex-Array
	GLCanvas canvas; // OpenGL Window
	MyGLBase1 mygl; // OpenGL Basis-Funktionen¨
	double quadA = 1.5, quadB = 1.3, quadC = 1.3;
	float elevation = 10;
	float azimut = 40;
	double speed = 0.01;

	RotKoerper rotk;

	Mat4 M; // ModelView-Matrix
	Mat4 P; // Projektions-Matrix

	Stack<Mat4> matrixStack = new Stack<>();

	// -------- Viewing-Volume ---------------
	float left = -8f, right = 8f;
	float bottom, top;
	float near = -10, far = 1000;

	// LookAt-Parameter fuer Kamera-System
	Vec3 A = new Vec3(0, 0, 4); // Kamera-Pos. (Auge)
	Vec3 B = new Vec3(0, 0, 0); // Zielpunkt
	Vec3 up = new Vec3(0, 1, 0); // up-Richtung

	double dx = 0; // Quader speed

	private GyroQuad[] gdQuads = new GyroQuad[6];

	// Quaternion qStart = Quaternion.fromAxis(new Vec3(1, 0, 0), 10);
	// Quaternion qEnd = Quaternion.fromAxis(new Vec3(0, 1, 1), 70);
	// double t = 0, dt = 0.01; // slurp parameter

	boolean cameraIsLight = false;

	// quader-options, 27
	double[][] options = { { 1, 1, 1 }, { 1, 1, 0.75 }, { 1, 1, 0.1 }, { 1, 0.75, 1 }, { 1, 0.75, 0.75 },
			{ 1, 0.5, 0.1 }, { 1, 0.5, 1 }, { 1, 0.5, 0.75 }, { 1, 0.5, 0.5 }, { 0.75, 1, 1 }, { 0.75, 1, 0.75 },
			{ 0.75, 1, 0.5 }, { 0.75, 0.75, 1 }, { 0.75, 0.75, 0.75 }, { 0.75, 0.75, 0.5 }, { 0.75, 0.5, 1 },
			{ 0.75, 0.5, 0.75 }, { 0.75, 0.5, 0.5 }, { 0.5, 1, 1 }, { 0.5, 1, 0.75 }, { 0.5, 1, 0.5 }, { 0.5, 0.75, 1 },
			{ 0.5, 0.75, 0.75 }, { 0.5, 0.75, 0.5 }, { 0.5, 0.5, 1 }, { 0.5, 0.5, 0.75 }, { 0.5, 0.5, 0.5 } };

	int globalIndex = 0;
	// --------- Methoden ----------------------------------

	public PhysikU5() // Konstruktor
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
		FPSAnimator anim = new FPSAnimator(canvas, 120, true);
		anim.start();

		String optionPaneText = "Leertaste: Start/Stop Schwerpunktbewegung \r\n"
				+ "Pfeil Hoch/Runter: Geschwindigkeit \r\n" + "0-9: Verschiedene Quader \r\n"
				+ "n/N: Nächste Quaderform (bis zu 27 Möglichkeiten) \r\n" + "p/P: Vorherige Quaderform \r\n"
				+ "WASD: Kamera \r\n" + "Quaderoptionen: \r\n";

		int i = 0;
		for (double[] arr : options) {
			optionPaneText += "[" + ("000" + i).substring((i + "").length()) + "] | {";
			boolean first = true;
			for (double d : arr) {
				if (!first) {
					optionPaneText += ", ";
				}
				optionPaneText += d;
				first = false;
			}
			optionPaneText += "} \r\n";
			++i;
		}

		JOptionPane pane = new JOptionPane(optionPaneText);
		JDialog dialog = pane.createDialog(null, "Anleitung");
		dialog.setModal(false);
		dialog.show();

	};

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

		drawQuad(0);
	}

	private void setQuadOptions(double[] params) {
		quadA = params[0];
		quadB = params[1];
		quadC = params[2];
	}

	private void instanciateGdQuad() {
		double paramA = (quadA * quadA + quadB * quadB) / 12;
		double paramB = (quadA * quadA + quadC * quadC) / 12;
		double paramC = (quadB * quadB + quadC * quadC) / 12;

		Random r = new Random();
		double delta = (top - bottom) / gdQuads.length;

		for (int i = 0; i < gdQuads.length; ++i) {
			gdQuads[i] = new GyroQuad(paramA, paramB, paramC);
			gdQuads[i].setState(1, 1, 1, 0, 1, 1, 1);
			gdQuads[i].x = 0;

			gdQuads[i].y = i * delta + bottom + (0.5)*delta;

			gdQuads[i].z = r.nextDouble();

			gdQuads[i].quad = new Quader(mygl);

			gdQuads[i].quadA = quadA;
			gdQuads[i].quadB = quadB;
			gdQuads[i].quadC = quadC;
		}

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
		matrixStack.push(M);
		mygl.setM(gl, M); // Blickrichtung A --> B
		mygl.setColor(1, 1, 1);
		mygl.setShadingLevel(gl, 0);
		mygl.drawAxis(gl, 2, 2, 2); // Koordinatenachsen

		if (cameraIsLight) {
			M = Mat4.ID;
			matrixStack.push(M);
			mygl.setM(gl, matrixStack.pop());
		}
		mygl.setLightPosition(gl, 2, 4, 4); 
		M = matrixStack.pop();
		mygl.setM(gl, M);

		mygl.setShadingParam(gl, 0.2f, 0.8f);
		mygl.setShadingLevel(gl, 1);

		//matrixStack.push(M);
		// normal
		for (GyroQuad gyroQuad : gdQuads) {
			mygl.setColor(gyroQuad.color1, gyroQuad.color2, gyroQuad.color3);
			// save matrix;
			matrixStack.push(M);

			// translate quad
			M = M.postMultiply(Mat4.translate((float) gyroQuad.x, (float) gyroQuad.y, (float) gyroQuad.z));

			// rotate quad
			gyroQuad.move(speed);
			double[] state = gyroQuad.getState();
			M = M.postMultiply(Mat4.rotate((float) state[3], new Vec3(state[4], state[5], state[6])));

			// draw quad
			mygl.setM(gl, M);
			gyroQuad.quad.zeichne(gl, (float) gyroQuad.quadA, (float) gyroQuad.quadB, (float) gyroQuad.quadC, true);

			// zeichne linie
			mygl.setColor(1, 0, 0);
			zeichneLinie(gl, new Vec3(state[0], state[1], state[2]));

			// move quad forwards
			gyroQuad.x += dx;

			// cleanup matrix
			M = matrixStack.pop();
		}

	}

	public void zeichneLinie(GL3 gl, Vec3 vec) {
		// System.out.println(vec.x + ";" + vec.y + ";" + vec.z);
		mygl.rewindBuffer(gl);
		mygl.putVertex(0, 0, 0); // Startpunkt -> muss mit Matrizen verschoben werden
		mygl.putVertex(vec.x, vec.y, vec.z);
		mygl.copyBuffer(gl);
		mygl.drawArrays(gl, GL3.GL_LINE_STRIP);
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
		new PhysikU5();
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
		if ((e.getKeyCode() == KeyEvent.VK_UP)) {
			speed += 0.01;
		}
		if ((e.getKeyCode() == KeyEvent.VK_DOWN)) {
			speed -= 0.01;
		}
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

		if ((e.getKeyChar() == ' ')) {
			if (dx == 0) {
				dx = 0.01;
			} else {
				dx = 0;
			}
		}

		if (e.getKeyChar() == 'X' || (e.getKeyChar() == 'x')) {
			randomQuads();
		}

		if ((e.getKeyChar() == 'N') || (e.getKeyChar() == 'n')) {
			drawQuad(++globalIndex % options.length);
		}

		if ((e.getKeyChar() == 'P') || (e.getKeyChar() == 'p')) {
			if (globalIndex == 0) {
				globalIndex = options.length;
			}
			drawQuad(--globalIndex);
		}

		int keyInt = e.getKeyChar() - '0';
		if (keyInt <= 9 && keyInt >= 0) {
			drawQuad(keyInt);
		}
	}

	private void drawQuad(int index) {
		double[] params = options[index];
		setQuadOptions(params);
		instanciateGdQuad();

		globalIndex = index;
	}

	private void randomQuads() {
		double delta = (top - bottom) / gdQuads.length;
		Random r = new Random();

		for (int i = 0; i < gdQuads.length; ++i) {
			double[] params = options[r.nextInt(options.length)];
			setQuadOptions(params);
			double paramA = (quadA * quadA + quadB * quadB) / 12;
			double paramB = (quadA * quadA + quadC * quadC) / 12;
			double paramC = (quadB * quadB + quadC * quadC) / 12;

			gdQuads[i] = new GyroQuad(paramA, paramB, paramC);
			gdQuads[i].setState(1, 1, 1, 0, 1, 1, 1);
			gdQuads[i].x = 0;

			gdQuads[i].y = i * delta + bottom + (0.5)*delta;

			gdQuads[i].z = r.nextDouble() * 4;

			gdQuads[i].quad = new Quader(mygl);

			gdQuads[i].quadA = quadA;
			gdQuads[i].quadB = quadB;
			gdQuads[i].quadC = quadC;
		}
	}
}
