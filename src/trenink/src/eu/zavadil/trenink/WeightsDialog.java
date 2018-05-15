/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import java.util.List;
import java.util.Optional;
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
import eu.zavadil.trenink.model.Weight;

/**
 *
 * @author karel
 */
public class WeightsDialog {
    
    public static void displayDialog(Window owner) {
        WeightsDialog dialog = new WeightsDialog();
        Stage stage = dialog.prepareUI();   
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.showAndWait();
    }
    
    private void reloadData() {
        TypedQuery<Weight> q = Trenink.getEntityManager().createQuery("SELECT c FROM Weight c ORDER BY c.weight", Weight.class);
        List<Weight> weights = q.getResultList();
        //weights.sort(Weight.defaultComparator);
        weightsTable.setItems(FXCollections.observableArrayList(weights));        
    }
    
    private void saveWeight(float weight) {
        Trenink.getEntityManager().getTransaction().begin();
        try {
            Weight w = new Weight();
            w.setWeight(Float.valueOf(weight));
            Trenink.getEntityManager().persist(w);
            Trenink.getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            Trenink.getEntityManager().getTransaction().rollback();
        }
    }
    
    private void deleteWeight() {
        Weight w = weightsTable.getSelectionModel().getSelectedItem();
        Trenink.getEntityManager().getTransaction().begin();
        try {
            Trenink.getEntityManager().remove(w);
            Trenink.getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            Trenink.getEntityManager().getTransaction().rollback();
        }        
    }
    
    private Stage stage;
    private Button addButton;
    private Button removeButton;
    private Button closeButton;
    private TableView<Weight> weightsTable;
    
    private void refreshControls() {
        removeButton.setDisable(weightsTable.getSelectionModel().getSelectedItem() == null);
    }
    
    private Stage prepareUI() {
        Insets defaultInsets = new Insets(5);
        
        VBox root = new VBox();
        root.setPadding(defaultInsets);
        
        // horni cast - tlacitka        
        HBox top = new HBox();
        
        addButton = new Button();
        addButton.setText("Přidat");
        addButton.setOnAction(addWeightEventHandler);        
        HBox.setMargin(addButton, defaultInsets);
                
        removeButton = new Button();
        removeButton.setText("Odebrat");
        removeButton.setOnAction(removeWeightEventHandler);
        removeButton.setDisable(true); 
        HBox.setMargin(removeButton, defaultInsets);
        
        top.getChildren().addAll(addButton, removeButton);
        root.getChildren().add(top);
        
        // tabulka hmotnosti     
        weightsTable = new TableView<Weight>();
        VBox.setMargin(weightsTable, defaultInsets);
        
        TableColumn<Weight, String> weightCol = new TableColumn<>("Váha");
        weightCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getWeightFormatted()));
        weightCol.setMinWidth(238);
        weightCol.setMaxWidth(250);
        weightCol.setResizable(false);
        weightCol.setSortable(true);
        
        weightsTable.getColumns().add(weightCol);
        weightsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);           
        weightsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            refreshControls();
        });
        
        weightsTable.setEditable(false);        
                
        root.getChildren().add(weightsTable);
        
        /* load data */
        reloadData();
        
        closeButton = new Button();
        closeButton.setText("Zavřít");
        closeButton.setOnAction(closeWindowEventHandler);
        closeButton.setMinWidth(240);
        closeButton.setAlignment(Pos.CENTER);
        VBox.setMargin(closeButton, defaultInsets);
        root.getChildren().add(closeButton);
        
        Scene scene = new Scene(root, 250, 480);
        stage = new Stage();
        stage.setTitle("Váhy");
        stage.setScene(scene);
        stage.setMinWidth(250);
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
    
    private final EventHandler<ActionEvent> addWeightEventHandler = new EventHandler<ActionEvent>() {
        
        @Override
        public void handle(ActionEvent event) {
            Optional<Float> newWeight = AddWeightDialog.show(stage);
            if (newWeight != null) {
                saveWeight(newWeight.get());
                reloadData();
            }       
        }

    };
    
    private final EventHandler<ActionEvent> removeWeightEventHandler = new EventHandler<ActionEvent>() {
        
        @Override
        public void handle(ActionEvent event) {
            if (MessageDialog.showYesNoQuestion("Opravdu si přejete smazat tuto váhu?")) {
                deleteWeight();
                reloadData();
            }
        }

    };
}