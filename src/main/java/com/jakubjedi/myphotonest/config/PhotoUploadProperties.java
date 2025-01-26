package com.jakubjedi.myphotonest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "myphotonest.upload")
public class PhotoUploadProperties {
    private String directory;
}
