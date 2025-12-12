package app.entities;

public class CarportSvg {
    private int widthCarport;
    private int lengthCarport;
    private int widthShed;
    private int lengthShed;
    private Svg carportSvg;

    public CarportSvg(int widthCarport, int lengthCarport, int widthShed, int lengthShed) {
        this.widthCarport = widthCarport;
        this.lengthCarport = lengthCarport;
        this.widthShed = widthShed;
        this.lengthShed = lengthShed;
        carportSvg = new Svg(0, 0, "0 0 855 690", "50%");
        carportSvg.addRectangle(0, 0, 600, 780, "style=\"stroke:#000000; fill: #ffffff\"");
        addBeams();
        addRafters();
        addPost();
    }

    private void addBeams(){
        carportSvg.addRectangle(0, 35, 4.5, 780, "style=\"stroke:#000000; fill: #ffffff\"");
        carportSvg.addRectangle(0, 565, 4.5, 780, "style=\"stroke:#000000; fill: #ffffff\"");
    }

    private void addRafters() {

            for(int i = 0; i < 775.5; i = i += 55) {
                carportSvg.addRectangle(i, 0, 600, 4.5, "\"stroke:#000000; fill: #ffffff\"");
            }
    }

    private void addPost() {

        for (int i = 110; i <= 730; i += 310) {
            carportSvg.addRectangle(i, 32, 9.7, 10, "style=\"stroke:#000000; fill: #ffffff\"");
            carportSvg.addRectangle(i, 562, 9.7, 10, "style=\"stroke:#000000; fill: #ffffff\"");
        }
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
