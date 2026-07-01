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
    private Color lastHighlightColor;
    private int lastFillOpacity = -1;
    private int lastBorderOpacity = -1;
    private Color translucentFillColor;
    private Color translucentBorderColor;

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

        Widget parent = widget.getParent();
        if (parent != null && parent.isHidden())
        {
            return null;
        }

        Rectangle bounds = widget.getBounds();
        if (bounds == null || bounds.width <= 0 || bounds.height <= 0)
        {
            return null;
        }

        Color highlightColor = config.highlightColor();
        int fillOpacity = config.highlightOpacity();
        int borderOpacity = config.highlightBorderOpacity();
        if (!highlightColor.equals(lastHighlightColor) || fillOpacity != lastFillOpacity || borderOpacity != lastBorderOpacity)
        {
            lastHighlightColor = highlightColor;
            lastFillOpacity = fillOpacity;
            lastBorderOpacity = borderOpacity;
            int fillAlpha = Math.max(0, Math.min(100, fillOpacity)) * 255 / 100;
            int borderAlpha = Math.max(0, Math.min(100, borderOpacity)) * 255 / 100;
            translucentFillColor = new Color(highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue(), fillAlpha);
            translucentBorderColor = new Color(highlightColor.getRed(), highlightColor.getGreen(), highlightColor.getBlue(), borderAlpha);
        }
        graphics.setColor(translucentFillColor);
        graphics.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);

        graphics.setColor(translucentBorderColor);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);

        return null;
    }

    private Widget getPrayerWidget(Prayer prayer)
    {
        int packedId;
        switch (prayer)
        {
            case PROTECT_FROM_MAGIC:
                packedId = InterfaceID.Prayerbook.PRAYER13;
                break;
            case PROTECT_FROM_MISSILES:
                packedId = InterfaceID.Prayerbook.PRAYER14;
                break;
            case PROTECT_FROM_MELEE:
                packedId = InterfaceID.Prayerbook.PRAYER15;
                break;
            default:
                return null;
        }
        return client.getWidget(packedId);
    }
}
