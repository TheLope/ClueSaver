# Clue Saver
Clue Saver blocks certain methods to obtain clue scrolls while player is ineligible to receive another.

Clue scrolls and scroll boxes for each tier are tracked and compared with your scroll box capacities to determine if you are eligible to receive any more scroll boxes for a given tier.
If you are unable to receive more scroll boxes for that tier, the methods selected in the plugin configured with be blocked.

## Overlays
The "Show chat message" toggle will provide a chatbox message when a method is being blocked, letting you know the reason why.

The "Show infobox" toggle will provide an infobox letting you know the reason why methods for each tier is are being blocked.

<img width="362" height="273" alt="2zFI7dC" src="https://github.com/user-attachments/assets/c4a6e9fc-8ad9-477e-8c13-c2e78370c057" />

The "Show tooltip" toggle will provide a tooltip message when hovering over a method which is being blocked, letting you know the reason why.

<img width="362" height="273" alt="G3HqRIn" src="https://github.com/user-attachments/assets/c18fe2f8-df16-4a5f-892d-a25dad1524d9" />

## Savers
Methods which could reward scroll boxes for each tier are listed with the ability to block that method once you reach your scroll box capacity.

Please suggest additional methods you would find helpful to "save".

## Debug
Debug options are provided in the event the plugin misbehaves. These will provide what the plugin "thinks" is the current state of your clues.

## Limitations
When opening caskets, spam clicking too fast (more than once per tick) may cause additional caskets to be opened after receiving a master scroll box.
- Use the 'Spamming cooldown' config option to avoid this behavior.
