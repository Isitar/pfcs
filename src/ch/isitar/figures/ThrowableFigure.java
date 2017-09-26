package ch.isitar.figures;

import ch.fhnw.pfcs.Point;

public interface ThrowableFigure extends Figure {
    public Point getPoint();

    public void setPoint(Point p);
}
