package com.jakubjedi.myphotonest.photos.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class HashService {
    public String calculateSHA256(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        return DigestUtils.sha256Hex(fileBytes);
    }
}
