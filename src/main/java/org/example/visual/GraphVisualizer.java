package org.example.visual;


import org.example.model.IslandMap;
import org.example.model.Ship;
import org.example.model.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.List;

public class GraphVisualizer extends JFrame {
    private IslandMap islandMap;

    private IslandMap.Tiles myShipsTiles = new IslandMap.Tiles();

    private IslandMap.Tiles enemyShipsTiles = new IslandMap.Tiles();

    private JPanel canvas;

    private long selectedX = 0;

    private long selectedY = 0;

//    public static Image buffer;
//
//    public static Graphics bg;

    public GraphVisualizer(IslandMap islandMap) {
        super("canvas");

        this.islandMap = islandMap;

        int W = (int) islandMap.getWidth();
        int H = (int) islandMap.getHeight();

        setSize(W, H);

        addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent e){
                selectedX = e.getX();
                selectedY = e.getYOnScreen();
                System.out.printf("Move to %d %d\n", selectedX, selectedY);
                updateGraph();
            }

            public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
            public void mousePressed(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent arg0) {}
        });

        canvas = new JPanel() {

            // paint the canvas
            public void paint(Graphics g)
            {
                // set color to red
                g.setColor(new Color(0, 200, 0));
                g.drawRect((int) selectedX, (int) selectedY, 3, 3);

                g.setColor(new Color(0, 0, 200));
                paintTiles(myShipsTiles, g);

                g.setColor(new Color(200, 0, 0));
                paintTiles(enemyShipsTiles, g);

                g.setColor(new Color(200, 200, 0));
                paintTiles(islandMap.getRawTiles(), g);
            }
        };

        add(canvas);
        show();
    }

    private void paintTiles(IslandMap.Tiles tiles, Graphics g) {
        Iterator<Tile> it = tiles.iterateTiles();
        while (it.hasNext()) {
            Tile tile = it.next();
            g.drawRect((int) tile.getX(), (int) tile.getY(), 1, 1);
        }
    }

    public void updateGraph() {
        repaint();
    }

    public void setMyShips(List<Ship> myShips) {
        myShipsTiles = new IslandMap.Tiles();
        myShips.forEach(s -> s.toTiles().forEach(myShipsTiles::addTile));
    }

    public void setEnemyShips(List<Ship> enemyShips) {
        enemyShipsTiles = new IslandMap.Tiles();
        enemyShips.forEach(s -> s.toTiles().forEach(enemyShipsTiles::addTile));
    }

    public long getSelectedX() {
        return selectedX;
    }

    public long getSelectedY() {
        return selectedY;
    }
}
