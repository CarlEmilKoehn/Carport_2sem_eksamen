package app.entities;

public class Svg {

    private StringBuilder svg;

    public Svg(int x, int y, String viewBox, String width, String height){}

    public addRectangle(int x, int y, double height, double width, String style){}

    public addLine(int x1, int y1, int x2, int y2, String style){}

    public addArrow(int x1, int y1, int x2, int y2, String style){}

    public addText(int x, int y, int rotation, String text){}

    public addSvg(Svg innerSvg){}



    @Override
    public toString(){}
}
