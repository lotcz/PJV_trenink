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
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
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
    
    /**
     * this will be selected as active workout after refresh
     */
    private Workout selectWorkout;
    
    private ObservableList<Workout> workouts;
        
    @FXML private MenuItem changeDateMenuItem;
    @FXML private MenuItem removeWorkoutMenuItem;
    @FXML private MenuItem exportMenuItem;
     
    @FXML private Button addWorkoutButton;
    @FXML private TableView<Workout> workoutsTable;
    @FXML private TableColumn<Workout, String> dateColumn;
    
    @FXML private VBox exerciseSectionVBox;
    @FXML private Label workoutDateLabel;
    @FXML private Button addExerciseButton;
    @FXML private Button editExerciseButton;
    @FXML private Button removeExerciseButton;
        
    @FXML private TableView<Exercise> exercisesTable;
    @FXML private TableColumn<Exercise, String> exerciseColumn;
    @FXML private TableColumn<Exercise, String> seriesColumn;
    @FXML private TableColumn<Exercise, String> repetitionsColumn;
    @FXML private TableColumn<Exercise, String> weightColumn;
    
    /**
     * Initialize window controls and load workouts.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshWorkoutForm();
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDateFormattedLong()));       
        
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
        
        loadWorkouts();
    }   
    
    private WorkoutLoader loader;
    private Thread loaderThread;
    private Label noWorkoutDataLabel = new Label("V deníku nejsou žádné tréninky.");
    private Label loadingWorkoutsDataLabel = new Label("Načítám tréninky z databáze.");
    private Label errorLoadingWorkoutsDataLabel = new Label("Při načítání tréninků se vyskytla chyba.");
    
    /**
     * Start loading workouts in new thread.
     */
    public void loadWorkouts() {
        workoutsTable.setPlaceholder(loadingWorkoutsDataLabel);
        workoutsTable.getItems().clear();
        exportMenuItem.setDisable(true);
        loader = new WorkoutLoader();
        loader.setOnSucceeded(workoutsLoaded);        
        loader.setOnFailed(workoutsLoadingFailed);
        loaderThread = new Thread(loader);
        loaderThread.start();
    }
        
    /**
     * Event handler called when workouts are loaded.
     */
    public EventHandler<WorkerStateEvent> workoutsLoaded = new EventHandler<WorkerStateEvent>() {
        
        @Override
        public void handle(WorkerStateEvent t) {
            workouts = loader.getValue();
            if (workouts.size() == 0) {
                 workoutsTable.setPlaceholder(noWorkoutDataLabel);
            } else {
                exportMenuItem.setDisable(false);
                workoutsTable.setItems(workouts);
                if (selectWorkout != null) {
                    workoutsTable.getSelectionModel().select(selectWorkout);
                    selectWorkout = null;
                }
            }            
        }
    
    };
    
    /**
     * Event handler called when workouts loading fails.
     */
    public EventHandler<WorkerStateEvent> workoutsLoadingFailed = new EventHandler<WorkerStateEvent>() {
        
        @Override
        public void handle(WorkerStateEvent t) {
            workoutsTable.setPlaceholder(errorLoadingWorkoutsDataLabel);
        }
    
    };
    
    /**
     * Currently selected workout.
     * @return Currently selected workout.
     */
    public Workout getSelectedWorkout() {
        return workoutsTable.getSelectionModel().getSelectedItem();
    }
    
    /**
     * Add new workout to a list and select it.
     * @param w 
     */
    public void addWorkoutAndSelect(Workout w) {
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
    
    /**
     * Refresh buttons and menu items related to selected workout.
     */
    public void refreshWorkoutForm() {        
        Workout w = getSelectedWorkout();
        exercisesTable.getItems().clear();
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
       
    /**
    * Ask user for a date and add new workout if value is entered.
    */
     public void addNewWorkout() {
        Optional<Date> d = GetDateDialog.show(
            Trenink.getPrimaryStage(),
            "Nový trénink",
            "Vložte datum tréninku.",
            new Date()
        );
        
        if (d.isPresent()) {
            Workout w = new Workout(d.get());
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
    
     /**
     * Let user change the date of selected workout.
     */
    public void changeWorkoutDate() {
        Workout w = getSelectedWorkout();
        if (w != null) {
            Optional<Date> d = GetDateDialog.show(
                Trenink.getPrimaryStage(),
                "Přesunout trénink",
                "Vložte nové datum tréninku.",
                w.getDate()
            );
            if (d.isPresent()) {                
                w.setDate(d.get());
                Trenink.getEntityManager().getTransaction().begin();
                try {            
                    Trenink.getEntityManager().persist(w);
                    Trenink.getEntityManager().getTransaction().commit();
                    selectWorkout = w;
                    loadWorkouts();
                } catch (Exception e) {
                    Trenink.getEntityManager().getTransaction().rollback();
                    MessageDialog.show(e.getMessage());
                }
            }
        }
    }
    
    /**
     * Let user delete a workout.
     */
    public void removeWorkout() {
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
    
    /**
     * Returns currently selected exercise.
     * @return 
     */
    public Exercise getSelectedExercise() {
        return exercisesTable.getSelectionModel().getSelectedItem();
    }
    
    /**
     * Refresh buttons and menu items related to exercise.
     */
    public void refreshExercisesButtons() {        
        if (getSelectedExercise() == null) {
            editExerciseButton.setDisable(true);
            removeExerciseButton.setDisable(true);
        } else {
            editExerciseButton.setDisable(false);
            removeExerciseButton.setDisable(false);
        }
    }
    
    /**
     * Let user add new exercise.
     */
    public void addNewExercise() {
        Exercise e = EditExerciseDialog.show(
            Trenink.getPrimaryStage(),            
            new Exercise()
        );
        
        if (e != null) {            
            Trenink.getEntityManager().getTransaction().begin();
            try {         
                Workout w = getSelectedWorkout();
                e.setWorkout(w);                
                Trenink.getEntityManager().persist(e);
                Trenink.getEntityManager().getTransaction().commit();                
                w.getExercises().add(e);
                refreshWorkoutForm();
            } catch (Exception ex) {
                Trenink.getEntityManager().getTransaction().rollback();
                MessageDialog.show(ex.getMessage());
            }
        }
    }
    
    /**
     * Edit selected exercise.
     */
    public void editExercise() {
        Exercise e = getSelectedExercise();
        if (e != null) {
            e = EditExerciseDialog.show(Trenink.getPrimaryStage(), e);
            
            if (e != null) {                
                Trenink.getEntityManager().getTransaction().begin();
                try {            
                    Trenink.getEntityManager().persist(e);
                    Trenink.getEntityManager().getTransaction().commit();
                    refreshWorkoutForm();
                } catch (Exception ex) {
                    Trenink.getEntityManager().getTransaction().rollback();
                    MessageDialog.show(ex.getMessage());
                }
            }
        }
    }
    
    /**
     * Delete selected exercise.
     */
    public void removeExercise() {
        Exercise e = getSelectedExercise();
        if (e != null) {            
            if (MessageDialog.showYesNoQuestion("Opravdu si přejete odebrat tento cvik z tréninku?")) {
                Trenink.getEntityManager().getTransaction().begin();
                try {
                    Workout w = e.getWorkout();
                    w.getExercises().remove(e);
                    Trenink.getEntityManager().remove(e);
                    Trenink.getEntityManager().getTransaction().commit();                    
                    refreshWorkoutForm();
                } catch (Exception ex) {
                    Trenink.getEntityManager().getTransaction().rollback();
                    MessageDialog.show(ex.getMessage());
                }
            }
        }
    }
    
    /**
     * Exports workouts into a CSV file and opens the file in OS default application for CSV.
     */
    public void generateCsvFile() {
        try {
            String outputFilePath = "kettlebel-treninky.csv";
            FileWriter writer = new FileWriter(outputFilePath);

            workouts.forEach((w) -> {
                    
                w.getExercises().forEach((exercise) -> {
                        try {
                            System.out.println(w.getDateFormattedLong());
                            writer.append(w.getDate().toString());
                            writer.append(';');
                            writer.append(exercise.getExerciseType().getName());
                            writer.append(';');
                            writer.append(exercise.getSeries().toString());
                            writer.append(';');
                            writer.append(exercise.getRepetitions().toString());
                            writer.append(';');
                            writer.append(Weight.formatWeight(exercise.getWeight()));
                            writer.append('\n');
                        } catch (IOException e) {
                             e.printStackTrace();   
                        }
                    }
                );
                
            });
            
            writer.flush();
            writer.close();
            Desktop dt = Desktop.getDesktop();
            dt.open(new File(outputFilePath));
        } catch(IOException e) {
              e.printStackTrace();
        } 
    }
    
    public void exit() {
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
    private void handleAddExerciseButtonAction(ActionEvent event) {
        addNewExercise();
    }
    
    @FXML
    private void handleEditExerciseButtonAction(ActionEvent event) {
        editExercise();
    }
    
    @FXML
    private void handleRemoveExerciseButtonAction(ActionEvent event) {
        removeExercise();
    }
    
    @FXML
    private void handleAboutButtonAction(ActionEvent event)  throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AboutDialogWindow.fxml"));        
        Scene scene = new Scene(root);
        Stage stage = new Stage();        
        stage.setScene(scene);        
        stage.setTitle("O programu");
        stage.getIcons().add(Trenink.getIcon()); 
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Trenink.getPrimaryStage());
        stage.showAndWait();
    }
    
    @FXML
    private void handleExportButtonAction(ActionEvent event)  throws Exception {
        generateCsvFile();
    }
    
}
