package hellfall.visualores.commands;

import codechicken.lib.command.ClientCommandBase;
import hellfall.visualores.VisualOres;
import hellfall.visualores.database.ClientCacheManager;
import hellfall.visualores.network.CCLPacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.util.List;

public class CommandShareProspectingData extends ClientCommandBase {
    @Override
    public void execute(Minecraft minecraft, EntityPlayerSP entityPlayerSP, String[] strings) throws CommandException {
        if (VisualOres.isClientOnlyMode()) {
            minecraft.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("visualores.share.clientonly"));
            return;
        }

        if (!(strings.length == 1)) {
            throw new PlayerNotFoundException("commands.generic.player.unspecified");
        }

        Thread sendThread = new Thread(new ProspectingShareTask(entityPlayerSP.getName(), strings[0]));
        sendThread.start();
    }

    @Override
    public @Nonnull String getName() {
        return "share";
    }

    @Override
    public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
        return "/vo share <player>";
    }

    private static class ProspectingShareTask implements Runnable {
        private final List<ClientCacheManager.ProspectionInfo> prospectionData;
        private final String sender;
        private final String reciever;
        public ProspectingShareTask(String sender, String reciever) {
            prospectionData = ClientCacheManager.getProspectionShareData();
            this.sender = sender;
            this.reciever = reciever;
        }

        @Override
        public void run() {
            boolean first = true;
            for (ClientCacheManager.ProspectionInfo info : prospectionData) {
                CCLPacketSender.sendSharePacketToServer(sender, reciever, info.cacheName, info.key, info.isDimCache, info.dim, info.data, first);
                first = false;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
