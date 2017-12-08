package ch.fhnw.pfcs.helpers;

import java.util.function.Consumer;
import java.util.function.Function;

public class Dynamics {

    /**
     * @param x Vektor
     * @param dt Zeitschritt
     * @param f function Arr
     * @return neuer Vektor
     */
    public static double[] euler(double[] x, double dt, Function<double[], double[]> f) {
        double[] y = f.apply(x);
        double[] retArr = new double[x.length];
        for (int i = 0; i < x.length; ++i) {
            retArr[i] = x[i] + y[i] * dt;
        }
        return retArr;
    }

    /**
     * @param x Vektor
     * @param dt Zeitschritt
     * @param f function Arr
     * @return neuer Vektor
     */
    public static double[] rungeKutta(double[] x, double dt, Consumer<double[]> f) {
        double[] y1 = x.clone();
        f.accept(y1);

        double[] y2 = new double[x.length];
        for (int i = 0; i < y2.length; ++i) {
            y2[i] = x[i] + dt / 2 * y1[i];
        }
        f.accept(y2);

        double[] y3 = new double[x.length];
        for (int i = 0; i < y3.length; ++i) {
            y3[i] = x[i] + dt / 2 * y2[i];
        }
        f.accept(y3);

        double[] y4 = new double[x.length];
        for (int i = 0; i < y4.length; ++i) {
            y4[i] = x[i] + dt * y3[i];
        }
        f.accept(y4);

        double[] y = new double[x.length];
        for (int i = 0; i < y.length; ++i) {
            y[i] = (y1[i] + 2 * (y2[i] + y3[i]) + y4[i]) / 6;
        }

        double[] retArr = new double[x.length];
        for (int i = 0; i < x.length; ++i) {
            retArr[i] = x[i] + y[i] * dt;
        }
        return retArr;
    }

}
