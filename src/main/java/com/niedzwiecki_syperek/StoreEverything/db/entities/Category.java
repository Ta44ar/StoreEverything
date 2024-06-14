package com.niedzwiecki_syperek.StoreEverything.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "CATEGORIES")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @NotBlank(message = "Category name cannot be blank.")
    @Size(min = 3, max = 20, message = "The category name must be between 3 and 20 characters long.")
    @Pattern(regexp = "^[a-z]+$", message = "The category name must consist of only lowercase letters.")
    private String name;

}