/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

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
import eu.zavadil.trenink.model.Weight;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import javafx.scene.control.DatePicker;

/**
 *
 * @author karel
 */
public class GetDateDialog {
    
    public static Optional<Date> show(Window owner, String dialogTitle, String headerText, Date defaultDate) {
        
        Dialog<Date> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(dialogTitle);
        dialog.setHeaderText(headerText);
        
        ButtonType saveButtonType = new ButtonType("Uložit", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Storno", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker dateField = new DatePicker();
        dateField.setPromptText("vložte datum");
        
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        
        if (defaultDate == null) {
            saveButton.setDisable(true);
        } else {
            ZoneId defaultZoneId = ZoneId.systemDefault();        
            Instant instant = defaultDate.toInstant();
            dateField.setValue(instant.atZone(defaultZoneId).toLocalDate());
        }
        
        grid.add(new Label("Datum:"), 0, 0);
        grid.add(dateField, 1, 0);

        dateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue == null);
        });

        dialog.getDialogPane().setContent(grid);        
        Platform.runLater(() -> dateField.requestFocus());
               
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {  
                ZoneId defaultZoneId = ZoneId.systemDefault();
                return Date.from(dateField.getValue().atStartOfDay(defaultZoneId).toInstant());
            }
            return null;
        });

        return dialog.showAndWait();  
    }

}
