package hellfall.visualores.database;

import codechicken.lib.command.ClientCommandBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentTranslation;
import org.jetbrains.annotations.NotNull;

public class CommandResetClientCache extends ClientCommandBase {
    @Override
    public void execute(Minecraft minecraft, EntityPlayerSP entityPlayerSP, String[] strings) {
        ClientCache.instance.clear();
        minecraft.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("visualores.resetclientcache"));
    }

    @Override
    public @NotNull String getName() {
        return "vo_resetclientcache";
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender sender) {
        return "Resets the VisualOres client cache";
    }
}
