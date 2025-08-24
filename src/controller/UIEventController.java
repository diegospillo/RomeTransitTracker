package controller;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import app.AppLauncher;
import service.FavoritesManager;
import view.MainView;
import waypoint.EventWaypoint;
import waypoint.MyWaypoint;

/**
 * Controller responsabile della gestione degli eventi dell'interfaccia
 * grafica. Collega i componenti della vista con i vari controller applicativi
 * e gestisce interazioni come click, ricerca e gestione dei preferiti.
 */
public class UIEventController {
    private final MainView mainView;
    private final LineController lineController;
    private final StopController stopController;
    private final BusController busController;
    private final MapController mapController;
    private final GeneralController generalController;
    private final FavoritesController favoritesController;
    private final FavoritesManager favoritesManager = FavoritesManager.getInstance();
    
    private Timer timer = new Timer();
    private final int DELAY = 300; // ms di attesa dopo l'ultima digitazione

    /**
     * Inizializza il controller degli eventi UI collegando la vista ai vari
     * controller di dominio.
     *
     * @param mainView vista principale
     * @param lineController controller delle linee
     * @param stopController controller delle fermate
     * @param busController controller dei bus
     * @param mapController controller della mappa
     * @param generalController controller generale
     * @param favoritesController controller dei preferiti
     */
    public UIEventController(MainView mainView,
                             LineController lineController,
                             StopController stopController,
                             BusController busController,
                             MapController mapController,
                             GeneralController generalController,
                             FavoritesController favoritesController) {
        this.mainView = mainView;
        this.lineController = lineController;
        this.stopController = stopController;
        this.busController = busController;
        this.mapController = mapController;
        this.generalController = generalController;
        this.favoritesController = favoritesController;
        setupEventHandlers();
        updateSearchList("");
    }

    /**
     * Configura gli handler degli eventi dell'interfaccia grafica.
     */
    private void setupEventHandlers() {

        // Aggiungere un listener per il doppio click su una fermata
    	
    	// Singolo click / cambio selezione (solo quando è definitivo)
    	mainView.get_fermateList().addListSelectionListener(e -> {
    	    if (!e.getValueIsAdjusting()) { // evita eventi intermedi
    	    	int index = mainView.get_fermateList().getSelectedIndex();
    	    	if (index < 0) {
    	    	    // nessuna selezione valida: opzionale refresh UI e return
    	    	    return;
    	    	}
    	        mapController.clearPingWaypoint(stopController.get_PingWaypoints());
    	        stopController.setPingWaypoints();
    	        mapController.initPingWaypoint(stopController.get_PingWaypoints());
    	    }
    	});

    	// Doppio click
    	mainView.get_fermateList().addMouseListener(new MouseAdapter() {
    	    @Override public void mouseClicked(MouseEvent e) {
    	        if (!SwingUtilities.isLeftMouseButton(e)) return;

    	        // Assicurati che si stia cliccando su una cella valida
    	        int index = mainView.get_fermateList().locationToIndex(e.getPoint());
    	        if (index < 0 || !mainView.get_fermateList().getCellBounds(index, index).contains(e.getPoint())) return;

    	        if (e.getClickCount() == 2) {
    	            stopController.viewTimesByStop();
    	            generalController.setStopSelected(true);
    	        }
    	    }
    	});
        
        mainView.get_lineeList().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
            	if (e.getClickCount() == 2) {// Doppio click
            		String selctedValue = mainView.get_lineeList().getSelectedValue();
            		String[] selctedValueSplitted = selctedValue.split("/",2);
            		generalController.visualizzaLinea(selctedValueSplitted[0],true);
                }
            }
        });

        mainView.get_btnInvertiDirezione().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                swapeDirection();
            }
        });
        
        mainView.get_btnLive().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                getNextArrivals();
            }
        });

        mainView.get_btnIndietro().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	generalController.setStopSelected(false);
            	lineController.showLinea(lineController.getSelectedTrip());
            }
        });
        
        mainView.get_btnMoreInfo().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	generalController.setStopSelected(false);
            	selectMoreInfo(evt);
            }
        });
        
        mainView.get_btnAddFavorite().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFavorite();
            }
        });
        
        mainView.get_btnDeleteFavorite().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFavorite();
            }
        });
        
        mainView.get_btnCloseSidePanel().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	CloseSidePanel();
            }
        });
        
        mainView.get_btnLogout().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	Logout();
            }
        });
        
        mainView.get_toggleSidePanelBtn().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	controlSidePanel();
            }
        });
        
        mainView.get_comboSearchControl().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	comboSearchControlActionPerformed(evt);
            }
        });
        
        mainView.get_searchList().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String value = mainView.get_searchList().getSelectedValue();
                    if (value != null) {
                        mainView.setSearchText(value);
                        SearchActionPerformed();
                    }
                }
            }
        });
        
        mainView.get_btnViewFavorites().addActionListener(e -> showFavorites());

        mainView.get_comboFavorites().addActionListener(e -> showFavorites());

        mainView.get_favoritesList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String value = mainView.get_favoritesList().getSelectedValue();
                    if (value != null) {
                        String id = extractId(value);
                        int selected = mainView.get_comboFavorites().getSelectedIndex();
                        if (selected == 0) {
                            generalController.visualizzaLinea(id, true);
                        } else {
                            generalController.visualizzaFermata(id);
                        }
                    }
                }
            }
        });
        
        mainView.get_TextInput().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aggiorna();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aggiorna();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Questo metodo viene usato per cambiamenti di stile, di solito non serve per JTextField semplice
            }

            // Usa un debounce
            private void aggiorna() {
                timer.cancel();
                timer = new Timer();
                
                final String text = mainView.getSearchText();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                    	updateSearchList(text);
                    }
                }, DELAY);
            }
        });
        
        mainView.set_event(getEvent());
    }
    
    /**
     * Chiude il pannello laterale e ferma gli aggiornamenti correnti.
     */
    public void CloseSidePanel() {
        generalController.Close();
        controlSidePanel();
        mainView.get_toggleSidePanelBtn().setVisible(false);
    }
    
    /**
     * Crea il listener per la selezione dei waypoint sulla mappa.
     *
     * @return implementazione di {@link EventWaypoint}
     */
    private EventWaypoint getEvent() {
        return new EventWaypoint() {
            @Override
            public void selected(MyWaypoint waypoint) {
            	int index = waypoint.getIndex();
            	if (waypoint.getPointType().equals(MyWaypoint.PointType.BUS) || waypoint.getPointType().equals(MyWaypoint.PointType.METRO) || waypoint.getPointType().equals(MyWaypoint.PointType.TRAM)) {
            		System.out.println(waypoint.getName());
            		generalController.visualizzaTrip(waypoint.getName());
            	}
            	else if (stopController.get_Waypoints().size() > 1) {
            		mainView.get_fermateList().setSelectedIndex(index);
            		stopController.viewTimesByStop();
            		generalController.setStopSelected(true);
            		mapController.clearPingWaypoint(stopController.get_PingWaypoints());
            		stopController.setPingWaypoints();
            		mapController.initPingWaypoint(stopController.get_PingWaypoints());
            	}
            }
        };
    }
    
    /**
     * Esegue la ricerca in base al testo inserito dall'utente.
     */
    private void SearchActionPerformed() {
    	String text = mainView.getSearchText(); 
    	int value = generalController.getStateControl();
    	if(value == 0) {
    		String route_id = extractId(text);
	        generalController.visualizzaLinea(route_id, true);
    	}
    	else if (value == 1) {
    		String stop_id = extractId(text);
    		generalController.visualizzaFermata(stop_id);
    	}
        mainView.setSearchText("");
        updateSearchList("");
    	mainView.get_toggleSidePanelBtn().setVisible(true);
        mainView.get_sidePanel().setVisible(true);
        mainView.get_toggleSidePanelBtn().setIcon(mainView.get_leftIcon());
    }
    
    /**
     * Inverte la direzione della linea corrente.
     */
    private void swapeDirection() {
        boolean direzione = !lineController.get_direction();
        generalController.visualizzaLinea(lineController.get_route_id(), direzione);
    }
    
    /**
     * Aggiorna la linea visualizzata mantenendo la direzione corrente.
     */
    private void getNextArrivals() {
        generalController.visualizzaLinea(lineController.get_route_id(), lineController.get_direction());
    }
    
    /**
     * Mostra maggiori dettagli sulla fermata selezionata.
     *
     * @param evt evento dell'interfaccia
     */
    private void selectMoreInfo(java.awt.event.ActionEvent evt) {
        String stopId = stopController.get_stopId();
        generalController.visualizzaFermata(stopId);
    }

    /**
     * Aggiunge la fermata o la linea corrente ai preferiti.
     */
    private void addFavorite() {
        String stopId = stopController.get_stopId();
        String lineId = lineController.get_route_id();
        if (stopId != null && generalController.getCurrentSate()==1){
            favoritesManager.addStop(stopId);
            stopController.setIsFavorite(true);
            stopController.showFermata();
        }
        if (lineId != null && generalController.getCurrentSate()==0){
            favoritesManager.addLine(lineId);
            lineController.setIsFavorite(true);
            lineController.showLinea(lineController.getSelectedTrip());
        }
        JOptionPane.showMessageDialog(mainView, "Aggiunto ai preferiti");
    }

    /**
     * Rimuove la fermata o la linea corrente dai preferiti.
     */
    private void deleteFavorite() {
        String stopId = stopController.get_stopId();
        String lineId = lineController.get_route_id();
        if (stopId != null && generalController.getCurrentSate()==1) {
            favoritesManager.removeStop(stopId);
            stopController.setIsFavorite(false);
            stopController.showFermata();
        }
        if (lineId != null && generalController.getCurrentSate()==0) {
            favoritesManager.removeLine(lineId);
            lineController.setIsFavorite(false);
            lineController.showLinea(lineController.getSelectedTrip());
        }
    }

    /**
     * Visualizza la lista dei preferiti dell'utente.
     */
    private void showFavorites() {
    	generalController.Close();
        boolean showLines = mainView.get_comboFavorites().getSelectedIndex() == 0;
        favoritesController.showFavorites(showLines);

        mainView.get_lblLinea().setText("Preferiti");
        mainView.get_lblDescription().setText("");
        mainView.get_lblDettagli().setVisible(false);
        mainView.get_btnAddFavorite().setVisible(false);
        mainView.get_btnDeleteFavorite().setVisible(false);
        mainView.get_btnInvertiDirezione().setVisible(false);
        mainView.get_btnLive().setVisible(false);
        mainView.get_btnIndietro().setVisible(false);
        mainView.get_btnMoreInfo().setVisible(false);
        mainView.get_btnCloseSidePanel().setVisible(true);
        mainView.get_comboFavorites().setVisible(true);
        mainView.get_scrollPanel().setViewportView(mainView.get_favoritesList());
        if (!mainView.get_sidePanel().isVisible()) {
            mainView.get_sidePanel().setVisible(true);
            mainView.get_toggleSidePanelBtn().setVisible(true);
            mainView.get_toggleSidePanelBtn().setIcon(mainView.get_leftIcon());
        }
        mainView.adjustSidePanelWidth();
    }
    
    /**
     * Alterna la visibilità del pannello laterale.
     */
    private void controlSidePanel() {
        boolean visible = mainView.get_sidePanel().isVisible();
        mainView.get_sidePanel().setVisible(!visible);
        mainView.get_toggleSidePanelBtn().setIcon(visible ? mainView.get_rightIcon() : mainView.get_leftIcon());
        mainView.revalidate();
        mainView.repaint();
    }
    
    /**
     * Cambia il tipo di ricerca tra linee e fermate.
     *
     * @param evt evento di selezione
     */
    private void comboSearchControlActionPerformed(java.awt.event.ActionEvent evt) {
        int selected = mainView.get_comboSearchControl().getSelectedIndex();
        generalController.setStateControl(selected);

        mainView.get_modelSearch().clear();
        mainView.setSearchText("");
        updateSearchList("");
    }
    
    /**
     * Aggiorna in modo asincrono la lista dei suggerimenti di ricerca.
     *
     * @param text testo attualmente digitato
     */
    private void updateSearchList(String text) {
    	SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                int selected = mainView.get_comboSearchControl().getSelectedIndex();
                if(text.equals("Cerca...")){
                	mainView.get_searchScroll().setVisible(false);
                	mainView.revalidate();
                    mainView.repaint();
                	return List.of();
                }
                else if(!text.isEmpty()) {
                	mainView.get_searchScroll().setVisible(true);
                	mainView.revalidate();
                    mainView.repaint();
	                if (selected == 0) {
	                    return lineController.getLinesOf(text);
	                } else {
	                    return stopController.getStopsOf(text);
	                }
                }
                else{
                	mainView.get_searchScroll().setVisible(false);
                	mainView.revalidate();
                    mainView.repaint();
                	return List.of();
                }
            }

            @Override
            protected void done() {
                try {
                    List<String> values = get();
                    mainView.get_modelSearch().clear();
                    for (String v : values) {
                        mainView.get_modelSearch().addElement(v);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Esegue il logout dell'utente e riavvia l'applicazione.
     */
    public void Logout() {
        SwingUtilities.invokeLater(() -> {
            for (Window w : Window.getWindows()) {
                w.dispose();
            }

            FavoritesManager.getInstance().setCurrentUser(null);

            new Thread(() -> AppLauncher.launchApp(new String[0]), "AppRelauncher").start();
        });
    }

    /**
     * Estrae un identificativo racchiuso tra parentesi da una stringa.
     *
     * @param itemString stringa contenente l'identificativo
     * @return valore dell'identificativo oppure stringa vuota
     */
    public static String extractId(String itemString) {
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(itemString);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

}

