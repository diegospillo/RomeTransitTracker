// view/MainView.java
package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    private final JPanel offlinePanel = new JPanel();
    
    private DefaultListModel<String> modelFermate;
    private DefaultListModel<String> modelOrari;
    private DefaultListModel<String> modelLinee;
    private DefaultListModel<String> modelFavorites;
    private EventWaypoint event;
    private JLabel lblLinea;
    private JLabel lblDescription;
    private JLabel lblDettagli;
    private JButton btnInvertiDirezione;
    private JButton btnLive;
    private JButton btnIndietro;
    private JButton btnMoreInfo;
    private JButton btnAddFavorite;
    private JButton btnDeleteFavorite;
    private JButton btnCloseSidePanel;
    private JButton toggleSidePanelBtn;
    private JButton btnLogout;
    private JScrollPane scrollPanel;
    private JList<String> fermateList;
    private JList<String> orariList;
    private JList<String> lineeList;
    private JList<String> favoritesList;
    private DefaultListModel<String> modelSearch;
    private JList<String> searchList;
    private JScrollPane searchScroll;
    private JButton btnViewFavorites;
    private JComboBox<String> comboFavorites;
    private JPanel topBar;
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

        topBar = new JPanel(new BorderLayout());
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

        /*comboMapType.setModel(new DefaultComboBoxModel<>(new String[] { "Open Street", "Virtual Earth", "Hybrid", "Satellite" }));
        topBar.add(comboMapType, BorderLayout.EAST);

        comboMapType.addActionListener(this::comboMapTypeActionPerformed);
        comboMapType.setVisible(false);
        */
        ImageIcon offlineIcon = new ImageIcon(getClass().getResource("/icon/offlineIcon.png"));
        Image offlineImg = offlineIcon.getImage();
        Image offlineResized = offlineImg.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon offlineScaledIcon = new ImageIcon(offlineResized);
        JLabel offlineIconLabel = new JLabel(offlineScaledIcon);
        offlinePanel.add(offlineIconLabel);
        offlinePanel.setOpaque(false);
        offlinePanel.setVisible(false);
        topBar.add(offlinePanel, BorderLayout.EAST);
        
        comboSearchControl.setModel(new DefaultComboBoxModel<>(new String[] { "Linee", "Fermate" }));
        searchPanel.add(comboSearchControl, BorderLayout.EAST);
        
        ImageIcon favListIcon = new ImageIcon(getClass().getResource("/icon/fav-list.png"));
        Image favImg = favListIcon.getImage();
        Image favResized = favImg.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon favScaledIcon = new ImageIcon(favResized);
        btnViewFavorites = new JButton(favScaledIcon);
        searchPanel.add(btnViewFavorites);

        //add(topBar, BorderLayout.NORTH);
        btnLogout = new JButton("Logout");
        
        
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
        
        modelFavorites = new DefaultListModel<>();
        favoritesList = new JList<>(modelFavorites);
        favoritesList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        favoritesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        favoritesList.setCellRenderer(new FermataRenderer());
        
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
        
        ImageIcon swapeIcon = new ImageIcon(getClass().getResource("/icon/swape.png"));
        Image img4 = swapeIcon.getImage();
        Image resizedImg4 = img4.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon4 = new ImageIcon(resizedImg4);
        btnInvertiDirezione = new JButton(scaledIcon4);
        
        ImageIcon liveIcon = new ImageIcon(getClass().getResource("/icon/live.png"));
        Image img5 = liveIcon.getImage();
        Image resizedImg5 = img5.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon5 = new ImageIcon(resizedImg5);
        btnLive = new JButton(scaledIcon5);
        
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
        
        ImageIcon favIcon = new ImageIcon(getClass().getResource("/icon/fav.png"));
        Image img6 = favIcon.getImage();
        Image resizedImg6 = img6.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon6 = new ImageIcon(resizedImg6);
        btnAddFavorite = new JButton(scaledIcon6);
        
        ImageIcon fav_fillIcon = new ImageIcon(getClass().getResource("/icon/fav-fill.png"));
        Image img7 = fav_fillIcon.getImage();
        Image resizedImg7 = img7.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon7 = new ImageIcon(resizedImg7);
        btnDeleteFavorite = new JButton(scaledIcon7);
        
        lblDettagli = new JLabel("", SwingConstants.CENTER);
        lblDettagli.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        lblDescription = new JLabel("", SwingConstants.CENTER);
        lblDescription.setFont(new Font("SansSerif", Font.HANGING_BASELINE, 16));
        
        backPanel.add(btnCloseSidePanel,BorderLayout.CENTER);
        backPanel.add(btnIndietro,BorderLayout.WEST);
        
        //TopPanel.add(btnMoreInfo, BorderLayout.EAST);
        JPanel eastPanel = new JPanel();
        eastPanel.setOpaque(false);
        eastPanel.add(btnMoreInfo);
        eastPanel.add(btnAddFavorite);
        eastPanel.add(btnDeleteFavorite);
        TopPanel.add(eastPanel, BorderLayout.EAST);
        
        descriptionPanel.add(lblDescription, BorderLayout.WEST);
        descriptionPanel.add(btnLive, BorderLayout.WEST);
        descriptionPanel.add(btnInvertiDirezione, BorderLayout.EAST);
        comboFavorites = new JComboBox<>(new String[] { "Linee", "Fermate" });
        comboFavorites.setVisible(false);
        descriptionPanel.add(comboFavorites);
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
                                .addComponent(offlinePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jXMapViewerLayout.setVerticalGroup(
                jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jXMapViewerLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jXMapViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(sidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                                        .addComponent(offlinePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(topBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        private static final long serialVersionUID = 1L;

        private final JLabel iconLabel = new JLabel();
        // Lo tengo per compatibilità ma non lo uso per dipingere il testo
        private final JLabel textLabel = new JLabel();

        private boolean isSelected;
        private boolean isPassed;
        private String displayText = ""; // testo senza il marker [LIVE]
        private Icon liveIcon;
        private Icon offlineIcon;
        private Icon favoriteLineIcon;
        private Icon favoriteStopIcon;

        public FermataRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(6, 8, 6, 8));
            setOpaque(true);

            // carica le icone (dimensioni 18x18)
            liveIcon = loadIcon("/icon/online.png", 18, 18);
            offlineIcon = loadIcon("/icon/offline.png", 18, 18);
            favoriteLineIcon = loadIcon("/icon/distance.png", 18, 18);
            favoriteStopIcon = loadIcon("/icon/bus-stand.png", 18, 18);
            

            // icona a sinistra, testo al centro (ma non lo mostriamo: disegniamo noi)
            add(iconLabel, BorderLayout.WEST);
            add(textLabel, BorderLayout.CENTER);
            textLabel.setVisible(false); // evitiamo doppia stampa del testo
            iconLabel.setVisible(false);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends String> list, String value, int index,
                boolean isSelected, boolean cellHasFocus) {

            this.isSelected = isSelected;

            // valuta se LIVE o PASSED
            boolean isLive = value != null && value.contains("[LIVE]");
            boolean isPassed = value != null && value.contains("[PASSED]");
            boolean isFavoriteLine = value != null && value.contains("[LINEA]");
            boolean isFavoriteStop = value != null && value.contains("[STOP]");
            this.isPassed = isPassed;
            // testo da visualizzare
            displayText = (value == null) ? "" : value
                    .replace("[LIVE]", "")
                    .replace("[PASSED]", "")
                    .replace("[LINEA]", "")
                    .replace("[STOP]", "");

            // set icona
            iconLabel.setIcon(isLive ? liveIcon : offlineIcon);
            
            if(isFavoriteLine) {
            	iconLabel.setIcon(favoriteLineIcon);
            }
            else if(isFavoriteStop) {
            	iconLabel.setIcon(favoriteStopIcon);
            }

            // dimensione preferita (larghezza testo + icona + padding)
            FontMetrics fm = getFontMetrics(getFont());
            int textW = fm.stringWidth(displayText);
            int iconW = (iconLabel.getIcon() != null) ? iconLabel.getIcon().getIconWidth() + 8 : 0;
            int prefW = Math.max(220, textW + iconW + 60);
            setPreferredSize(new Dimension(prefW, 40));

            return this;
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int xLinea = 20;
                int yCentro = getHeight() / 2;

                // linea verticale
                g2.setColor(Color.GRAY);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(xLinea, 0, xLinea, getHeight());
                
                // pallino
                g2.setColor(isSelected ? new Color(163, 0, 0) : Color.BLACK);
                g2.fillOval(xLinea - 5, yCentro - 5, 10, 10);


                // icona a destra della linea
                Icon icon = iconLabel.getIcon();
                if (icon != null) {
                    int iconX = xLinea + 10; // spostamento a destra
                    int iconY = yCentro - (icon.getIconHeight() / 2);
                    icon.paintIcon(this, g2, iconX, iconY);
                }

                // testo (orario + fermata)
                String[] parts = displayText.split("/", 2);
                String orario = parts.length > 0 ? parts[0] : "";
                String nomeFermata = parts.length > 1 ? parts[1] : "";

                int xTesto = xLinea + 10 + ((icon != null) ? icon.getIconWidth() + 8 : 0);

                g2.setColor(isSelected ? new Color(163, 0, 0) : Color.BLACK);
                g2.setColor(isPassed ? Color.GRAY : Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString(orario, xTesto, yCentro + 5);

                g2.setFont(new Font("Arial", Font.PLAIN, 14));
                int orarioW = g2.getFontMetrics().stringWidth(orario);
                g2.drawString(nomeFermata, xTesto + orarioW + 10, yCentro + 5);
            } finally {
                g2.dispose();
            }
        }


        private static ImageIcon loadIcon(String path, int w, int h) {
            java.net.URL url = FermataRenderer.class.getResource(path);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
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
    public JButton get_btnLive() {
    	return btnLive;
    }
    public JButton get_btnIndietro() {
    	return btnIndietro;
    }
    public JButton get_btnMoreInfo() {
    	return btnMoreInfo;
    }
    public JButton get_btnAddFavorite() {
        return btnAddFavorite;
    }
    public JButton get_btnDeleteFavorite() {
        return btnDeleteFavorite;
    }
    public JButton get_btnCloseSidePanel() {
    	return btnCloseSidePanel;
    }
    public JButton get_btnLogout() {
    	return btnLogout;
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
    public JList<String> get_favoritesList(){
        return favoritesList;
    }
    public JList<String> get_searchList(){
        return searchList;
    }
    public JPanel get_sidePanel() {
    	return sidePanel;
    }
    public JPanel get_topBar() {
        return topBar;
    }
    public JPanel get_offlinePanel() {
    	return offlinePanel;
    }
    public ImageIcon get_leftIcon() {
    	return leftIcon;
    }
    public ImageIcon get_rightIcon() {
    	return rightIcon;
    }
    public DefaultListModel<String> get_modelFavorites() {
        return modelFavorites;
    }

    public JButton get_btnViewFavorites() {
        return btnViewFavorites;
    }

    public JComboBox<String> get_comboFavorites() {
        return comboFavorites;
    }

    public MapView getMapView() {
        return mapView;
    }
}
