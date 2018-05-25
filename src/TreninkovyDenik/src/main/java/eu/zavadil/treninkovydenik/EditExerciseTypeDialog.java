/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.treninkovydenik;

import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;
import eu.zavadil.treninkovydenik.model.ExerciseType;

/**
 *
 * @author karel
 */
public class EditExerciseTypeDialog {
    
    public static ExerciseType show(Window owner, ExerciseType t) {
        
        ExerciseType originalExerciseType = t;
        
        Dialog<ExerciseType> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Cvik");
        if (originalExerciseType.getId() == null) {
            dialog.setHeaderText("Přidat nový druh cviku.");
        } else {
            dialog.setHeaderText("Upravit druh cviku.");
        }
        
        ButtonType saveButtonType = new ButtonType("Uložit", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Storno", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        if (originalExerciseType.getId() != null) {
            grid.add(new Label("ID:"), 0, 0);
            grid.add(new Label(originalExerciseType.getId().toString()), 1, 0);
        }
        
        TextField nameField = new TextField(originalExerciseType.getName());
        nameField.setPromptText("Název");
        
        grid.add(new Label("Název:"), 0, 1);
        grid.add(nameField, 1, 1);

        // Enable/Disable save button
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);        
        Platform.runLater(() -> nameField.requestFocus());
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {                
                originalExerciseType.setName(nameField.getText());
                return originalExerciseType;
            }
            return null;
        });

        Optional<ExerciseType> result = dialog.showAndWait();  
        if (result.isPresent()) {
            return result.get();            
        } else {
            return null;
        }
                
    }

}
