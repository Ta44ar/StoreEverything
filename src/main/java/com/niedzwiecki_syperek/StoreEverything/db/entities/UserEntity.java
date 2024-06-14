package com.niedzwiecki_syperek.StoreEverything.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotBlank(message = "User's name cannot be blank.")
    @Size(min = 3, max = 20, message = "User's name must be between 3 and 20 characters long.")
    @Pattern(regexp = "^[A-Z][a-z]+$", message = "The user's name should begin with an uppercase letter and then consist only of lowercase letters.")
    private String firstName;

    @NotBlank(message = "The user's forname cannot be blank.")
    @Size(min = 3, max = 50, message = "User's forname must be between 3 and 20 characters long.")
    @Pattern(regexp = "^[A-Z][a-z]+$", message = "The user's forname should begin with an uppercase letter and then consist only of lowercase letters.")
    private String lastName;

    @NotBlank(message = "The user's login cannot be blank.")
    @Size(min = 3, max = 20, message = "User's login must be between 3 and 20 characters long.")
    @Pattern(regexp = "^[a-z]+$", message = "The user's login should consist only of lowercase letters.")
    private String username;

    @NotBlank(message = "The user's login cannot be blank.")
    @Size(min = 5, message = "User password must be at least 5 characters long.")
    private String password;

    @Min(value = 18, message = "The user must be an adult.")
    private int age;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLES", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();
}
