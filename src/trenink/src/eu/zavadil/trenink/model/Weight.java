/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Comparator;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author karel
 */
@Entity
public class Weight implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private Float weight;

    public Float getWeight() {
        return weight;
    }

    private static DecimalFormat numberFormat = null;
    
    public static DecimalFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = new DecimalFormat("#.##");
        }        
        return numberFormat;
    }
   
    public static String formatWeight(Float weight) {
        return String.format("%s kg", getNumberFormat().format(weight));
    }
    
    public String getWeightFormatted() {
        return formatWeight(weight);
    }
    
    public void setWeight(Float w) {
        this.weight = w;
    }

    public static Comparator defaultComparator = new Comparator<Weight>() {
     
        @Override
        public int compare(Weight w1, Weight w2)
        {
            return Float.compare(w1.getWeight(), w2.getWeight());
        }        
    };
    
    public static Float parseWeight(String s) throws ParseException {
        return getNumberFormat().parse(s).floatValue();
    }
     
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (weight != null ? weight.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Weight)) {
            return false;
        }
        Weight other = (Weight) object;
        if ((this.weight == null && other.weight != null) || (this.weight != null && !this.weight.equals(other.weight))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Weight[" + weight + "]";
    }
    
}
