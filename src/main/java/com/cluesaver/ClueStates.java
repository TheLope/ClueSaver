package com.cluesaver;

import com.cluesaver.ids.Caskets;
import com.cluesaver.ids.ClueScrolls;
import com.cluesaver.ids.ImplingJars;
import com.cluesaver.ids.ScrollBox;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.MenuOptionClicked;

public class ClueStates
{
	private final Client client;
	private final ClueSaverUtils scrollBoxUtils;

	@Inject
	public ClueStates(Client client, ClueSaverUtils scrollBoxUtils)
	{
		this.client = client;
		this.scrollBoxUtils = scrollBoxUtils;
	}

	private final Integer beginnerClueId =  ItemID.CLUE_SCROLL_BEGINNER;
	private final List<Integer> easyClueIds = ClueScrolls.getEasyIds();
	private final List<Integer> mediumClueIds = ClueScrolls.getMediumIds();
	private final List<Integer> hardClueIds = ClueScrolls.getHardIds();
	private final List<Integer> eliteClueIds = ClueScrolls.getEliteIds();
	private final Integer masterClueId = ItemID.CLUE_SCROLL_MASTER;

	private final ClueScrollState beginnerClueState = new ClueScrollState(ClueTier.BEGINNER);
	private final ClueScrollState easyClueState = new ClueScrollState(ClueTier.EASY);
	private final ClueScrollState mediumClueState = new ClueScrollState(ClueTier.MEDIUM);
	private final ClueScrollState hardClueState = new ClueScrollState(ClueTier.HARD);
	private final ClueScrollState eliteClueState = new ClueScrollState(ClueTier.ELITE);
	private final ClueScrollState masterClueState = new ClueScrollState(ClueTier.MASTER);

	private final ScrollBoxState beginnerBoxState  = new ScrollBoxState(ClueTier.BEGINNER);
	private final ScrollBoxState easyBoxState  = new ScrollBoxState(ClueTier.EASY);
	private final ScrollBoxState mediumBoxState = new ScrollBoxState(ClueTier.MEDIUM);
	private final ScrollBoxState hardBoxState = new ScrollBoxState(ClueTier.HARD);
	private final ScrollBoxState eliteBoxState = new ScrollBoxState(ClueTier.ELITE);
	private final ScrollBoxState masterBoxState  = new ScrollBoxState(ClueTier.MASTER);

	public void trackBankEvents(MenuOptionClicked event)
	{
		boolean depositEvent = event.getMenuOption().contains("Deposit");
		boolean withdrawEvent = event.getMenuOption().contains("Withdraw");

		// Deposited clue scrolls
		if (beginnerClueId == event.getItemId())  beginnerClueState.setDeposited(depositEvent);
		if (easyClueIds.contains(event.getItemId()))  easyClueState.setDeposited(depositEvent);
		if (mediumClueIds.contains(event.getItemId()))  mediumClueState.setDeposited(depositEvent);
		if (hardClueIds.contains(event.getItemId()))  hardClueState.setDeposited(depositEvent);
		if (eliteClueIds.contains(event.getItemId()))  eliteClueState.setDeposited(depositEvent);
		if (masterClueId == event.getItemId())  masterClueState.setDeposited(depositEvent);
		// Deposited scroll boxes
		if (ScrollBox.CLUE_SCROLL_BOX_BEGINNER == event.getItemId())  beginnerBoxState.setDeposited(depositEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_EASY == event.getItemId())  easyBoxState.setDeposited(depositEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_MEDIUM == event.getItemId())  mediumBoxState.setDeposited(depositEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_HARD == event.getItemId())  hardBoxState.setDeposited(depositEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_ELITE == event.getItemId())  eliteBoxState.setDeposited(depositEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_MASTER == event.getItemId())  masterBoxState.setDeposited(depositEvent);
		// Withdrawn scroll boxes
		if (ScrollBox.CLUE_SCROLL_BOX_BEGINNER == event.getItemId()) beginnerBoxState.setWithdrawn(withdrawEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_EASY == event.getItemId()) easyBoxState.setWithdrawn(withdrawEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_MEDIUM == event.getItemId()) mediumBoxState.setWithdrawn(withdrawEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_HARD == event.getItemId()) hardBoxState.setWithdrawn(withdrawEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_ELITE == event.getItemId()) eliteBoxState.setWithdrawn(withdrawEvent);
		if (ScrollBox.CLUE_SCROLL_BOX_MASTER == event.getItemId()) masterBoxState.setWithdrawn(withdrawEvent);
	}

	public void setDepositedState()
	{
		beginnerClueState.setDeposited(true);
		easyClueState.setDeposited(true);
		mediumClueState.setDeposited(true);
		hardClueState.setDeposited(true);
		eliteClueState.setDeposited(true);
		masterClueState.setDeposited(true);
	}

	public void updateWidgetClosed()
	{
		updatePrevInventory(beginnerBoxState);
		updatePrevInventory(easyBoxState);
		updatePrevInventory(mediumBoxState);
		updatePrevInventory(hardBoxState);
		updatePrevInventory(eliteBoxState);
		updatePrevInventory(masterBoxState);
	}

	private void updatePrevInventory(ScrollBoxState state)
	{
		if (state.isWithdrawn() || state.isDeposited())
		{
			state.setPrevInventoryCount(state.getInventoryCount());
		}
	}

	public void updateMasterReward(boolean bool)
	{
		masterBoxState.setInReward(bool);
	}

	public boolean isMasterInReward()
	{
		return masterBoxState.isInReward();
	}

	public void checkContainer(ItemContainer container, ClueLocation location)
	{
		scrollBoxUtils.setClueLocation(container, location, beginnerClueId, beginnerClueState);
		scrollBoxUtils.setClueLocation(container, location, easyClueIds, easyClueState);
		scrollBoxUtils.setClueLocation(container, location, mediumClueIds, mediumClueState);
		scrollBoxUtils.setClueLocation(container, location, hardClueIds, hardClueState);
		scrollBoxUtils.setClueLocation(container, location, eliteClueIds, eliteClueState);
		scrollBoxUtils.setClueLocation(container, location, masterClueId, masterClueState);

		scrollBoxUtils.setBoxLocation(container, location, ScrollBox.CLUE_SCROLL_BOX_BEGINNER, beginnerBoxState);
		scrollBoxUtils.setBoxLocation(container, location, ScrollBox.CLUE_SCROLL_BOX_EASY, easyBoxState);
		scrollBoxUtils.setBoxLocation(container, location, ScrollBox.CLUE_SCROLL_BOX_MEDIUM, mediumBoxState);
		scrollBoxUtils.setBoxLocation(container, location, ScrollBox.CLUE_SCROLL_BOX_HARD, hardBoxState);
		scrollBoxUtils.setBoxLocation(container, location, ScrollBox.CLUE_SCROLL_BOX_ELITE, eliteBoxState);
		scrollBoxUtils.setBoxLocation(container, location, ScrollBox.CLUE_SCROLL_BOX_MASTER, masterBoxState);
	}

	public ClueScrollState getClueStateFromTier(ClueTier tier)
	{
		if (tier == ClueTier.BEGINNER) return beginnerClueState;
		if (tier == ClueTier.EASY) return easyClueState;
		if (tier == ClueTier.MEDIUM) return mediumClueState;
		if (tier == ClueTier.HARD) return hardClueState;
		if (tier == ClueTier.ELITE) return eliteClueState;
		if (tier == ClueTier.MASTER) return masterClueState;
		return null;
	}

	public ScrollBoxState getBoxStateFromTier(ClueTier tier)
	{
		if (tier == ClueTier.BEGINNER) return beginnerBoxState;
		if (tier == ClueTier.EASY) return easyBoxState;
		if (tier == ClueTier.MEDIUM) return mediumBoxState;
		if (tier == ClueTier.HARD) return hardBoxState;
		if (tier == ClueTier.ELITE) return eliteBoxState;
		if (tier == ClueTier.MASTER) return masterBoxState;
		return null;
	}

	public ClueTier getTierFromItemId(int itemId)
	{
		if (ScrollBox.CLUE_SCROLL_BOX_BEGINNER == itemId
			|| beginnerClueId == itemId
			|| (ImplingJars.beginnerIds).contains(itemId))
		{
			return ClueTier.BEGINNER;
		}
		if (ScrollBox.CLUE_SCROLL_BOX_EASY == itemId
			|| easyClueIds.contains(itemId)
			|| (ImplingJars.easyIds).contains(itemId))
		{
			return ClueTier.EASY;
		}
		if (ScrollBox.CLUE_SCROLL_BOX_MEDIUM == itemId
			|| mediumClueIds.contains(itemId)
			|| (ImplingJars.mediumIds).contains(itemId))
		{
			return ClueTier.MEDIUM;
		}
		if (ScrollBox.CLUE_SCROLL_BOX_HARD == itemId
			|| hardClueIds.contains(itemId)
			|| (ImplingJars.hardIds).contains(itemId))
		{
			return ClueTier.HARD;
		}
		if (ScrollBox.CLUE_SCROLL_BOX_ELITE == itemId
			|| eliteClueIds.contains(itemId)
			|| (ImplingJars.eliteIds).contains(itemId)
			|| ItemID.DARK_TOTEM == itemId
			)
		{
			return ClueTier.ELITE;
		}
		if (ScrollBox.CLUE_SCROLL_BOX_MASTER == itemId
			|| masterClueId == itemId
			|| (Caskets.itemIds).contains(itemId))
		{
			return ClueTier.MASTER;
		}
		return null;
	}

	public boolean maxedTier(ClueScrollState clueState, ScrollBoxState boxState)
	{
		// Max scroll boxes for particular tier
		int tierBonus = ClueSaverUtils.getMaxClueCount(clueState.tier, client);
		// Clue scroll in possession or scroll box in reward reduces max scroll boxes
		int modifier = 0;

		boolean hasClue = !clueState.getLocation().equals(ClueLocation.UNKNOWN);
		if (hasClue)
		{
			modifier = boxState.isInReward() ? 2 : 1;
		}
		else if (boxState.isInReward())
		{
			modifier = 1;
		}
		return boxState.getTotalCount() >= (tierBonus - modifier);
	}

	public boolean shouldShow(ClueSaverConfig config)
	{
		return shouldShowBeginner(config)
			|| shouldShowEasy(config)
			|| shouldShowMedium(config)
			|| shouldShowHard(config)
			|| shouldShowElite(config)
			|| shouldShowMaster(config);
	}

	public boolean shouldShowBeginner(ClueSaverConfig config)
	{
		return config.beginnerEnabled() && (maxedBeginners() || config.showBeginnerInfo());
	}

	public boolean shouldShowEasy(ClueSaverConfig config)
	{
		return config.easyEnabled() && (maxedEasies() || config.showEasyInfo());
	}

	public boolean shouldShowMedium(ClueSaverConfig config)
	{
		return config.mediumEnabled() && (maxedMediums() || config.showMediumInfo());
	}

	public boolean shouldShowHard(ClueSaverConfig config)
	{
		return config.hardEnabled() && (maxedHards() || config.showHardInfo());
	}

	public boolean shouldShowElite(ClueSaverConfig config)
	{
		return config.eliteEnabled() && (maxedElites() || config.showEliteInfo());
	}

	public boolean shouldShowMaster(ClueSaverConfig config)
	{
		return config.masterEnabled() && (maxedMasters() || config.showMasterInfo());
	}

	public boolean maxedBeginners()
	{
		return maxedTier(beginnerClueState, beginnerBoxState);
	}

	public boolean maxedEasies()
	{
		return maxedTier(easyClueState, easyBoxState);
	}

	public boolean maxedMediums()
	{
		return maxedTier(mediumClueState, mediumBoxState);
	}

	public boolean maxedHards()
	{
		return maxedTier(hardClueState, hardBoxState);
	}

	public boolean maxedElites()
	{
		return maxedTier(eliteClueState, eliteBoxState);
	}

	public boolean maxedMasters()
	{
		return maxedTier(masterClueState, masterBoxState);
	}

	public void setFromTierState(TierState loadedTierData, ClueTier tier)
	{
		// Update tier clue scroll state from config
		ClueScrollState scrollState = getClueStateFromTier(tier);
		scrollState.setLocation(loadedTierData.getClueScrollLocation());

		// Update tier scroll box state from config
		ScrollBoxState boxState = getBoxStateFromTier(tier);
		boxState.setInventoryCount(loadedTierData.getScrollBoxInventoryCount());
		boxState.setBankCount(loadedTierData.getScrollBoxBankCount());
	}
}
