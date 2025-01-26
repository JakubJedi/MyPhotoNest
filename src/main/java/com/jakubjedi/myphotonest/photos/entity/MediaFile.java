package com.jakubjedi.myphotonest.photos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

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
    private String originCreationDate;

    @NotNull
    private String uploadDate;

    @NotBlank
    private String filePath;

}
