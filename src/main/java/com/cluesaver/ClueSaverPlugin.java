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

import com.cluesaver.ids.GoldChest;
import com.cluesaver.ids.ImplingJars;
import com.cluesaver.ids.ScrollBox;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.Objects;
import javax.inject.Inject;
import lombok.Getter;
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
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
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
	PluginManager pluginManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ClueSaverOverlay infoOverlay;

	@Inject
	private ClueSaverConfig config;

	@Inject
	private ClueSaverUtils scrollBoxUtils;

	@Inject
	@Getter
	private ClueStates clueStates;

	@Getter
	@Inject
	private TierStateSaveManager tierSaveManager;

	private boolean profileChanged;
	private int casketCooldown;
	private boolean loggingIn;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(infoOverlay);
		tierSaveManager.loadStateFromConfig();
		loggingIn = true;
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(infoOverlay);
		removeInfoBox();
		tierSaveManager.saveStateToConfig();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			tierSaveManager.saveStateToConfig();
		}

		if (event.getGameState() == GameState.LOGGED_IN)
		{
			if (profileChanged)
			{
				profileChanged = false;
				tierSaveManager.loadStateFromConfig();
			}
		}

		if (event.getGameState() == GameState.LOGGING_IN)
		{
			loggingIn = true;
		}
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		profileChanged = true;
	}

	@Subscribe(priority = 100)
	private void onClientShutdown(ClientShutdown event)
	{
		tierSaveManager.saveStateToConfig();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INVENTORY.getId())
		{
			checkBank();
			clueStates.checkContainer(event.getItemContainer(), ClueLocation.INVENTORY);
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANK)
		{
			checkBank();
		}
		else if (event.getGroupId() == InterfaceID.CLUESCROLL_REWARD)
		{
			checkReward();
		}
	}

	private void checkBank()
	{
		ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);

		if (bankContainer != null)
		{
			clueStates.checkContainer(bankContainer, ClueLocation.BANK);
		}
	}

	// Consider scroll boxes in reward screen which count toward max
	private void checkReward()
	{
		final Widget clueScrollReward = client.getWidget(ComponentID.CLUESCROLL_REWARD_ITEM_CONTAINER);

		for (Widget widget : Objects.requireNonNull(clueScrollReward.getChildren()))
		{
			if (widget.getItemId() == ScrollBox.CLUE_SCROLL_BOX_MASTER)
			{
				clueStates.updateMasterReward(true);
			}
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		if (event.getGroupId() == InterfaceID.BANK)
		{
			clueStates.updateWidgetClosed();
		}
		else if (event.getGroupId() == InterfaceID.CLUESCROLL_REWARD
			&& clueStates.isMasterInReward())
		{
			clueStates.updateMasterReward(false);
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Track event options to detect banking edge cases
		clueStates.trackBankEvents(event);

		// If impling saver is active
		if (event.isItemOp() && ImplingJars.itemIds.contains(event.getItemId()) && event.getMenuOption().equals("Loot"))
		{
			// Consume Impling Loot events
			saveImpling(event);
		}

		// If elite clue method saver is active
		if (config.saveGoldKeys()
			|| config.saveDarkTotems()
			|| config.saveTobRewardsChests()
			|| config.saveGauntletRewardChests())
		{
			MenuEntry entry = event.getMenuEntry();
			int objectId = objectIdForEntry(entry);

			// Consume clue method events
			if (objectId != -1 && isEliteClueMethodToSave(objectId, entry.getOption()))
			{
				if (clueStates.maxedElites())
				{
					saveClue(event, ClueTier.ELITE);
				}
			}
		}

		// If master clue method saver is active
		// Consume Casket Open events
		if (event.isItemOp() && isMasterClueMethodToSave(event.getItemId()) && event.getMenuOption().equals("Open"))
		{
			if (clueStates.maxedMasters())
			{
				saveCasket(event);
			}

			if (config.casketCooldown())
			{
				if (casketCooldown == 0)
				{
					casketCooldown = 1;
				}
				else
				{
					event.consume();
				}
			}
		}
	}

	public boolean isEliteClueMethodToSave(Integer objectId, String menuOption)
	{
		// Save Dark totems
		if (config.saveDarkTotems() && objectId == ObjectID.ALTAR_28900 && menuOption.equals("Use"))
		{
			return true;
		}

		// Save Gold keys
		if (config.saveGoldKeys() && GoldChest.getItemIds().contains(objectId) && menuOption.equals("Open"))
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

	public boolean isMasterClueMethodToSave(Integer itemId)
	{
		return (itemId == ItemID.REWARD_CASKET_EASY && config.saveEasyCaskets())
			|| (itemId == ItemID.REWARD_CASKET_MEDIUM && config.saveMediumCaskets())
			|| (itemId == ItemID.REWARD_CASKET_HARD && config.saveHardCaskets())
			|| (itemId == ItemID.REWARD_CASKET_ELITE && config.saveEliteCaskets());
	}

	public boolean isImplingToSave(Integer itemId)
	{
		return (ImplingJars.beginnerIds.contains(itemId)
			&& config.saveBeginnerImplings()
			&& clueStates.maxedBeginners())
			|| (ImplingJars.easyIds.contains(itemId)
			&& config.saveEasyImplings()
			&& clueStates.maxedEasies())
			|| (ImplingJars.mediumIds.contains(itemId)
			&& config.saveMediumImplings()
			&& clueStates.maxedMediums())
			|| (ImplingJars.hardIds.contains(itemId)
			&& config.saveHardImplings()
			&& clueStates.maxedHards())
			|| (ImplingJars.eliteIds.contains(itemId)
			&& config.saveEliteImplings()
			&&  clueStates.maxedElites());
	}

	public boolean isItemIdMethodToSave(Integer itemId)
	{
		return (isMasterClueMethodToSave(itemId) || isImplingToSave(itemId));
	}

	@Subscribe
	public void onMenuShouldLeftClick(MenuShouldLeftClick event)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();
		for (MenuEntry entry : menuEntries)
		{
			if (entry.getOption().equals("Deposit inventory"))
			{
				clueStates.setDepositedState();
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		handleInfoBox();

		if (config.casketCooldown())
		{
			casketCooldown = 0;
		}

		if (loggingIn)
		{
			loggingIn = false;
			notifyPluginUninstall("ImplingSaver");
			notifyPluginUninstall("CasketSaver");
		}
	}

	@Provides
	ClueSaverConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClueSaverConfig.class);
	}

	private void handleInfoBox()
	{
		var isShowing = infoBox != null;
		var shouldShow = config.showInfobox() && clueStates.isSaving(config);

		if (isShowing && !shouldShow)
		{
			removeInfoBox();
		}
		else if (shouldShow)
		{
			if (!isShowing)
			{
				infoBox = new InfoBox(itemManager.getImage(ItemID.MIMIC_SCROLL_CASE), this)
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

			infoBox.setTooltip(getInfoboxSavingCauses());

			if (!isShowing)
			{
				infoBoxManager.addInfoBox(infoBox);
			}
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

	public String getInfoboxSavingCauses()
	{
		StringBuilder savingCause = getActiveSavingText();

		String beginnerCause = getTierSavingCause(ClueTier.BEGINNER, config.showBeginnerInfo());
		if (beginnerCause != null) savingCause.append(beginnerCause).append("<br>");

		String easyCause = getTierSavingCause(ClueTier.EASY, config.showEasyInfo());
		if (easyCause != null) savingCause.append(easyCause).append("<br>");

		String mediumCause = getTierSavingCause(ClueTier.MEDIUM, config.showMediumInfo());
		if (mediumCause != null) savingCause.append(mediumCause).append("<br>");

		String hardCause = getTierSavingCause(ClueTier.HARD, config.showHardInfo());
		if (hardCause != null) savingCause.append(hardCause).append("<br>");

		String eliteCause = getTierSavingCause(ClueTier.ELITE, config.showEliteInfo());
		if (eliteCause != null) savingCause.append(eliteCause).append("<br>");

		String masterCause = getTierSavingCause(ClueTier.MASTER, config.showMasterInfo());
		if (masterCause != null) savingCause.append(masterCause);

		return savingCause.toString();
	}

	public StringBuilder getActiveSavingText()
	{
		return new StringBuilder()
			.append(ColorUtil.wrapWithColorTag("Clue Saver: ", Color.YELLOW))
			.append(ColorUtil.wrapWithColorTag("active", Color.GREEN))
			.append("<br>");
	}

	public String getTierSavingCause(ClueTier tier)
	{
		return getTierSavingCause(tier, false);
	}

	public String getTierSavingCause(ClueTier tier, boolean override)
	{
		ClueScrollState clueState = clueStates.getClueStateFromTier(tier);
		if (clueState == null) return null;
		ScrollBoxState boxState = clueStates.getBoxStateFromTier(tier);
		if (boxState == null) return null;

		if (!clueStates.maxedTier(clueState, boxState) && !override)
		{
			return null;
		}

		if (clueState.getLocation() == ClueLocation.UNKNOWN && boxState.getTotalCount() == 0) return null;

		StringBuilder savingCause = new StringBuilder()
			.append(ColorUtil.wrapWithColorTag(tier.toString(), Color.YELLOW))
			.append(ColorUtil.wrapWithColorTag(": ", Color.YELLOW))
			.append("<br>");

		if (clueState.getLocation() != ClueLocation.UNKNOWN)
		{
			savingCause
				.append("- Clue ")
				.append("in ")
				.append(ColorUtil.wrapWithColorTag(clueState.getLocation().toString(), Color.RED));
		}
		if (boxState.getTotalCount() > 0)
		{
			if (clueState.getLocation() != ClueLocation.UNKNOWN) savingCause.append("<br>");
			if (config.separateBoxCounts())
			{
				savingCause
					.append("- Inv Boxes: ")
					.append(ColorUtil.wrapWithColorTag(String.valueOf(boxState.getInventoryCount()), Color.RED))
					.append("<br>")
					.append("- Bank Boxes: ")
					.append(ColorUtil.wrapWithColorTag(String.valueOf(boxState.getBankCount()), Color.RED));
			}
			else
			{
				savingCause
					.append("- Scroll Boxes: ")
					.append(ColorUtil.wrapWithColorTag(String.valueOf(boxState.getTotalCount()), Color.RED));
			}
		}
		return savingCause.toString();
	}

	private void saveClue(MenuOptionClicked event, ClueTier tier)
	{
		event.consume();
		consumeChatMessage(tier);
	}

	private void saveCasket(MenuOptionClicked event)
	{
		int itemId = event.getItemId();
		if (isMasterClueMethodToSave(itemId))
		{
			saveClue(event, clueStates.getTierFromItemId(itemId));
		}
	}

	private void saveImpling(MenuOptionClicked event)
	{
		int itemId = event.getItemId();
		if (isImplingToSave(itemId))
		{
			saveClue(event, clueStates.getTierFromItemId(itemId));
		}
	}

	private void notifyPluginUninstall(String pluginName)
	{
		if (pluginManager.getPlugins().stream().anyMatch(plugin -> plugin.getName().equals(pluginName)))
		{
			sendChatConsoleMessage(pluginName + " has been integrated into Clue Saver. You may now uninstall " + pluginName);
		}
	}

	private void sendChatConsoleMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder()
			.append(ChatColorType.HIGHLIGHT)
			.append(chatMessage)
			.build();

		chatMessageManager.queue(
			QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(message)
				.build());
	}

	private void consumeChatMessage(ClueTier tier)
	{
		if (config.showChatMessage())
		{
			String chatMessage = getActiveSavingText() + getTierSavingCause(tier).replace("<br>", " ");
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", chatMessage, "");
		}
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
