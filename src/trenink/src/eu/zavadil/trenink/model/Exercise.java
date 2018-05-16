/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author karel
 */
@Entity
public class Exercise implements Serializable {

    private static final long serialVersionUID = 4L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long series;
    
    public Long getSeries() {
        return this.series;
    }

    public void setSeries(Long s) {
        this.series = s;
    }
    
     private Long repetitions;
    
    public Long getRepetitions() {
        return this.repetitions;
    }

    public void setRepetitions(Long s) {
        this.repetitions = s;
    }
    
    private Float weight;
    
    public Float getWeight() {
        return this.weight;
    }

    public void setWeight(Float s) {
        this.weight = s;
    }
    
    @ManyToOne
    @JoinColumn(name = "type")
    private ExerciseType exerciseType;
       
    public ExerciseType getExerciseType() {
        return this.exerciseType;
    }

    public void setExerciseType(ExerciseType et) {
        this.exerciseType = et;
    }
    
    @ManyToOne
    @JoinColumn(name = "workout")
    private Workout workout;
        
    public Workout getWorkout() {
        return this.workout;
    }

    public void setWorkout(Workout w) {
        this.workout = w;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Exercise)) {
            return false;
        }
        Exercise other = (Exercise) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Exercise[ id=" + id + " ]";
    }
    
}
