package com.gym.crm.app.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class YamlStorageHandler {
      @Value("${storage.file}")
    private String STORAGE_FILE_NAME;
    @Getter
    private Map<String, Object> dataBase;

    @PostConstruct
    public void init() {
        Yaml yaml = new Yaml();
        dataBase = new LinkedHashMap<>();
        try (InputStream input = new FileInputStream(STORAGE_FILE_NAME)) {
            Map<String, Object> loaded = yaml.load(input);
            if (loaded != null) {
                dataBase.putAll(loaded);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load YAML file", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        Yaml yaml = new Yaml();
        try (Writer writer = new FileWriter(STORAGE_FILE_NAME)) {
            yaml.dump(dataBase, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write YAML file", e);
        }
    }
}
