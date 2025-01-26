package com.jakubjedi.myphotonest.photos.service;

import com.jakubjedi.myphotonest.config.PhotoUploadProperties;
import com.jakubjedi.myphotonest.photos.entity.MediaFile;
import com.jakubjedi.myphotonest.photos.repository.MediaFileRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MediaFileCreator {
    private final PhotoUploadProperties props;
    private final HashService hashService;
    private final MediaFileRepository mediaFileRepository;

    public void create(final MultipartFile file, final String originCreationDate) throws IOException {
        try (var fileSaver = new MultipartFileSaver(file)) {
            Path tempFile = fileSaver.getSavedFile();
            var fileSHA256 = hashService.calculateSHA256(tempFile);

            ensureUploadDirExistence(Paths.get(props.getDirectory()));
            Path path = Paths.get(props.getDirectory(), "%s_%s".formatted(fileSHA256, file.getOriginalFilename()));
            persistMetadata("%s_%s".formatted(fileSHA256, file.getOriginalFilename()), originCreationDate, fileSHA256, path);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
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

    private void persistMetadata(final String fileName,
                                 final String originCreationDate,
                                 final String fileSHA256,
                                 final Path path) {
        var mediaFile = new MediaFile();
        mediaFile.setFileName(fileName);
        mediaFile.setSha256Hash(fileSHA256);
        mediaFile.setFilePath(path.toAbsolutePath().normalize().toString());
        var persistDateTime = LocalDateTime.now();
        if (StringUtils.isBlank(originCreationDate)) {
            mediaFile.setOriginCreationDate(persistDateTime.toString());
        } else {
            mediaFile.setOriginCreationDate(LocalDateTime.parse(originCreationDate).toString());
        }
        mediaFile.setUploadDate(persistDateTime.toString());
        mediaFileRepository.save(mediaFile);
    }

}
