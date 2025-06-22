package com.gym.crm.app.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class YamlStorageHandler {

    private static final String STORAGE_FILE_NAME = "storage.yml";
    private Map<String, Object> dataBase;

    @PostConstruct
    public void init() {
        Yaml yaml = new Yaml();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(STORAGE_FILE_NAME)) {
            if (input != null) {
                Map<String, Object> loaded = yaml.load(input);
                dataBase = new LinkedHashMap<>();
                if (loaded != null) {
                    dataBase.putAll(loaded);
                }
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
