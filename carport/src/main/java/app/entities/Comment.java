package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor

public class Comment {

    private int id;
    private int CustomerOrderId;
    private String note;
    private Timestamp createdAt;
    private String whichAdminMadeThisComment;
}
