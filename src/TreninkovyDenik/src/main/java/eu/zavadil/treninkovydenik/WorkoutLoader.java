/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.treninkovydenik;

import javafx.concurrent.Task;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.persistence.TypedQuery;
import eu.zavadil.treninkovydenik.model.Workout;

/**
 *
 * @author karel
 */
public class WorkoutLoader extends Task<ObservableList<Workout>> {
    
    @Override protected ObservableList<Workout> call() throws Exception {
        TypedQuery<Workout> q = MainApp.getEntityManager().createQuery("SELECT w FROM Workout w ORDER BY w.date DESC", Workout.class);
        List<Workout> weights = q.getResultList();
        //Thread.sleep(1000);
        return FXCollections.observableArrayList(weights);
    }

}
