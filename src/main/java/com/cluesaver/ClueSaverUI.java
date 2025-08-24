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
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class ClueSaverUI extends Overlay implements MouseListener
{
	private static final int PADDING = 2;
	private static final int POSITION_SPEED_MULTIPLIER = 2;
	private static final int EXPANDED_START_Y_OFFSET = 3;
	private static final int CLUE_X_OFFSET = 4;
	private static final int BUTTON_VERTICAL_OFFSET = 8;
	private static final int EXPANDED_UI_EXTRA_WIDTH = 50;
	private static final int EXPANDED_BUTTON_LEFT_OFFSET = 45;
	private static final int PIP_VERTICAL_OVERLAP = 1;
	private static final int PIP_ADDITIONAL_Y_OFFSET = 6;
	private static final int CLOSED_UI_Y_DIVIDER = 3;
	private final Client client;
	private final ClueSaverConfig config;
	private final ClueSaverPlugin clueSaverPlugin;
	private int cachedVisibleTierCount = 0;
	private int previousTotalBoxes = 0;
	private boolean visibilityNeedsUpdate = true;
	private boolean shouldDraw = false;
	private boolean isButtonHovered = false;
	private boolean isExpanded = false;
	private Rectangle buttonBounds;
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
	public ClueSaverUI(Client client, ClueSaverPlugin clueSaverPlugin, ClueSaverConfig config)
	{
		this.config = config;
		this.client = client;
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
		if (!shouldDraw || !config.showUI() || closedUIImage == null || buttonUIImage == null
			|| buttonUIHoveredImage == null)
		{
			return null;
		}

		updateVisibilityIfNeeded();

		if (cachedVisibleTierCount == 0)
		{
			return null;
		}

		BufferedImage firstClueImage = getClueImage(ClueTier.values()[0]);
		int clueImageHeight = firstClueImage != null ? firstClueImage.getHeight() : 0;
		int totalHeight = (clueImageHeight + PADDING) * cachedVisibleTierCount;

		final int canvasWidth = client.getCanvasWidth();
		final int canvasHeight = client.getCanvasHeight();

		final boolean anchorRight = config.uiAnchor() == ClueSaverConfig.UIAnchor.RIGHT;

		final int closedUIX = anchorRight
			? canvasWidth - closedUIImage.getWidth()
			: 0;

		final int closedUIY = (canvasHeight - totalHeight)
			/ CLOSED_UI_Y_DIVIDER + POSITION_SPEED_MULTIPLIER * config.uiVerticalOffset();

		int cropHeight = Math.min(closedUIImage.getHeight(), totalHeight);

		graphics.drawImage(
			closedUIImage,
			closedUIX, closedUIY,
			closedUIX + closedUIImage.getWidth(),
			closedUIY + cropHeight,
			0, 0,closedUIImage.getWidth(), cropHeight,
			null
		);

		final int expandedUIWidth = firstClueImage.getWidth() + EXPANDED_UI_EXTRA_WIDTH;

		if (isExpanded)
		{
			final int expandedUIX = anchorRight
				? closedUIX - expandedUIWidth
				: closedUIX + closedUIImage.getWidth();

			final int startY = closedUIY + EXPANDED_START_Y_OFFSET;

			int currentY = startY;

			for (ClueTier tier : ClueTier.values())
			{
				if (!shouldShowTier(tier))
				{
					continue;
				}

				BufferedImage clueImage = getClueImage(tier);
				if (clueImage == null) continue;

				int nextY = currentY + clueImage.getHeight() + PADDING;

				final int clueX = anchorRight
					? expandedUIX + expandedUIWidth - clueImage.getWidth() - CLUE_X_OFFSET
					: expandedUIX + CLUE_X_OFFSET;

				graphics.drawImage(clueImage, clueX, currentY, null);

				// Get tier state
				ClueScrollState clueState = clueSaverPlugin.getClueStates().getClueStateFromTier(tier);
				int totalClueCount = getTotalClueCount(tier);
				int maxClueCount = ClueSaverUtils.getMaxClueCount(tier, client);

				if (clueState.isLocationInventory() && invIcon != null)
				{
					final int invIconX = anchorRight
						? clueX
						: clueX + clueImage.getWidth() - invIcon.getWidth();

					final int invIconY = currentY + clueImage.getHeight() - invIcon.getHeight();
					graphics.drawImage(invIcon, invIconX, invIconY, null);
				}

				if (clueState.isLocationBank() && bankIcon != null)
				{
					final int bankIconX = anchorRight
						? clueX
						: clueX + clueImage.getWidth() - bankIcon.getWidth();

					final int bankIconY = currentY + clueImage.getHeight() - bankIcon.getHeight();
					graphics.drawImage(bankIcon, bankIconX, bankIconY, null);
				}

				final int pipX = anchorRight
					? clueX + clueImage.getWidth()
					: clueX - pipImage.getWidth();

				final int pipStartY = currentY + clueImage.getHeight();

				if (totalClueCount == maxClueCount && activeClueSaver != null)
				{
					graphics.drawImage(activeClueSaver, clueX, currentY, null);
				}

				for (int pip = maxClueCount - 1; pip >= 0; pip--)
				{
					int pipY = pipStartY
						- ((pip + 1) * (pipImage.getHeight() - PIP_VERTICAL_OVERLAP))
						- PIP_ADDITIONAL_Y_OFFSET;

					BufferedImage pipToDraw;
					if (totalClueCount == maxClueCount)
					{
						pipToDraw = pipRedImage;
					}
					else if (maxClueCount - totalClueCount == 1 && pip < totalClueCount)
					{
						pipToDraw = pipOrangeImage;
					}
					else if (pip < totalClueCount)
					{
						pipToDraw = pipGreenImage;
					}
					else
					{
						pipToDraw = pipImage;
					}

					if (anchorRight)
					{
						graphics.translate(pipX + pipToDraw.getWidth(), pipY);
						graphics.scale(-1, 1);
						graphics.drawImage(pipToDraw, 0, 0, null);
						graphics.scale(-1, 1);
						graphics.translate(-(pipX + pipToDraw.getWidth()), -pipY);
					}
					else
					{
						graphics.drawImage(pipToDraw, pipX, pipY, null);
					}
				}
				currentY = nextY;
			}
		}

		BufferedImage buttonToDraw = isButtonHovered ? buttonUIHoveredImage : buttonUIImage;
		final int buttonWidth = buttonToDraw.getWidth();
		final int buttonHeight = buttonToDraw.getHeight();

		final int buttonUIX = anchorRight
			? (isExpanded
			? closedUIX - (int)Math.round(0.5 * expandedUIWidth) - buttonWidth + 1
			: closedUIX - buttonWidth)
			: (closedUIX + closedUIImage.getWidth() + (isExpanded ? EXPANDED_BUTTON_LEFT_OFFSET : 0)) - 1;

		final int buttonUIY = closedUIY + BUTTON_VERTICAL_OFFSET;

		if (anchorRight)
		{
			graphics.drawImage(buttonToDraw,
				buttonUIX + buttonWidth, buttonUIY,
				-buttonWidth, buttonHeight,
				null);
		}
		else
		{
			graphics.drawImage(buttonToDraw, buttonUIX, buttonUIY, null);
		}

		buttonBounds = new Rectangle(buttonUIX, buttonUIY, buttonWidth, buttonHeight);

		return null;
	}

	private void updateVisibilityIfNeeded()
	{
		if (visibilityNeedsUpdate || hasBoxCountChanged())
		{
			cachedVisibleTierCount = 0;
			for (ClueTier tier : ClueTier.values())
			{
				if (shouldShowTier(tier))
				{
					cachedVisibleTierCount++;
				}
			}
			visibilityNeedsUpdate = false;
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

	private int getTotalClueCount(ClueTier tier)
	{
		ScrollBoxState boxState = clueSaverPlugin.getClueStates().getBoxStateFromTier(tier);
		ClueScrollState clueState = clueSaverPlugin.getClueStates().getClueStateFromTier(tier);

		// Scroll boxes + clue scroll
		return (boxState.getTotalCount()
			+ (clueState.isLocationInventory() ? 1 : 0)
			+ (clueState.isLocationBank() ? 1 : 0));
	}

	private boolean shouldShowTier(ClueTier tier)
	{
		switch (tier)
		{
			case BEGINNER:
				return clueSaverPlugin.getClueStates().shouldShowBeginner(config);
			case EASY:
				return clueSaverPlugin.getClueStates().shouldShowEasy(config);
			case MEDIUM:
				return clueSaverPlugin.getClueStates().shouldShowMedium(config);
			case HARD:
				return clueSaverPlugin.getClueStates().shouldShowHard(config);
			case ELITE:
				return clueSaverPlugin.getClueStates().shouldShowElite(config);
			case MASTER:
				return clueSaverPlugin.getClueStates().shouldShowMaster(config);
			default:
				return false;
		}
	}

	private boolean hasBoxCountChanged()
	{
		int currentTotalBoxes = 0;
		for (ClueTier tier : ClueTier.values())
		{
			currentTotalBoxes += getTotalClueCount(tier);
		}

		if (currentTotalBoxes != previousTotalBoxes)
		{
			previousTotalBoxes = currentTotalBoxes;
			return true;
		}
		return false;
	}

	public void setVisible(boolean visible)
	{
		this.shouldDraw = visible;
	}

	public void onConfigChanged()
	{
		visibilityNeedsUpdate = true;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent e)
	{
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
		if (buttonBounds != null && buttonBounds.contains(e.getPoint()))
		{
			isExpanded = !isExpanded;
			isButtonHovered = false;
			e.consume();
		}
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
