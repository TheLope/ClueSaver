# Clue Saver
Clue Saver blocks certain methods to obtain clue scrolls while player is ineligible to receive another.

Clue scrolls and scroll boxes for each tier are tracked and compared with your scroll box capacities to determine if you are eligible to receive any more scroll boxes for a given tier.
If you are unable to receive more scroll boxes for that tier, the methods configured in the plugin will be blocked.

## Overlays

### User Interface
The `Show UI` toggle will provide a collapsible UI to display clue scroll capacity by tier.

It shows how many clues you can still receive, how many you're currently holding (with color indicators), where the clue is (bank or inventory), with an indicator that you are at capacity.

This feature is disabled by default. It can be configured to always show info per tier with `Debug` toggles, or hide info per tier using the `Tier Toggles`.

<img width="350" height="287" alt="clueSaverUI" src="https://github.com/user-attachments/assets/6e2cbbb3-4fc0-4835-8fdb-4a806c63dada" />

### Other
The `Show chat message` toggle will provide a chatbox message when a method is being blocked, letting you know the reason why.

<img width="303" height="32" alt="Ny3Jf4W" src="https://github.com/user-attachments/assets/c977c94d-700d-4c97-b84f-e5724bf3c176" />

The `Show tooltip` toggle will provide a tooltip message when hovering over a method which is being blocked, letting you know the reason why.

<img width="166" height="146" alt="Z0hNhkg" src="https://github.com/user-attachments/assets/8bf94609-babf-4beb-8e8a-994865856181" />

The `Show infobox` toggle will provide an infobox letting you know the reason why methods for each tier is are being blocked.

<img width="376" height="261" alt="TRiWLbK" src="https://github.com/user-attachments/assets/dedce160-a8b8-4e9b-aad8-dc957d0085ca" />

## Savers
Methods which could reward scroll boxes for each tier are provided, with the ability to block that method once you reach your scroll box capacity.

Please suggest additional methods you would find helpful to "save".

## Debug
Debug toggles provide additional information not shown by default.

In the event the plugin misbehaves, these will provide what the plugin "thinks" is the current state of your clues.

`Separate scroll box counts` is enabled in the above images.

## Tier Toggles
Tier toggles provide the ability to disable overlays and saving per tier.

## Limitations
When opening caskets, spam clicking too fast (more than once per tick) may cause additional caskets to be opened after receiving a master scroll box.
- Use the `Spamming cooldown` config option to avoid this behavior.

Depositing Scroll Boxes via Bank Deposit Boxes is not properly tracked.
