package com.jakubjedi.myphotonest.photos.controller;

import com.jakubjedi.myphotonest.config.PhotoUploadProperties;
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
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/photos")
public class PhotosController {
    private final PhotoUploadProperties props;

    public PhotosController(PhotoUploadProperties props) {
        this.props = props;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            var basePath = props.getDirectory();
            ensureUploadDirExistence(Paths.get(basePath));
            Path path = Paths.get(basePath, file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("Image uploaded");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload error");
        }
    }

    public void ensureUploadDirExistence(final Path uploadDir) {
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
