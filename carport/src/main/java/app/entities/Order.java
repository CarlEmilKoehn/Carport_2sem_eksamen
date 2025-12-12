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
    private int lengthMM;
    private Timestamp createdAt;
    private List<Material> materials;
    private List<Comment> comments;
    private BigDecimal totalPrice;

    public Order(String email, String status, RoofType roofType, int widthMM, int heightMM, int lengthMM, List<Material> materials, List<Comment> comments, BigDecimal totalPrice) {
        this.email = email;
        this.status = status;
        this.roofType = roofType;
        this.widthMM = widthMM;
        this.heightMM = heightMM;
        this.lengthMM = lengthMM;
        this.materials = materials;
        this.comments = comments;
        this.totalPrice = totalPrice;
    }
}
