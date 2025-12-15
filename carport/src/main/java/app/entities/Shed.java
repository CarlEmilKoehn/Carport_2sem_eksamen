package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Shed {

    private int id;
    private int widthMM;
    private int lengthMM;

    public Shed(int widthMM, int lengthMM) {
        this.widthMM = widthMM;
        this.lengthMM = lengthMM;
    }
}
