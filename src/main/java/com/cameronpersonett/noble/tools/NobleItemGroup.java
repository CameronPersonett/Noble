package com.cameronpersonett.noble.tools;

import com.cameronpersonett.noble.core.Registration;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class NobleItemGroup extends ItemGroup {
    public static final NobleItemGroup NOBLE = new NobleItemGroup(ItemGroup.getGroupCountSafe(), "noble");

    public NobleItemGroup(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(Registration.COMBUSTION_ENGINE_BLOCK.get());
    }
}