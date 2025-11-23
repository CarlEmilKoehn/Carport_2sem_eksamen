package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Shed {

    private int id;
    private int widthMM;
    private int lengthMM;
}
