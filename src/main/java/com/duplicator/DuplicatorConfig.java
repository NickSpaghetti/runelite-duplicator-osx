package com.duplicator;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.io.File;

@ConfigGroup("RuneLite Duplicator")
public interface DuplicatorConfig extends Config
{
	String CONFIG_GROUP = "runeliteduplicator";
	@ConfigItem(
		keyName = "runelite-duplicator-osx",
		name = "Welcome To The Duplicator",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Hello";
	}

	@ConfigItem(
			keyName = Constants.CUSTOM_RUNE_LITE_DIRECTORY,
			name = "Custom Rune lite Directory",
			description = "<html>Set the directory where your runelite.app lives.</html>"
	)
	default String customRuneLiteDirectory()
	{
		return System.getProperty("user.home");
	}


	@ConfigItem(
			keyName = Constants.USE_CUSTOM_DIRECTORY,
			name = "Use Custom Directory",
			description = "Enable to use the custom directory instead of the default"
	)
	default boolean useCustomDirectory()
	{
		return false;
	}
}
