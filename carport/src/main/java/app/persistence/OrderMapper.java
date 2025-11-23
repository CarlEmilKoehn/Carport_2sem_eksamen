package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public List<Order> getAllOrders() throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT user_order_id, user_email, order_status, length_mm, width_mm, created_at " +
                     "FROM public.\"user_order\" JOIN ";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("user_order_id");
                String email = rs.getString("user_email");
                String status = rs.getString("order_status");
                RoofType roofType = new RoofType(rs.getInt("roof_type_id"), rs.getString("roof_type_name"), rs.getInt("roof_type_deg"), rs.getBigDecimal("roof_type_price"));
                int widthMM = rs.getInt("width_mm");
                int heightMM = rs.getInt("height_mm");
                Timestamp createdAt = rs.getTimestamp("created_at");
                BigDecimal totalPrice = rs.getBigDecimal("");
                List<Material> materials = getAllMaterialsFromOrder(id);
                List<Comment> comments = getAllCommentsFromOrder(id);
                Shed shed = new Shed(rs.getInt("shed_id"), rs.getInt("shed_width_MM"), rs.getInt("shed_length_MM"));

                if (shed == null) {
                    orders.add(new Order(id, email, status, roofType, widthMM, heightMM, createdAt, materials, comments, totalPrice));
                } else {
                    orders.add(new OrderWithShed(id, email, status, roofType, widthMM, heightMM, createdAt, materials, comments, totalPrice, shed));
                }
            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public List<Order> getAllOrdersByEmail(String email) throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = "";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }
}
