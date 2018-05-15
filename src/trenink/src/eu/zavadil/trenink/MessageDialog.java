/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import java.util.Optional;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.util.Pair;

/**
 *
 * @author karel
 */
public class MessageDialog {
    
    public static void show(String text) {
        Dialog dialog = new Dialog<>();
        ButtonType saveButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(saveButtonType);
        dialog.getDialogPane().setContent(new Label(text));        
        dialog.showAndWait();
    }
    
    public static boolean showYesNoQuestion(String question) {
        Dialog<Boolean> dialog = new Dialog<Boolean>();
        ButtonType yesButtonType = new ButtonType("Ano", ButtonBar.ButtonData.YES);
        ButtonType noButtonType = new ButtonType("Ne", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(yesButtonType, noButtonType);
        dialog.getDialogPane().setContent(new Label(question));
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == yesButtonType) {                
                return true;
            } else {
                return false;
            }
        });
        Optional<Boolean> result = dialog.showAndWait();
        return (result.isPresent() && result.get());
    }
}
