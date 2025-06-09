package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

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
    
    private void SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
    	String text = mainView.getSearchText(); 
    	int value = generalController.getStateControl();
    	if(value ==0) {
	        generalController.visualizzaLinea(text, true);
    	}
    	else if (value ==1) {
    		generalController.visualizzaFermata(text);
    	}
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
    }

}

