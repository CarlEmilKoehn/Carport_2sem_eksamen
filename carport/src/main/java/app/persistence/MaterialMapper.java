package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    private static int toDbLength(int mm) {
        return (int) Math.ceil(mm / 10.0);
    }

    private static Integer fromDbLength(Integer dbLen) {
        if (dbLen == null) return null;
        return (dbLen > 0 && dbLen < 1000) ? dbLen * 10 : dbLen;
    }

    private static Material findByMinLengthAndCategory(int minLengthMM, String category) throws DatabaseException {

        String sql = """
            SELECT
                mp.material_product_id,
                mp.material_product_name,
                mp.material_product_description,
                mp.length_mm,
                mp.material_price,
                u.unit_name,
                u.unit_short_name
            FROM material_product mp
            JOIN unit u ON mp.unit_id = u.unit_id
            JOIN material_category mc ON mp.material_category_id = mc.material_category_id
            WHERE mc.material_category_name = ?
              AND mp.length_mm >= ?
            ORDER BY mp.length_mm ASC
            LIMIT 1
            """;

        int dbMin = toDbLength(minLengthMM);

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, category);
            ps.setInt(2, dbMin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Material(
                            rs.getInt("material_product_id"),
                            rs.getString("material_product_name"),
                            rs.getString("material_product_description"),
                            fromDbLength(rs.getObject("length_mm", Integer.class)),
                            rs.getBigDecimal("material_price"),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name")
                    );
                }
            }

            throw new DatabaseException(
                    "Manglende materiale",
                    "Ingen materialer i kategori '" + category + "' med min længde " + minLengthMM + " mm"
            );

        } catch (SQLException e) {
            throw new DatabaseException("DB-fejl", "findByMinLengthAndCategory failed: " + e.getMessage());
        }
    }

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

            try (ResultSet rs = ps.executeQuery()) {
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
                            fromDbLength(rs.getObject("length_mm", Integer.class)),
                            rs.getString("unit_name"),
                            rs.getString("unit_short_name"),
                            rs.getBigDecimal("material_price")
                    ));
                }
            }

            return materials;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af materialer til ordren.", "getAllMaterialsFromOrder failed: " + e.getMessage());
        }
    }

    public static Material findPostForLength(int minLengthMM) throws DatabaseException {
        return findByMinLengthAndCategory(minLengthMM, "Stolper");
    }

    public static Material findRemForLength(int minLengthMM) throws DatabaseException {
        return findByMinLengthAndCategory(minLengthMM, "Rem/Spær");
    }

    public static Material findRafterForLength(int minLengthMM) throws DatabaseException {
        return findByMinLengthAndCategory(minLengthMM, "Rem/Spær");
    }

    public static Material findUnderSternForLength(int minLengthMM) throws DatabaseException {
        return findByMinLengthAndCategory(minLengthMM, "Understern");
    }

    public static Material findOverSternForLength(int minLengthMM) throws DatabaseException {
        return findByMinLengthAndCategory(minLengthMM, "Overstern");
    }

    public static Material findRoofSheetForLength(int minLengthMM) throws DatabaseException {
        return findByMinLengthAndCategory(minLengthMM, "Tagplader");
    }

    public static Material findCladdingForHeight(int minLengthMM) throws DatabaseException {
        return findByMinLengthAndCategory(minLengthMM, "Beklædning");
    }
}
