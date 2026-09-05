package com.forensics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

    public List<FileRecord> scanDirectory(String targetPath) {
        List<FileRecord> records = new ArrayList<>();
        File rootDir = new File(targetPath);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            System.err.println("Invalid directory path: " + targetPath);
            return records;
        }

        traverseAndScan(rootDir, records);
        return records;
    }

    private void traverseAndScan(File file, List<FileRecord> records) {
        File[] files = file.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                traverseAndScan(f, records);
            } else {
                String status = "NORMAL";
                if (f.isHidden() || f.getName().startsWith(".")) {
                    status = "SUSPICIOUS_HIDDEN";
                }

                String hash = HashUtils.calculateSHA256(f.getAbsolutePath());
                FileRecord record = new FileRecord(f.getAbsolutePath(), hash, f.length(), status);
                records.add(record);
            }
        }
    }
}
