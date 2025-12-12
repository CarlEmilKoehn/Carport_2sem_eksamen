package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import com.zaxxer.hikari.pool.HikariProxyResultSet;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static int createOrder(Order order) throws DatabaseException{

        String orderSql = "INSERT INTO public.user_order " +
                          "(user_email, order_status, width_mm, height_mm, length_mm, order_price, roof_type_id, shed_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                          "RETURNING user_order_id";

        String materialSql = "INSERT INTO public.order_material " +
                             "(user_order_id, material_product_id, quantity, note, total_price) " +
                             "VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            connection.setAutoCommit(false);

            try(PreparedStatement orderStmt = connection.prepareStatement(orderSql);
                PreparedStatement materialStmt = connection.prepareStatement(materialSql)) {

                orderStmt.setString(1, order.getEmail());
                orderStmt.setString(2, order.getStatus());
                orderStmt.setInt(3, order.getWidthMM());
                orderStmt.setInt(4, order.getHeightMM());
                orderStmt.setInt(5, order.getLengthMM());
                orderStmt.setBigDecimal(5, order.getTotalPrice());
                orderStmt.setInt(6, order.getRoofType().getId());

                Shed shed = null;
                if (order instanceof OrderWithShed ows) {
                    shed = ows.getShed();
                }

                if (shed != null) {
                    orderStmt.setInt(7, shed.getId());
                } else {
                    orderStmt.setNull(7, Types.INTEGER);
                }

                ResultSet rs = orderStmt.executeQuery();

                int orderId;

                if (rs.next()) {
                    orderId = rs.getInt("user_order_id");
                } else {
                    throw new DatabaseException("Order was not confirmed", "No id returned");
                }

                if (order.getMaterials() != null) {
                    for (Material m : order.getMaterials()) {

                        int materialProductId = m.getProductId();
                        int quantity = m.getQuantity();
                        String materialNote = m.getNote();
                        BigDecimal materialPrice = m.getTotalPrice();

                        materialStmt.setInt(1, orderId);
                        materialStmt.setInt(2, materialProductId);
                        materialStmt.setInt(3, quantity);

                        if (materialNote == null || materialNote.isBlank()) {
                            materialStmt.setNull(4, Types.VARCHAR);
                        } else {
                            materialStmt.setString(4, materialNote);
                        }

                        materialStmt.setBigDecimal(5, materialPrice);

                        materialStmt.addBatch();
                    }
                    materialStmt.executeBatch();
                }

                connection.commit();

                return orderId;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static Order getOrderByOrderId(int orderId) throws DatabaseException {

        String sql = "SELECT " +
                "uo.user_order_id, uo.user_email, uo.order_status, uo.width_mm, uo.height_mm, uo.length_mm, uo.order_price, uo.created_at, " +
                "rt.roof_type_id, rt.roof_type_name, rt.roof_type_deg, rt.roof_type_price, " +
                "s.shed_id, s.shed_width_mm, s.shed_length_mm " +
                "FROM public.user_order uo " +
                "JOIN roof_type rt ON uo.roof_type_id = rt.roof_type_id " +
                "LEFT JOIN shed s ON uo.shed_id = s.shed_id" +
                "WHERE uo.order_id = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, orderId);

            try(ResultSet rs = ps.executeQuery()) {

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
                int lengthMM = rs.getInt("length_mm");
                Timestamp createdAt = rs.getTimestamp("created_at");
                BigDecimal totalPrice = rs.getBigDecimal("order_price");

                List<Material> materials = MaterialMapper.getAllMaterialsFromOrder(orderId);
                List<Comment> comments = getAllCommentsFromOrder(orderId);

                Shed shed = new Shed(
                        rs.getInt("shed_id"),
                        rs.getInt("shed_width_mm"),
                        rs.getInt("shed_length_mm")
                );

                if (shed.getId() == 0 || shed.getWidthMM() == 0 || shed.getLengthMM() == 0) {
                    return new Order(orderId, email, status, roofType, widthMM, heightMM, lengthMM, createdAt, materials, comments, totalPrice);
                } else {
                    return new OrderWithShed(orderId, email, status, roofType, widthMM, heightMM, lengthMM, createdAt, materials, comments, totalPrice, shed);
                }
            }

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
                insertStmt.setString(2, admin.getAdminEmail());
                if (comment == null || comment.isBlank()) {
                    insertStmt.setNull(3, Types.VARCHAR);
                } else {
                    insertStmt.setString(3, comment);
                }
                insertStmt.executeUpdate();

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static List<Order> getAllOrders() throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT " +
                     "uo.user_order_id, uo.user_email, uo.order_status, uo.width_mm, uo.height_mm, uo.length_mm, uo.order_price, uo.created_at, " +
                     "rt.roof_type_id, rt.roof_type_name, rt.roof_type_deg, rt.roof_type_price, " +
                     "s.shed_id, s.shed_width_mm, s.shed_length_mm " +
                     "FROM public.user_order uo " +
                     "JOIN roof_type rt ON uo.roof_type_id = rt.roof_type_id " +
                     "LEFT JOIN shed s ON uo.shed_id = s.shed_id";

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
                int lengthMM = rs.getInt("length_mm");
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
                    orders.add(new Order(orderID, email, status, roofType, widthMM, heightMM, lengthMM, createdAt, materials, comments, totalPrice));
                } else {
                    orders.add(new OrderWithShed(orderID, email, status, roofType, widthMM, heightMM, lengthMM, createdAt, materials, comments, totalPrice, shed));
                }
            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static List<Comment> getAllCommentsFromOrder(int orderID) throws DatabaseException {

        List<Comment> comments = new ArrayList<>();

        String sql = "SELECT user_order_change_id, user_order_id, admin_email, admin_note, created_at " +
                     "FROM public.user_order_change " +
                     "WHERE user_order_id = ? " +
                     "ORDER BY created_at";

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

    public static RoofType getRoofTypeBySlopeDegrees(Integer roofDeg) throws DatabaseException{

        String sql = "SELECT roof_type_id, roof_type_name, roof_type_deg, roof_type_price FROM public.roof_type WHERE roof_type_deg = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, roofDeg);

            try(ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new RoofType(
                            rs.getInt("roof_type_id"),
                            rs.getString("roof_type_name"),
                            rs.getInt("roof_type_deg"),
                            rs.getBigDecimal("roof_type_price")
                    );
                }
            }

            throw new DatabaseException("No roofType with the slope = " + roofDeg);

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }
}