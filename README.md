# Last Prayer Highlight
![Demo](https://i.imgur.com/nK2QHag.gif)

Highlights the last used protection prayer on the prayer tab.

When you switch between Protect from Magic, Missiles, or Melee, the last used one stays highlighted so you always know which protection prayer you were using last — even when the prayer has deactivated or you're prayer-flicking.

## Features

- Highlights the protection prayer widget on the prayer tab with a configurable color
- Optional infobox showing a colored tile with the prayer name
- Works with both standard and quick-prayer activation
- Instant response — uses event-driven varbit tracking

## Configuration

Open RuneLite settings → _Last Prayer Highlight_:

| Setting | Default | Description |
|---|---|---|
| Highlight Color | Yellow | Color of the prayer tab overlay |
| Show on Prayer Tab | On | Toggle the prayer tab highlight on/off |
| Show Infobox | Off | Toggle the infobox on/off |
| Infobox Text Color | White | Color of the text on the infobox tile |

## Building

```bash
./gradlew build
```

The output JAR is at `build/libs/last-prayer-highlight-*.jar`.
