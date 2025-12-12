package app.entities;

import app.persistence.AdminMapper;
import lombok.*;

@Data
@NoArgsConstructor

public class Admin {
    private String adminEmail;
    private String adminPassword;
    private String adminFirstname;
    private String adminLastname;

    public Admin(String adminEmail, String adminPassword, String adminFirstname, String adminLastname) {
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFirstname = adminFirstname;
        this.adminLastname = adminLastname;
    }
    public Admin(String adminEmail, String adminFirstname, String adminLastname) {
        this.adminEmail = adminEmail;
        this.adminPassword = null;
        this.adminFirstname = adminFirstname;
        this.adminLastname = adminLastname;
    }
}