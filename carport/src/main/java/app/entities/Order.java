package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor

public class Order {

    private int id;
    private String email;
    private String status;
    private int widthMM;
    private int heightMM;
    private Timestamp createdAt;
    private BigDecimal totalPrice;

}
