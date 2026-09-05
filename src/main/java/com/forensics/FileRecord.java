package com.forensics;

public class FileRecord {
    private String filePath;
    private String fileHash;
    private long fileSize;
    private String status;

    public FileRecord(String filePath, String fileHash, long fileSize, String status) {
        this.filePath = filePath;
        this.fileHash = fileHash;
        this.fileSize = fileSize;
        this.status = status;
    }

    public String getFilePath() { return filePath; }
    public String getFileHash() { return fileHash; }
    public long getFileSize() { return fileSize; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
