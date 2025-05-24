package com.pharmacy.supplierinventory.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "suppliers")
public class Supplier {
    @Id
    private String supplierId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Contact is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact must be a 10-digit number")
    private String contact;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
}
