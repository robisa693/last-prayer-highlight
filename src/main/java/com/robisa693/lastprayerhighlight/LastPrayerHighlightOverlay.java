package com.robisa693.lastprayerhighlight;

import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import java.awt.*;

public class LastPrayerHighlightOverlay extends Overlay
{
    private final Client client;
    private final LastPrayerHighlightPlugin plugin;

    public LastPrayerHighlightOverlay(Client client, LastPrayerHighlightPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(Overlay.PRIORITY_HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        LastPrayerHighlightConfig config = plugin.getConfig();
        if (config == null || !config.showOnPrayerTab())
        {
            return null;
        }

        Prayer lastPrayer = plugin.getLastProtectionPrayer();
        if (lastPrayer == null)
        {
            return null;
        }

        Widget widget = getPrayerWidget(lastPrayer);
        if (widget == null || widget.isHidden())
        {
            return null;
        }

        Rectangle bounds = widget.getBounds();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0)
        {
            return null;
        }

        Color highlightColor = config.highlightColor();
        graphics.setColor(new Color(highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue(), 100));
        graphics.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);

        graphics.setColor(highlightColor);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);

        return null;
    }

    private Widget getPrayerWidget(Prayer prayer)
    {
        int childId;
        switch (prayer)
        {
            case PROTECT_FROM_MAGIC:
                childId = InterfaceID.Prayerbook.PRAYER13 & 0xFFFF;
                break;
            case PROTECT_FROM_MISSILES:
                childId = InterfaceID.Prayerbook.PRAYER14 & 0xFFFF;
                break;
            case PROTECT_FROM_MELEE:
                childId = InterfaceID.Prayerbook.PRAYER15 & 0xFFFF;
                break;
            default:
                return null;
        }
        return client.getWidget(InterfaceID.PRAYERBOOK, childId);
    }
}
