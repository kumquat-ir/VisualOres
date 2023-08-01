package hellfall.visualores.database.thaumcraft;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AuraFluxPosition {
    private static final float[] BASE_FLUX_HSB = Color.RGBtoHSB(0x6F, 0x16, 0x7C, null);
    private static final DecimalFormat format = new DecimalFormat("#######.#");

    public int x;
    public int z;

    public short base;
    public float aura;
    public float flux;

    public int color;
    public int midColor;
    public List<String> tooltips;

    public AuraFluxPosition(short base, float aura, float flux, int x, int z) {
        this.base = base;
        this.aura = aura;
        this.flux = flux;
        this.x = x;
        this.z = z;

        double totalAmount = (aura + flux) / base;
        float fluxAmount = flux / (aura + flux);

        int alpha = (int) Math.min(0xFF, Math.floor(totalAmount * 200));
        int midAlpha = (int) Math.min(0x77, Math.floor(totalAmount * 100));
        int colorBase = Color.HSBtoRGB(BASE_FLUX_HSB[0], BASE_FLUX_HSB[1] * fluxAmount, 1 + (BASE_FLUX_HSB[2] - 1) * fluxAmount);

        color = (alpha << 24) + colorBase;
        midColor = (midAlpha << 24) + colorBase;

        tooltips = new ArrayList<>(3);
        tooltips.add(I18n.format("visualores.thaumcraft.aura", format.format(aura)));
        tooltips.add(I18n.format("visualores.thaumcraft.flux", format.format(flux)));
        tooltips.add(I18n.format("visualores.thaumcraft.base", format.format(aura + flux), base));
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound result = new NBTTagCompound();
        result.setShort("base", base);
        result.setFloat("aura", aura);
        result.setFloat("flux", flux);
        return result;
    }
}
