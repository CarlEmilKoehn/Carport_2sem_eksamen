package app.entities;

import lombok.*;

@Data
@AllArgsConstructor

public class Admin {
    private String adminEmail;
    private String adminPassword;
    private String adminFirstname;
    private String adminLastname;
}