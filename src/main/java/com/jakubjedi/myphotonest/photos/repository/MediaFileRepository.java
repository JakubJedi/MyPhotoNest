package com.jakubjedi.myphotonest.photos.repository;

import com.jakubjedi.myphotonest.photos.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findByFileName(String fileName);
}
