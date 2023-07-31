package hellfall.visualores.commands;

import codechicken.lib.command.ClientCommandBase;
import hellfall.visualores.database.ClientCacheManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class CommandResetClientCache extends ClientCommandBase {
    @Override
    public void execute(Minecraft minecraft, EntityPlayerSP entityPlayerSP, String[] strings) {
        ClientCacheManager.resetCaches();
        minecraft.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("visualores.command.reset.success"));
    }

    @Override
    public @Nonnull String getName() {
        return "resetClientCache";
    }

    @Override
    public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
        return "visualores.command.reset.usage";
    }
}
