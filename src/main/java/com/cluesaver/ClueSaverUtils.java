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

import com.cluesaver.ids.ScrollCase;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;

// Credit to thelaakes for borrowed utiltites from scrollboxcounter
public class ClueSaverUtils
{
	private static final int BASE_CLUE_COUNT = 2;

	public static int getMaxClueCount(ClueTier tier, Client client)
	{
		int tierBonus = getTierBonus(tier, client);
		int mimicBonus = getMimicBonus(client);
		return BASE_CLUE_COUNT + tierBonus + mimicBonus;
	}

	public static int getMimicBonus(Client client)
	{
		return client.getVarbitValue(ScrollCase.SCROLL_CASE_MIMIC);
	}

	public static int getTierBonus(ClueTier tier, Client client)
	{
		int bonus = 0;

		switch (tier)
		{
			case BEGINNER:
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_BEGINNER_MINOR);
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_BEGINNER_MAJOR);
				break;
			case EASY:
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_EASY_MINOR);
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_EASY_MAJOR);
				break;
			case MEDIUM:
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_MEDIUM_MINOR);
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_MEDIUM_MAJOR);
				break;
			case HARD:
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_HARD_MINOR);
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_HARD_MAJOR);
				break;
			case ELITE:
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_ELITE_MINOR);
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_ELITE_MAJOR);
				break;
			case MASTER:
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_MASTER_MINOR);
				bonus += client.getVarbitValue(ScrollCase.SCROLL_CASE_MASTER_MAJOR);
				break;
		}
		return bonus;
	}

	public void setBoxLocation(ItemContainer container, ClueLocation location, Integer boxId, ScrollBoxState state)
	{
		// If scroll boxes found in container, update count
		if (Arrays.stream(container.getItems()).anyMatch(item -> item.getId() == boxId))
		{
			Optional<Item> scrollBox = Arrays.stream(container.getItems())
				.filter(item -> item.getId() == boxId)
				.findFirst();

			if (scrollBox.isPresent())
			{
				int count = scrollBox.get().getQuantity();
				setScrollBoxState(location, count, state);

				// Check if some boxes were withdrawn/deposited
				if (location.equals(ClueLocation.INVENTORY) && state.getPrevInventoryCount() != -1)
				{
					if (state.isWithdrawn())
					{
						// Subtract inventory difference from bank
						setScrollBoxState(
							ClueLocation.BANK,
							state.getBankCount() - (state.getInventoryCount() - state.getPrevInventoryCount()),
							state);
					}
					else if (state.isDeposited())
					{
						// Add inventory difference to bank
						setScrollBoxState(
							ClueLocation.BANK,
							state.getBankCount() + (state.getPrevInventoryCount() - state.getInventoryCount()),
							state);
					}
				}
				// Clear prevInventory count
				state.setPrevInventoryCount(-1);
			}
		}
		// If scroll box were previously located in container and not found, update location
		else
		{
			// Check if all box were banked
			if (location.equals(ClueLocation.INVENTORY)
				&& state.isDeposited())
			{
				// Add inventory count to bank
				setScrollBoxState(
					ClueLocation.BANK,
					state.getBankCount() + state.getInventoryCount(),
					state);
				// Clear inventory count
				setScrollBoxState(ClueLocation.INVENTORY, 0, state);
			}
			// Reset count for container
			else
			{
				setScrollBoxState(location, 0, state);
			}
		}
		// Reset banking edge case tracking
		state.setDeposited(false);
		state.setWithdrawn(false);
	}

	public void setClueLocation(ItemContainer container, ClueLocation location, List<Integer> clues, ClueScrollState state)
	{
		if (Arrays.stream(container.getItems()).anyMatch(item -> clues.contains(item.getId())))
		{
			setClueScrollState(location, state);
		}
		// If clue was previously located in container and not found, update location
		else if (state.getLocation().equals(location))
		{
			// Check if clue was banked
			if (location.equals(ClueLocation.INVENTORY) && state.isDeposited())
			{
				setClueScrollState(ClueLocation.BANK, state);
			}
			else
			{
				setClueScrollState(ClueLocation.UNKNOWN, state);
			}
		}
	}

	public void setClueLocation(ItemContainer container, ClueLocation location, Integer clue, ClueScrollState state)
	{
		if (Arrays.stream(container.getItems()).anyMatch(item -> item.getId() == clue))
		{
			setClueScrollState(location, state);
		}
		// If clue was previously located in container and not found, update location
		else if (state.getLocation().equals(location))
		{
			// Check if clue was banked
			if (location.equals(ClueLocation.INVENTORY) && state.isDeposited())
			{
				setClueScrollState(ClueLocation.BANK, state);
			}
			else
			{
				setClueScrollState(ClueLocation.UNKNOWN, state);
			}
		}
	}

	public void setClueScrollState(ClueLocation location, ClueScrollState state)
	{
		state.setDeposited(false);
		state.setLocation(location);
	}

	public void setScrollBoxState(ClueLocation location, Integer count, ScrollBoxState state)
	{
		if (location == ClueLocation.INVENTORY)
		{
			state.setInventoryCount(count);
		}
		if (location == ClueLocation.BANK)
		{
			state.setBankCount(count);
		}
	}
}
