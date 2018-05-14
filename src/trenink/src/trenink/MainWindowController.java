/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trenink;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import model.Weight;

/**
 *
 * @author karel
 */
public class MainWindowController implements Initializable {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("treninkPU", configuration.getProperties());

        EntityManager entitymanager = emfactory.createEntityManager();
      
        entitymanager.getTransaction().begin();

        Weight w = new Weight();
        w.setWeight(Float.valueOf(8));

        entitymanager.persist(w);
        entitymanager.getTransaction().commit();
              
        TypedQuery<Weight> q2 = entitymanager.createQuery("SELECT c FROM Weight c", Weight.class);

        List<Weight> list = q2.getResultList();
        
        entitymanager.close();
        emfactory.close();
    }   
    
    private void exit() {
        System.exit(0);
    }
    
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
       exit(); 
    }
    
     
    
}
