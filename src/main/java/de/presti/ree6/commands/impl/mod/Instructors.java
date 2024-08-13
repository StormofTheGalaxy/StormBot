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
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.internal.interactions.component.StringSelectMenuImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A command to set Ree6 up.
 */
@Command(name = "instructors", description = "Создать панель запроса обучения", category = Category.MOD)
public class Instructors implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {
        if (!commandEvent.isSlashCommand()){
            commandEvent.reply(commandEvent.getResource("command.perform.onlySlashSupported")); return;
        }
        //if (commandEvent.getGuild().getOwnerIdLong() != 996141113847074826L) {
         //   commandEvent.reply("Разрешено только в официальном сервере SoG"); return;
        //}

        if ((commandEvent.getMember().hasPermission(Permission.ADMINISTRATOR) && commandEvent.getMember().hasPermission(Permission.MANAGE_SERVER)) || commandEvent.getUser().getId().equals("434280207847784449")) {


            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("Обучение")
                    .setFooter(commandEvent.getGuild().getName() + " - " + BotConfig.getAdvertisement(), commandEvent.getGuild().getIconUrl())
                    .setColor(Color.decode("#4b77dd"))
                    .setDescription("Используя кнопку ниже вы можете запросить обучение у инструкторов");


            commandEvent.getChannel().sendMessageEmbeds(embedBuilder.build())
                    .addActionRow(Button.primary("training", "Записаться на тренировку")).queue();
            commandEvent.reply("Панель создана!", 5);
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
