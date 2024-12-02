package com.duplicator;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("RuneLite Duplicator")
public interface DuplicatorConfig extends Config
{
	String CONFIG_GROUP = "runeliteduplicator";

	@ConfigItem(
			keyName = Constants.CUSTOM_RUNE_LITE_DIRECTORY,
			name = "Custom Rune lite Directory",
			description = "<html>Set the directory where your runelite.app lives.</html>"
	)
	default String customRuneLiteDirectory()
	{
		return Constants.DEFAULT_RUNE_LITE_DIRECTORY;
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
