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
import service.FavoritesManager;
import view.LoginView;
import view.MainView;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import auth.AuthenticationManager;
import auth.AuthenticationService;
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
        // Parte subito in background
        CompletableFuture<Void> preload = CompletableFuture.runAsync(() -> {
            GTFSManager.getInstance().loadData(LOCAL_FILE, CurrentDateProvider.getCurrentDateFormatted());
            ModelManager.getInstance().loadData();
        });

        // Login sincrono come prima
        FileUserRepository repo = new FileUserRepository("users.txt");
        AuthenticationService auth = new AuthenticationManager(repo);
        String user = LoginView.showLogin(auth);
        if (user == null) { System.out.println("Login fallito"); return; }
        FavoritesManager.getInstance().setCurrentUser(user);

        // Mostra subito la UI "vuota" e abilitala quando il preload finisce
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            ConnectivityUtil.setInstanceMainView(mainView);
            mainView.setEnabled(false);
            mainView.setVisible(true);

            preload.whenComplete((ok, ex) -> SwingUtilities.invokeLater(() -> {
                if (ex != null) {
                    JOptionPane.showMessageDialog(mainView, "Errore nel caricamento: " + ex.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                MapController mapController = new MapController(mainView);
                LineController lineController = new LineController(mainView);
                StopController stopController = new StopController(mainView);
                BusController busController = new BusController(mainView);
                GeneralController generalController = new GeneralController(
                        lineController, stopController, busController, mapController);
                FavoritesController favoritesController = new FavoritesController(mainView);
                UIEventController uiEventController = new UIEventController(
                        mainView, lineController, stopController, busController, mapController, generalController, favoritesController);
                uiEventController.CloseSidePanel();
                mainView.setEnabled(true);
            }));
        });
    }
}