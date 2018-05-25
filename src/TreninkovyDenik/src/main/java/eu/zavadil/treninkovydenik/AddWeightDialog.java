/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.treninkovydenik;

import java.util.Optional;
import java.text.ParseException;
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
import eu.zavadil.treninkovydenik.model.Weight;

/**
 *
 * @author karel
 */
public class AddWeightDialog {
    
    public static Optional<Float> show(Window owner) {
        
        // Create the custom dialog.
        Dialog<Float> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Přidat váhu");
        dialog.setHeaderText("Přidat novou váhu kettlebellu.");
        
        // Set the button types.
        ButtonType saveButtonType = new ButtonType("Uložit", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Storno", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField weightField = new TextField();
        weightField.setPromptText("Váha");
        
        grid.add(new Label("Váha:"), 0, 0);
        grid.add(weightField, 1, 0);
        grid.add(new Label("kg"), 2, 0);

        // Enable/Disable save button
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);        
        Platform.runLater(() -> weightField.requestFocus());
               
        // Convert the result to a float
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {                
                Float result = null;
                try {
                    result = Weight.parseWeight(weightField.getText());
                    return result;
                } catch (ParseException ex) {
                    MessageDialog.show("Neplatný formát čísla!");
                }                
            }
            return null;
        });

        return dialog.showAndWait();  
    }

}
