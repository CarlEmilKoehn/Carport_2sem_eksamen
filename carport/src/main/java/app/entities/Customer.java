package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Customer {

    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private int postalCode;

    @Override
    public String toString() {
        return "Customer information" + "\n Full name: " + firstName + " " + lastName +
                "\n Address: " + address + " " + postalCode + "\n Email: " + email;
    }

}
