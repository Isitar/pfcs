package ch.fhnw.pfcs.helpers;

import java.util.function.Consumer;

public class GyroDynamics {
	private double I1, I2, I3; // Trägheitsmomente
	double[] x;

	private Consumer<double[]> gyroDynamicFunction = (double[] x) -> {
		double w1 = x[0];
		double w2 = x[1];
		double w3 = x[2];

		// Quaternion
		double phi = x[3];
		double q1 = x[4];
		double q2 = x[5];
		double q3 = x[6];

		x[0] = (I2-I3)/I1*w2*w3;
		x[1] = (I3-I1)/I2*w3*w1;
		x[2] = (I1-I2)/I3*w1*w2;
		
		// script
		x[3] = (I3-I1)/I2*w3*w1;
		x[4] = (I3-I1)/I2*w3*w1;
		x[5] = (I3-I1)/I2*w3*w1;
		x[1] = (I3-I1)/I2*w3*w1;
	};

	public GyroDynamics(double i1, double i2, double i3) {
		super();
		I1 = i1;
		I2 = i2;
		I3 = i3;
	}

	public void move(double dt) {
		x = Dynamics.rungeKutta(x, dt, gyroDynamicFunction);
	}

}
