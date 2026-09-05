package com.forensics;

import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  Digital Forensics Analyzer Initialized  ");
        System.out.println("==========================================");

        FileScanner scanner = new FileScanner();
        String scanDir = System.getProperty("user.dir");

        System.out.println("Scanning directory: " + scanDir);
        List<FileRecord> results = scanner.scanDirectory(scanDir);

        System.out.println("\n--- Scan Results ---");
        for (FileRecord record : results) {
            System.out.println(record);
        }
        System.out.println("\nTotal files scanned: " + results.size());
    }
}
