/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.zavadil.trenink;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import javafx.scene.image.Image;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
            
    /**
     * Entity manager used across whole project. It is lazy initialized when first 
     * @return 
     */
    public static EntityManager getEntityManager() {
        if (entitymanager == null) {
            entitymanager = getEntityManagerFactory().createEntityManager();
        }
        return entitymanager;
    }
    
    /**
     * Close entity manager and entity manager factory.
     */
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
    
    private static Image icon;
    
    /**
     * Universal icon for this application.
     * @return Image object with icon.
     */
    public static Image getIcon() {
        if (icon == null) {
            icon = new Image(Trenink.class.getResourceAsStream( "icon.png" ));
        }
        return icon;
    }
    
    private static Stage primaryStage;
    
    /**
     * Primary window of this application.
     * @return 
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Start the application. This contains main initialization code. It will check existence of config file and whether db is accessible.
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        
        primaryStage = stage;
        
        /* check if config file is present */
        try {
            dbconf = new ConfigurationFile("config/db.cfg");
        } catch (Exception e) {
            MessageDialog.show("Nepodařilo se načíst konfigurační soubor.");
            stop();
        }     
        
        /* check presence of jdbc driver */        
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            MessageDialog.show("Nebyl nalezen ovladač PostgreSQL JDBC!");
            stop();
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
            stop();
        }
        
        /* show main window */
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));        
        Scene scene = new Scene(root);        
        stage.setScene(scene);        
        stage.setTitle("Tréninkový deník");
        stage.getIcons().add(getIcon()); 
        stage.setMinHeight(300);
        stage.setMinWidth(500);
        stage.show();
    }

    /**
     * Close the application.
     * 
     * @throws Exception 
     */
    @Override
    public void stop() throws Exception {
        closePersistence();
        System.exit(0);
    }
      
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
