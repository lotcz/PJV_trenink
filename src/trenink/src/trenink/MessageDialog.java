/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trenink;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

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
}
