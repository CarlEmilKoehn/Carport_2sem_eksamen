package app.entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor

public class Material {

    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal totalPrice;
    private String note;

    private String productName;
    private String productDescription;
    private Integer lengthMM;
    private String unitName;
    private String unitShortName;
    private BigDecimal unitPrice;

    public Material(int orderId, int productId, int quantity, BigDecimal totalPrice, String note, String productName, String productDescription, Integer lengthMM, String unitName, String unitShortName, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.note = note;
        this.productName = productName;
        this.productDescription = productDescription;
        this.lengthMM = lengthMM;
        this.unitName = unitName;
        this.unitShortName = unitShortName;
        this.unitPrice = unitPrice;
    }

    public Material(int materialProductId, String materialProductName, String materialProductDescription, int lengthMm, BigDecimal materialPrice, String unitName, String unitShortName) {
    }
}
