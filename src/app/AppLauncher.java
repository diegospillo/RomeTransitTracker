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

import java.util.concurrent.CompletableFuture;

import javax.swing.*;

import auth.AuthenticationManager;
import auth.AuthenticationService;
import auth.FileUserRepository;

/**
 * Gestisce l'avvio dell'interfaccia grafica dell'applicazione Rome Transit Tracker.
 * Si occupa di caricare i dati statici GTFS, inizializzare il modello e
 * predisporre i controller e la vista principale.
 */
public class AppLauncher {
    private static final String LOCAL_FILE = "rome_static_gtfs";

    /**
     * Costruttore privato per evitare l'instanziazione della classe di utilità.
     */
    private AppLauncher() {
        // Nascosto
    }

    /**
     * Inizializza l'applicazione caricando i dati statici, gestendo il login
     * utente e predisponendo i controller e la vista principale.
     *
     * @param args argomenti della riga di comando
     */
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

            preload.whenComplete((_, ex) -> SwingUtilities.invokeLater(() -> {
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