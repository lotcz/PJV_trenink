/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trenink;

import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import model.Weight;

/**
 *
 * @author karel
 */
public class Trenink extends Application {
        
    public static ConfigurationFile dbconf;
    
    private static EntityManagerFactory emfactory;
    
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emfactory == null) {
            emfactory = Persistence.createEntityManagerFactory("treninkPU", dbconf.getProperties());           
        }
        return emfactory;
    }
    
    private static EntityManager entitymanager;
            
    public static EntityManager getEntityManager() {
        if (entitymanager == null) {
            entitymanager = getEntityManagerFactory().createEntityManager();
        }
        return entitymanager;
    }
    
    public static void closePersistence() {
        if (entitymanager != null) {
            entitymanager.close();
            entitymanager = null;
        }
        if (emfactory != null) {
            emfactory.close();
            emfactory = null;
        }
    }
            
    @Override
    public void start(Stage stage) throws Exception {
        
        /* check if config file is present */
        try {
            dbconf = new ConfigurationFile("config/db.cfg");
        } catch (Exception e) {
            MessageDialog.show("Nepodařilo se načíst konfigurační soubor.");
            System.exit(0);
        }     
        
        /* check presence of jdbc driver */        
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            MessageDialog.show("Nebyl nalezen ovladač PostgreSQL JDBC!");
            System.exit(0);
        }

        /* check db connection */
        try {
            Connection connection = null;
            connection = DriverManager.getConnection(dbconf.get("javax.persistence.jdbc.url"), 
                    dbconf.get("javax.persistence.jdbc.user"), 
                    dbconf.get("javax.persistence.jdbc.password"));
            connection.close();
        } catch (SQLException e) {
            MessageDialog.show("Nelze se připojit k databázi!");
            System.exit(0);
        }
        
        /* show main window */
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));        
        Scene scene = new Scene(root);        
        stage.setScene(scene);
        stage.setTitle("Tréninkový deník");
        stage.show();        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
