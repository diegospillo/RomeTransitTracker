// app/AppLauncher.java
package app;

import controller.MapController;
import controller.UIEventController;
import model.ModelManager;
import net.ConnectivityUtil;
import controller.LineController;
import controller.StopController;
import controller.BusController;
import controller.GeneralController;
import controller.FavoritesController;
import service.GTFSManager;
import service.CurrentDateProvider;
import view.MainView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.*;

import auth.AuthenticationManager;
import auth.FileUserRepository;

/**
 * AppLauncher → Entry point grafico dell'app
 * Inizializza:
 * - GTFSStaticService: parsing dei file statici da 'rome_static_gtfs'
 * - MainView: interfaccia utente principale
 * - MapController: imposta la mappa iniziale e le interazioni
 * - LineController: logica per mostrare linee su mappa
 * - StopController: logica per visualizzare orari/fermate
 * - UIEventController: collega pulsanti e eventi della GUI ai controller
 */
public class AppLauncher {
    private static final String LOCAL_FILE = "rome_static_gtfs";
    public static void launchApp(String[] args) {
    	FileUserRepository repo = new FileUserRepository("users.txt");
        AuthenticationManager auth = new AuthenticationManager(repo);

        // Registrazione
        auth.register("tino", "password123".toCharArray());

        // Login
        boolean ok = auth.login("mario", "password124".toCharArray());
        System.out.println("Login riuscito? " + ok);
        SwingUtilities.invokeLater(() -> {
        
        	GTFSManager.getInstance().loadData(LOCAL_FILE,CurrentDateProvider.getCurrentDateFormatted());
        	ModelManager.getInstance().loadData();
        	
            MainView mainView = new MainView();
            ConnectivityUtil.setInstanceMainView(mainView);
            MapController mapController = new MapController(mainView);
            LineController lineController = new LineController(mainView);
            StopController stopController = new StopController(mainView);
            BusController busController = new BusController(mainView);
            GeneralController generalController = new GeneralController(lineController, stopController, busController, mapController);
            FavoritesController favoritesController = new FavoritesController(mainView);
            UIEventController uiEventController = new UIEventController(mainView, lineController, stopController, busController, mapController, generalController, favoritesController);
            uiEventController.CloseSidePanel();
            mainView.setVisible(true);
        });
    }
}



//TO DO autenticazione utente
//TO DO Rileggere la consegna e fare un quadro completo