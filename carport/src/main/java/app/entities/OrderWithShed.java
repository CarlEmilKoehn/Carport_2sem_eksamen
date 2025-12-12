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

    public OrderWithShed(int id, String email, String status, RoofType roofType, int widthMM, int heightMM, int lengthMM, Timestamp createdAt, List<Material> materials, List<Comment> comments, BigDecimal totalPrice, Shed shed) {
        super(id, email, status, roofType, widthMM, heightMM, lengthMM, createdAt, materials, comments, totalPrice);

        this.shed = shed;
    }

    public OrderWithShed(String email, String status, RoofType roofType, int widthMM, int heightMM, int lengthMM, List<Material> materials, List<Comment> comments, BigDecimal totalPrice, Shed shed) {
        super(email, status, roofType, widthMM, heightMM, lengthMM, materials, comments, totalPrice);

        this.shed = shed;
    }
}
