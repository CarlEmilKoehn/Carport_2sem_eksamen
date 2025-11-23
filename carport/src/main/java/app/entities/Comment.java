package app.entities;

import java.sql.Timestamp;

public class Comment {

    private int id;
    private String note;
    private Timestamp createdAt;
    private String whichAdminMadeThisComment;
}
