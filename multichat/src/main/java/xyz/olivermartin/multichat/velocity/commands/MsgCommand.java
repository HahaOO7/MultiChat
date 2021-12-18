package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import ninja.leaping.configurate.ConfigurationNode;
import xyz.olivermartin.multichat.velocity.*;

import java.util.Optional;

/**
 * Message Command
 * <p>Allows players to send private messages to each other</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MsgCommand extends Command {

    public MsgCommand() {
        super("msg", (String[]) ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("msgcommand").getList(String::valueOf).toArray(new String[0]));
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();
        if (!sender.hasPermission("multichat.chat.msg")) {
            return;
        }

        if (args.length < 1) {

            // Show usage (not enough args)

            MessageManager.sendMessage(sender, "command_msg_usage");
            MessageManager.sendMessage(sender, "command_msg_usage_toggle");

        } else {

            boolean toggleresult;

            if (args.length == 1) {

                // 1 arg --> toggle
                Player target = MultiChat.getInstance().getServer().getPlayer(args[0]).orElse(null);

                if (target != null) {


                    if ((sender instanceof Player player)) {

                        toggleresult = Events.togglePM(player.getUniqueId(), target.getUniqueId());

                        if (toggleresult) {

                            ConfigurationNode config = ConfigManager.getInstance().getHandler("config.yml").getConfig();

                            if (config.getChildrenMap().containsKey("toggle_pm") && !config.getNode("toggle_pm").getBoolean()) {

                                MessageManager.sendMessage(sender, "command_msg_no_toggle");

                            } else {
                                MessageManager.sendSpecialMessage(sender, "command_msg_toggle_on", target.getUsername());
                            }

                        } else {
                            MessageManager.sendMessage(sender, "command_msg_toggle_off");
                        }

                    } else {
                        MessageManager.sendMessage(sender, "command_msg_only_players");
                    }

                } else {

                    Player player = (Player) sender;

                    if (Events.PMToggle.containsKey(player.getUniqueId())) {
                        Events.PMToggle.remove(player.getUniqueId());
                        MessageManager.sendMessage(sender, "command_msg_toggle_off");
                    } else {
                        MessageManager.sendMessage(sender, "command_msg_not_online");
                    }

                }

            } else if ((sender instanceof Player)) {

                // >1 arg and the sender is a PLAYER

                String message = MultiChatUtil.getMessageFromArgs(args, 1);

                Optional<String> crm;

                if (ChatControl.isMuted(((Player) sender).getUniqueId(), "private_messages")) {
                    MessageManager.sendMessage(sender, "mute_cannot_send_message");
                    return;
                }

                if (ChatControl.handleSpam(((Player) sender), message, "private_messages")) {
                    return;
                }

                crm = ChatControl.applyChatRules(message, "private_messages", ((Player) sender).getUsername());

                if (crm.isPresent()) {
                    message = crm.get();
                } else {
                    return;
                }

                if (MultiChat.getInstance().getServer().getPlayer(args[0]).orElse(null) != null) {

                    Player target = MultiChat.getInstance().getServer().getPlayer(args[0]).orElse(null);

                    boolean permittedToMessage = true;

                    if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {

                        BungeeComm.sendMessage(((Player) sender).getUsername(), ((Player) sender).getCurrentServer().get().getServerInfo());
                        BungeeComm.sendMessage(target.getUsername(), target.getCurrentServer().get().getServerInfo());

                    }

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(((Player) sender).getCurrentServer().get().getServerInfo().getName())) {

                        if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(target.getCurrentServer().get().getServerInfo().getName())) {

                            if (ChatControl.ignores(((Player) sender).getUniqueId(), target.getUniqueId(), "private_messages")) {
                                ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
                                return;
                            }

                            PrivateMessageManager.getInstance().sendMessage(message, (Player) sender, target);

                        } else {
                            MessageManager.sendMessage(sender, "command_msg_disabled_target");
                        }

                    } else {
                        MessageManager.sendMessage(sender, "command_msg_disabled_sender");
                    }

                } else if (args[0].equalsIgnoreCase("console")) {

                    // New console target stuff here!

                    if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {

                        BungeeComm.sendMessage(((Player) sender).getUsername(), ((Player) sender).getCurrentServer().get().getServerInfo());

                    }

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(((Player) sender).getCurrentServer().get().getServerInfo().getName())) {

                        PrivateMessageManager.getInstance().sendMessageConsoleTarget(message, (Player) sender);

                    } else {
                        MessageManager.sendMessage(sender, "command_msg_disabled_sender");
                    }

                    // End of console target stuff

                } else {
                    MessageManager.sendMessage(sender, "command_msg_not_online");
                }

            } else {

                // >1 arg and the sender is the CONSOLE

                String message = MultiChatUtil.getMessageFromArgs(args, 1);

                if (MultiChat.getInstance().getServer().getPlayer(args[0]).isPresent()) {

                    Player target = MultiChat.getInstance().getServer().getPlayer(args[0]).get();

                    if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {

                        BungeeComm.sendMessage(target.getUsername(), target.getCurrentServer().get().getServerInfo());

                    }

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(target.getCurrentServer().get().getServerInfo().getName())) {

                        PrivateMessageManager.getInstance().sendMessageConsoleSender(message, target);

                    } else {
                        MessageManager.sendMessage(sender, "command_msg_disabled_target");
                    }

                } else {
                    MessageManager.sendMessage(sender, "command_msg_not_online");
                }

            }
        }
    }
}
