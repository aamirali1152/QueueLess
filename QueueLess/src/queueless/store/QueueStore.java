package queueless.store;

import queueless.model.QueueToken;
import queueless.model.TokenStatus;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class QueueStore {
    private final Path file;

    public QueueStore(String fileName) {
        this.file = Paths.get(fileName);
    }

    public synchronized void save(Collection<QueueToken> tokens) {
        try {
            Path parent = file.getParent();
            if (parent != null) Files.createDirectories(parent);
            try (BufferedWriter out = Files.newBufferedWriter(file)) {
                out.write("token,id,name,service,priority,createdAt,status");
                out.newLine();
                for (QueueToken t : tokens) {
                    out.write(t.csv());
                    out.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not save queue data: " + e.getMessage(), e);
        }
    }

    public synchronized List<String> readRaw() {
        if (!Files.exists(file)) return new ArrayList<>();
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
