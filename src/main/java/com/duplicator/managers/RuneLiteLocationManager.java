package com.duplicator.managers;

import com.duplicator.Constants;
import com.duplicator.DuplicatorConfig;
import lombok.val;
import net.runelite.api.Actor;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RuneLiteLocationManager {

    @Inject
    private GithubFileHashManger githubFileHashManger;
    @Inject
    private DuplicatorConfig config;
    @Inject
    private Client client;

    private List<String> LastThreeRuneLiteJarHashes = new ArrayList<String>();

    public String GetRuneLiteLocation() {
        val runeLiteDir = config.useCustomDirectory() ? config.customRuneLiteDirectory() : Constants.DEFAULT_RUNE_LITE_DIRECTORY;
        val basePath = Paths.get(runeLiteDir);
        val fullPath = basePath.resolve(Constants.RUNE_LITE_APP);
        if(fullPath.toFile().exists()) {
            return fullPath.toAbsolutePath().toString();
        }
        return null;
    }

    public String GetRuneLiteJarLocation() {
        val baseLocation = this.GetRuneLiteLocation();
        if(baseLocation == null) {
            return null;
        }
        val jarPath = baseLocation + Constants.RUNE_LITE_APP_JAR;
        if(!Paths.get(jarPath).toFile().exists()) {
            return null;
        }
        return jarPath;
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
        String runeLiteJarPath = config.customRuneLiteDirectory() + "/" + Constants.RUNE_LITE_APP + Constants.RUNE_LITE_APP_JAR;
        if(!Files.exists(Paths.get(runeLiteJarPath))){
            SwingUtilities.invokeLater(()->{sb.append("Unable to find runelite.jar \n");
                JOptionPane.showMessageDialog(client.getCanvas(),
                        sb.toString(),
                        "Invalid Directory",
                        JOptionPane.ERROR_MESSAGE);
            });
            return;
        }
        if(!isValidHash(runeLiteJarPath)){
            SwingUtilities.invokeLater(()->{sb.append("Invalid hash \n");
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

    private String getFileSHA256(String filePath) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(Paths.get(filePath));
             DigestInputStream dis = new DigestInputStream(is, md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1);
            md = dis.getMessageDigest();
        }

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public boolean isValidHash(String runeLiteJarFilePath){
        try{
            if(LastThreeRuneLiteJarHashes.isEmpty()){
                LastThreeRuneLiteJarHashes.addAll(githubFileHashManger.getFileHashes(Constants.RUNE_LITE_JAR));
            }
            String localJarHash = getFileSHA256(runeLiteJarFilePath);
            return LastThreeRuneLiteJarHashes.contains(localJarHash);
        } catch(Exception e){
            return false;
        }
    }

}
