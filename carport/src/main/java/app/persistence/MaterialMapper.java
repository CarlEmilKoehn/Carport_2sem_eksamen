package app.persistence;
import app.entities.Material;
import app.entities.Order;
import app.exceptions.DatabaseException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    public static List<Material> getAllMaterialsFromOrder(int orderId) throws DatabaseException {

        List<Material> materials = new ArrayList<>();

        String sql = """
            SELECT
                om.order_material_id,
                om.customer_order_id,
                om.material_product_id,
                om.quantity,
                om.note,
                om.total_price,
                mp.material_product_name,
                mp.material_product_description,
                mp.length_mm,
                mp.material_price,
                u.unit_name,
                u.unit_short_name
            FROM order_material om
            JOIN material_product mp ON om.material_product_id = mp.material_product_id
            JOIN unit u ON mp.unit_id = u.unit_id
            WHERE om.customer_order_id = ?
            """;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                materials.add(new Material(
                        rs.getInt("order_material_id"),
                        rs.getInt("customer_order_id"),
                        rs.getInt("material_product_id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("total_price"),
                        rs.getString("note"),
                        rs.getString("material_product_name"),
                        rs.getString("material_product_description"),
                        rs.getObject("length_mm", Integer.class),
                        rs.getString("unit_name"),
                        rs.getString("unit_short_name"),
                        rs.getBigDecimal("material_price")
                ));
            }

            return materials;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af materialer til ordren.", "getAllMaterialsFromOrder failed for orderId= " + orderId + ": " + e.getMessage());
        }
    }

    public static Material findPostForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mc.material_category_name = 'Stolper' " +
                     "ORDER BY mp.length_mm ASC " +
                     "LIMIT 1;";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, minLengthMM);

            try(ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            rs.getInt("length_mm"),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException("Ingen stolper fundet i den nødvendige længde.", "findPostForLength no match for minLengthMM = " + minLengthMM);

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl ved opslag af stolper.", "findPostForLength SQL failed for minLengthMM = " + minLengthMM + ": " + e.getMessage());
        }
    }

    public static Material findRemForLength(int minLengthMM) throws DatabaseException{

        String sql =
                "SELECT " +
                        "mp.material_product_id, mp.material_product_name, mp.material_product_description, " +
                        "mp.length_mm, mp.material_price, " +
                        "u.unit_name, u.unit_short_name " +
                        "FROM material_product mp " +
                        "JOIN unit u ON mp.unit_id = u.unit_id " +
                        "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                        "WHERE mp.length_mm >= ? " +
                        "AND mc.material_category_name = 'Rem/Spær' " +
                        "ORDER BY mp.length_mm ASC " +
                        "LIMIT 1";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, minLengthMM);

            try(ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            rs.getInt("length_mm"),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException("Ingen remme fundet i den nødvendige længde.", "findRemForLength no match for minLengthMM = " + minLengthMM);

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl ved opslag af remme.", "findRemForLength SQL failed for minLengthMM = " + minLengthMM + ": " + e.getMessage());
        }
    }

    public static Material findRafterForLength(int minLengthMM) throws DatabaseException{

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mc.material_category_name = 'Rem/Spær' " +
                     "ORDER BY mp.length_mm ASC " +
                     "LIMIT 1;";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, minLengthMM);

            try(ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            rs.getInt("length_mm"),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException("Ingen spær fundet i den nødvendige længde.", "findRafterForLength no match for minLengthMM = " + minLengthMM);

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl ved opslag af spær.", "findRafterForLength SQL failed for minLengthMM = " + minLengthMM + ": " + e.getMessage());
        }
    }

    public static Material findUnderSternForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mc.material_category_name = 'Understern' " +
                     "ORDER BY mp.length_mm ASC " +
                     "LIMIT 1;";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, minLengthMM);

            try(ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            rs.getInt("length_mm"),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException("Ingen understern fundet i den nødvendige længde.", "findUnderSternForLength no match for minLengthMM = " + minLengthMM);

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl ved opslag af understern.", "findUnderSternForLength SQL failed for minLengthMM = " + minLengthMM + ": " + e.getMessage());
        }
    }

    public static Material findOverSternForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mc.material_category_name = 'Overstern' " +
                     "ORDER BY mp.length_mm ASC " +
                     "LIMIT 1;";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, minLengthMM);

            try(ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            rs.getInt("length_mm"),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException("Ingen overstern fundet i den nødvendige længde.", "findOverSternForLength no match for minLengthMM = " + minLengthMM);

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl ved opslag af overstern.", "findOverSternForLength SQL failed for minLengthMM = " + minLengthMM + ": " + e.getMessage());
        }
    }

    public static Material findRoofSheetForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mc.material_category_name = 'Tagplader' " +
                     "ORDER BY mp.length_mm ASC " +
                     "LIMIT 1;";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, minLengthMM);

            try(ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            rs.getInt("length_mm"),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException("Ingen tagplader fundet i den nødvendige længde.", "findRoofSheetForLength no match for minLengthMM = " + minLengthMM);

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl ved opslag af tagplader.", "findRoofSheetForLength SQL failed for minLengthMM = " + minLengthMM + ": " + e.getMessage());
        }
    }

    public static Material findCladdingForHeight(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, " +
                     "mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mc.material_category_name = 'Beklædning' " +
                     "ORDER BY mp.length_mm ASC " +
                     "LIMIT 1;";

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, minLengthMM);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            rs.getInt("length_mm"),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException("Ingen beklædningsbrædder fundet i den nødvendige længde.", "findCladdingForHeight no match for minLengthMM = " + minLengthMM);

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl ved opslag af beklædning.", "findCladdingForHeight SQL failed for minLengthMM = " + minLengthMM + ": " + e.getMessage());
        }
    }
}