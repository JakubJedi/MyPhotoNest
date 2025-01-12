package com.jakubjedi.myphotonest.photos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
public class MediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String fileName;

    @NotBlank
    @Column(name = "sha256_hash", unique = true, nullable = false, length = 64)
    private String sha256Hash;

    @NotNull
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime originCreationDate;

    @NotNull
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime uploadDate;

    @NotBlank
    private String filePath;

    @PrePersist
    public void prePersist() {
        this.uploadDate = LocalDateTime.now();
    }
}
