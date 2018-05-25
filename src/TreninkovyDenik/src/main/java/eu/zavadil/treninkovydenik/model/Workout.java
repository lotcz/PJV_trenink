/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.treninkovydenik.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 * Represents a single training session.
 */
@Entity
public class Workout implements Serializable {

    public Workout() {
        
    }
        
    public Workout(Date d) {
        this.setDate(d);
        this.setExercises(new ArrayList<Exercise>());
    }
    
    private static final long serialVersionUID = 2L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Date date;
    
    /**
     * Get date of this training session.
     */
    public Date getDate() {
        return date;
    }

    private static SimpleDateFormat dateFormatter;
    
    private SimpleDateFormat getDateFormatter() {
        if (dateFormatter == null) {
            dateFormatter = new SimpleDateFormat("dd.MM.yyyy, EEEE");
        }
        return dateFormatter;
    }
    
    /**
     * Get date of this training session formatted as long string with day of week.
     */
    public String getDateFormattedLong() {
        return getDateFormatter().format(getDate());        
    }
    
    /**
     * Get date of this training session as LocalDate class.
     */
    public LocalDate getAsLocalDate() {
        ZoneId defaultZoneId = ZoneId.systemDefault();        
        Instant instant = date.toInstant();        
        return instant.atZone(defaultZoneId).toLocalDate();        
    }
    
    /**
     * Set date of this training session formatted.
     */
    public void setDate(Date d) {
        this.date = d;
    }
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workout")
    private List<Exercise> exercises;
        
    public List<Exercise> getExercises() {
        return this.exercises;
    }

    public void setExercises(List<Exercise> e) {
        this.exercises = e;
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
        if (!(object instanceof Workout)) {
            return false;
        }
        Workout other = (Workout) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Workout[ id=" + id + " ]";
    }
    
}
