/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

    public void setWeight(Float w) {
        this.weight = w;
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
