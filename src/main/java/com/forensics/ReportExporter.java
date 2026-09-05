package com.forensics;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportExporter {

    public static void exportToCSV(String destinationPath, List<FileRecord> records) throws IOException {
        try (FileWriter writer = new FileWriter(destinationPath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("Status", "File Path", "Size (Bytes)", "SHA-256 Hash")
                     .build())) {

            for (FileRecord rec : records) {
                csvPrinter.printRecord(
                    rec.getStatus(),
                    rec.getFilePath(),
                    rec.getFileSize(),
                    rec.getFileHash()
                );
            }
            csvPrinter.flush();
        }
    }
}
