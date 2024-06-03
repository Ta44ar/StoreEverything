package com.niedzwiecki_syperek.StoreEverything.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
public class Information {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    @Size(min = 3, max = 20, message = "Tytuł musi mieć od 3 do 20 znaków")
    @NotNull(message = "Tytuł nie może być pusty")
    private String title;

    @Column(nullable = false, length = 500)
    @Size(min = 5, max = 500, message = "Treść musi mieć od 5 do 500 znaków")
    @NotNull(message = "Treść nie może być pusta")
    private String content;

    private String link;

    @Column(nullable = false)
    @NotNull(message = "Data dodania nie może być pusta")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateAdded;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Kategoria nie może być pusta")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "Użytkownik nie może być pusty")
    private User user;

    @PrePersist
    protected void onCreate() {
        dateAdded = LocalDate.now();
    }
}
