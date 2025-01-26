package com.jakubjedi.myphotonest.photos.service;

import com.google.common.base.Preconditions;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Saves {@link MultipartFile} locally.
 * <p>
 * Data is saved in a temporary dir but keeping the original file name.
 * Resources are released on {@code close}.
 */
public class MultipartFileSaver implements Closeable {

    private final MultipartFile multipartFile;
    private Path savedFile;

    public MultipartFileSaver(MultipartFile multipartFile) {
        Preconditions.checkArgument(StringUtils.isNotBlank(multipartFile.getOriginalFilename()), "Multipart file must contain a not blank Original Filename");
        this.multipartFile = multipartFile;
    }

    private static Path provisionFileInTempDir(final String fileName) throws IOException {
        var tempDir = Files.createTempDirectory(null);
        Path sanitizedFilepath = sanitize(tempDir, fileName);
        return Files.createFile(sanitizedFilepath);
    }

    private static Path sanitize(Path basePath, String originalFilename) {
        String name = FilenameUtils.getName(originalFilename);
        String sanitizedFilename = FilenameUtils.concat(basePath.toString(), name);
        return Path.of(sanitizedFilename);
    }

    public Path getSavedFile() throws IOException {
        this.savedFile = provisionFileInTempDir(multipartFile.getOriginalFilename());
        multipartFile.transferTo(savedFile);
        return savedFile;
    }

    @Override
    public void close() {
        if (isNotClosed()) {
            FileUtils.deleteQuietly(savedFile.getParent().toFile());
            setAsClosed();
        }
    }

    private boolean isNotClosed() {
        return savedFile != null;
    }

    private void setAsClosed() {
        savedFile = null;
    }
}
