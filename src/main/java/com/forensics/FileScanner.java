package com.forensics;

import java.io.File;
import java.util.*;

public class FileScanner {

    private static final Set<String> SUSPICIOUS_EXTENSIONS = new HashSet<>(
        Arrays.asList(".exe", ".bat", ".vbs", ".sh", ".ps1", ".dll", ".cmd")
    );

    public List<FileRecord> scanDirectory(String pathStr) {
        List<FileRecord> records = new ArrayList<>();
        File target = new File(pathStr);

        if (!target.exists()) {
            return records;
        }

        if (target.isFile()) {
            processSingleFile(target, records);
        } else if (target.isDirectory()) {
            scanRecursive(target, records);
        }

        markDuplicates(records);
        return records;
    }

    private void scanRecursive(File dir, List<FileRecord> records) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanRecursive(file, records);
                } else {
                    processSingleFile(file, records);
                }
            }
        }
    }

    private void processSingleFile(File file, List<FileRecord> records) {
        String hash = HashUtils.calculateSHA256(file.getAbsolutePath());
        String name = file.getName().toLowerCase();

        boolean isSuspicious = SUSPICIOUS_EXTENSIONS.stream().anyMatch(name::endsWith);
        String status = "CLEAN";

        if (file.isHidden()) {
            status = "HIDDEN";
        } else if (isSuspicious) {
            status = "SUSPICIOUS";
        }

        records.add(new FileRecord(file.getAbsolutePath(), hash, file.length(), status));
    }

    private void markDuplicates(List<FileRecord> records) {
        Map<String, List<FileRecord>> hashMap = new HashMap<>();

        for (FileRecord rec : records) {
            hashMap.computeIfAbsent(rec.getFileHash(), k -> new ArrayList<>()).add(rec);
        }

        for (Map.Entry<String, List<FileRecord>> entry : hashMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                for (FileRecord rec : entry.getValue()) {
                    if ("CLEAN".equals(rec.getStatus())) {
                        rec.setStatus("DUPLICATE");
                    }
                }
            }
        }
    }
}
