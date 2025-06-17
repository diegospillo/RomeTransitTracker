// view/MainView.java
package view;

import javax.swing.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import waypoint.EventWaypoint;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.*;

public class MainView extends JFrame {
	private final JTextField textInput = new JTextField(16);
    private final MapView mapView = new MapView();
    private final JComboBox<String> comboMapType = new JComboBox<>();
    private final JComboBox<String> comboSearchControl = new JComboBox<>();

    private DefaultListModel<String> modelFermate;
    private DefaultListModel<String> modelOrari;
    private DefaultListModel<String> modelLinee;
    private EventWaypoint event;
    private JLabel lblLinea;
    private JLabel lblDescription;
    private JLabel lblDettagli;
    private JButton btnInvertiDirezione;
    private JButton btnIndietro;
    private JButton btnMoreInfo;
    private JButton btnCloseSidePanel;
    private JButton toggleSidePanelBtn;
    private JScrollPane scrollPanel;
    private JList<String> fermateList;
    private JList<String> orariList;
    private JList<String> lineeList;
    private DefaultListModel<String> modelSearch;
    private JList<String> searchList;
    private JScrollPane searchScroll;
    private JPanel sidePanel;
    private ImageIcon leftIcon;
    private ImageIcon rightIcon;

    public MainView() {
        super("Rome Transit Tracker");
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        getContentPane().setBackground(new Color(240, 240, 240));
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBar.setBackground(new Color(163, 0, 0));


        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        textInput.setFont(new Font("SansSerif", Font.PLAIN, 18));
     // Aggiungi un FocusListener per gestire il placeholder
        textInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textInput.getText().equals("Cerca...")) {
                	textInput.setText("");
                	textInput.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textInput.getText().isEmpty()) {
                	textInput.setText("Cerca...");
                	textInput.setForeground(Color.GRAY);
                }
            }
        });
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.add(textInput);
        topBar.add(searchPanel, BorderLayout.CENTER);

        comboMapType.setModel(new DefaultComboBoxModel<>(new String[] { "Open Street", "Virtual Earth", "Hybrid", "Satellite" }));
        topBar.add(comboMapType, BorderLayout.EAST);

        comboMapType.addActionListener(this::comboMapTypeActionPerformed);
        
        comboSearchControl.setModel(new DefaultComboBoxModel<>(new String[] { "Linee", "Fermate" }));
        searchPanel.add(comboSearchControl, BorderLayout.EAST);

        //add(topBar, BorderLayout.NORTH);

        modelFermate = new DefaultListModel<>();
        fermateList = new JList<>(modelFermate);
        fermateList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fermateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fermateList.setCellRenderer(new FermataRenderer());
        
        modelOrari = new DefaultListModel<>();
        orariList = new JList<>(modelOrari);
        orariList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        orariList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orariList.setCellRenderer(new FermataRenderer());
        
        modelLinee = new DefaultListModel<>();
        lineeList = new JList<>(modelLinee);
        lineeList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lineeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lineeList.setCellRenderer(new FermataRenderer());
        
        modelSearch = new DefaultListModel<>();
        searchList = new JList<>(modelSearch);
        searchList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchList.setCellRenderer(new DefaultListCellRenderer());
        searchScroll = new JScrollPane(searchList);
        searchScroll.setPreferredSize(new Dimension(200, 150));
        topBar.add(searchScroll, BorderLayout.SOUTH);

        scrollPanel = new JScrollPane(fermateList);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(Color.WHITE);
        
        

        JPanel TopPanel = new JPanel(new BorderLayout());
        TopPanel.setBackground(new Color(163, 0, 0));
        JPanel InfoPanel = new JPanel(new BorderLayout());
        InfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        InfoPanel.setBackground(new Color(163, 0, 0));
        
        JPanel backPanel = new JPanel(new BorderLayout());
        backPanel.setBackground(new Color(163, 0, 0));

        lblLinea = new JLabel("Linea: -", SwingConstants.CENTER);
        lblLinea.setFont(new Font("SansSerif", Font.BOLD, 16));
        TopPanel.add(lblLinea,BorderLayout.CENTER);
        TopPanel.add(backPanel, BorderLayout.WEST);
        
        InfoPanel.add(TopPanel, BorderLayout.NORTH);

        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setOpaque(false);
        btnInvertiDirezione = new JButton("Inverti Direzione");
        ImageIcon indietroIcon = new ImageIcon(getClass().getResource("/icon/turn-back.png"));
        Image img = indietroIcon.getImage();
        Image resizedImg = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(resizedImg);
        btnIndietro = new JButton(scaledIcon);
   
        ImageIcon closeIcon = new ImageIcon(getClass().getResource("/icon/close.png"));
        Image img2 = closeIcon.getImage();
        Image resizedImg2 = img2.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon2 = new ImageIcon(resizedImg2);
        btnCloseSidePanel = new JButton(scaledIcon2);
        
        ImageIcon infoIcon = new ImageIcon(getClass().getResource("/icon/share.png"));
        Image img3 = infoIcon.getImage();
        Image resizedImg3 = img3.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon3 = new ImageIcon(resizedImg3);
        btnMoreInfo = new JButton(scaledIcon3);
        
        lblDettagli = new JLabel("", SwingConstants.CENTER);
        lblDettagli.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        lblDescription = new JLabel("", SwingConstants.CENTER);
        lblDescription.setFont(new Font("SansSerif", Font.HANGING_BASELINE, 16));
        
        backPanel.add(btnCloseSidePanel,BorderLayout.CENTER);
        backPanel.add(btnIndietro,BorderLayout.WEST);
        
        TopPanel.add(btnMoreInfo, BorderLayout.EAST);
        descriptionPanel.add(lblDescription, BorderLayout.WEST);
        descriptionPanel.add(btnInvertiDirezione, BorderLayout.EAST);
        InfoPanel.add(lblDettagli,BorderLayout.CENTER);
        InfoPanel.add(descriptionPanel, BorderLayout.SOUTH);
        sidePanel.add(InfoPanel, BorderLayout.NORTH);
        sidePanel.add(scrollPanel, BorderLayout.CENTER);
        
     // Toggle button to show/hide the side panel
        leftIcon = new ImageIcon(getClass().getResource("/icon/left-arrow.png"));
        rightIcon = new ImageIcon(getClass().getResource("/icon/right-arrow.png"));
        toggleSidePanelBtn = new JButton(leftIcon);
        toggleSidePanelBtn.setFocusable(false);
        toggleSidePanelBtn.setMargin(new Insets(2, 8, 2, 8));
        toggleSidePanelBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        topBar.add(toggleSidePanelBtn, BorderLayout.WEST);
        
     // Aggiungere il menu laterale alla finestra principale
        getContentPane().add(topBar, BorderLayout.NORTH);
        getContentPane().add(sidePanel, BorderLayout.WEST);

        javax.swing.GroupLayout jXMapViewerLayout = new javax.swing.GroupLayout(mapView.getMapViewer());
        mapView.getMapViewer().setLayout(jXMapViewerLayout);
        jXMapViewerLayout.setHorizontalGroup(
                jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jXMapViewerLayout.createSequentialGroup()
                                .addComponent(sidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 761, Short.MAX_VALUE)
                                .addComponent(comboMapType, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jXMapViewerLayout.setVerticalGroup(
                jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jXMapViewerLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(sidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                                        .addComponent(comboMapType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        )
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

        /*
        add(sidePanel, BorderLayout.WEST);
        add(mapView.getMapViewer(), BorderLayout.CENTER);
        */
        pack();
        setLocationRelativeTo(null);
    }

    public void adjustSidePanelWidth() {
        if (modelFermate == null || fermateList == null) return;
        FontMetrics fm = fermateList.getFontMetrics(fermateList.getFont());
        int maxWidth = 0;
        for (int i = 0; i < modelFermate.size(); i++) {
            int width = fm.stringWidth(modelFermate.get(i));
            maxWidth = Math.max(maxWidth, width);
        }
        maxWidth += 80;
        sidePanel.setPreferredSize(new Dimension(maxWidth, getHeight()));
        sidePanel.revalidate();
    }

    private void comboMapTypeActionPerformed(java.awt.event.ActionEvent evt) {
        TileFactoryInfo info;
        int selected = comboMapType.getSelectedIndex();
        if (selected == 0) {
            info = new OSMTileFactoryInfo();
        } else if (selected == 1) {
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
        } else if (selected == 2) {
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
        } else {
            info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
        }
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapView.getMapViewer().setTileFactory(tileFactory);
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
                g2.setColor(new Color(163, 0, 0)); // Blu per la selezione
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
            g2.setColor(isSelected ? new Color(163, 0, 0) : Color.BLACK);
            g2.fillOval(xPallino - 5, yCentro - 5, 10, 10);

            // Dividi il testo in orario e nome della fermata
            String[] parts = text.split(" ", 2); // Supponiamo che l'orario e il nome siano separati da uno spazio
            String orario = parts[0];
            String nomeFermata = parts.length > 1 ? parts[1] : "";

            // Disegna l'orario in grassetto
            g2.setColor(isSelected ? new Color(163, 0, 0) : Color.BLACK); // Cambia il colore del testo quando selezionato
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(orario, 40, yCentro + 5);

            // Disegna il nome della fermata in normale
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.drawString(nomeFermata, 40 + g2.getFontMetrics().stringWidth(orario) + 10, yCentro + 5); // Aggiungi uno spazio dopo l'orario
        }
    }

    public JComboBox<String> get_comboSearchControl() {
    	return comboSearchControl;
    }

    public JTextField get_TextInput() {
    	return textInput;
    }
    public String getSearchText() {
        return textInput.getText();
    }
    public void setSearchText(String text) {
        textInput.setText(text);
    }
    public DefaultListModel<String> get_modelFermate(){
    	return modelFermate;
    }
    public DefaultListModel<String> get_modelOrari(){
    	return modelOrari;
    }
    public DefaultListModel<String> get_modelLinee(){
    	return modelLinee;
    }
    public DefaultListModel<String> get_modelSearch(){
        return modelSearch;
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
    public JLabel get_lblDescription() {
    	return lblDescription;
    }
    public JLabel get_lblDettagli() {
    	return lblDettagli;
    }
    
    public JButton get_btnInvertiDirezione() {
    	return btnInvertiDirezione;
    }
    public JButton get_btnIndietro() {
    	return btnIndietro;
    }
    public JButton get_btnMoreInfo() {
    	return btnMoreInfo;
    }
    public JButton get_btnCloseSidePanel() {
    	return btnCloseSidePanel;
    }
    public JButton get_toggleSidePanelBtn() {
    	return toggleSidePanelBtn;
    }
    public JScrollPane get_scrollPanel() {
    	return scrollPanel;
    }
    public JScrollPane get_searchScroll() {
    	return searchScroll;
    }
    public JList<String> get_fermateList(){
    	return fermateList;
    }
    public JList<String> get_orariList(){
    	return orariList;
    }
    public JList<String> get_lineeList(){
    	return lineeList;
    }
    public JList<String> get_searchList(){
        return searchList;
    }
    public JPanel get_sidePanel() {
    	return sidePanel;
    }
    public ImageIcon get_leftIcon() {
    	return leftIcon;
    }
    public ImageIcon get_rightIcon() {
    	return rightIcon;
    }

    public MapView getMapView() {
        return mapView;
    }
}
