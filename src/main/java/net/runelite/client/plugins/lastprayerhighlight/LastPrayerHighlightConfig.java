package net.runelite.client.plugins.lastprayerhighlight;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import java.awt.Color;

@ConfigGroup(LastPrayerHighlightConfig.GROUP)
public interface LastPrayerHighlightConfig extends Config
{
    String GROUP = "lastprayerhighlight";

    @ConfigSection(
        name = "Highlight Settings",
        description = "Settings for the prayer highlight",
        position = 0
    )
    String highlightSection = "highlight";

    @ConfigItem(
        keyName = "highlightColor",
        name = "Highlight Color",
        description = "Color to highlight the last used protection prayer",
        section = highlightSection,
        position = 0
    )
    default Color highlightColor()
    {
        return Color.YELLOW;
    }

    @ConfigItem(
        keyName = "showOnPrayerTab",
        name = "Show on Prayer Tab",
        description = "Highlight the last used protection prayer on the prayer tab",
        section = highlightSection,
        position = 1
    )
    default boolean showOnPrayerTab()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showInfobox",
        name = "Show Infobox",
        description = "Show an infobox with the last used protection prayer",
        section = highlightSection,
        position = 2
    )
    default boolean showInfobox()
    {
        return false;
    }

    @ConfigItem(
        keyName = "infoboxTextColor",
        name = "Infobox Text Color",
        description = "Color of the text in the infobox",
        section = highlightSection,
        position = 3
    )
    default Color infoboxTextColor()
    {
        return Color.WHITE;
    }
}