package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    //TODO: Make createOrder save the materials as well.
    public static void createOrder(String email, String orderStatus, int widthMM, int heightMM, BigDecimal price, RoofType roofType, Shed shed) throws DatabaseException{

        String sql = "INSERT INTO public.user_order (user_email, order_status, width_mm, height_mm, order_price, roof_type_id, shed_id) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, email);
            stmt.setString(2, orderStatus);
            stmt.setInt(3, widthMM);
            stmt.setInt(4, heightMM);
            stmt.setBigDecimal(5, price);
            stmt.setInt(6, roofType.getId());

            if (shed != null) {
                stmt.setInt(7, shed.getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static void changeOrderStatus(int orderId, String orderStatus) throws DatabaseException {

        String sql = "UPDATE public.user_order SET order_status = ? WHERE user_order_id = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, orderStatus);
            stmt.setInt(2, orderId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static void changeOrderPrice(int orderId, BigDecimal orderPrice, Admin admin, String comment) throws DatabaseException{

        String updateSql = "UPDATE public.user_order  " +
                           "SET order_price = ? " +
                           "WHERE user_order_id = ?";

        String insertSql = "INSERT INTO public.user_order_change (user_order_id, admin_email, admin_note) " +
                           "VALUES (?, ?, ?)";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try(PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

                updateStmt.setBigDecimal(1, orderPrice);
                updateStmt.setInt(2, orderId);
                updateStmt.executeUpdate();

                insertStmt.setInt(1, orderId);
                insertStmt.setString(2, admin.getEmail());
                if (comment == null || comment.isBlank()) {
                    insertStmt.setNull(3, Types.VARCHAR);
                } else {
                    insertStmt.setString(3, comment);
                }
                insertStmt.executeUpdate();

                connection.commit();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static List<Order> getAllOrders() throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT user_order_id, user_email, order_status, width_mm, height_mm, order_price, created_at, " +
                     "roof_type_id, roof_type_name, roof_type_deg, roof_type_price, " +
                     "shed_id, shed_width_mm, shed_length_mm " +
                     "FROM public.user_order " +
                     "JOIN roof_type ON roof_type_id = roof_type_id " +
                     "LEFT JOIN shed ON shed_id = shed_id; ";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int orderID = rs.getInt("user_order_id");
                String email = rs.getString("user_email");
                String status = rs.getString("order_status");

                RoofType roofType = new RoofType(
                        rs.getInt("roof_type_id"),
                        rs.getString("roof_type_name"),
                        rs.getInt("roof_type_deg"),
                        rs.getBigDecimal("roof_type_price")
                );

                int widthMM = rs.getInt("width_mm");
                int heightMM = rs.getInt("height_mm");
                Timestamp createdAt = rs.getTimestamp("created_at");
                BigDecimal totalPrice = rs.getBigDecimal("order_price");

                List<Material> materials = MaterialMapper.getAllMaterialsFromOrder(orderID);
                List<Comment> comments = getAllCommentsFromOrder(orderID);

                Shed shed = new Shed(
                        rs.getInt("shed_id"),
                        rs.getInt("shed_width_mm"),
                        rs.getInt("shed_length_mm")
                );

                if (shed.getId() == 0 || shed.getWidthMM() == 0 || shed.getLengthMM() == 0) {
                    orders.add(new Order(orderID, email, status, roofType, widthMM, heightMM, createdAt, materials, comments, totalPrice));
                } else {
                    orders.add(new OrderWithShed(orderID, email, status, roofType, widthMM, heightMM, createdAt, materials, comments, totalPrice, shed));
                }
            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static List<Comment> getAllCommentsFromOrder(int orderID) throws DatabaseException {

        List<Comment> comments = new ArrayList<>();

        String sql = "SELECT user_order_id, admin_email, admin_note, created_at " +
                     "FROM public.user_order_change " +
                     "WHERE user_order_change_id = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, orderID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                int orderChangeId = rs.getInt("user_order_change_id");
                String whichAdminMadeThisComment = rs.getString("admin_email");
                String note = rs.getString("admin_note");
                Timestamp createdAt = rs.getTimestamp("created_at");

                comments.add(new Comment(orderChangeId, orderID, note, createdAt, whichAdminMadeThisComment));
            }

            return comments;
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }
}