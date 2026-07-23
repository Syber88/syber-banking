package com.syber.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy="customer")
    private List<Account> accounts;
    private String firstName;
    private String lastName;

    @NotBlank(message = "ID is required")
    private String nationalId;

    @Email(message = "Email should be valid.")
    private String email;
    private String passwordHash;

    public Customer(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
