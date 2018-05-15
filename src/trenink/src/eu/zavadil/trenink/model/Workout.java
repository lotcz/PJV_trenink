/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import jdk.nashorn.internal.ir.annotations.Ignore;


/**
 *
 * @author karel
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Date date;
    
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
    
    public String getDateFormattedLong() {
        return getDateFormatter().format(getDate());        
    }
    
    public LocalDate getAsLocalDate() {
        ZoneId defaultZoneId = ZoneId.systemDefault();        
        Instant instant = date.toInstant();        
        return instant.atZone(defaultZoneId).toLocalDate();        
    }
    
    public void setDate(Date d) {
        this.date = d;
    }
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workout", cascade = CascadeType.ALL)
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
