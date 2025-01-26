package com.jakubjedi.myphotonest.photos.controller;

import com.jakubjedi.myphotonest.config.PhotoUploadProperties;
import com.jakubjedi.myphotonest.photos.repository.MediaFileRepository;
import com.jakubjedi.myphotonest.photos.service.MediaFileCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/photos")
@Slf4j
@RequiredArgsConstructor
public class PhotosController {
    private final PhotoUploadProperties props;
    private final MediaFileCreator mediaFileCreator;
    private final MediaFileRepository mediaFileRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file,
                                              @RequestParam(name = "originCreationDate", required = false) String originCreationDate) {
        try {
            mediaFileCreator.create(file, originCreationDate);
            return ResponseEntity.ok("Image uploaded");
        } catch (Exception e) {
            log.error("Upload error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload error");
        }
    }

    @GetMapping("/list")
    public List<String> listPhotos() {
        File folder = new File(props.getDirectory());
        return Arrays.stream(folder.listFiles())
                .filter(File::isFile)
                .map(File::getName).toList();
    }

    @GetMapping("/preview")
    public ResponseEntity<byte[]> previewPhoto(@RequestParam(name = "imageName") String imageName) throws IOException {
        var image = Files.readAllBytes(Paths.get(props.getDirectory(), imageName));
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }

}
