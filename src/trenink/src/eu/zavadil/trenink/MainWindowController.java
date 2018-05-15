/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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
import java.util.Date;
import java.util.Optional;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author karel
 */
public class MainWindowController implements Initializable {
    
    private ObservableList<Workout> workouts;
        
    @FXML public MenuItem changeDateMenuItem;
    @FXML public MenuItem removeWorkoutMenuItem;
    
    @FXML public Button addWorkoutButton;
    @FXML public TableView<Workout> workoutsTable;
    @FXML public TableColumn<Workout, String> dateColumn;
    
    @FXML public VBox exerciseSectionVBox;
    @FXML public Label workoutDateLabel;
    @FXML public Button addExerciseButton;
    @FXML public Button editExerciseButton;
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
            workouts = loader.getValue();
            if (workouts.size() == 0) {
                 workoutsTable.setPlaceholder(noWorkoutDataLabel);
            }
            workoutsTable.setItems(workouts);
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
    
    private void addWorkoutAndSelect(Workout w) {
        int i = 0;
        while (i < workouts.size() && w.getDate().before(workouts.get(i).getDate())) {
            i++;
        }
        workouts.add(i, w);
        workoutsTable.getSelectionModel().select(i);
    }
    
    private Label noWorkoutSelectedLabel = new Label();
    private Label noExerciseDataLabel = new Label("Tento trénink jste nic necvičili.");
    private Label loadingExercisesDataLabel = new Label("Načítám trénink.");
    private Label errorLoadingExercisesDataLabel = new Label("Při načítání tréninku se vyskytla chyba.");
    
    private void refreshWorkoutForm() {
        exercisesTable.getItems().clear();
        Workout w = getSelectedWorkout();
        if (w == null) {
            workoutDateLabel.setText("");
            exercisesTable.setPlaceholder(noWorkoutSelectedLabel);
            exerciseSectionVBox.setDisable(true);
            changeDateMenuItem.setDisable(true);
            removeWorkoutMenuItem.setDisable(true);
        } else {
            exerciseSectionVBox.setDisable(false);    
            changeDateMenuItem.setDisable(false);
            removeWorkoutMenuItem.setDisable(false);
            exercisesTable.setPlaceholder(loadingExercisesDataLabel);
            workoutDateLabel.setText(w.getDateFormattedLong());                       
            List<Exercise> exercises = w.getExercises();
            if (exercises.isEmpty()) {
                exercisesTable.setPlaceholder(noExerciseDataLabel);
            } else {            
                exercisesTable.setItems(FXCollections.observableArrayList(exercises));
            }
            refreshExercisesButtons();
        }
    }
    
    private void addNewWorkout() {
        Optional<Date> d = GetDateDialog.show(
            Trenink.getPrimaryStage(),
            "Nový trénink",
            "Vložte datum tréninku.",
            new Date()
        );
        
        if (d.isPresent()) {
            Workout w = new Workout(d.get());
            //w.setDate();
            // TO DO - copy latest workout exercises
            Trenink.getEntityManager().getTransaction().begin();
            try {            
                Trenink.getEntityManager().persist(w);
                Trenink.getEntityManager().getTransaction().commit();
                addWorkoutAndSelect(w);
            } catch (Exception e) {
                Trenink.getEntityManager().getTransaction().rollback();
                MessageDialog.show(e.getMessage());
            }
        }
    }
    
    private void changeWorkoutDate() {
        Workout w = getSelectedWorkout();
        if (w != null) {
            Optional<Date> d = GetDateDialog.show(
                Trenink.getPrimaryStage(),
                "Přesunout trénink",
                "Vložte nové datum tréninku.",
                w.getDate()
            );
            if (d.isPresent()) {
                workouts.remove(w);
                w.setDate(d.get());
                Trenink.getEntityManager().getTransaction().begin();
                try {            
                    Trenink.getEntityManager().persist(w);
                    Trenink.getEntityManager().getTransaction().commit();
                    addWorkoutAndSelect(w);
                } catch (Exception e) {
                    Trenink.getEntityManager().getTransaction().rollback();
                    MessageDialog.show(e.getMessage());
                }
            }
        }
    }
    
    private void removeWorkout() {
        Workout w = getSelectedWorkout();
        if (w != null) {            
            if (MessageDialog.showYesNoQuestion("Opravdu si přejete smazat tento trénink?")) {                                
                Trenink.getEntityManager().getTransaction().begin();
                try {            
                    Trenink.getEntityManager().remove(w);
                    Trenink.getEntityManager().getTransaction().commit();
                    workouts.remove(w);
                } catch (Exception e) {
                    Trenink.getEntityManager().getTransaction().rollback();
                    MessageDialog.show(e.getMessage());
                }
            }
        }
    }
    
    private Exercise getSelectedExercise() {
        return exercisesTable.getSelectionModel().getSelectedItem();
    }
    
    private void refreshExercisesButtons() {        
        if (getSelectedExercise() == null) {
            editExerciseButton.setDisable(true);
            removeExerciseButton.setDisable(true);
        } else {
            editExerciseButton.setDisable(false);
            removeExerciseButton.setDisable(false);
        }
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
    
    @FXML
    private void handleAddWorkoutButtonAction(ActionEvent event) {
        addNewWorkout();
    }
    
    @FXML
    private void handleChangeDateButtonAction(ActionEvent event) {
        changeWorkoutDate();
    }
    
    @FXML
    private void handleRemoveWorkoutButtonAction(ActionEvent event) {
        removeWorkout();
    }
       
    @FXML
    private void handleAboutButtonAction(ActionEvent event)  throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AboutDialogWindow.fxml"));        
        Scene scene = new Scene(root);
        Stage stage = new Stage();        
        stage.setScene(scene);        
        stage.setTitle("O programu");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Trenink.getPrimaryStage());
        stage.showAndWait();
    }
    
}
