package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor

public class Order {

    private int id;
    private String email;
    private String status;
    private RoofType roofType;
    private int widthMM;
    private int heightMM;
    private Timestamp createdAt;
    private List<Material> materials;
    private BigDecimal totalPrice;

}
