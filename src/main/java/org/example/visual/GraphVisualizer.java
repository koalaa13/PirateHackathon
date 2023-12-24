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
    private double zoom = 0.45;
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

        int W = (int) (islandMap.getWidth() + 2);
        int H = (int) (islandMap.getHeight() + 2);

        setSize((int) Math.ceil(W * zoom), (int) Math.ceil(H * zoom));

        addMouseListener(new MouseListener(){
            public void mouseClicked(MouseEvent e){
                selectedX = (int) (e.getX() / zoom);
                selectedY = (int) ((e.getY() - 25) / zoom);
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
                g.setColor(new Color(0, 180, 0));
                g.drawRect((int) (selectedX * zoom), (int) (selectedY * zoom), 2, 2);

                g.setColor(new Color(0, 0, 180));
                paintTiles(myShipsTiles, g);

                g.setColor(new Color(180, 0, 0));
                paintTiles(enemyShipsTiles, g);

                g.setColor(new Color(180, 180, 0));
                paintTiles(islandMap.getRawTiles(), g);

                g.setColor(new Color(230, 230, 230));
                g.drawRect((int) (1000 * zoom), (int) (1000 * zoom), 2, 2);
            }
        };

        add(canvas);
        show();
    }

    private void paintTiles(IslandMap.Tiles tiles, Graphics g) {
        Iterator<Tile> it = tiles.iterateTiles();
        while (it.hasNext()) {
            Tile tile = it.next();
            g.drawRect((int) (tile.getX() * zoom), (int) (tile.getY() * zoom), 1, 1);
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
