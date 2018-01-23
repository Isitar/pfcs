package ch.fhnw.pfcs;

import java.util.Random;

import ch.fhnw.pfcs.helpers.GyroDynamics;

public class GyroQuad extends GyroDynamics {
	public GyroQuad(double i1, double i2, double i3) {
		super(i1, i2, i3);
		Random r = new Random();
		color1 = r.nextFloat();
		color2 = r.nextFloat();
		color3 = r.nextFloat();
	}

	public double x;
	public double y;
	public double z;
	
	public Quader quad;
	
	public float color1;
	public float color2;
	public float color3;
	
	
	public double quadA;
	public double quadB;
	public double quadC;
}
