package com.syber.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "customer")
    private AppUser appUser;

    @OneToMany(mappedBy="customer")
    private List<Account> accounts;
    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String nationalId;

    @Column(unique = true, nullable = false)
    private String email;

    public Customer(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
