package app.entities;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Material {

    private int materialProductId;
    private int materialCategoryId;
    private int unitId;
    private int lengthMM;
    private String materialProductName;
    private String materialProductDescription;
    private String materialCategoryName;
    private double materialPrice;



    @Override
    public String toString() {
        return
                "Materiale Info:" +
                        "Materiale produkt id:" + materialProductId + "\n" +
                        "Materiale kategori id:" + materialCategoryId + "\n" +
                        "Materiale kategori navn:" + materialCategoryName + "\n" +
                        "Enheds id:" + unitId + "\n" +
                        "Materiale længde i mm:" + lengthMM + "\n" +
                        "Produkt navn:" + materialProductName + "\n" +
                        "Produkt beskrivelse:" + materialProductDescription + "\n" +
                        "Pris:" +  materialPrice + "\n";
    }


}
