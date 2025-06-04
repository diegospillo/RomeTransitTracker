package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import view.MainView;
import waypoint.EventWaypoint;
import waypoint.MyWaypoint;

public class UIEventController {
    private final MainView mainView;
    private final LineController lineController;
    private final StopController stopController;
    private final MapController mapController;
    private final GeneralController generalController;

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
    }
    
    private void setupEventHandlers() {
       
        mainView.getSearchButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchActionPerformed(evt);
            }
        });

        // Aggiungere un listener per il doppio click su una fermata
        mainView.get_fermateList().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {// Doppio click
                	lineController.showOrariFermata();
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
        
        mainView.set_event(getEvent());
    }
    
    private EventWaypoint getEvent() {
        return new EventWaypoint() {
            @Override
            public void selected(MyWaypoint waypoint) {
            	mainView.get_fermateList().setSelectedIndex(waypoint.getIndex());
            	lineController.showOrariFermata();
            }
        };
    }
    
    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
        String text = mainView.getSearchText();   
        generalController.visualizzaLinea(text, true);
    }
    
    private void swapeDirection() {
        boolean direzione = !lineController.get_direction();
        generalController.visualizzaLinea(lineController.get_route_id(), direzione);
    }

}

