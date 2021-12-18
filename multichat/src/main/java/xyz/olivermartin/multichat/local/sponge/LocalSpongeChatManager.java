package xyz.olivermartin.multichat.local.sponge;

import org.spongepowered.api.text.serializer.TextSerializers;
import xyz.olivermartin.multichat.velocity.MultiChatUtil;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public class LocalSpongeChatManager extends LocalChatManager {

    @Override
    public String translateColourCodes(String message, boolean rgb) {

        if (rgb) {
            message = MultiChatLocal.getInstance().getChatManager().reformatRGB(message);
            message = message.replaceAll("&(?=[a-f0-9k-orx])", "§");
            message = MultiChatUtil.approximateHexCodes(message);
        }
        return TextSerializers.formattingCode('§').serialize(TextSerializers.FORMATTING_CODE.deserialize(message));

    }

    @Override
    public String processExternalPlaceholders(MultiChatLocalPlayer player, String message) {

        // If we are hooked with PAPI then use their placeholders!

        return message;

    }

}
