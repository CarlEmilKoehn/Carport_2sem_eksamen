package app.persistence;
import app.entities.Material;
import app.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {

    public static List<Material> getAllMaterialsFromOrder(int orderId) throws DatabaseException {
        List<Material> materials = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    mp.material_product_id, " +
                        "    mp.material_category_id, " +
                        "    mc.material_category_name, " +
                        "    mp.unit_id, " +
                        "    mp.length_mm, " +
                        "    mp.material_product_name, " +
                        "    mp.material_product_description, " +
                        "    mp.material_price " +
                        "FROM order_material om " +
                        "JOIN material_product mp " +
                        "    ON om.material_product_id = mp.material_product_id " +
                        "JOIN material_category mc " +
                        "    ON mp.material_category_id = mc.material_category_id " +
                        "WHERE om.user_order_id = ?;";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int materialProductId = rs.getInt("material_product_id");
                    int materialCategoryId = rs.getInt("material_category_id");
                    int unitId = rs.getInt("unit_id");
                    int lengthMM = rs.getInt("length_mm");
                    String materialProductName = rs.getString("material_product_name");
                    String materialProductDescription = rs.getString("material_product_description");
                    String materialCategoryName = rs.getString("material_category_name");
                    double materialPrice = rs.getDouble("material_price");

                    materials.add(new Material
                            (materialProductId,
                             materialCategoryId,
                             unitId,
                             lengthMM,
                             materialProductName,
                             materialProductDescription,
                             materialCategoryName,
                             materialPrice));
                }
            }
            return materials;

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke få forbindelse til databasen", e.getMessage());
        }
    }

}
