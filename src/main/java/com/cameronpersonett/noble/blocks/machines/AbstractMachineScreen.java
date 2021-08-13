package com.cameronpersonett.noble.blocks.machines;

import com.cameronpersonett.noble.blocks.engines.AbstractEngineContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractMachineScreen extends ContainerScreen<AbstractMachineContainer> implements IHasContainer<AbstractMachineContainer> {
    protected ResourceLocation Gui;
    protected AbstractMachineContainer container;
    protected String title;

    public AbstractMachineScreen(AbstractMachineContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;

        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 174;
        this.imageHeight = 164;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBg(stack, partialTicks, mouseX, mouseY);
        renderSlots(stack);
        renderCursorSlot(stack, mouseX, mouseY);
        renderLabels(stack, mouseX, mouseY);
        renderTooltip(stack, mouseX, mouseY);
    }

    public void renderSlots(MatrixStack stack) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        for(Slot slot : container.slots) {
            super.itemRenderer.renderGuiItem(slot.getItem(), slot.x + x, slot.y + y);
            if(!slot.getItem().isEmpty() && !(slot.getItem().getCount() == 1)) {
                this.font.draw(stack, Integer.toString(slot.getItem().getCount()), slot.x + x + 8, slot.y + y + 8, 0xffffff);
            }
        }
    }

    public void renderCursorSlot(MatrixStack stack, int mouseX, int mouseY) {
        super.itemRenderer.renderGuiItem(inventory.getCarried(), mouseX - 8, mouseY - 8);
        if(!inventory.getCarried().isEmpty()) {
            this.font.draw(stack, Integer.toString(inventory.getCarried().getCount()), mouseX, mouseY, 0xffffff);
        }
    }

    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int stringWidth = font.width(title);
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.font.draw(stack, title, screenWidth/2-stringWidth/2, 6 + y, 0xffffff);

        int energy = container.getEnergy();
        int maxEnergy = container.getMaxEnergy();
        int perc = (int)(((double)energy/(double)maxEnergy) * 100d);
        this.font.draw(stack, "Energy: " + energy + " (" + perc + "%)", 6 + x, 68 + y, 0xffffff);

        String progress = container.entity.progress + "%";
        stringWidth = font.width(progress);
        this.font.draw(stack, progress, screenWidth/2-stringWidth/2, 57 + y, 0xffffff);
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bind(Gui);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        blit(stack, x, y, 0, 0, 0, this.imageWidth, this.imageHeight, this.imageHeight, this.imageWidth);
    }

    @Override
    public abstract String getNarrationMessage();

    @Override
    public AbstractMachineContainer getMenu() {
        return container;
    }
}
