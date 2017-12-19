package ch.fhnw.pfcs.helpers;

import java.util.function.Consumer;

import ch.fhnw.util.math.Vec3;

public class GyroDynamics {
	private double I1, I2, I3; // Trägheitsmomente
	double[] state;

	private Consumer<double[]> gyroDynamicFunction = (double[] x) -> {
		double w1 = x[0];
		double w2 = x[1];
		double w3 = x[2];

		// Quaternion
		double q0 = x[3];
		double q1 = x[4];
		double q2 = x[5];
		double q3 = x[6];

		x[0] = (I2 - I3) / I1 * w2 * w3;
		x[1] = (I3 - I1) / I2 * w3 * w1;
		x[2] = (I1 - I2) / I3 * w1 * w2;

		// script
		x[3] = -0.5 * (q1 * w1 + q2 * w2 + q3 * w3);
		x[4] = 0.5 * (q0 * w1 + q2 * w3 - q3 * w2);
		x[5] = 0.5 * (q0 * w2 + q3 * w1 - q1 * w3);
		x[6] = 0.5 * (q0 * w3 + q1 * w2 - q2 * w1);
	};

	public GyroDynamics(double i1, double i2, double i3) {
		super();
		I1 = i1;
		I2 = i2;
		I3 = i3;
	}

	/**
	 * 
	 * @return phi in degrees
	 */
	public double[] getState() {
		double w1 = state[0];
		double w2 = state[1];
		double w3 = state[2];

		// Quaternion
		double q0 = state[3];
		double q1 = state[4];
		double q2 = state[5];
		double q3 = state[6];

		double phi = Math.toDegrees(2 * Math.acos(q0));

		return new double[] { w1, w2, w3, phi, q1, q2, q3 };
	}

	public void setState(double w1, double w2, double w3, double phi, double x, double y, double z) {
		double phiHalf = Math.toRadians(phi / 2);
		Vec3 q = new Vec3(x, y, z).normalize().scale((float) Math.sin(phiHalf));
		state = new double[] { w1, w2, w3, Math.cos(phiHalf), q.x, q.y, q.z };
	}

	public void move(double dt) {
		state = Dynamics.rungeKutta(state, dt, gyroDynamicFunction);
	}

}
