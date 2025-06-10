package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import view.MainView;
import waypoint.EventWaypoint;
import waypoint.MyWaypoint;

public class UIEventController {
    private final MainView mainView;
    private final LineController lineController;
    private final StopController stopController;
    private final MapController mapController;
    private final GeneralController generalController;
    Timer timer = new Timer();
    final int DELAY = 300; // ms di attesa dopo l'ultima digitazione

    public UIEventController(MainView mainView,
                             LineController lineController,
                             StopController stopController,
                             MapController mapController,
                             GeneralController generalController) {
        this.mainView = mainView;
        this.lineController = lineController;
        this.stopController = stopController;
        this.mapController = mapController;
        this.generalController = generalController;
        setupEventHandlers();
        updateSearchList("");
    }
   
    private void setupEventHandlers() {

        // Aggiungere un listener per il doppio click su una fermata
        mainView.get_fermateList().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {// Doppio click
                	stopController.showOrariFermata(lineController.get_route_id(),lineController.get_nome_linea());
                }
            }
        });

        mainView.get_btnInvertiDirezione().addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                swapeDirection();
            }
        });

        mainView.get_btnIndietro().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	lineController.showLinea();
            }
        });
        
        mainView.get_btnMoreInfo().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	selectMoreInfo(evt);
            }
        });
        
        mainView.get_btnCloseSidePanel().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	CloseSidePanel();
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
                // Cancella eventuali task pendenti
                timer.cancel();
                timer = new Timer();

                // Fissa un nuovo task che parte dopo il delay
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Essendo su un thread non EDT, aggiorno la GUI in modo thread-safe
                        SwingUtilities.invokeLater(() -> {
                            String text = mainView.getSearchText();
                            updateSearchList(text);
                        });
                    }
                }, DELAY);
            }
        });
        
        mainView.set_event(getEvent());
    }
    
    public void CloseSidePanel() {
    	Set<MyWaypoint> waypoints = stopController.get_Waypoints();
    	mapController.clearWaypoint(waypoints);
    	controlSidePanel();
    	mainView.get_toggleSidePanelBtn().setVisible(false);
    }
    
    private EventWaypoint getEvent() {
        return new EventWaypoint() {
            @Override
            public void selected(MyWaypoint waypoint) {
            	int index = waypoint.getIndex();	
        		mainView.get_fermateList().setSelectedIndex(index);
        		stopController.showOrariFermata(lineController.get_route_id(),lineController.get_nome_linea());
            }
        };
    }
    
    private void SearchActionPerformed() {//GEN-FIRST:event_cmdAddActionPerformed
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
    	mainView.get_modelSearch().clear();
        mainView.setSearchText("");
        updateSearchList("");
    	mainView.get_toggleSidePanelBtn().setVisible(true);
        mainView.get_sidePanel().setVisible(true);
        mainView.get_toggleSidePanelBtn().setIcon(mainView.get_leftIcon());
    }
    
    private void swapeDirection() {
        boolean direzione = !lineController.get_direction();
        generalController.visualizzaLinea(lineController.get_route_id(), direzione);
    }
    
    private void selectMoreInfo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        String stopId = stopController.get_stopId();
        System.out.println(stopId);
        generalController.visualizzaFermata(stopId);
    }
    
    private void controlSidePanel() {
    	boolean visible = mainView.get_sidePanel().isVisible();
        mainView.get_sidePanel().setVisible(!visible);
        mainView.get_toggleSidePanelBtn().setIcon(visible ? mainView.get_rightIcon() : mainView.get_leftIcon());
        mainView.revalidate();
        mainView.repaint();
    }
    
    private void comboSearchControlActionPerformed(java.awt.event.ActionEvent evt) {
        int selected = mainView.get_comboSearchControl().getSelectedIndex();
        if (selected == 0) {
        	generalController.setStateControl(selected);
        } else if (selected == 1) {
        	generalController.setStateControl(selected);
        }
        mainView.get_modelSearch().clear();
        mainView.setSearchText("");
        updateSearchList("");
    }
    
    private void updateSearchList(String text) {
        mainView.get_modelSearch().clear();
        int selected = mainView.get_comboSearchControl().getSelectedIndex();
        List<String> values;
        if (selected == 0) {
        		values = lineController.getLinesOf(text);
        } else {
        		values = stopController.getStopsOf(text);
        }
        for (String v : values) {
            mainView.get_modelSearch().addElement(v);
        }
    }
    
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

