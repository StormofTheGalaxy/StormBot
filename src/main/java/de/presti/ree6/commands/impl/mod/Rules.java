package de.presti.ree6.commands.impl.mod;

import de.presti.ree6.bot.BotConfig;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.SQLSession;
import de.presti.ree6.sql.entities.Setting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * A command to set Ree6 up.
 */
@Command(name = "rules", description = "command.description.rules", category = Category.MOD)
public class Rules implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {

        if (commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && commandEvent.getMember().hasPermission(Permission.MANAGE_SERVER)) {

            String messageContent1 = SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "message_rules_1")
                    .getStringValue();
            String messageContent2 = SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "message_rules_2")
                    .getStringValue();
            String messageContent3 = SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getId(), "message_rules_3")
                    .getStringValue();

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Глава 1 Правила дискорд ников")
                    .setFooter(commandEvent.getGuild().getName() + " - " + BotConfig.getAdvertisement(), commandEvent.getGuild().getIconUrl())
                    .setColor(Color.decode("#4b77dd"))
                    .setDescription(messageContent1);

            EmbedBuilder embedBuilder2 = new EmbedBuilder()
                    .setTitle("Глава 2 Правила каналов")
                    .setFooter(commandEvent.getGuild().getName() + " - " + BotConfig.getAdvertisement(), commandEvent.getGuild().getIconUrl())
                    .setColor(Color.decode("#4b77dd"))
                    .setDescription(messageContent2);

            EmbedBuilder embedBuilder3 = new EmbedBuilder()
                    .setTitle("Глава 3 Общий перечень правил")
                    .setFooter(commandEvent.getGuild().getName() + " - " + BotConfig.getAdvertisement(), commandEvent.getGuild().getIconUrl())
                    .setColor(Color.decode("#4b77dd"))
                    .setDescription(messageContent3);


            List<SelectOption> optionList = new ArrayList<>();
            optionList.add(SelectOption.of("Дочерние дискорды", "child"));
            optionList.add(SelectOption.of("Адинистрация", "admin"));
            optionList.add(SelectOption.of("Прочее", "other"));

            SelectMenu selectMenu = new StringSelectMenuImpl("rulesMenu", "Дополнительно", 1, 1, false, optionList);

            if (commandEvent.isSlashCommand()) {
                commandEvent.getChannel().sendMessageEmbeds(embedBuilder.build(),embedBuilder2.build(),embedBuilder3.build())
                        .addActionRow(selectMenu).queue();
            } else {
                commandEvent.getChannel().sendMessageEmbeds(embedBuilder.build(),embedBuilder2.build(),embedBuilder3.build())
                        .addActionRow(selectMenu).queue();
            }
            commandEvent.reply("Панель правил создана!", 5);
        } else {
            commandEvent.reply(commandEvent.getResource("message.default.insufficientPermission", Permission.ADMINISTRATOR.name() + "/" + Permission.MANAGE_SERVER.name()));
        }
        Main.getInstance().getCommandManager().deleteMessage(commandEvent.getMessage(), commandEvent.getInteractionHook());
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandData getCommandData() {
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[0];
    }
}
