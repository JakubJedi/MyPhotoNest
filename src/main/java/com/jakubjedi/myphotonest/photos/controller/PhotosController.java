package com.jakubjedi.myphotonest.photos.controller;

import com.jakubjedi.myphotonest.config.PhotoUploadProperties;
import com.jakubjedi.myphotonest.photos.entity.MediaFile;
import com.jakubjedi.myphotonest.photos.repository.MediaFileRepository;
import com.jakubjedi.myphotonest.photos.service.HashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/photos")
@Slf4j
public class PhotosController {
    private final PhotoUploadProperties props;
    private final HashService hashService;
    private final MediaFileRepository mediaFileRepository;

    public PhotosController(PhotoUploadProperties props, HashService hashService, MediaFileRepository mediaFileRepository) {
        this.props = props;
        this.hashService = hashService;
        this.mediaFileRepository = mediaFileRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            var basePath = props.getDirectory();
            ensureUploadDirExistence(Paths.get(basePath));
            Path path = Paths.get(basePath, file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            var fileSHA256 = hashService.calculateSHA256(path);

            // Persist metadata
            var mediaFile = new MediaFile();
            mediaFile.setFileName(file.getOriginalFilename());
            mediaFile.setSha256Hash(fileSHA256);
            mediaFile.setFilePath(path.toAbsolutePath().toString());
            mediaFile.setOriginCreationDate(LocalDateTime.now().minusDays(2));
            mediaFileRepository.save(mediaFile);
            return ResponseEntity.ok("Image uploaded");
        } catch (Exception e) {
            log.error("Upload error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload error");
        }
    }

    private void ensureUploadDirExistence(final Path uploadDir) {
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new RuntimeException("Error when creating dir: " + uploadDir, e);
            }
        }
    }

    @GetMapping("/list")
    public List<String> listPhotos() {
        File folder = new File(props.getDirectory());
        return Arrays.stream(folder.listFiles())
                .filter(File::isFile)
                .map(File::getName).toList();
    }

}
