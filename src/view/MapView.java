// view/MapView.java
package view;

import data.JXMapViewerCustom; // o usa JXMapViewer se non usi una classe custom
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import javax.swing.event.MouseInputListener;

import javax.swing.*;

public class MapView {
    private final JXMapViewerCustom mapViewer;

    public MapView() {
        mapViewer = new JXMapViewerCustom();
        initMap();
    }

    private void initMap() {
    	TileFactoryInfo info = new OSMTileFactoryInfo() {
            @Override
            public int getMaximumZoomLevel() {
                return 8; // Imposta il massimo livello di zoom
            }
        };
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        mapViewer.setZoom(8);
        mapViewer.setAddressLocation(new GeoPosition(41.8903475, 12.4665003)); // Roma
        
        //  Create event mouse move
        MouseInputListener mm = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mm);
        mapViewer.addMouseMotionListener(mm);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(mapViewer));
    }

    public JComponent getComponent() {
        return mapViewer;
    }

    public JXMapViewerCustom getMapViewer() {
        return mapViewer;
    }
}
