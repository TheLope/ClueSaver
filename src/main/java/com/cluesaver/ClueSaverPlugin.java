/*
 * Copyright (c) 2025, TheLope <https://github.com/TheLope>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.cluesaver;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GroundObject;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.ObjectID;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.MenuShouldLeftClick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

@Slf4j
@PluginDescriptor(
	name = "Clue Saver"
)
public class ClueSaverPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	private InfoBox infoBox = null;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClueSaverOverlay infoOverlay;

	@Inject
	private ClueSaverConfig config;
	private ClueLocation eliteLocation = ClueLocation.UNKNOWN;
	private boolean clueDeposited = false;

	private final List<Integer> goldChests = Arrays.asList(
		ObjectID.GOLD_CHEST,
		ObjectID.GOLD_CHEST_41213,
		ObjectID.GOLD_CHEST_41214,
		ObjectID.GOLD_CHEST_41215,
		ObjectID.GOLD_CHEST_41216);

	private final List<Integer> eliteClues = Arrays.asList(
		ItemID.CLUE_SCROLL_ELITE,
		ItemID.CLUE_SCROLL_ELITE_12157,
		ItemID.CLUE_SCROLL_ELITE_28912,
		ItemID.CLUE_SCROLL_ELITE_12089,
		ItemID.CLUE_SCROLL_ELITE_12091,
		ItemID.CLUE_SCROLL_ELITE_12110,
		ItemID.CLUE_SCROLL_ELITE_12086,
		ItemID.CLUE_SCROLL_ELITE_12111,
		ItemID.CLUE_SCROLL_ELITE_12107,
		ItemID.CLUE_SCROLL_ELITE_23770,
		ItemID.CLUE_SCROLL_ELITE_12100,
		ItemID.CLUE_SCROLL_ELITE_12098,
		ItemID.CLUE_SCROLL_ELITE_25499,
		ItemID.CLUE_SCROLL_ELITE_12102,
		ItemID.CLUE_SCROLL_ELITE_12103,
		ItemID.CLUE_SCROLL_ELITE_25498,
		ItemID.CLUE_SCROLL_ELITE_12088,
		ItemID.CLUE_SCROLL_ELITE_12099,
		ItemID.CLUE_SCROLL_ELITE_25787,
		ItemID.CLUE_SCROLL_ELITE_19813,
		ItemID.CLUE_SCROLL_ELITE_12085,
		ItemID.CLUE_SCROLL_ELITE_12108,
		ItemID.CLUE_SCROLL_ELITE_12106,
		ItemID.CLUE_SCROLL_ELITE_26944,
		ItemID.CLUE_SCROLL_ELITE_12096,
		ItemID.CLUE_SCROLL_ELITE_12104,
		ItemID.CLUE_SCROLL_ELITE_23146,
		ItemID.CLUE_SCROLL_ELITE_12090,
		ItemID.CLUE_SCROLL_ELITE_12094,
		ItemID.CLUE_SCROLL_ELITE_12105,
		ItemID.CLUE_SCROLL_ELITE_12087,
		ItemID.CLUE_SCROLL_ELITE_23148,
		ItemID.CLUE_SCROLL_ELITE_12109,
		ItemID.CLUE_SCROLL_ELITE_12101,
		ItemID.CLUE_SCROLL_ELITE_12092,
		ItemID.CLUE_SCROLL_ELITE_12095,
		ItemID.CLUE_SCROLL_ELITE_23147,
		ItemID.CLUE_SCROLL_ELITE_12097,
		ItemID.CLUE_SCROLL_ELITE_12093,
		ItemID.CLUE_SCROLL_ELITE_22000,
		ItemID.CLUE_SCROLL_ELITE_24253,
		ItemID.CLUE_SCROLL_ELITE_12156,
		ItemID.CLUE_SCROLL_ELITE_19797,
		ItemID.CLUE_SCROLL_ELITE_19805,
		ItemID.CLUE_SCROLL_ELITE_19804,
		ItemID.CLUE_SCROLL_ELITE_19798,
		ItemID.CLUE_SCROLL_ELITE_19799,
		ItemID.CLUE_SCROLL_ELITE_19800,
		ItemID.CLUE_SCROLL_ELITE_19806,
		ItemID.CLUE_SCROLL_ELITE_19796,
		ItemID.CLUE_SCROLL_ELITE_19803,
		ItemID.CLUE_SCROLL_ELITE_19801,
		ItemID.CLUE_SCROLL_ELITE_19802,
		ItemID.CLUE_SCROLL_ELITE_12151,
		ItemID.CLUE_SCROLL_ELITE_19809,
		ItemID.CLUE_SCROLL_ELITE_19793,
		ItemID.CLUE_SCROLL_ELITE_19784,
		ItemID.CLUE_SCROLL_ELITE_12075,
		ItemID.CLUE_SCROLL_ELITE_19789,
		ItemID.CLUE_SCROLL_ELITE_12078,
		ItemID.CLUE_SCROLL_ELITE_28910,
		ItemID.CLUE_SCROLL_ELITE_12132,
		ItemID.CLUE_SCROLL_ELITE_12138,
		ItemID.CLUE_SCROLL_ELITE_12076,
		ItemID.CLUE_SCROLL_ELITE_21524,
		ItemID.CLUE_SCROLL_ELITE_12134,
		ItemID.CLUE_SCROLL_ELITE_12079,
		ItemID.CLUE_SCROLL_ELITE_12158,
		ItemID.CLUE_SCROLL_ELITE_19810,
		ItemID.CLUE_SCROLL_ELITE_12150,
		ItemID.CLUE_SCROLL_ELITE_12154,
		ItemID.CLUE_SCROLL_ELITE_21525,
		ItemID.CLUE_SCROLL_ELITE_19785,
		ItemID.CLUE_SCROLL_ELITE_12145,
		ItemID.CLUE_SCROLL_ELITE_12141,
		ItemID.CLUE_SCROLL_ELITE_12140,
		ItemID.CLUE_SCROLL_ELITE_12080,
		ItemID.CLUE_SCROLL_ELITE_19791,
		ItemID.CLUE_SCROLL_ELITE_12155,
		ItemID.CLUE_SCROLL_ELITE_12144,
		ItemID.CLUE_SCROLL_ELITE_12152,
		ItemID.CLUE_SCROLL_ELITE_12153,
		ItemID.CLUE_SCROLL_ELITE_19782,
		ItemID.CLUE_SCROLL_ELITE_12074,
		ItemID.CLUE_SCROLL_ELITE_12083,
		ItemID.CLUE_SCROLL_ELITE_19792,
		ItemID.CLUE_SCROLL_ELITE_12082,
		ItemID.CLUE_SCROLL_ELITE_19790,
		ItemID.CLUE_SCROLL_ELITE_12136,
		ItemID.CLUE_SCROLL_ELITE_12133,
		ItemID.CLUE_SCROLL_ELITE_23144,
		ItemID.CLUE_SCROLL_ELITE_12135,
		ItemID.CLUE_SCROLL_ELITE_19794,
		ItemID.CLUE_SCROLL_ELITE_23145,
		ItemID.CLUE_SCROLL_ELITE_19787,
		ItemID.CLUE_SCROLL_ELITE_12146,
		ItemID.CLUE_SCROLL_ELITE_19795,
		ItemID.CLUE_SCROLL_ELITE_25786,
		ItemID.CLUE_SCROLL_ELITE_12077,
		ItemID.CLUE_SCROLL_ELITE_19807,
		ItemID.CLUE_SCROLL_ELITE_12127,
		ItemID.CLUE_SCROLL_ELITE_19788,
		ItemID.CLUE_SCROLL_ELITE_12130,
		ItemID.CLUE_SCROLL_ELITE_12159,
		ItemID.CLUE_SCROLL_ELITE_12143,
		ItemID.CLUE_SCROLL_ELITE_19786,
		ItemID.CLUE_SCROLL_ELITE_12142,
		ItemID.CLUE_SCROLL_ELITE_12137,
		ItemID.CLUE_SCROLL_ELITE_12149,
		ItemID.CLUE_SCROLL_ELITE_12148,
		ItemID.CLUE_SCROLL_ELITE_19808,
		ItemID.CLUE_SCROLL_ELITE_28911,
		ItemID.CLUE_SCROLL_ELITE_12081,
		ItemID.CLUE_SCROLL_ELITE_19811,
		ItemID.CLUE_SCROLL_ELITE_12147,
		ItemID.CLUE_SCROLL_ELITE_19783,
		ItemID.CLUE_SCROLL_ELITE_12113,
		ItemID.CLUE_SCROLL_ELITE_12114,
		ItemID.CLUE_SCROLL_ELITE_12115,
		ItemID.CLUE_SCROLL_ELITE_12116,
		ItemID.CLUE_SCROLL_ELITE_12117,
		ItemID.CLUE_SCROLL_ELITE_12118,
		ItemID.CLUE_SCROLL_ELITE_12119,
		ItemID.CLUE_SCROLL_ELITE_12120,
		ItemID.CLUE_SCROLL_ELITE_12121,
		ItemID.CLUE_SCROLL_ELITE_12122,
		ItemID.CLUE_SCROLL_ELITE_12123,
		ItemID.CLUE_SCROLL_ELITE_12124,
		ItemID.CLUE_SCROLL_ELITE_12125,
		ItemID.CLUE_SCROLL_ELITE_12126,
		ItemID.CLUE_SCROLL_ELITE_24773,
		ItemID.CLUE_SCROLL_ELITE_26943,
		ItemID.CLUE_SCROLL_ELITE_29855,
		ItemID.CLUE_SCROLL_ELITE_29856,
		ItemID.CHALLENGE_SCROLL_ELITE);

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(infoOverlay);
		clientThread.invoke(this::loadFromConfig);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(infoOverlay);
		removeInfoBox();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invoke(this::loadFromConfig);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INVENTORY.getId())
		{
			checkBank();
			checkContainer(event.getItemContainer(), ClueLocation.INVENTORY);
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANK)
		{
			checkBank();
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Track clue interactions to detect banking edge cases
		if (eliteClues.contains(event.getItemId()))
		{
			clueDeposited = event.getMenuOption().contains("Deposit");
		}

		// If any clue method saver is active
		if (config.saveGoldKeys()
			|| config.saveDarkTotems()
			|| config.saveTobRewardsChests()
			|| config.saveGauntletRewardChests())
		{
			MenuEntry entry = event.getMenuEntry();
			int objectId = objectIdForEntry(entry);

			// Consume clue method events
			if (objectId != -1 && isClueMethodToSave(objectId, entry.getOption()))
			{
				if (hasClue())
				{
					saveClue(event);
				}
			}
		}
	}

	@Subscribe
	public void onMenuShouldLeftClick(MenuShouldLeftClick event)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();
		for (MenuEntry entry : menuEntries)
		{
			if (entry.getOption().equals("Deposit inventory"))
			{
				clueDeposited = true;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		handleInfoBox();
	}

	@Provides
	ClueSaverConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClueSaverConfig.class);
	}

	private void checkBank()
	{
		ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);

		if (bankContainer != null)
		{
			checkContainer(bankContainer, ClueLocation.BANK);
		}
	}

	private void checkContainer(ItemContainer container, ClueLocation location)
	{
		if (Arrays.stream(container.getItems()).anyMatch(item -> eliteClues.contains(item.getId())))
		{
			setEliteLocation(location);
		}
		// If clue was previously located in container and not found, update location
		else if (eliteLocation.equals(location))
		{
			// Check if clue was banked
			if (location.equals(ClueLocation.INVENTORY) && clueDeposited)
			{
				setEliteLocation(ClueLocation.BANK);
			}
			else
			{
				setEliteLocation(ClueLocation.UNKNOWN);
			}
		}
	}

	public String getCause()
	{
		if (!hasClue())
		{
			return null;
		}

		StringBuilder savingCause = new StringBuilder()
			.append(ColorUtil.wrapWithColorTag("Clue Saver: ", Color.YELLOW))
			.append(ColorUtil.wrapWithColorTag("active", Color.GREEN))
			.append(ColorUtil.wrapWithColorTag("<br>Cause: ", Color.YELLOW))
			.append("Elite clue ");
		if (eliteLocation.equals(ClueLocation.BANK))
		{
			savingCause.append("in ").append(ColorUtil.wrapWithColorTag("bank", Color.RED));
		}
		else if (eliteLocation.equals(ClueLocation.INVENTORY))
		{
			savingCause.append("in ").append(ColorUtil.wrapWithColorTag("inventory", Color.RED));
		}
		return savingCause.toString();
	}

	private void handleInfoBox()
	{
		var isShowing = infoBox != null;
		var shouldShow = config.showInfobox() && hasClue();

		if (isShowing && !shouldShow)
		{
			removeInfoBox();
		}
		else if (shouldShow)
		{
			if (!isShowing)
			{
				infoBox = new InfoBox(itemManager.getImage(ItemID.CLUE_SCROLL_23814), this)
				{
					@Override
					public String getText()
					{
						return "";
					}

					@Override
					public Color getTextColor()
					{
						return null;
					}
				};
			}

			infoBox.setTooltip(getCause());

			if (!isShowing)
			{
				infoBoxManager.addInfoBox(infoBox);
			}
		}
	}

	private boolean hasClue()
	{
		return !eliteLocation.equals(ClueLocation.UNKNOWN);
	}

	public boolean isClueMethodToSave(Integer objectId, String menuOption)
	{
		// Save Dark totems
		if (config.saveDarkTotems() && objectId == ObjectID.ALTAR_28900 && menuOption.equals("Use"))
		{
			return true;
		}

		// Save Gold keys
		if (config.saveGoldKeys() && goldChests.contains(objectId) && menuOption.equals("Open"))
		{
			return true;
		}

		// Save Gauntlet Reward Chests
		if (config.saveGauntletRewardChests() && objectId == ObjectID.REWARD_CHEST_36087 && menuOption.equals("Open"))
		{
			return true;
		}

		// Save ToB Rewards Chests
		if (config.saveTobRewardsChests() && objectId == ObjectID.REWARDS_CHEST_41435 && menuOption.equals("Claim"))
		{
			return true;
		}

		// TODO: Block BA gambles
		return false;
	}

	private void loadFromConfig()
	{
		ClueLocation loadedClueLocation = configManager.getRSProfileConfiguration("cluesaver", "clueLocation",
			ClueLocation.class);
		if (loadedClueLocation != null)
		{
			eliteLocation = loadedClueLocation;
		}
	}

	private void removeInfoBox()
	{
		if (infoBox != null)
		{
			infoBoxManager.removeInfoBox(infoBox);
			infoBox = null;
		}
	}

	private void saveClue(MenuOptionClicked event)
	{
		event.consume();
		if (config.showChatMessage())
		{
			String chatMessage = getCause().replace("<br>", " ");
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", chatMessage, "");
		}
	}

	private void setEliteLocation(ClueLocation location)
	{
		clueDeposited = false;
		eliteLocation = location;
		configManager.setRSProfileConfiguration("cluesaver", "eliteLocation", eliteLocation);
	}

	TileObject findTileObject(int x, int y, int id)
	{
		x += (Constants.EXTENDED_SCENE_SIZE - Constants.SCENE_SIZE) / 2;
		y += (Constants.EXTENDED_SCENE_SIZE - Constants.SCENE_SIZE) / 2;
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getExtendedTiles();
		Tile tile = tiles[client.getPlane()][x][y];
		if (tile != null)
		{
			for (GameObject gameObject : tile.getGameObjects())
			{
				if (gameObject != null && gameObject.getId() == id)
				{
					return gameObject;
				}
			}

			WallObject wallObject = tile.getWallObject();
			if (wallObject != null && wallObject.getId() == id)
			{
				return wallObject;
			}

			DecorativeObject decorativeObject = tile.getDecorativeObject();
			if (decorativeObject != null && decorativeObject.getId() == id)
			{
				return decorativeObject;
			}

			GroundObject groundObject = tile.getGroundObject();
			if (groundObject != null && groundObject.getId() == id)
			{
				return groundObject;
			}
		}
		return null;
	}

	public MenuEntry hoveredMenuEntry(final MenuEntry[] menuEntries)
	{
		final int menuX = client.getMenuX();
		final int menuY = client.getMenuY();
		final int menuWidth = client.getMenuWidth();
		final Point mousePosition = client.getMouseCanvasPosition();

		int dy = mousePosition.getY() - menuY;
		dy -= 19; // Height of Choose Option
		if (dy < 0)
		{
			return menuEntries[menuEntries.length - 1];
		}

		int idx = dy / 15; // Height of each menu option
		idx = menuEntries.length - 1 - idx;

		if (mousePosition.getX() > menuX && mousePosition.getX() < menuX + menuWidth
			&& idx >= 0 && idx < menuEntries.length)
		{
			return menuEntries[idx];
		}
		return menuEntries[menuEntries.length - 1];
	}

	public Integer objectIdForEntry(MenuEntry entry)
	{
		MenuAction menuAction = entry.getType();

		switch (menuAction)
		{
			case WIDGET_TARGET_ON_GAME_OBJECT:
			case GAME_OBJECT_FIRST_OPTION:
			case GAME_OBJECT_SECOND_OPTION:
			case GAME_OBJECT_THIRD_OPTION:
			case GAME_OBJECT_FOURTH_OPTION:
			case GAME_OBJECT_FIFTH_OPTION:
			case EXAMINE_OBJECT:
			{
				int x = entry.getParam0();
				int y = entry.getParam1();
				int id = entry.getIdentifier();
				TileObject tileObject = findTileObject(x, y, id);
				if (tileObject != null)
				{
					return tileObject.getId();
				}
				break;
			}
		}
		return -1;
	}
}
