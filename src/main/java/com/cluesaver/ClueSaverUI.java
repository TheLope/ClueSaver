/*
 * Copyright (c) 2025, lalochazia <https://github.com/lalochazia>
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class ClueSaverUI extends Overlay implements MouseListener
{
	private final Client client;
	private final ClientThread clientThread;
	private final ClueSaverConfig config;
	private final ClueSaverUtils clueSaverUtils;
	private final ClueSaverPlugin clueSaverPlugin;
	private boolean shouldDraw = false;
	private boolean isButtonHovered = false;
	private boolean isExpanded = false;
	private Rectangle buttonBounds;
	private Rectangle beginnerIconBounds;
	private Rectangle easyIconBounds;
	private Rectangle mediumIconBounds;
	private Rectangle hardIconBounds;
	private Rectangle eliteIconBounds;
	private Rectangle masterIconBounds;
	private final BufferedImage closedUIImage;
	private final BufferedImage buttonUIImage;
	private final BufferedImage buttonUIHoveredImage;
	private final BufferedImage clueScrollBeginnerImage;
	private final BufferedImage clueScrollEasyImage;
	private final BufferedImage clueScrollMediumImage;
	private final BufferedImage clueScrollHardImage;
	private final BufferedImage clueScrollEliteImage;
	private final BufferedImage clueScrollMasterImage;
	private final BufferedImage pipImage;
	private final BufferedImage pipGreenImage;
	private final BufferedImage pipOrangeImage;
	private final BufferedImage pipRedImage;
	private final BufferedImage activeClueSaver;
	private final BufferedImage invIcon;
	private final BufferedImage bankIcon;

	@Inject
	public ClueSaverUI(Client client, ClientThread clientThread,
					   ClueSaverUtils clueSaverUtils, ClueSaverPlugin clueSaverPlugin,
					   ClueSaverConfig config)
	{
		this.config = config;
		this.client = client;
		this.clientThread = clientThread;
		this.clueSaverUtils = clueSaverUtils;
		this.clueSaverPlugin = clueSaverPlugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(100);

		closedUIImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/ClosedUI.png");
		buttonUIImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/buttonUI.png");
		buttonUIHoveredImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/buttonUIhovered.png");
		clueScrollBeginnerImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/cluescrollBeginner.png");
		clueScrollEasyImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/cluescrollEasy.png");
		clueScrollMediumImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/cluescrollMedium.png");
		clueScrollHardImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/cluescrollHard.png");
		clueScrollEliteImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/cluescrollElite.png");
		clueScrollMasterImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/cluescrollMaster.png");
		pipImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/pip.png");
		pipGreenImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/pipGreen.png");
		pipOrangeImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/pipOrange.png");
		pipRedImage = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/pipRed.png");
		invIcon = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/invIcon.png");
		bankIcon = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/bankIcon.png");
		activeClueSaver = ImageUtil.loadImageResource(getClass(), "/com/cluesaver/activeClueSaver.png");
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!shouldDraw || closedUIImage == null || buttonUIImage == null ||  buttonUIHoveredImage == null)
		{
			return null;
		}

		if (!config.showUI() || !shouldDraw)
		{
			return null;
		}

		int visibleTierCount = 0;
		for (ClueTier tier : ClueTier.values())
		{
			if (shouldShowTier(tier))
			{
				visibleTierCount++;
			}
		}

		if (visibleTierCount == 0)
		{
			return null;
		}

		BufferedImage firstClueImage = getClueImage(ClueTier.values()[0]);
		int clueImageHeight = firstClueImage != null ? firstClueImage.getHeight() : 0;
		int padding = 2;
		int totalHeight = (clueImageHeight + padding) * visibleTierCount;

		final int closedUIX = 0;
		final int closedUIY = (client.getCanvasHeight() - totalHeight) / 3;

		graphics.drawImage(closedUIImage,
			closedUIX, closedUIY,
			closedUIX + closedUIImage.getWidth(), closedUIY + totalHeight,
			0, 0,
			closedUIImage.getWidth(), closedUIImage.getHeight(),
			null);

		for (ClueTier tier : ClueTier.values())
		{
			if (!shouldShowTier(tier))
			{
				continue;
			}
		}

		if (isExpanded)
		{
			final int expandedUIX = closedUIX + closedUIImage.getWidth();
			final int startX = expandedUIX + 4;
			final int startY = closedUIY + 3;

			int currentY = startY;

			for (ClueTier tier : ClueTier.values())
			{
				if (!shouldShowTier(tier))
				{
					continue;
				}

				BufferedImage clueImage = getClueImage(tier);
				if (clueImage == null) continue;

				int nextY = currentY + clueImage.getHeight() + padding;
				graphics.drawImage(clueImage, startX, currentY, null);

				TierStats stats = calculateTierStats(tier);

				if (stats.hasClueInInventory() && invIcon != null)
				{
					int invIconX = startX + clueImage.getWidth() - invIcon.getWidth();
					int invIconY = currentY + clueImage.getHeight() - invIcon.getHeight();
					graphics.drawImage(invIcon, invIconX, invIconY, null);
				}

				if (stats.hasClueInBank() && bankIcon != null)
				{
					int bankIconX = startX + clueImage.getWidth() - bankIcon.getWidth();
					int bankIconY = currentY + clueImage.getHeight() - bankIcon.getHeight();
					graphics.drawImage(bankIcon, bankIconX, bankIconY, null);
				}

				int pipX = startX - 5;
				int pipStartY = currentY + clueImage.getHeight();

				if (stats.getTotalBoxes() == stats.getMaxClueCount() && activeClueSaver != null)
				{
					graphics.drawImage(activeClueSaver, startX, currentY, null);
				}

				for (int pip = stats.getMaxClueCount() - 1; pip >= 0; pip--)
				{
					int pipY = pipStartY - ((pip + 1) * (pipImage.getHeight() - 1)) - 6;
					if (stats.getTotalBoxes() == stats.getMaxClueCount())
					{
						graphics.drawImage(pipRedImage, pipX, pipY, null);
					}
					else if (stats.getMaxClueCount() - stats.getTotalBoxes() == 1 && pip < stats.getTotalBoxes())
					{
						graphics.drawImage(pipOrangeImage, pipX, pipY, null);
					}
					else if (pip < stats.getTotalBoxes())
					{
						graphics.drawImage(pipGreenImage, pipX, pipY, null);
					}
					else
					{
						graphics.drawImage(pipImage, pipX, pipY, null);
					}
				}

				updateIconBounds(tier, startX, currentY, clueImage);
				currentY = nextY;
			}
		}

		final int buttonUIX = closedUIX + closedUIImage.getWidth() +
			(isExpanded ? 45 : 0);
		final int buttonUIY = closedUIY + 8;
		buttonBounds = new Rectangle(buttonUIX, buttonUIY,
			buttonUIImage.getWidth(), buttonUIImage.getHeight());
		BufferedImage buttonToDraw = isButtonHovered ? buttonUIHoveredImage : buttonUIImage;
		graphics.drawImage(buttonToDraw, buttonUIX, buttonUIY, null);

		return null;
	}

	private void updateIconBounds(ClueTier tier, int x, int y, BufferedImage image)
	{
		Rectangle bounds = new Rectangle(x, y, image.getWidth(), image.getHeight());
		switch (tier)
		{
			case BEGINNER:
				beginnerIconBounds = bounds;
				break;
			case EASY:
				easyIconBounds = bounds;
				break;
			case MEDIUM:
				mediumIconBounds = bounds;
				break;
			case HARD:
				hardIconBounds = bounds;
				break;
			case ELITE:
				eliteIconBounds = bounds;
				break;
			case MASTER:
				masterIconBounds = bounds;
				break;
		}
	}

	private Rectangle getIconBounds(ClueTier tier)
	{
		switch (tier)
		{
			case BEGINNER: return beginnerIconBounds;
			case EASY: return easyIconBounds;
			case MEDIUM: return mediumIconBounds;
			case HARD: return hardIconBounds;
			case ELITE: return eliteIconBounds;
			case MASTER: return masterIconBounds;
			default: return new Rectangle();
		}
	}

	private BufferedImage getClueImage(ClueTier tier)
	{
		switch (tier)
		{
			case BEGINNER: return clueScrollBeginnerImage;
			case EASY: return clueScrollEasyImage;
			case MEDIUM: return clueScrollMediumImage;
			case HARD: return clueScrollHardImage;
			case ELITE: return clueScrollEliteImage;
			case MASTER: return clueScrollMasterImage;
			default: return null;
		}
	}

	private static class TierStats
	{
		@Getter
		private final int totalBoxes;
		@Getter
		private final int maxClueCount;
		private final boolean hasClueInInventory;
		private final boolean hasClueInBank;

		public TierStats(int totalBoxes, int maxClueCount, boolean hasClueInInventory, boolean hasClueInBank)
		{
			this.totalBoxes = totalBoxes;
			this.maxClueCount = maxClueCount;
			this.hasClueInInventory = hasClueInInventory;
			this.hasClueInBank = hasClueInBank;
		}

		public boolean hasClueInInventory()
		{
			return hasClueInInventory;
		}

		public boolean hasClueInBank()
		{
			return hasClueInBank;
		}
	}

	private TierStats calculateTierStats(ClueTier tier)
	{
		int totalBoxes = 0;
		boolean hasClueInInventory = false;
		boolean hasClueInBank = false;

		String savingCause = clueSaverPlugin.getTierSavingCause(tier, true);
		if (savingCause != null)
		{
			String cleanedCause = savingCause
				.replaceAll("<col=[^>]+>", "")
				.replaceAll("</col>", "");

			String[] parts = cleanedCause.split(" \\| ");
			for (String part : parts)
			{
				if (config.separateBoxCounts())
				{
					String[] lines = part.split("<br>");
					for (String line : lines)
					{
						line = line.trim();
						if (line.contains("Inv Boxes:"))
						{
							try
							{
								String countStr = line.substring(line.indexOf("Inv Boxes:") + "Inv Boxes:".length()).trim();
								totalBoxes += Integer.parseInt(countStr);
							}
							catch (Exception e)
							{
								log.debug("Error processing Inv Boxes", e);
							}
						}
						if (line.contains("Bank Boxes:"))
						{
							try
							{
								String countStr = line.substring(line.indexOf("Bank Boxes:") + "Bank Boxes:".length()).trim();
								totalBoxes += Integer.parseInt(countStr);
							}
							catch (Exception e)
							{
								log.debug("Error processing Bank Boxes", e);
							}
						}
						if (line.contains("Clue in inventory"))
						{
							hasClueInInventory = true;
							totalBoxes++;
						}
						else if (line.contains("Clue in bank"))
						{
							hasClueInBank = true;
							totalBoxes++;
						}
					}
				}
				else
				{
					if (part.contains("Scroll Boxes:"))
					{
						try
						{
							String countStr = part.substring(part.indexOf("Scroll Boxes:") + "Scroll Boxes:".length()).trim();
							totalBoxes = Integer.parseInt(countStr);
						}
						catch (Exception e)
						{
							log.debug("Error processing Scroll Boxes", e);
						}
					}
					if (part.contains("Clue in inventory"))
					{
						hasClueInInventory = true;
						totalBoxes++;
					}
					else if (part.contains("Clue in bank"))
					{
						hasClueInBank = true;
						totalBoxes++;
					}
				}
			}
		}
		int maxClueCount = clueSaverUtils.getMaxClueCount(tier, client);
		return new TierStats(totalBoxes, maxClueCount, hasClueInInventory, hasClueInBank);
	}

	private boolean shouldShowTier(ClueTier tier)
	{
		switch (tier)
		{
			case BEGINNER:
				return clueSaverPlugin.getClueStates().maxedBeginners() || config.showBeginnerInfo();
			case EASY:
				return clueSaverPlugin.getClueStates().maxedEasies() || config.showEasyInfo();
			case MEDIUM:
				return clueSaverPlugin.getClueStates().maxedMediums() || config.showMediumInfo();
			case HARD:
				return clueSaverPlugin.getClueStates().maxedHards() || config.showHardInfo();
			case ELITE:
				return clueSaverPlugin.getClueStates().maxedElites() || config.showEliteInfo();
			case MASTER:
				return clueSaverPlugin.getClueStates().maxedMasters() || config.showMasterInfo();
			default:
				return false;
		}
	}

	public void setVisible(boolean visible)
	{
		this.shouldDraw = visible;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent e)
	{
		if (buttonBounds != null && buttonBounds.contains(e.getPoint()))
		{
			isExpanded = !isExpanded;
			e.consume();
		}
		return e;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent e)
	{
		if (buttonBounds != null && buttonBounds.contains(e.getPoint()))
		{
			e.consume();
		}
		return e;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent e)
	{
		return e;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent e)
	{
		if (buttonBounds != null)
		{
			boolean wasHovered = isButtonHovered;
			isButtonHovered = buttonBounds.contains(e.getPoint());
			if (wasHovered != isButtonHovered)
			{
				e.consume();
			}
		}
		return e;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent e)
	{
		return e;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent e)
	{
		isButtonHovered = false;
		return e;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent e)
	{
		return e;
	}
}
