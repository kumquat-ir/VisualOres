package hellfall.visualores.commands;

import codechicken.lib.command.ClientCommandBase;
import hellfall.visualores.database.ClientCacheManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nonnull;
import java.io.IOException;

public class CommandOpenCacheFolder extends ClientCommandBase {
    @Override
    public void execute(Minecraft minecraft, EntityPlayerSP entityPlayerSP, String[] strings) throws CommandException {
        ITextComponent component = new TextComponentTranslation("visualores.command.openfolder.message");
        try {
            component.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, ClientCacheManager.getWorldFolder().getCanonicalPath()));
        } catch (IOException e) {
            throw new CommandException("visualores.command.openfolder.error", e);
        }
        component.getStyle().setUnderlined(true);
        minecraft.ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public @Nonnull String getName() {
        return "openCacheFolder";
    }

    @Override
    public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
        return "visualores.command.openfolder.usage";
    }
}
