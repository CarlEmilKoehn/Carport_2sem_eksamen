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

        BigDecimal total = materials.stream()
                .map(Material::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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


        Material line = new Material(
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
        );

       materials.add(line);
    }

    private void addRems(Order order, List<Material> materials) throws DatabaseException {

        int totalRems = 4;

        int requiredLength = order.getLengthMM();

        Material baseRem = MaterialMapper.findRemForLength(requiredLength);

        BigDecimal remPrice = baseRem.getUnitPrice()
                .multiply(BigDecimal.valueOf(totalRems));

        Material line = new Material(
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
        );

        materials.add(line);
    }

    private void addRafters(Order order, List<Material> materials) throws DatabaseException {

        int totalRafters = calculateRaftersForLength(order.getLengthMM());

        int requiredLength = order.getWidthMM();

        Material baseRafter = MaterialMapper.findRafterForLength(requiredLength);

        BigDecimal raftersPrice = baseRafter.getUnitPrice().multiply(BigDecimal.valueOf(totalRafters));

        Material rafter = new Material(
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
        );
    }

    private int calculateRaftersForLength(int lengthMM) {
        return (int) Math.ceil(lengthMM / 555.00);
    }

}
