package org.example.visual;


import org.example.model.IslandMap;
import org.example.model.Ship;
import org.example.model.Tile;
import org.example.model.Zone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class GraphVisualizer extends JFrame {
    private double zoom = 0.4;
    private IslandMap islandMap;

    private List<Ship> myShips = new ArrayList<>();

    private IslandMap.Tiles myShipsTiles = new IslandMap.Tiles();

    private IslandMap.Tiles enemyShipsTiles = new IslandMap.Tiles();

    private JPanel canvas;

    private long selectedX = 0;

    private long selectedY = 0;

    private Zone zone = null;

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
                g.setColor(new Color(0, 180, 0));
                g.drawRect((int) (selectedX * zoom), (int) (selectedY * zoom), 2, 2);

                g.setColor(new Color(0, 0, 180));
                paintTiles(myShipsTiles, g);

                g.setColor(new Color(180, 0, 0));
                paintTiles(enemyShipsTiles, g);

                g.setColor(new Color(180, 180, 0));
                paintTiles(islandMap.getRawTiles(), g);

                g.setColor(new Color(20, 20, 20));
                g.drawRect((int) (W / 2 * zoom), (int) (H / 2 * zoom), 2, 2);
                paintShipLabels(g);

                g.setColor(new Color(240, 100, 100));
                paintZone(g);
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
        this.myShips = new ArrayList<>(myShips);
        myShipsTiles = new IslandMap.Tiles();
        myShips.forEach(s -> s.toTiles().forEach(myShipsTiles::addTile));
    }

    private void paintShipLabels(Graphics g) {
        for (Ship ship : myShips) {
            g.setFont(new Font("Bold", 1, 3));
            g.drawString(ship.getId() + "", (int) (ship.getX() * zoom), (int) ((ship.getY() - 30) * zoom));
        }
    }

    public void setEnemyShips(List<Ship> enemyShips) {
        enemyShipsTiles = new IslandMap.Tiles();
        enemyShips.forEach(s -> s.toTiles().forEach(enemyShipsTiles::addTile));
    }

    private void paintZone(Graphics g) {
        if (zone == null) return;
        g.drawRect((int) (zone.getX() * zoom), (int) (zone.getY() * zoom), 1, 1);
        g.drawOval((int) ((zone.getX() - zone.getRadius()) * zoom), (int) ((zone.getY() - zone.getRadius()) * zoom),
                (int) (2 * zone.getRadius() * zoom), (int) (2 * zone.getRadius() * zoom));
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public long getSelectedX() {
        return selectedX;
    }

    public long getSelectedY() {
        return selectedY;
    }
}
