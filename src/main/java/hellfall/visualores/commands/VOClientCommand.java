package hellfall.visualores.commands;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class VOClientCommand extends CommandTreeBase {

    public VOClientCommand() {
        addSubcommand(new CommandShareProspectingData());
        addSubcommand(new CommandOpenCacheFolder());
        addSubcommand(new CommandResetClientCache());
    }

    @Override
    public @Nonnull String getName() {
        return "vo";
    }

    @Override
    public @Nonnull List<String> getAliases() {
        return Collections.singletonList("visualores");
    }

    @Override
    public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
        return "/vo <share/openCacheFolder/resetClientCache>";
    }
}
