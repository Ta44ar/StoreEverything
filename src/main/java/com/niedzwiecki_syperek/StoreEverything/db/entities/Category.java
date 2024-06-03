package com.niedzwiecki_syperek.StoreEverything.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @NotBlank(message = "Nazwa kategorii nie może być pusta")
    @Size(min = 3, max = 20, message = "Nazwa kategorii musi mieć od 3 do 20 znaków")
    @Pattern(regexp = "^[a-z]+$", message = "Nazwa kategorii musi składać się z samych małych liter")
    private String name;

}