package app.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter

public class OrderWithShed extends Order{
    public OrderWithShed(int id, String email, String status, int widthMM, int heightMM, Timestamp createdAt, BigDecimal totalPrice, int shedWidthMM, int shedLengthMM) {
        super(id, email, status, widthMM, heightMM, createdAt, totalPrice);
        this.shedWidthMM = shedWidthMM;
        this.shedLengthMM = shedLengthMM;
    }

    private int shedWidthMM;
    private int shedLengthMM;

}
