/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import eu.zavadil.trenink.model.Exercise;
import eu.zavadil.trenink.model.Weight;
import eu.zavadil.trenink.model.Workout;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 *
 * @author karel
 */
public class MainWindowController implements Initializable {
    
    private List<Workout> workouts;
        
    @FXML public Button addWorkoutButton;
    @FXML public TableView<Workout> workoutsTable;
    @FXML public TableColumn<Workout, String> dateColumn;
    
    @FXML public VBox exerciseSectionVBox;
    @FXML public Label workoutDateLabel;
    @FXML public Button addExerciseButton;
    @FXML public Button removeExerciseButton;
        
    @FXML public TableView<Exercise> exercisesTable;
    @FXML public TableColumn<Exercise, String> exerciseColumn;
    @FXML public TableColumn<Exercise, String> seriesColumn;
    @FXML public TableColumn<Exercise, String> repetitionsColumn;
    @FXML public TableColumn<Exercise, String> weightColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshWorkoutForm();
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDateFormattedLong()));       
        loadWorkouts();
        workoutsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        workoutsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            refreshWorkoutForm();
        });
        
        exercisesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        exercisesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            refreshExercisesButtons();
        });
        exerciseColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getExerciseType().getName()));
        seriesColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSeries().toString()));
        repetitionsColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getRepetitions().toString()));
        weightColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(Weight.formatWeight(data.getValue().getWeight())));
    }   
    
    private WorkoutLoader loader;
    private Thread loaderThread;
    private Label noWorkoutDataLabel = new Label("V deníku nejsou žádné tréninky.");
    private Label loadingWorkoutsDataLabel = new Label("Načítám tréninky z databáze.");
    private Label errorLoadingWorkoutsDataLabel = new Label("Při načítání tréninků se vyskytla chyba.");
    
    private void loadWorkouts() {
        workoutsTable.setPlaceholder(loadingWorkoutsDataLabel);
        workoutsTable.getItems().clear();
        loader = new WorkoutLoader();
        loader.setOnSucceeded(workoutsLoaded);        
        loader.setOnFailed(workoutsLoadingFailed);
        loaderThread = new Thread(loader);
        loaderThread.start();
    }
        
    private EventHandler<WorkerStateEvent> workoutsLoaded = new EventHandler<WorkerStateEvent>() {
        
        @Override
        public void handle(WorkerStateEvent t) {
            ObservableList<Workout> result = loader.getValue();
            if (result.size() == 0) {
                 workoutsTable.setPlaceholder(noWorkoutDataLabel);
            }
            workoutsTable.setItems(result);
        }
    
    };
    
    private EventHandler<WorkerStateEvent> workoutsLoadingFailed = new EventHandler<WorkerStateEvent>() {
        
        @Override
        public void handle(WorkerStateEvent t) {
            workoutsTable.setPlaceholder(errorLoadingWorkoutsDataLabel);
        }
    
    };
    
    private Workout getSelectedWorkout() {
        return workoutsTable.getSelectionModel().getSelectedItem();
    }
    
    private void refreshWorkoutForm() {
        exercisesTable.getItems().clear();
        Workout w = getSelectedWorkout();
        if (w == null) {
            workoutDateLabel.setText("");
            exerciseSectionVBox.setDisable(true);
        } else {
            exerciseSectionVBox.setDisable(false);            
            workoutDateLabel.setText(w.getDateFormattedLong());
            removeExerciseButton.setDisable(true);
            exercisesTable.setItems(FXCollections.observableArrayList(w.getExercises()));
        }
    }
    
    private Exercise getSelectedExercise() {
        return exercisesTable.getSelectionModel().getSelectedItem();
    }
    
    private void refreshExercisesButtons() {        
        removeExerciseButton.setDisable((getSelectedExercise() == null));
    }
    
    private void exit() {
        Trenink.closePersistence();
        System.exit(0);
    }
    
    @FXML
    private void handleRefreshButtonAction(ActionEvent event) {
        loadWorkouts();
    }
    
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
       exit(); 
    }
    
    @FXML
    private void handleEditWeightsButtonAction(ActionEvent event) {
       WeightsDialog.displayDialog(Trenink.getPrimaryStage());
    }
    
    @FXML
    private void handleEditExerciseTypesButtonAction(ActionEvent event) {
       ExerciseTypesDialog.displayDialog(Trenink.getPrimaryStage());
    }
        
}
