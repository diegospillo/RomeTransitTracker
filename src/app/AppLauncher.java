// app/AppLauncher.java
package app;

import controller.MapController;
import controller.UIEventController;
import controller.LineController;
import controller.StopController;
import controller.GeneralController;
import service.GTFSManager;
import view.MainView;

import javax.swing.*;

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
    public static void launchApp(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	GTFSManager.getInstance().loadData("rome_static_gtfs");
            MainView mainView = new MainView();
            MapController mapController = new MapController(mainView);
            LineController lineController = new LineController(mainView);
            StopController stopController = new StopController(mainView);
            GeneralController generalController = new GeneralController(lineController, stopController,mapController);
            UIEventController uiEventController = new UIEventController(mainView, lineController, stopController,mapController,generalController);
            
            uiEventController.CloseSidePanel();
            mainView.setVisible(true);
        });
    }
}