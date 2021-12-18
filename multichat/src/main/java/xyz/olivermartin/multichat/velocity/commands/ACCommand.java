package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.DebugManager;
import xyz.olivermartin.multichat.velocity.Events;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChatUtil;
import xyz.olivermartin.multichat.velocity.StaffChatManager;

/**
 * Admin-Chat command
 * <p>Allows the user to toggle / send a message to admin-chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ACCommand extends Command {

    private static String[] aliases = new String[]{};

    public ACCommand() {
        super("ac", aliases);
    }

    public void execute(Invocation invocation) {

        var args = invocation.arguments();
        var sender = invocation.source();
        if (!sender.hasPermission("multichat.staff.admin")) {
            return;
        }

        boolean toggleresult;

        if (args.length < 1) {

            if ((sender instanceof Player player)) {

                DebugManager.log("[ACCommand] Command sender is a player");

                toggleresult = Events.toggleAC(player.getUniqueId());

                DebugManager.log("[ACCommand] AC new toggle state: " + toggleresult);

                if (toggleresult) {
                    MessageManager.sendMessage(sender, "command_ac_toggle_on");
                } else {
                    MessageManager.sendMessage(sender, "command_ac_toggle_off");
                }

            } else {

                MessageManager.sendMessage(sender, "command_ac_only_players");

            }

        } else if ((sender instanceof Player player)) {

            DebugManager.log("[ACCommand] Command sender is a player");

            String message = MultiChatUtil.getMessageFromArgs(args);

            StaffChatManager chatman = new StaffChatManager();

            DebugManager.log("[ACCommand] Next line of code will send the message, if no errors, then it worked!");
            player.getCurrentServer().ifPresent(server -> chatman.sendAdminMessage(player.getGameProfile().getName(), player.getUsername(), server.getServerInfo().getName(), message));
        } else {
            DebugManager.log("[ACCommand] Command sender is the console");

            String message = MultiChatUtil.getMessageFromArgs(args);

            StaffChatManager chatman = new StaffChatManager();

            DebugManager.log("[ACCommand] Next line of code will send the message, if no errors, then it worked!");

            chatman.sendAdminMessage("CONSOLE", "CONSOLE", "#", message);
        }
    }
}
