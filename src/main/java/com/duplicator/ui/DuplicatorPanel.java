package com.duplicator.ui;

import com.duplicator.DuplicatorConfig;
import com.duplicator.managers.RuneLiteLocationManager;
import lombok.val;
import net.runelite.api.Client;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.io.File;

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

        Dimension buttonSize = new Dimension(200, 60); // Width: 200, Height: 60
        duplicateButton.setPreferredSize(buttonSize);
        duplicateButton.setMaximumSize(buttonSize);
        duplicateButton.setMinimumSize(buttonSize);

        duplicateButton.setFont(new Font(duplicateButton.getFont().getName(), Font.PLAIN, 16));

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
        if(!locationManager.isValidHash(locationManager.GetRuneLiteJarLocation())){
            SwingUtilities.invokeLater(()->{
                JOptionPane.showMessageDialog(null,
                        "The current RuneLite.jar hash does not match the last 3 recent releases at https://github.com/runelite/launcher/releases",
                        "Unable to verify RuneLite.jar",
                        JOptionPane.ERROR_MESSAGE);
            });
        }
        SwingUtilities.invokeLater(() -> {
            try {
                String normalizedPath = new File(runeLiteLocation).getCanonicalPath();
                String[] command = {"open", "-n", "-a", normalizedPath};
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.start();
            } catch (Exception ex) {
                SwingUtilities.invokeLater(()-> JOptionPane.showMessageDialog(client.getCanvas(),
                        ex.getMessage(),
                        "Error Opening Up RuneLite.app",
                        JOptionPane.ERROR_MESSAGE));
            }
        });
    }

}
