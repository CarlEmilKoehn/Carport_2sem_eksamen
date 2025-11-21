package app.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class Admin {
    private String adminEmail;
    private String adminPassword;
    private String adminFirstname;
    private String adminLastname;

}