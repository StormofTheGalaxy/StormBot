package de.presti.ree6.commands.impl.mod;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.presti.ree6.bot.BotConfig;
import de.presti.ree6.bot.util.WebhookUtil;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.SQLSession;
import de.presti.ree6.sql.entities.webhook.Webhook;
import de.presti.ree6.utils.external.RequestUtility;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.apache.maven.plugin.lifecycle.Execution;
import org.json.JSONObject;

import java.awt.*;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * A command to mute a user.
 */
@Command(name = "linked", description = "command.description.linked", category = Category.MOD)
public class LinkedRoles implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        if (!commandEvent.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            commandEvent.reply(commandEvent.getResource("message.default.needPermission", Permission.MODERATE_MEMBERS.name()), 5);
            return;
        }
        if (!commandEvent.isSlashCommand()) {
            commandEvent.reply(commandEvent.getResource("command.perform.onlySlashSupported"));
            return;
        }
        if (!commandEvent.getGuild().getId().equals("996141113847074826")) {
            commandEvent.reply("Это доступно только на официальном сервере SoG");
            return;
        }

        if (commandEvent.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {

            OptionMapping targetOption = commandEvent.getOption("target");
            String group = commandEvent.getSubcommandGroup();

            if (targetOption != null && group != null && targetOption.getAsMember() != null) {

                RequestUtility.Request request = RequestUtility.Request.builder()
                        .url("https://stormgalaxy.com/api/arma3/update?access_token=Hfjkdlcj493Fjfld&discord="+targetOption.getAsMember().getId()+
                                "&type="+group)
                        .build();

                JsonObject response = RequestUtility.requestJson(request).getAsJsonObject();

                commandEvent.reply(response.toString());

                if (response.isJsonObject()) {
                    if (response.get("status").getAsBoolean()) {
                        commandEvent.reply("Успешно выполнено", 15);
                    } else {
                        commandEvent.reply("Произошла ошибка: " + response.get("message").getAsString(), 15);
                    }
                } else { commandEvent.reply("Ошибка отправки запроса"); }
            } else {
                commandEvent.reply(commandEvent.getResource("message.default.noMention.user"), 5);
            }

        } else {
            commandEvent.reply(commandEvent.getResource("message.default.insufficientPermission", Permission.MODERATE_MEMBERS.name()), 5);
        }
        Main.getInstance().getCommandManager().deleteMessage(commandEvent.getMessage(), commandEvent.getInteractionHook());
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl("linked", "Управляет всеми привязанными ролями")
                .addSubcommands(new SubcommandData("block", "Удаляет все привязанные роли и блокирует их получение")
                        .addOptions(new OptionData(OptionType.USER, "target", "Кому?").setRequired(true)))
                .addSubcommands(new SubcommandData("unblock", "Разрешает получение привязанных ролей")
                        .addOptions(new OptionData(OptionType.USER, "target", "Кому?").setRequired(true)))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[0];
    }
}
