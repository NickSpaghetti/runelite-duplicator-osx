package com.duplicator.ui;

import com.duplicator.Constants;
import com.duplicator.DuplicatorConfig;
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

    DuplicatorPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton duplicateButton = new JButton("Duplicate RuneLite");
        duplicateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        duplicateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        duplicateButton.addActionListener(e -> duplicateRuneLite());


        add(Box.createRigidArea(new Dimension(0, 10)));
        add(duplicateButton);
    }

    private void duplicateRuneLite()
    {
        val runeLiteDir = config.useCustomDirectory() ? config.customRuneLiteDirectory() : Constants.DEFAULT_RUNE_LITE_DIRECTORY;
        SwingUtilities.invokeLater(() -> {
            try {
                String[] command = {"open", "-n", "-a", runeLiteDir + "/" + Constants.RUNE_LITE_APP};
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
