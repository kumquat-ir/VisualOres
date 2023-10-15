package hellfall.visualores.database.immersiveengineering;

import blusunrize.immersiveengineering.common.IEContent;
import flaxbeard.immersivepetroleum.api.crafting.PumpjackHandler;
import flaxbeard.immersivepetroleum.common.EventHandler;
import hellfall.visualores.map.DrawUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExcavatorVeinPosition {
    public int x;
    public int z;
    public int dim;
    public String mineral;
    public Integer depletion;
    public boolean infinite;
    public long timestamp;

    public String resType;
    public Integer oil;

    public int color;

    private List<String> tooltip;
    private ItemStack cachedStack;

    public ExcavatorVeinPosition(@Nullable ItemStack stack, NBTTagCompound nbt) {
        if (stack == null) {
            stack = new ItemStack(IEContent.itemCoresample);
            stack.setTagCompound(nbt);
        }

        cachedStack = stack;
        color = 0xFFFFFFFF;
        int[] coords = nbt.getIntArray("coords");
        x = coords[1];
        z = coords[2];
        dim = coords[0];
        timestamp = nbt.getLong("timestamp");
        if (nbt.hasKey("mineral")) {
            mineral = nbt.getString("mineral");
        }
        if (nbt.hasKey("depletion")) {
            depletion = nbt.getInteger("depletion");
        }
        if (nbt.hasKey("infinite")) {
            infinite = nbt.getBoolean("infinite");
        }
        if (nbt.hasKey("resType")) {
            resType = nbt.getString("resType");
            if (Loader.isModLoaded("immersivepetroleum")) {
                color = DrawUtils.getFluidColor(getIPFluid(resType));
            }
        }
        if (nbt.hasKey("oil")) {
            oil = nbt.getInteger("oil");
        }
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound result = new NBTTagCompound();
        result.setLong("timestamp", timestamp);
        result.setIntArray("coords", new int[]{dim, x, z});
        if (mineral != null) {
            result.setString("mineral", mineral);
        }
        if (depletion != null) {
            result.setInteger("depletion", depletion);
        }
        if (infinite) {
            result.setBoolean("infinite", true);
        }
        if (resType != null) {
            result.setString("resType", resType);
        }
        if (oil != null) {
            result.setInteger("oil", oil);
        }
        return result;
    }

    public List<String> getTooltip() {
        // this logic needs to be deferred until the client world has had time to update its total time value
        // the constructor is too early and results in "are you a time traveller?" in every tooltip
        if (tooltip == null) {
            tooltip = new ArrayList<>();
            // tooltip info should be exactly the same as on the item, so do the exact same logic
            cachedStack.getItem().addInformation(cachedStack, null, tooltip, ITooltipFlag.TooltipFlags.NORMAL);
            if (Loader.isModLoaded("immersivepetroleum")) {
                addIPInfo(cachedStack, tooltip);
            }
            cachedStack = null;
        }
        return tooltip;
    }

    private void addIPInfo(ItemStack stack, List<String> tooltip) {
        EventHandler.handleItemTooltip(new ItemTooltipEvent(stack, null, tooltip, ITooltipFlag.TooltipFlags.NORMAL));
    }

    private Fluid getIPFluid(String name) {
        for(PumpjackHandler.ReservoirType type : PumpjackHandler.reservoirList.keySet()) {
            if (name.equals(type.name)) {
                return type.getFluid();
            }
        }
        return null;
    }
}
