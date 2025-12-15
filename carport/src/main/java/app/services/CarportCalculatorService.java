package app.services;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.MaterialMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarportCalculatorService {

    private static final int POST_HEIGHT_MM = 2200;
    private static final int SHED_WALL_HEIGHT_MM = 2200;

    public static void calculate(Order order) throws DatabaseException {

        List<Material> materials = new ArrayList<>();

        addPosts(order, materials);
        addRems(order, materials);
        addRafters(order, materials);
        addSterns(order, materials);
        addRoofSheets(order, materials);
        addShed(order, materials);

        BigDecimal materialsTotal = materials.stream()
                .map(Material::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal slopePrice = calculateSlopePrice(order);

        order.setMaterials(materials);
        order.setTotalPrice(materialsTotal.add(slopePrice));
    }

    private static void addPosts(Order order, List<Material> materials) throws DatabaseException {

        int postsPerSide = (int) Math.ceil(order.getLengthMM() / 3000.0) + 1;
        int totalPosts = postsPerSide * 2;

        Material post = MaterialMapper.findPostForLength(POST_HEIGHT_MM);

        BigDecimal price = post.getUnitPrice().multiply(BigDecimal.valueOf(totalPosts));

        materials.add(new Material(
                0,
                order.getId(),
                post.getProductId(),
                totalPosts,
                price,
                "Stolper",
                post.getProductName(),
                post.getProductDescription(),
                post.getLengthMM(),
                post.getUnitName(),
                post.getUnitShortName(),
                post.getUnitPrice()
        ));
    }

    private static void addRems(Order order, List<Material> materials) throws DatabaseException {

        Material rem = MaterialMapper.findRemForLength(order.getLengthMM());
        int qty = 4;

        BigDecimal price = rem.getUnitPrice().multiply(BigDecimal.valueOf(qty));

        materials.add(new Material(
                0, order.getId(), rem.getProductId(), qty, price,
                "Remme",
                rem.getProductName(),
                rem.getProductDescription(),
                rem.getLengthMM(),
                rem.getUnitName(),
                rem.getUnitShortName(),
                rem.getUnitPrice()
        ));
    }

    private static void addRafters(Order order, List<Material> materials) throws DatabaseException {

        int qty = (int) Math.ceil(order.getLengthMM() / 555.0);
        Material rafter = MaterialMapper.findRafterForLength(order.getWidthMM());

        BigDecimal price = rafter.getUnitPrice().multiply(BigDecimal.valueOf(qty));

        materials.add(new Material(
                0, order.getId(), rafter.getProductId(), qty, price,
                "Spær",
                rafter.getProductName(),
                rafter.getProductDescription(),
                rafter.getLengthMM(),
                rafter.getUnitName(),
                rafter.getUnitShortName(),
                rafter.getUnitPrice()
        ));
    }

    private static void addSterns(Order order, List<Material> materials) throws DatabaseException {

        Material underL = MaterialMapper.findUnderSternForLength(order.getLengthMM());
        Material underW = MaterialMapper.findUnderSternForLength(order.getWidthMM());
        Material overL  = MaterialMapper.findOverSternForLength(order.getLengthMM());
        Material overW  = MaterialMapper.findOverSternForLength(order.getWidthMM());

        add(materials, order, underL, 2, "Understern langsider");
        add(materials, order, underW, 2, "Understern for/bag");
        add(materials, order, overL,  2, "Overstern langsider");
        add(materials, order, overW,  2, "Overstern for/bag");
    }

    private static void addRoofSheets(Order order, List<Material> materials) throws DatabaseException {

        int sheetsAcross = (int) Math.ceil(order.getWidthMM() / 1000.0);
        Material sheet = MaterialMapper.findRoofSheetForLength(order.getLengthMM());

        BigDecimal price = sheet.getUnitPrice().multiply(BigDecimal.valueOf(sheetsAcross));

        materials.add(new Material(
                0, order.getId(), sheet.getProductId(), sheetsAcross, price,
                "Tagplader",
                sheet.getProductName(),
                sheet.getProductDescription(),
                sheet.getLengthMM(),
                sheet.getUnitName(),
                sheet.getUnitShortName(),
                sheet.getUnitPrice()
        ));
    }

    private static void addShed(Order order, List<Material> materials) throws DatabaseException {

        if (!(order instanceof OrderWithShed ows)) return;
        Shed shed = ows.getShed();
        if (shed == null) return;

        int perimeter = 2 * (shed.getWidthMM() + shed.getLengthMM());
        int boards = (int) Math.ceil(perimeter / 100.0) * 2;

        Material board = MaterialMapper.findCladdingForHeight(SHED_WALL_HEIGHT_MM);

        BigDecimal price = board.getUnitPrice().multiply(BigDecimal.valueOf(boards));

        materials.add(new Material(
                0, order.getId(), board.getProductId(), boards, price,
                "Beklædning skur",
                board.getProductName(),
                board.getProductDescription(),
                board.getLengthMM(),
                board.getUnitName(),
                board.getUnitShortName(),
                board.getUnitPrice()
        ));
    }

    private static void add(List<Material> list, Order order, Material m, int qty, String note) {
        list.add(new Material(
                0, order.getId(), m.getProductId(),
                qty,
                m.getUnitPrice().multiply(BigDecimal.valueOf(qty)),
                note,
                m.getProductName(),
                m.getProductDescription(),
                m.getLengthMM(),
                m.getUnitName(),
                m.getUnitShortName(),
                m.getUnitPrice()
        ));
    }

    private static BigDecimal calculateSlopePrice(Order order) {
        if (order.getRoofType() == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(order.getRoofType().getDegrees()).multiply(BigDecimal.valueOf(240));
    }
}
