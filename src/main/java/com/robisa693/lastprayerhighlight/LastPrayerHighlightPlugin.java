package com.robisa693.lastprayerhighlight;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.callback.ClientThread;
import com.google.inject.Provides;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

@PluginDescriptor(
    name = "Last Prayer Highlight",
    description = "Highlights the last used protection prayer (Protect from Magic, Missiles, Melee) on the prayer tab",
    tags = {"prayer", "pvm", "boss", "highlight", "protect"}
)
public class LastPrayerHighlightPlugin extends Plugin
{
    private static final int PROTECT_FROM_MAGIC_VARBIT = VarbitID.PRAYER_PROTECTFROMMAGIC;
    private static final int PROTECT_FROM_MISSILES_VARBIT = VarbitID.PRAYER_PROTECTFROMMISSILES;
    private static final int PROTECT_FROM_MELEE_VARBIT = VarbitID.PRAYER_PROTECTFROMMELEE;

    @Inject
    private LastPrayerHighlightConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private InfoBoxManager infoBoxManager;

    private LastPrayerHighlightOverlay overlay;
    private Prayer lastProtectionPrayer = null;
    private ScheduledFuture<?> future;
    private PrayerInfoBox infoBox;

    @Provides
    LastPrayerHighlightConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(LastPrayerHighlightConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlay = new LastPrayerHighlightOverlay(client, this);
        overlayManager.add(overlay);
        future = executor.scheduleAtFixedRate(this::pollProtectionPrayers, 0, 600, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void shutDown()
    {
        if (future != null)
        {
            future.cancel(false);
            future = null;
        }
        clientThread.invoke(() -> {
            overlayManager.remove(overlay);
            removeInfoBox();
            lastProtectionPrayer = null;
        });
    }

    private void pollProtectionPrayers()
    {
        clientThread.invokeLater(() -> {
            Prayer detected = null;
            if (client.getVarbitValue(PROTECT_FROM_MAGIC_VARBIT) == 1)
            {
                detected = Prayer.PROTECT_FROM_MAGIC;
            }
            else if (client.getVarbitValue(PROTECT_FROM_MISSILES_VARBIT) == 1)
            {
                detected = Prayer.PROTECT_FROM_MISSILES;
            }
            else if (client.getVarbitValue(PROTECT_FROM_MELEE_VARBIT) == 1)
            {
                detected = Prayer.PROTECT_FROM_MELEE;
            }

            if (detected != null)
            {
                lastProtectionPrayer = detected;
                updateInfoBox(detected);
            }
        });
    }

    private void updateInfoBox(Prayer prayer)
    {
        if (!config.showInfobox())
        {
            removeInfoBox();
            return;
        }

        if (infoBox != null && infoBox.prayer == prayer)
        {
            return;
        }

        removeInfoBox();

        BufferedImage image = createPrayerIcon(prayer);
        if (image == null)
        {
            return;
        }

        infoBox = new PrayerInfoBox(image, this, prayer, config);
        infoBoxManager.addInfoBox(infoBox);
    }

    private void removeInfoBox()
    {
        if (infoBox != null)
        {
            infoBoxManager.removeInfoBox(infoBox);
            infoBox = null;
        }
    }

    private static BufferedImage createPrayerIcon(Prayer prayer)
    {
        int iconSize = 32;
        BufferedImage img = new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bgColor;
        switch (prayer)
        {
            case PROTECT_FROM_MAGIC:
                bgColor = new Color(70, 100, 200);
                break;
            case PROTECT_FROM_MISSILES:
                bgColor = new Color(50, 160, 70);
                break;
            case PROTECT_FROM_MELEE:
                bgColor = new Color(190, 60, 60);
                break;
            default:
                g.dispose();
                return null;
        }

        g.setColor(bgColor);
        g.fillRoundRect(2, 2, iconSize - 4, iconSize - 4, 6, 6);

        g.dispose();
        return img;
    }

    public Prayer getLastProtectionPrayer()
    {
        return lastProtectionPrayer;
    }

    public LastPrayerHighlightConfig getConfig()
    {
        return config;
    }

    private static class PrayerInfoBox extends InfoBox
    {
        private final Prayer prayer;
        private final LastPrayerHighlightConfig config;

        PrayerInfoBox(BufferedImage image, Plugin plugin, Prayer prayer, LastPrayerHighlightConfig config)
        {
            super(image, plugin);
            this.prayer = prayer;
            this.config = config;
        }

        @Override
        public String getTooltip()
        {
            return getPrayerDisplayName(prayer);
        }

        @Override
        public String getText()
        {
            switch (prayer)
            {
                case PROTECT_FROM_MAGIC: return "Magic";
                case PROTECT_FROM_MISSILES: return "Ranged";
                case PROTECT_FROM_MELEE: return "Melee";
                default: return "";
            }
        }

        @Override
        public Color getTextColor()
        {
            return config.infoboxTextColor();
        }

        private String getPrayerDisplayName(Prayer prayer)
        {
            switch (prayer)
            {
                case PROTECT_FROM_MAGIC: return "Protect from Magic";
                case PROTECT_FROM_MISSILES: return "Protect from Missiles";
                case PROTECT_FROM_MELEE: return "Protect from Melee";
                default: return "";
            }
        }
    }
}
