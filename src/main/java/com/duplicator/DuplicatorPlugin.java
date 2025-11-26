package com.duplicator;

import com.duplicator.managers.RuneLiteLocationManager;
import com.duplicator.ui.DuplicatorPanel;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Files;
import java.nio.file.Paths;

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
		log.info("Duplicator-macOS started!");
		if(OSType.getOSType() != OSType.MacOS){
			log.error("OS type is not MacOS");
			JOptionPane.showMessageDialog(null,
					"This plugin is only available on MacOS.\n" +
							"Current OS: " + OSType.getOSType().name(),
					"Unsupported Operating System",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		addNavBar();
		validateRuneLiteApp();

	}

	public void validateRuneLiteApp(){
		if(config.useCustomDirectory()){
			runeLiteLocationManager.validateCustomRuneLiteDirectory();
		} else {
			String runeliteAppLocation = runeLiteLocationManager.GetRuneLiteLocation();
			String runeLiteJarLocation = runeliteAppLocation + Constants.RUNE_LITE_APP_JAR;
			if(!Files.exists(Paths.get(runeLiteJarLocation))){
				SwingUtilities.invokeLater(()->{
					JOptionPane.showMessageDialog(null,
							"Could not find RuneLite.jar to verify sha-256 hash is the last 3 recent releases",
							"Unable to verify RuneLite.jar",
							JOptionPane.ERROR_MESSAGE);
				});
				return;
			}
			boolean isValidHash = runeLiteLocationManager.isValidHash(runeLiteJarLocation);
			if(!isValidHash){
				SwingUtilities.invokeLater(()->{
					JOptionPane.showMessageDialog(null,
							"The current RuneLite.jar hash does not match the last 3 recent releases at https://github.com/runelite/launcher/releases",
							"Unable to verify RuneLite.jar",
							JOptionPane.ERROR_MESSAGE);
				});
				return;
			}
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Duplicator-macOS stopped!");
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
}
