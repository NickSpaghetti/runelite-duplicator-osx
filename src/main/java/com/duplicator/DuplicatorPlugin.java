package com.duplicator;

import com.duplicator.managers.RuneLiteLocationManager;
import com.duplicator.ui.DuplicatorPanel;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.runelite.api.Client;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.OSType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;

import static com.duplicator.DuplicatorConfig.CONFIG_GROUP;

@Slf4j
@PluginDescriptor(
	name = "Duplicator-OSX"
)
public class DuplicatorPlugin extends Plugin
{
	@Inject
	private ConfigManager configManager;

	@Inject
	private Client client;

	@Inject
	private DuplicatorConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private RuneLiteLocationManager runeLiteLocationManager;

	private DuplicatorPanel panel;
	private NavigationButton navButton;


	@Override
	protected void startUp() throws Exception
	{
		log.info("Duplicator-OSX started!");
		if(OSType.getOSType() != OSType.MacOS){
			log.error("OS type is not MacOS");
			return;
		}
		addNavBar();
		if(config.useCustomDirectory()){
			runeLiteLocationManager.validateCustomRuneLiteDirectory();
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Duplicator-OSX stopped!");
		clientToolbar.removeNavigation(navButton);
		panel = null;
		navButton = null;
	}

	public void addNavBar(){
		panel = injector.getInstance(DuplicatorPanel.class);
		BufferedImage ICON = ImageUtil.loadImageResource(DuplicatorPlugin.class, "/nav-icon.png");
		navButton = NavigationButton.builder()
				.tooltip("RuneLite Duplicator")
				.icon(ICON)
				.priority(12)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);
	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {}

	@Provides
	DuplicatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DuplicatorConfig.class);
	}

	@Subscribe()
	public void onConfigChanged(ConfigChanged event){
		String configName = event.getKey();
		if(configName.equals(Constants.USE_CUSTOM_DIRECTORY)){
			boolean isCustomDirectoryUsed =  Boolean.parseBoolean(event.getNewValue());
			if(!isCustomDirectoryUsed){
				return;
			}
			runeLiteLocationManager.validateCustomRuneLiteDirectory();
		}
		if(configName.equals(Constants.CUSTOM_RUNE_LITE_DIRECTORY) && config.useCustomDirectory()){
			runeLiteLocationManager.validateCustomRuneLiteDirectory();
		}
	}


	private void openDirectoryChooser() {
		SwingUtilities.invokeLater(() -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle("Select Directory");

			int result = chooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				String selectedPath = chooser.getSelectedFile().getAbsolutePath();
				configManager.setConfiguration(CONFIG_GROUP, Constants.CUSTOM_RUNE_LITE_DIRECTORY, selectedPath);
				runeLiteLocationManager.validateCustomRuneLiteDirectory();
			}

		});
	}
}
