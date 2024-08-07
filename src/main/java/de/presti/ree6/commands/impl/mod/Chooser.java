package de.presti.ree6.commands.impl.mod;

import de.presti.ree6.bot.BotConfig;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.SQLSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A command to set Ree6 up.
 */
@Command(name = "chooser", description = "Создание меню выбора ролей", category = Category.MOD)
public class Chooser implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {

        if (commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && commandEvent.getMember().hasPermission(Permission.MANAGE_SERVER)) {

            List<OptionMapping> roles = commandEvent.getOptions();

            List<SelectOption> optionList = new ArrayList<>();
            List<String> ListMessage = new ArrayList<>();
            assert roles != null;
            roles.forEach(role -> {
                if (role.getType() != OptionType.ROLE)
                    return;
                OptionMapping decsOp = commandEvent.getOption(role.getName() + "опис");
                OptionMapping emojiOp = commandEvent.getOption(role.getName() + "эмоджи");
                String decs;
                String emoji;

                if (decsOp != null && decsOp.getAsString() != null) {
                    decs = decsOp.getAsString();
                } else {
                    decs = "";
                }
                if (emojiOp != null && emojiOp.getAsString() != null) {
                    emoji = emojiOp.getAsString();
                } else {
                    emoji = "";
                }
                optionList.add(SelectOption.of(role.getAsRole().getName(), role.getAsRole().getId())
                                                                    .withDescription(decs)
                                                                    .withEmoji(Emoji.fromFormatted(emoji)));
            });
            roles.forEach(role -> {
                if (role.getType() != OptionType.ROLE)
                    return;
                ListMessage.add(role.getAsRole().getId());});
            String joined = String.join(">\n- <@&", ListMessage);

            SelectMenu selectMenu = new StringSelectMenuImpl("chooser", "Выберите роль", 1, 1, false, optionList);

            if (commandEvent.isSlashCommand()) {
                commandEvent.getChannel().sendMessage("Панель ниже позволяет получить следующие роли:\n- <@&"+joined+">")
                        .addActionRow(selectMenu).queue();
            } else {
                commandEvent.getChannel().sendMessage("Панель ниже позволяет получить следующие роли:\n- <@&"+joined+">")
                        .addActionRow(selectMenu).queue();
            }
            commandEvent.reply("Панель выбора ролей создана!", 5);
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
        return new CommandDataImpl("chooser", "Создание меню выбора ролей")
                .addOptions(
                        new OptionData(OptionType.ROLE, "роль1", "1 роль в списке", true),
                        new OptionData(OptionType.STRING, "роль1опис", "1 роль описание", false),
                        new OptionData(OptionType.STRING, "роль1эмоджи", "1 роль эмоджи", false),

                        new OptionData(OptionType.ROLE, "роль2", "2 роль в списке", false),
                        new OptionData(OptionType.STRING, "роль2опис", "2 роль описание", false),
                        new OptionData(OptionType.STRING, "роль2эмоджи", "2 роль эмоджи", false),

                        new OptionData(OptionType.ROLE, "роль3", "3 роль в списке", false),
                        new OptionData(OptionType.STRING, "роль3опис", "3 роль описание", false),
                        new OptionData(OptionType.STRING, "роль3эмоджи", "3 роль эмоджи", false),

                        new OptionData(OptionType.ROLE, "роль4", "4 роль в списке", false),
                        new OptionData(OptionType.STRING, "роль4опис", "4 роль описание", false),
                        new OptionData(OptionType.STRING, "роль4эмоджи", "4 роль эмоджи", false),

                        new OptionData(OptionType.ROLE, "роль5", "5 роль в списке", false),
                        new OptionData(OptionType.STRING, "роль5опис", "5 роль описание", false),
                        new OptionData(OptionType.STRING, "роль5эмоджи", "5 роль эмоджи", false)
                );

    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[0];
    }
}
