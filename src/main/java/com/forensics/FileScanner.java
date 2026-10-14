package com.forensics;

import java.io.File;
import java.util.*;

public class FileScanner {

    private static final Map<String, String> THREAT_TYPES = new LinkedHashMap<>();

    static {
        THREAT_TYPES.put(".exe", "Windows executable program");
        THREAT_TYPES.put(".dll", "Windows library (DLL)");
        THREAT_TYPES.put(".bat", "Windows batch script");
        THREAT_TYPES.put(".cmd", "Windows command script");
        THREAT_TYPES.put(".vbs", "VBScript (Visual Basic script)");
        THREAT_TYPES.put(".ps1", "PowerShell script");
        THREAT_TYPES.put(".sh", "Linux shell script");
    }

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

        String threatType = null;
        for (Map.Entry<String, String> entry : THREAT_TYPES.entrySet()) {
            if (name.endsWith(entry.getKey())) {
                threatType = entry.getValue();
                break;
            }
        }

        String status = "CLEAN";

        if (file.isHidden()) {
            status = "HIDDEN";
        } else if (threatType != null) {
            status = "SUSPICIOUS";
        }

        FileRecord record = new FileRecord(file.getAbsolutePath(), hash, file.length(), status);

        if (threatType != null) {
            record.setThreatType(threatType);
        } else if (file.isHidden()) {
            record.setThreatType("Hidden file");
        }

        records.add(record);
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
                        rec.setThreatType("Duplicate file");
                    }
                }
            }
        }
    }
}
