package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OrderMapper {

    public static List<Order> getAllOrders() throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT uo.user_order_id, uo.user_email, uo.order_status, uo.width_mm, uo.height_mm, uo.created_at, " +
                     "rt.roof_type_id, rt.roof_type_name, rt.roof_type_deg, rt.roof_type_price, " +
                     "s.shed_id, s.shed_width_mm, s.shed_length_mm, " +
                     "uoc.user_order_change_id, uoc.admin_note, uoc.created_at AS change_created_at " +
                     "FROM public.user_order uo " +
                     "INNER JOIN roof_type rt ON uo.roof_type_id = rt.roof_type_id " +
                     "INNER JOIN shed s ON uo.shed_id = s.shed_id " +
                     "INNER JOIN user_order_change uoc ON uo.user_order_id = uoc.user_order_id; ";

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
                List<Material> materials = MaterialMapper.getAllMaterialsFromOrder(id);
                List<Comment> comments = rs.Stream
                        .toList();
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

    public static List<Order> getAllOrdersByEmail(String email) throws DatabaseException {

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

    public static List<Comment> getAllCommentsFromOrder(int orderID) throws DatabaseException {

        String sql = "SELECT * ";

        try(Connection connection = ConnectionPool.instance.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {


            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }

        return null;
    }
}
