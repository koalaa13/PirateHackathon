package org.example.visual;

import org.example.model.IslandMap;

import javax.swing.*;
import java.awt.*;

public class FieldUI extends JFrame {
    TextField t1;

    public FieldUI() {
        setSize(200, 100);

        t1 = new TextField("0");

        add(t1);

        show();
    }

    public long getSelectedShip() {
        try {
            return Long.parseLong(t1.getText());
        } catch (NumberFormatException e) {
            System.err.println("Wrong ship id");
            return 0;
        }
    }
}
