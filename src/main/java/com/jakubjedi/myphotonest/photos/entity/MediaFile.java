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
    private Long id;

    @NotBlank
    private String fileName;

    @NotBlank
    private String sha256Hash;

    @NotNull
    private LocalDateTime originCreationDate;

    @NotNull
    private LocalDateTime uploadDate;

    @PrePersist
    public void prePersist() {
        this.uploadDate = LocalDateTime.now();
    }
}
