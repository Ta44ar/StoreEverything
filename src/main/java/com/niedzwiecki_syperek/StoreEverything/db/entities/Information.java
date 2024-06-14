package com.niedzwiecki_syperek.StoreEverything.db.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "INFORMATIONS")
@Data
public class Information {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    @Size(min = 3, max = 20, message = "The title must be between 3 and 20 characters long.")
    @NotNull(message = "The title can't be empty.")
    private String title;

    @Column(nullable = false, length = 500)
    @Size(min = 5, max = 500, message = "The content must be between 5 and 500 characters long.")
    @NotNull(message = "The content can't be empty.")
    private String content;

    @Column(unique = true)
    private String shareableLink;

    @Column(nullable = false)
    @NotNull(message = "The date of addition cannot be blank.")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateAdded;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotNull(message = "Category field cannot be blank.")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User cannot be blank.")
    private UserEntity userEntity;

    @ManyToMany
    @JoinTable(
            name = "information_shared_users",
            joinColumns = @JoinColumn(name = "information_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> sharedWithUsers;

    @PrePersist
    protected void onCreate() {
        dateAdded = LocalDate.now();
    }
}
