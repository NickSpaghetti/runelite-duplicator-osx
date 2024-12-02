package com.duplicator.ui;

import com.duplicator.Constants;
import com.duplicator.DuplicatorConfig;
import lombok.val;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

import static com.duplicator.DuplicatorConfig.CONFIG_GROUP;

public class DuplicatorPanel extends PluginPanel {
    private final JButton duplicateButton;

    @Inject
    DuplicatorConfig config;
    @Inject
    private Client client;

    DuplicatorPanel()
    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        duplicateButton = new JButton("Duplicate RuneLite");
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
                String[] command = {"open", "-n", "-a", runeLiteDir + "/RuneLite.app"};
                Runtime.getRuntime().exec(command);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(()->{
                    JOptionPane.showMessageDialog(client.getCanvas(),
                            ex.getMessage(),
                            "Error Opening Up RuneLite.app",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }

}
