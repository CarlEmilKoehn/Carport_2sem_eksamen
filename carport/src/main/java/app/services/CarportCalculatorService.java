package app.services;

import app.entities.Material;
import app.entities.Order;
import app.entities.OrderWithShed;
import app.entities.Shed;
import app.exceptions.DatabaseException;
import app.persistence.MaterialMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarportCalculatorService {

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

    static private void addShed(Order order, List<Material> materials) throws DatabaseException {

        if (!(order instanceof OrderWithShed ows)) {
            return;
        }
        if (ows.getShed() == null) {
            return;
        }

        Shed shed = ows.getShed();
        int shedWidth  = shed.getWidthMM();
        int shedLength = shed.getLengthMM();

        int wallHeightMM = 2200;

        int boardCoverWidthMM = 100;

        int perimeterMM = 2 * (shedWidth + shedLength);

        int boardsPerLayer = (int) Math.ceil(perimeterMM / (double) boardCoverWidthMM);

        int totalBoards = boardsPerLayer * 2;

        Material claddingBoard = MaterialMapper.findCladdingForHeight(wallHeightMM);

        BigDecimal shedCladdingPrice =
                claddingBoard.getUnitPrice().multiply(BigDecimal.valueOf(totalBoards));

        materials.add(new Material(
                0,
                order.getId(),
                claddingBoard.getProductId(),
                totalBoards,
                shedCladdingPrice,
                "Beklædning til redskabsrum",
                claddingBoard.getProductName(),
                claddingBoard.getProductDescription(),
                claddingBoard.getLengthMM(),
                claddingBoard.getUnitName(),
                claddingBoard.getUnitShortName(),
                claddingBoard.getUnitPrice()
        ));

    }

    private static int calculatePostsForLength(int lengthMM) {
        int postsPerSide = (int) Math.ceil(lengthMM / 3000.0) + 1;
        return postsPerSide * 2;
    }

    private static void addPosts(Order order, List<Material> materials) throws DatabaseException {
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

    private static void addRems(Order order, List<Material> materials) throws DatabaseException {

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

    private static void addRafters(Order order, List<Material> materials) throws DatabaseException {

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

    private static int calculateRaftersForLength(int lengthMM) {
        return (int) Math.ceil(lengthMM / 555.0);
    }

    private static void addSterns(Order order, List<Material> materials) throws DatabaseException {

        int lengthMM = order.getLengthMM();
        int widthMM  = order.getWidthMM();

        int boardsOnLengthSides = 2;
        int boardsOnWidthSides  = 2;

        Material underLength = MaterialMapper.findUnderSternForLength(lengthMM);
        Material underWidth  = MaterialMapper.findUnderSternForLength(widthMM);

        int qtyUnderLength = boardsOnLengthSides;
        int qtyUnderWidth  = boardsOnWidthSides;

        BigDecimal underLengthPrice = underLength.getUnitPrice()
                .multiply(BigDecimal.valueOf(qtyUnderLength));
        BigDecimal underWidthPrice = underWidth.getUnitPrice()
                .multiply(BigDecimal.valueOf(qtyUnderWidth));

        materials.add(new Material(
                0,
                order.getId(),
                underLength.getProductId(),
                qtyUnderLength,
                underLengthPrice,
                "Understern på langsider",
                underLength.getProductName(),
                underLength.getProductDescription(),
                underLength.getLengthMM(),
                underLength.getUnitName(),
                underLength.getUnitShortName(),
                underLength.getUnitPrice()
        ));

        materials.add(new Material(
                0,
                order.getId(),
                underWidth.getProductId(),
                qtyUnderWidth,
                underWidthPrice,
                "Understern på for/bag",
                underWidth.getProductName(),
                underWidth.getProductDescription(),
                underWidth.getLengthMM(),
                underWidth.getUnitName(),
                underWidth.getUnitShortName(),
                underWidth.getUnitPrice()
        ));

        Material overLength = MaterialMapper.findOverSternForLength(lengthMM);
        Material overWidth  = MaterialMapper.findOverSternForLength(widthMM);

        int qtyOverLength = boardsOnLengthSides;
        int qtyOverWidth  = boardsOnWidthSides;

        BigDecimal overLengthPrice = overLength.getUnitPrice()
                .multiply(BigDecimal.valueOf(qtyOverLength));
        BigDecimal overWidthPrice = overWidth.getUnitPrice()
                .multiply(BigDecimal.valueOf(qtyOverWidth));

        materials.add(new Material(
                0,
                order.getId(),
                overLength.getProductId(),
                qtyOverLength,
                overLengthPrice,
                "Overstern på langsider",
                overLength.getProductName(),
                overLength.getProductDescription(),
                overLength.getLengthMM(),
                overLength.getUnitName(),
                overLength.getUnitShortName(),
                overLength.getUnitPrice()
        ));

        materials.add(new Material(
                0,
                order.getId(),
                overWidth.getProductId(),
                qtyOverWidth,
                overWidthPrice,
                "Overstern på for/bag",
                overWidth.getProductName(),
                overWidth.getProductDescription(),
                overWidth.getLengthMM(),
                overWidth.getUnitName(),
                overWidth.getUnitShortName(),
                overWidth.getUnitPrice()
        ));
    }


    private static void addRoofSheets(Order order, List<Material> materials) throws DatabaseException {

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

    private static BigDecimal calculateSlopePrice(Order order) {

        if (order.getRoofType() == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(order.getRoofType().getDegrees())
                .multiply(BigDecimal.valueOf(240.0));
    }
}