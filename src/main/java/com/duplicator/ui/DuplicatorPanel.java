package com.duplicator.ui;

import com.duplicator.Constants;
import com.duplicator.DuplicatorConfig;
import com.duplicator.managers.RuneLiteLocationManager;
import lombok.val;
import net.runelite.api.Client;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class DuplicatorPanel extends PluginPanel {

    @Inject
    DuplicatorConfig config;
    @Inject
    private Client client;
    @Inject
    private RuneLiteLocationManager locationManager;

    DuplicatorPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton duplicateButton = new JButton("Duplicate RuneLite");
        duplicateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        duplicateButton.setMaximumSize(new Dimension(1000, 50));
        duplicateButton.addActionListener(e -> duplicateRuneLite());


        add(Box.createRigidArea(new Dimension(0, 10)));
        add(duplicateButton);
    }

    private void duplicateRuneLite()
    {
        val runeLiteLocation = locationManager.GetRuneLiteLocation();
        if(runeLiteLocation == null){
            SwingUtilities.invokeLater(()->{
                JOptionPane.showMessageDialog(client.getCanvas(),
                        String.format("RuneLite.app was not found in the directory %s",locationManager.GetRuneLiteParentDirectory()),
                        "Unable to find RuneLite.app",
                        JOptionPane.ERROR_MESSAGE);
            });
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                String[] command = {"open", "-n", "-a", runeLiteLocation};
                Runtime.getRuntime().exec(command);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(()-> JOptionPane.showMessageDialog(client.getCanvas(),
                        ex.getMessage(),
                        "Error Opening Up RuneLite.app",
                        JOptionPane.ERROR_MESSAGE));
            }
        });
    }

}
