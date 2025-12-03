package app.services;

import app.entities.Material;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.MaterialMapper;   // husk denne import

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarportCalculatorService {

    public void calculate(Order order) throws DatabaseException {

        List<Material> materials = new ArrayList<>();

        addPosts(order, materials);
        addRems(order, materials);
        addRafters(order, materials);
        addSterns(order, materials);
        addRoofSheets(order, materials);

        BigDecimal total = materials.stream()
                .map(Material::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal slopePrice = calculateSlopePrice(order);

        order.setMaterials(materials);
        order.setTotalPrice(total);
    }

    private int calculatePostsForLength(int lengthMM) {
        int postsPerSide = (int) Math.ceil(lengthMM / 3000.0) + 1;
        return postsPerSide * 2;
    }

    private void addPosts(Order order, List<Material> materials) throws DatabaseException {
        int totalPosts = calculatePostsForLength(order.getLengthMM());
        int requiredLength = 2200;

        Material basePost = MaterialMapper.findPostForLength(requiredLength);

        BigDecimal linePrice = basePost.getUnitPrice().multiply(BigDecimal.valueOf(totalPosts));

        materials.add(new Material(
                0,
                order.getId(),
                basePost.getProductId(),
                totalPosts,
                linePrice,
                "Stolper til carport",
                basePost.getProductName(),
                basePost.getProductDescription(),
                basePost.getLengthMM(),
                basePost.getUnitName(),
                basePost.getUnitShortName(),
                basePost.getUnitPrice()
        ));
    }

    private void addRems(Order order, List<Material> materials) throws DatabaseException {

        int totalRems = 4;

        int requiredLength = order.getLengthMM();

        Material baseRem = MaterialMapper.findRemForLength(requiredLength);

        BigDecimal remPrice = baseRem.getUnitPrice()
                .multiply(BigDecimal.valueOf(totalRems));


        materials.add(new Material(
                0,
                order.getId(),
                baseRem.getProductId(),
                totalRems,
                remPrice,
                "Ydre og inderrem",
                baseRem.getProductName(),
                baseRem.getProductDescription(),
                baseRem.getLengthMM(),
                baseRem.getUnitName(),
                baseRem.getUnitShortName(),
                baseRem.getUnitPrice()
        ));
    }

    private void addRafters(Order order, List<Material> materials) throws DatabaseException {

        int totalRafters = calculateRaftersForLength(order.getLengthMM());

        int requiredLength = order.getWidthMM();

        Material baseRafter = MaterialMapper.findRafterForLength(requiredLength);

        BigDecimal raftersPrice = baseRafter.getUnitPrice().multiply(BigDecimal.valueOf(totalRafters));

        materials.add(new Material(
                0,
                order.getId(),
                baseRafter.getProductId(),
                totalRafters,
                raftersPrice,
                "Spær",
                baseRafter.getProductName(),
                baseRafter.getProductDescription(),
                baseRafter.getLengthMM(),
                baseRafter.getUnitName(),
                baseRafter.getUnitShortName(),
                baseRafter.getUnitPrice()
        ));
    }

    private int calculateRaftersForLength(int lengthMM) {
        return (int) Math.ceil(lengthMM / 555.0);
    }

    private void addSterns(Order order, List<Material> materials) throws DatabaseException {


    }


    private void addRoofSheets(Order order, List<Material> materials) throws DatabaseException {

        int widthMM = order.getWidthMM();
        int lengthMM = order.getLengthMM();

        int sheetsAcross = (int) Math.ceil(widthMM / 1000.0);

        Material baseSheet = MaterialMapper.findRoofSheetForLength(lengthMM);

        BigDecimal roofSheetPrice = baseSheet.getUnitPrice().multiply(BigDecimal.valueOf(sheetsAcross));

        materials.add(new Material(
                order.getId(),
                baseSheet.getProductId(),
                sheetsAcross,
                roofSheetPrice,
                "Tagplader",
                baseSheet.getProductName(),
                baseSheet.getProductDescription(),
                baseSheet.getLengthMM(),
                baseSheet.getUnitName(),
                baseSheet.getUnitShortName(),
                baseSheet.getUnitPrice()
        ));
    }

    private BigDecimal calculateSlopePrice(Order order) {

        if (order.getRoofType() == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(order.getRoofType().getDegrees())
                .multiply(BigDecimal.valueOf(240.0));
    }
}