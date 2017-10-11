package ch.isitar.figures;

import java.util.List;

public interface FigureHolder {
    public void addFigure(Figure f);

    public void removeFigure(Figure f);

    public List<Figure> getFigures();
}
