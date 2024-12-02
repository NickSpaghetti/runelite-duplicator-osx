package com.duplicator.ui;

import java.io.File;

public class RuneLiteFileFilter extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().equalsIgnoreCase("runelite.app");
    }

    @Override
    public String getDescription() {
        return "macOS Rune Lite";
    }
}
