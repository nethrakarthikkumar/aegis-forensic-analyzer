package com.forensics;

import java.sql.*;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/forensics_db";
    private static final String USER = "root"; 
    private static final String PASSWORD = "password"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveAnalysisSession(String targetPath, List<FileRecord> records) {
        String insertAnalysisSQL = "INSERT INTO analysis_history (user_id, target_path, summary) VALUES (?, ?, ?)";
        String insertRecordSQL = "INSERT INTO file_records (analysis_id, file_path, file_hash, status, file_size) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            int analysisId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(insertAnalysisSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, 1);
                stmt.setString(2, targetPath);
                stmt.setString(3, "Scanned " + records.size() + " item(s)");
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    analysisId = rs.getInt(1);
                }
            }

            if (analysisId != -1) {
                try (PreparedStatement stmt = conn.prepareStatement(insertRecordSQL)) {
                    for (FileRecord rec : records) {
                        stmt.setInt(1, analysisId);
                        stmt.setString(2, rec.getFilePath());
                        stmt.setString(3, rec.getFileHash());
                        stmt.setString(4, rec.getStatus());
                        stmt.setLong(5, rec.getFileSize());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
