package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor

public class RoofType {

    private int id;
    private String name;
    private int degrees;
    private BigDecimal price;
}
