package com.forensics;

import javafx.application.Application;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainView extends Application {

    private TableView<FileRecord> table = new TableView<>();
    private ObservableList<FileRecord> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Digital Forensics Analyzer");

        TextField pathField = new TextField();
        pathField.setPromptText("Select a file or enter directory path...");
        pathField.setPrefWidth(350);

        Button browseBtn = new Button("Browse File");
        Button scanBtn = new Button("Start Scan");
        Button exportBtn = new Button("Export Report");

        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select File to Analyze");
            File selected = chooser.showOpenDialog(primaryStage);
            if (selected != null) {
                pathField.setText(selected.getAbsolutePath());
            }
        });

        scanBtn.setOnAction(e -> {
            String targetPath = pathField.getText();
            if (!targetPath.isEmpty()) {
                FileScanner scanner = new FileScanner();
                List<FileRecord> results = scanner.scanDirectory(targetPath);
                DatabaseManager.saveAnalysisSession(targetPath, results);
                data.setAll(results);
            }
        });

        exportBtn.setOnAction(e -> {
            if (data.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No scan results available to export!");
                alert.show();
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Forensic Report");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File saveFile = chooser.showSaveDialog(primaryStage);

            if (saveFile != null) {
                try {
                    ReportExporter.exportToCSV(saveFile.getAbsolutePath(), data);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report successfully exported to " + saveFile.getName());
                    alert.show();
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to export report: " + ex.getMessage());
                    alert.show();
                }
            }
        });

        HBox topBar = new HBox(10, pathField, browseBtn, scanBtn, exportBtn);
        topBar.setPadding(new Insets(10));

        TableColumn<FileRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        TableColumn<FileRecord, String> pathCol = new TableColumn<>("File Path");
        pathCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFilePath()));
        pathCol.setPrefWidth(300);

        TableColumn<FileRecord, Long> sizeCol = new TableColumn<>("Size (Bytes)");
        sizeCol.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getFileSize()).asObject());

        TableColumn<FileRecord, String> hashCol = new TableColumn<>("SHA-256 Hash");
        hashCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFileHash()));
        hashCol.setPrefWidth(300);

        table.getColumns().addAll(statusCol, pathCol, sizeCol, hashCol);
        table.setItems(data);

        BorderPane layout = new BorderPane();
        layout.setTop(topBar);
        layout.setCenter(table);

        Scene scene = new Scene(layout, 950, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
