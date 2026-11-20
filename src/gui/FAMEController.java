package gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class FAMEController implements Initializable {
    @FXML private VBox attributeList;
    @FXML private ComboBox<String> propertiesCombo;
    @FXML private TextArea plaintextArea;
    @FXML private TextArea ciphertextArea;
    @FXML private TextArea decryptedArea;
    
    private boolean darkMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate ComboBox with available .properties files in current directory
        File dir = new File(".");
        File[] propFiles = dir.listFiles((d, name) -> name.endsWith(".properties"));
        if (propFiles != null) {
            List<String> fileNames = new ArrayList<>();
            for (File f : propFiles) {
                fileNames.add(f.getName());
            }
            propertiesCombo.setItems(FXCollections.observableArrayList(fileNames));
            if (!fileNames.isEmpty()) {
                propertiesCombo.getSelectionModel().selectFirst();
            }
        }
        // Add one attribute field by default
        addAttributeField();
    }
    
    @FXML
    private void handleAddAttribute() {
        if (attributeList.getChildren().size() >= 10) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Limit Reached");
            alert.setHeaderText(null);
            alert.setContentText("You can only add up to 10 attributes.");
            alert.showAndWait();
            return;
        }
        addAttributeField();
    }

    
    /** Helper to add a new attribute TextField (with remove button) */
    private void addAttributeField() {
        HBox hbox = new HBox(5);
        TextField attrField = new TextField();
        attrField.setPromptText("ATTRIBUTE:VALUE");
        Button removeBtn = new Button("-");
        // Remove this attribute field when '-' button is clicked
        removeBtn.setOnAction(e -> attributeList.getChildren().remove(hbox));
        hbox.getChildren().addAll(attrField, removeBtn);
        attributeList.getChildren().add(hbox);
    }
    
    @FXML
    private void handleEncrypt() {
        String selectedFile = propertiesCombo.getValue();
        if (selectedFile == null || selectedFile.isEmpty()) {
            showError("Please select a properties file.");
            return;
        }
        // Collect all entered attributes
        List<String> attributes = new ArrayList<>();
        for (Node node : attributeList.getChildren()) {
            if (node instanceof HBox attrBox) {
                if (!attrBox.getChildren().isEmpty() && attrBox.getChildren().get(0) instanceof TextField tf) {
                    String attrText = tf.getText().trim();
                    if (attrText.isEmpty()) continue;

                    // Check format: must contain one ":" and both sides non-empty
                    String[] parts = attrText.split(":", 2);
                    if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
                        showError("Invalid attribute format. Use ATTRIBUTE:VALUE for each entry.");
                        return;
                    }

                    attributes.add(attrText);
                }
            }
        }

        if (attributes.isEmpty()) {
            showError("Please enter at least one attribute.");
            return;
        }
        String plaintext = plaintextArea.getText();
        if (plaintext == null) plaintext = "";
        
        // Perform encryption and decryption using the wrapper
        try {
            FameWrapper.EncryptionResult result = FameWrapper.encryptAndDecrypt(selectedFile, attributes, plaintext);
            ciphertextArea.setText(result.ciphertext);
            decryptedArea.setText(result.decryptedMessage);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Encryption/Decryption failed: " + ex.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        attributeList.getChildren().clear();
        plaintextArea.clear();
        ciphertextArea.clear();
        decryptedArea.clear();
        addAttributeField(); // Reset Attributes
    }
    
    @FXML
    private void handleToggleTheme() {
        Node anyNode = attributeList; // Any node to take Scene
        if (anyNode.getScene() == null) return;

        if (darkMode) {
            anyNode.getScene().getStylesheets().clear();
            anyNode.getScene().getStylesheets().add(getClass().getResource("light.css").toExternalForm());
        } else {
            anyNode.getScene().getStylesheets().clear();
            anyNode.getScene().getStylesheets().add(getClass().getResource("dark.css").toExternalForm());
        }
        darkMode = !darkMode;
    }

    /** Utility to show an error alert dialog */
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}