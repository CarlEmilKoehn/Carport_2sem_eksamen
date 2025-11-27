package app.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter

public class OrderWithShed extends Order{

    private Shed shed;

    public OrderWithShed(int id, String email, String status, RoofType roofType, int widthMM, int heightMM, Timestamp createdAt, List<Material> materials, List<Comment> comments, BigDecimal totalPrice, Shed shed) {
        super(id, email, status, roofType, widthMM, heightMM, createdAt, materials, comments, totalPrice);

        this.shed = shed;
    }
}
