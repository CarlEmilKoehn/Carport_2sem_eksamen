package app.entities;

public class Svg {

    private final String svgTemplate =
            "<svg version=\"1.1\"\n" +
                    "     x=\"%d\" y=\"%d\"\n" +
                    "     viewBox=\"%s\" width=\"%s\"\n" +
                    "     preserveAspectRatio=\"xMinYMin\">";

    private final String svgRectTemplate =
            "<rect x=\"%.2f\" y=\"%.2f\" height=\"%f\" width=\"%f\" style=\"%s\" />";

    private final String svgLineTemplate =
            "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"%s\" />";

    private StringBuilder svg = new StringBuilder();

    private static final String svgArrowDefs = "<defs>\n" +
            "  <marker id=\"beginArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"0\" refY=\"6\" orient=\"auto\">\n" +
            "    <path d=\"M0,6 L12,0 L12,12 L0,6\" style=\"fill:#000000;\" />\n" +
            "  </marker>\n" +
            "  <marker id=\"endArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"12\" refY=\"6\" orient=\"auto\">\n" +
            "    <path d=\"M0,0 L12,6 L0,12 L0,0\" style=\"fill:#000000;\" />\n" +
            "  </marker>\n" +
            "</defs>";

    private final String svgTextTemplate =
            "<text x=\"%d\" y=\"%d\" font-size=\"12\" text-anchor=\"middle\">%s</text>";



    public Svg(int x, int y, String viewBox, String width) {
        svg.append(String.format(svgTemplate, x, y, viewBox, width));
        svg.append(svgArrowDefs);
    }

    public void addRectangle(double x, double y, double height, double width, String style) {
        svg.append(String.format(svgRectTemplate, x, y, height, width, style));
    }

    public void addLine(int x1, int y1, int x2, int y2, String style) {
        svg.append(String.format(svgLineTemplate, x1, y1, x2, y2, style));
    }

    public void addArrowLine(
            int x1, int y1,
            int x2, int y2,
            String text
    ) {
        svg.append(String.format(
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" " +
                        "style=\"stroke:#000; stroke-width:1\" " +
                        "marker-start=\"url(#beginArrow)\" " +
                        "marker-end=\"url(#endArrow)\" />",
                x1, y1, x2, y2
        ));

        svg.append(String.format(svgTextTemplate, (x1 + x2) / 2, (y1 + y2) / 2 - 5, text));
    }

    @Override
    public String toString() {
        return svg.toString() + "</svg>";
    }
}
