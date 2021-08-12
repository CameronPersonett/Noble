package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.Noble;
import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ElectricSmelterScreen extends ContainerScreen<ElectricSmelterContainer> {
    private ResourceLocation GUI = new ResourceLocation(Noble.MOD_ID, "textures/gui/electric_smelter.png");
    private ElectricSmelterContainer container;

    public ElectricSmelterScreen(ElectricSmelterContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;

        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 174;
        this.imageHeight = 164;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBg(stack, partialTicks, mouseX, mouseY);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
        this.font.draw(stack, "Energy: " + container.getEnergy(), 10, 10, 0xffffff);
        this.font.draw(stack, "Combustion Engine", 49, 6, 4210752);
        this.font.draw(stack, "Energy", 6, 72, 4210752);
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bind(GUI);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        blit(stack, x, y, 0, 0, 0, this.imageWidth, this.imageHeight, this.imageHeight, this.imageWidth);
    }

    @Override
    public String getNarrationMessage() {
        return "CombustionEngineScreen";
    }
}
