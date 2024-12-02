package com.duplicator.managers;

import com.duplicator.Constants;
import com.duplicator.DuplicatorConfig;
import lombok.val;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;

public class RuneLiteLocationManager {

    @Inject
    private DuplicatorConfig config;
    @Inject
    private Client client;

    public String GetRuneLiteLocation() {
        val runeLiteDir = config.useCustomDirectory() ? config.customRuneLiteDirectory() : Constants.DEFAULT_RUNE_LITE_DIRECTORY;
        val basePath = Paths.get(runeLiteDir);
        val fullPath = basePath.resolve(Constants.RUNE_LITE_APP);
        if(fullPath.toFile().exists()) {
            return fullPath.toAbsolutePath().toString();
        }
        return null;
    }

    public String GetRuneLiteParentDirectory() {
        return config.useCustomDirectory() ? config.customRuneLiteDirectory() : Constants.DEFAULT_RUNE_LITE_DIRECTORY;
    }

    public void validateCustomRuneLiteDirectory(){
        val sb = new StringBuilder("The Custom Directory Path:");
        sb.append(config.customRuneLiteDirectory());
        if(!isValidDirectory(config.customRuneLiteDirectory())){
            sb.append(" does not exist. \n");
            SwingUtilities.invokeLater(()->{
                JOptionPane.showMessageDialog(client.getCanvas(),
                        sb.toString(),
                        "Invalid Directory",
                        JOptionPane.ERROR_MESSAGE);
            });
            return;
        }
        if(!doesContainRuneLiteApp(config.customRuneLiteDirectory())){
            SwingUtilities.invokeLater(()->{
                sb.append("You have set does not contain Constants.RUNE_LITE_APP. \n");
                JOptionPane.showMessageDialog(client.getCanvas(),
                        sb.toString(),
                        "Invalid Directory",
                        JOptionPane.ERROR_MESSAGE);
            });
            return;
        }
    }

    private boolean isValidDirectory(String path){
        val directory = new File(path);
        return  directory.exists() && directory.isDirectory();
    }
    private boolean doesContainRuneLiteApp(String path){
        val basePath = Paths.get(path);
        val fullPath = basePath.resolve(Constants.RUNE_LITE_APP);
        val runeLiteAppPath = new File(fullPath.toString());
        return runeLiteAppPath.exists();
    }
}
