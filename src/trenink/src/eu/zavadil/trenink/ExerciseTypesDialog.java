/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.persistence.TypedQuery;
import eu.zavadil.trenink.model.ExerciseType;

/**
 *
 * @author karel
 */
public class ExerciseTypesDialog {
    
    public static void displayDialog(Window owner) {
        ExerciseTypesDialog dialog = new ExerciseTypesDialog();
        Stage stage = dialog.prepareUI();   
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.showAndWait();
    }
    
    private void reloadData() {
        TypedQuery<ExerciseType> q = Trenink.getEntityManager().createQuery("SELECT c FROM ExerciseType c ORDER BY c.id", ExerciseType.class);
        List<ExerciseType> types = q.getResultList();
        typesTable.setItems(FXCollections.observableArrayList(types));        
    }
    
    private void saveExerciseType(ExerciseType t) {
        Trenink.getEntityManager().getTransaction().begin();
        try {            
            Trenink.getEntityManager().persist(t);
            Trenink.getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            Trenink.getEntityManager().getTransaction().rollback();
            MessageDialog.show(e.getMessage());
        }
    }
    
    private void deleteExerciseType() {
        ExerciseType t = typesTable.getSelectionModel().getSelectedItem();
        Trenink.getEntityManager().getTransaction().begin();
        try {
            Trenink.getEntityManager().remove(t);
            Trenink.getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            Trenink.getEntityManager().getTransaction().rollback();
        }
    }
    
    private Stage stage;
    private Button addButton;
    private Button editButton;
    private Button removeButton;
    private Button closeButton;
    private TableView<ExerciseType> typesTable;
    
    private void refreshControls() {
        removeButton.setDisable(typesTable.getSelectionModel().getSelectedItem() == null);
        editButton.setDisable(typesTable.getSelectionModel().getSelectedItem() == null);
    }
    
    private Stage prepareUI() {
        Insets defaultInsets = new Insets(5);
        
        VBox root = new VBox();
        root.setPadding(defaultInsets);
        
        // horni cast - tlacitka        
        HBox top = new HBox();
        
        addButton = new Button();
        addButton.setText("Přidat");
        addButton.setOnAction(addExerciseTypeEventHandler);        
        HBox.setMargin(addButton, defaultInsets);
                
        editButton = new Button();
        editButton.setText("Upravit");
        editButton.setOnAction(editExerciseTypeEventHandler);
        editButton.setDisable(true); 
        HBox.setMargin(editButton, defaultInsets);
        
        removeButton = new Button();
        removeButton.setText("Odebrat");
        removeButton.setOnAction(removeExerciseTypeEventHandler);
        removeButton.setDisable(true); 
        HBox.setMargin(removeButton, defaultInsets);
        
        top.getChildren().addAll(addButton, editButton, removeButton);
        root.getChildren().add(top);
        
        // tabulka
        typesTable = new TableView<ExerciseType>();
        VBox.setMargin(typesTable, defaultInsets);
        
        TableColumn<ExerciseType, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getId().toString()));
        idCol.setMinWidth(30);
        idCol.setMaxWidth(50);
        idCol.setResizable(false);
        idCol.setSortable(true);
        
        TableColumn<ExerciseType, String> nameCol = new TableColumn<>("Název");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.setMinWidth(200);
        nameCol.setMaxWidth(200);
        nameCol.setResizable(false);
        nameCol.setSortable(true);
        
        typesTable.getColumns().addAll(idCol, nameCol);
        typesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);           
        typesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            refreshControls();
        });
        
        typesTable.setEditable(false);        
                
        root.getChildren().add(typesTable);
        
        /* load data */
        reloadData();
        
        closeButton = new Button();
        closeButton.setText("Zavřít");
        closeButton.setOnAction(closeWindowEventHandler);
        closeButton.setMinWidth(290);
        closeButton.setAlignment(Pos.CENTER);
        VBox.setMargin(closeButton, defaultInsets);
        root.getChildren().add(closeButton);
        
        Scene scene = new Scene(root, 300, 480);
        stage = new Stage();
        stage.setTitle("Druhy cviků");
        stage.setScene(scene);
        stage.setMinWidth(290);
        stage.setMinHeight(480);
        stage.setResizable(false);       
        
        return stage;
    }
    
    private final EventHandler<ActionEvent> closeWindowEventHandler = new EventHandler<ActionEvent>() {
        
        @Override
        public void handle(ActionEvent event) {
            stage.close();       
        }

    };
    
    private final EventHandler<ActionEvent> addExerciseTypeEventHandler = new EventHandler<ActionEvent>() {
        
        @Override
        public void handle(ActionEvent event) {
            ExerciseType t = EditExerciseTypeDialog.show(stage, new ExerciseType());
            if (t != null) {
                saveExerciseType(t);
                reloadData();
            }       
        }

    };
    
    private final EventHandler<ActionEvent> editExerciseTypeEventHandler = new EventHandler<ActionEvent>() {
        
        @Override
        public void handle(ActionEvent event) {
            ExerciseType t = typesTable.getSelectionModel().getSelectedItem();
            ExerciseType newET = EditExerciseTypeDialog.show(stage, t);
            if (newET != null) {
                saveExerciseType(newET);
                reloadData();
            }       
        }

    };
    
    private final EventHandler<ActionEvent> removeExerciseTypeEventHandler = new EventHandler<ActionEvent>() {
        
        @Override
        public void handle(ActionEvent event) {
            if (MessageDialog.showYesNoQuestion("Opravdu si přejete smazat tento druh cviku?")) {
                deleteExerciseType();
                reloadData();
            }
        }

    };
}