/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import java.util.Optional;
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
import eu.zavadil.trenink.model.Exercise;
import eu.zavadil.trenink.model.ExerciseType;
import eu.zavadil.trenink.model.Weight;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javax.persistence.TypedQuery;

/**
 *
 * @author karel
 */
public class EditExerciseDialog {
        
    Window ownerWindow;
    Dialog<Exercise> dialog;
            
    Exercise originalExercise;
    
    ChoiceBox<ExerciseType> typeField;
    TextField seriesField;
    TextField repetitionsField;
    ComboBox<Weight> weightField;
    
    Node saveButton;
    
    public EditExerciseDialog(Window owner, Exercise e) {
        ownerWindow = owner;
        originalExercise = e;
        dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Cvik");

        if (originalExercise.getId() == null) {
            dialog.setHeaderText("Přidat nový cvik.");
        } else {
            dialog.setHeaderText("Upravit cvik.");
        }
        
        ButtonType saveButtonType = new ButtonType("Uložit", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Storno", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
               
        TypedQuery<ExerciseType> q = Trenink.getEntityManager().createQuery("SELECT c FROM ExerciseType c ORDER BY c.name", ExerciseType.class);
        List<ExerciseType> types = q.getResultList();
        typeField = new ChoiceBox(FXCollections.observableArrayList(types));
        grid.add(new Label("Cvik:"), 0, 0);
        grid.add(typeField, 1, 0);
        if (originalExercise.getExerciseType() != null) {
            typeField.getSelectionModel().select(originalExercise.getExerciseType());
        }

        seriesField = new TextField();
        grid.add(new Label("Počet sérií:"), 0, 1);
        grid.add(seriesField, 1, 1);
        if (originalExercise.getSeries() != null) {
            seriesField.setText(originalExercise.getSeries().toString());
        }
        
        repetitionsField = new TextField();
        grid.add(new Label("Počet opakování v sérii:"), 0, 2);
        grid.add(repetitionsField, 1, 2);
        if (originalExercise.getRepetitions() != null) {
            repetitionsField.setText(originalExercise.getRepetitions().toString());
        }
        
        TypedQuery<Weight> q2 = Trenink.getEntityManager().createQuery("SELECT c FROM Weight c ORDER BY c.weight", Weight.class);
        List<Weight> weights = q2.getResultList();
        weightField = new ComboBox(FXCollections.observableArrayList(weights));
        weightField.setEditable(true);
        grid.add(new Label("Váha:"), 0, 3);
        grid.add(weightField, 1, 3);
        grid.add(new Label("kg"), 2, 3);
        if (originalExercise.getWeight() != null) {
            weightField.getEditor().setText(originalExercise.getWeight().toString());
        }
        
        // Enable/Disable save button
        saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        validateForm();
       
        // Do some validation (using the Java 8 lambda syntax).
        typeField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });

        seriesField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });
        
        repetitionsField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });
        
        weightField.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });
        
        dialog.getDialogPane().setContent(grid);        
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (validateAndShowMessage()) {
                    originalExercise.setExerciseType(typeField.getSelectionModel().getSelectedItem());
                    originalExercise.setSeries(Long.parseLong(seriesField.getText()));
                    originalExercise.setRepetitions(Long.parseLong(repetitionsField.getText()));
                    originalExercise.setWeight(Float.parseFloat(weightField.getEditor().textProperty().getValue()));                
                    return originalExercise;
                }
            }
            return null;
        });
        
    }
    
    public void validateForm() {
        saveButton.setDisable(!isFormValid());
    }
    
    public boolean isFormValid() {
        return (
            (typeField.getSelectionModel().getSelectedItem() != null) &&
            (Helpers.isValidInt(seriesField.getText())) &&
            (Helpers.isValidInt(repetitionsField.getText())) &&
            (Helpers.isValidFloat(weightField.getEditor().getText()))
        );
    }
    
    public boolean validateAndShowMessage() {
        if (typeField.getSelectionModel().getSelectedItem() != null) {
            if (Helpers.isValidInt(seriesField.getText())) {
                if (Helpers.isValidInt(repetitionsField.getText())) {
                    if (Helpers.isValidFloat(weightField.getEditor().getText())) {
                        return true;
                    } else {
                        MessageDialog.show("Vložte prosím počet opakování v sérii.");
                        return false;
                    }
                } else {
                    MessageDialog.show("Vložte prosím počet opakování v sérii.");
                    return false;
                }
            } else {
                MessageDialog.show("Vložte prosím počet sérií.");
                return false;
            }
        } else {
            MessageDialog.show("Zvolte prosím druh cviku.");
            return false;
        }               
    }
    
    public Exercise show() {
        Optional<Exercise> result = dialog.showAndWait();  
        if (result.isPresent()) {
            return result.get();            
        } else {
            return null;
        }
    }

    public static Exercise show(Window owner, Exercise e) {
        EditExerciseDialog d = new EditExerciseDialog(owner, e);
        return d.show();        
    }
    
}
