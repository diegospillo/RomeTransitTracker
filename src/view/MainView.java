// view/MainView.java
package view;

import javax.swing.*;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

import waypoint.EventWaypoint;

import java.awt.*;

public class MainView extends JFrame {
    private final JTextField textInput = new JTextField(16);
    private final JButton searchButton = new JButton("Search");
    private final MapView mapView = new MapView();
    private javax.swing.JComboBox<String> comboMapType = new JComboBox<>();
    
    private DefaultListModel<String> modelFermate;
    private DefaultListModel<String> modelOrari;
    private EventWaypoint event;
    private JLabel lblLinea;
    private JButton btnInvertiDirezione;
    private JButton btnIndietro;
    private JScrollPane scrollPanel = new JScrollPane();;
    private JList<String> fermateList;
    private JList<String> orariList;

    public MainView() {
        super("Rome Transit Tracker");
        initUI();
    }

    private void initUI() {
    	setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // Barra superiore con ricerca
    	JPanel searchBar = new JPanel();
        searchBar.add(textInput);
        searchBar.add(searchButton);
        
        comboMapType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Open Street", "Virtual Earth", "Hybrid", "Satellite" }));
        comboMapType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMapTypeActionPerformed(evt);
            }
        });
        
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(new BorderLayout()); // Layout per la lista delle fermate
        menuLateral.setPreferredSize(new Dimension(200, getHeight()));

        // Pannello superiore con nome linea e bottone
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(200, 50));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Nome della linea
        lblLinea = new JLabel("none", SwingConstants.CENTER);
        lblLinea.setFont(new Font("Arial", Font.BOLD, 14));

        // Bottone per invertire direzione
        btnInvertiDirezione = new JButton("Inverti");
        btnInvertiDirezione.setVisible(true);

        // Bottone per andare indietro
        btnIndietro = new JButton("Indietro");
        btnIndietro.setVisible(false);


        topPanel.add(lblLinea, BorderLayout.CENTER);
        topPanel.add(btnIndietro, BorderLayout.WEST);
        topPanel.add(btnInvertiDirezione, BorderLayout.EAST);

        // Creazione del modello di lista per fermate
        modelFermate = new DefaultListModel<>();
        fermateList = new JList<>(modelFermate);

        //fermateList.setModel(modelFermate);
        fermateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fermateList.setFont(new Font("Arial", Font.PLAIN, 14));
        fermateList.setCellRenderer(new FermataRenderer());
        
        modelOrari = new DefaultListModel<>();
        orariList = new JList<>();

        orariList.setModel(modelOrari);
        orariList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orariList.setFont(new Font("Arial", Font.PLAIN, 14));
        orariList.setCellRenderer(new FermataRenderer());
        
        // ScrollPane per la lista delle fermate
        scrollPanel.setViewportView(fermateList);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setPreferredSize(new Dimension(200, getHeight()));

        // Aggiungere lo ScrollPane al menu laterale
        menuLateral.add(topPanel, BorderLayout.NORTH);
        menuLateral.add(scrollPanel, BorderLayout.CENTER);

        // Aggiungere il menu laterale alla finestra principale
        getContentPane().add(menuLateral, BorderLayout.WEST);

        javax.swing.GroupLayout jXMapViewerLayout = new javax.swing.GroupLayout(mapView.getMapViewer());
        mapView.getMapViewer().setLayout(jXMapViewerLayout);
        jXMapViewerLayout.setHorizontalGroup(
                jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jXMapViewerLayout.createSequentialGroup()
                                .addComponent(menuLateral, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 761, Short.MAX_VALUE)
                                .addComponent(comboMapType, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jXMapViewerLayout.setVerticalGroup(
                jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jXMapViewerLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(menuLateral, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                                        .addComponent(comboMapType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(mapView.getMapViewer(), javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(mapView.getMapViewer(), javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }
    
    private static class FermataRenderer extends JPanel implements ListCellRenderer<String> {
        private String text;
        private boolean isSelected;
        private boolean hasFocus;

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            this.text = value;
            this.isSelected = isSelected;
            this.hasFocus = cellHasFocus;
            setPreferredSize(new Dimension(Math.max(200, getFontMetrics(getFont()).stringWidth(text) + 40), 40)); // Dinamica in base al testo
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Impostazioni di colore per il testo e la selezione
            if (isSelected) {
                g2.setColor(new Color(70, 130, 180)); // Blu per la selezione
            } else {
                g2.setColor(Color.BLACK); // Nero per il normale
            }

            int xPallino = 20; // Posizione X del pallino
            int yCentro = getHeight() / 2; // Posizione verticale centrale

            // Disegna la linea verticale tra le fermate
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(xPallino, 0, xPallino, getHeight());

            // Disegna il pallino
            g2.setColor(isSelected ? new Color(70, 130, 180) : Color.BLACK);
            g2.fillOval(xPallino - 5, yCentro - 5, 10, 10);

            // Dividi il testo in orario e nome della fermata
            String[] parts = text.split(" ", 2); // Supponiamo che l'orario e il nome siano separati da uno spazio
            String orario = parts[0];
            String nomeFermata = parts.length > 1 ? parts[1] : "";

            // Disegna l'orario in grassetto
            g2.setColor(isSelected ? Color.blue : Color.BLACK); // Cambia il colore del testo quando selezionato
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(orario, 40, yCentro + 5);

            // Disegna il nome della fermata in normale
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString(nomeFermata, 40 + g2.getFontMetrics().stringWidth(orario) + 10, yCentro + 5); // Aggiungi uno spazio dopo l'orario
        }
    }
    
    private void comboMapTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMapTypeActionPerformed
        TileFactoryInfo info;
        int index = comboMapType.getSelectedIndex();
        if (index == 0) {
            info = new OSMTileFactoryInfo();
        } else if (index == 1) {
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
        } else if (index == 2) {
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
        } else {
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
        }
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapView.getMapViewer().setTileFactory(tileFactory);
    }//GEN-LAST:event_comboMapTypeActionPerformed

    public String getSearchText() {
        return textInput.getText();
    }

    public JButton getSearchButton() {
        return searchButton;
    }
    
    public DefaultListModel<String> get_modelFermate(){
    	return modelFermate;
    }
    public DefaultListModel<String> get_modelOrari(){
    	return modelOrari;
    }
    public void set_event(EventWaypoint event) {
    	this.event = event;
    }
    public EventWaypoint get_event() {
    	return event;
    }
    public JLabel get_lblLinea() {
    	return lblLinea;
    }
    public JButton get_btnInvertiDirezione() {
    	return btnInvertiDirezione;
    }
    public JButton get_btnIndietro() {
    	return btnIndietro;
    };
    public JScrollPane get_scrollPanel() {
    	return scrollPanel;
    }
    public JList<String> get_fermateList(){
    	return fermateList;
    }
    public JList<String> get_orariList(){
    	return orariList;
    }

    public MapView getMapView() {
        return mapView;
    }
}
