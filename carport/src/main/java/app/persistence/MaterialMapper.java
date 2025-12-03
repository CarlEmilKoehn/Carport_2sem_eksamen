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

        String sql = "SELECT " +
                     "om.order_material_id, om.user_order_id, om.material_product_id, om.quantity, om.note, om.total_price, " +
                     "mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM order_material om " +
                     "JOIN material_product mp ON om.material_product_id = mp.material_product_id " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "WHERE om.user_order_id = ?;";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, orderId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int orderMaterialId = rs.getInt("order_material_id");
                int userOrderId = rs.getInt("user_order_id");
                int materialProductId = rs.getInt("material_product_id");
                int quantity = rs.getInt("quantity");
                String note = rs.getString("note");
                BigDecimal totalPrice = rs.getBigDecimal("total_price");

                String productName = rs.getString("material_product_name");
                String productDescription = rs.getString("material_product_description");
                Integer lengthMM = rs.getObject("length_mm", Integer.class);
                BigDecimal unitPrice = rs.getBigDecimal("material_price");
                String unitName = rs.getString("unit_name");
                String unitShortName = rs.getString("unit_short_name");

                Material material = new Material(
                        orderMaterialId,
                        userOrderId,
                        materialProductId,
                        quantity,
                        totalPrice,
                        note,
                        productName,
                        productDescription,
                        lengthMM,
                        unitName,
                        unitShortName,
                        unitPrice
                );

                materials.add(material);
            }

            return materials;
            
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static Material findPostForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "WHERE mp.length_mm >= ? " +
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

            throw new DatabaseException("No pillars were found, of the length >= ", String.valueOf(minLengthMM));

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static Material findRemForLength(int minLengthMM) throws DatabaseException{

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "JOIN material_category mc ON mp.material_category_id = mc.material_category_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mc.material_category_name = 'Rem' " +
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

            throw new DatabaseException("No rems were found, of the length >= ", String.valueOf(minLengthMM));

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
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
                     "AND mc.material_category_name = 'Spær' " +
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

            throw new DatabaseException("No rafters were found, of the length >= ", String.valueOf(minLengthMM));

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static Material findUnderSternForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND  mp.material_product_description ILIKE '%understernbrædder%' " +
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

            throw new DatabaseException("No under stern were found, of the length >= ", String.valueOf(minLengthMM));

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static Material findOverSternForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mp.material_product_description ILIKE '%oversternbrædder%' " +
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

            throw new DatabaseException("No over stern were found, of the length >= ", String.valueOf(minLengthMM));

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static Material findRoofSheetForLength(int minLengthMM) throws DatabaseException {

        String sql = "SELECT " +
                     "mp.material_product_id, mp.material_product_name, mp.material_product_description, mp.length_mm, mp.material_price, " +
                     "u.unit_name, u.unit_short_name " +
                     "FROM material_product mp " +
                     "JOIN unit u ON mp.unit_id = u.unit_id " +
                     "WHERE mp.length_mm >= ? " +
                     "AND mp.material_product_description ILIKE '%tagplader monteres på spær%' " +
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

            throw new DatabaseException("No roof sheets were found, of the length >= ", String.valueOf(minLengthMM));

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }
}