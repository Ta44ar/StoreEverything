package com.niedzwiecki_syperek.StoreEverything.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotBlank(message = "Imię użytkownika nie może być puste")
    @Size(min = 3, max = 20, message = "Imię użytkownika musi mieć od 3 do 20 znaków")
    @Pattern(regexp = "^[A-Z][a-z]+$", message = "Imię użytkownika powinno rozpoczynać się od wielkiej litery, a następnie składać się wyłącznie z małych liter.")
    private String firstName;

    @NotBlank(message = "Nazwisko użytkownika nie może być puste")
    @Size(min = 3, max = 50, message = "Nazwisko użytkownika musi mieć od 3 do 50 znaków")
    @Pattern(regexp = "^[A-Z][a-z]+$", message = "Nazwisko użytkownika powinno rozpoczynać się od wielkiej litery, a następnie składać się wyłącznie z małych liter.")
    private String lastName;

    @NotBlank(message = "Login użytkownika nie może być pusty")
    @Size(min = 3, max = 20, message = "Login użytkownika musi mieć od 3 do 20 znaków")
    @Pattern(regexp = "^[a-z]+$", message = "Login użytkownika musi składać się z małych liter")
    private String username;

    @NotBlank(message = "Hasło użytkownika nie może być puste")
    @Size(min = 5, message = "Hasło użytkownika musi mieć co najmniej 5 znaków")
    private String password;

    @Min(value = 18, message = "Użytkownik musi być pełnoletni")
    private int age;

    @Column(name = "AKTYWNY")
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles;
}
