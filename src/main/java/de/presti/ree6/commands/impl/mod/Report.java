package de.presti.ree6.commands.impl.mod;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.presti.ree6.bot.BotConfig;
import de.presti.ree6.bot.util.WebhookUtil;
import de.presti.ree6.commands.Category;
import de.presti.ree6.commands.CommandEvent;
import de.presti.ree6.commands.interfaces.Command;
import de.presti.ree6.commands.interfaces.ICommand;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.SQLSession;
import de.presti.ree6.sql.entities.PunishmentsLog;
import de.presti.ree6.sql.entities.webhook.base.Webhook;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.time.Instant;

/**
 * A command to mute a user.
 */
@Command(name = "report", description = "command.description.report", category = Category.MOD)
public class Report implements ICommand {

    /**
     * @inheritDoc
     */
    @Override
    public void onPerform(CommandEvent commandEvent) {



            if (commandEvent.isSlashCommand()) {

                OptionMapping targetOption = commandEvent.getOption("target");
                OptionMapping reasonOption = commandEvent.getOption("punishment");
                OptionMapping ruleOption = commandEvent.getOption("rule");
                OptionMapping proofOption = commandEvent.getOption("proof");

                if (targetOption != null && reasonOption != null && targetOption.getAsMember() != null) {
                    sendModWebhook(commandEvent, targetOption.getAsMember(), reasonOption.getAsLong(), ruleOption.getAsString(), proofOption.getAsString(), commandEvent.getMember());
                } else {
                    commandEvent.reply(commandEvent.getResource("message.default.noMention.user"), 5);
                }

            } else {
                commandEvent.reply(commandEvent.getResource("command.perform.onlySlashSupported"));
            }


        Main.getInstance().getCommandManager().deleteMessage(commandEvent.getMessage(), commandEvent.getInteractionHook());
    }

    /**
     * @inheritDoc
     */
    @Override
    public CommandData getCommandData() {
        return new CommandDataImpl("report", "Отправляет отчёт на вебхук")
                .addOptions(new OptionData(OptionType.USER, "target", "Кого сегодня забаним?").setRequired(true))
                .addOptions(new OptionData(OptionType.INTEGER, "punishment", "Что мы с ним сделаем?")
                        .addChoice("Варн", 3)
                        .addChoice("Мут", 1)
                        .addChoice("Кик", 2)
                        .setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "rule", "Какой пункт правил был нарушен?").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "proof", "Ссылка на доказательства").setRequired(true))
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getAlias() {
        return new String[0];
    }

    public static void sendModWebhook(CommandEvent commandEvent, Member target, long punishment, String reason, String link, Member moder) {
        WebhookMessageBuilder wm = new WebhookMessageBuilder();
        String punishString = null;
        switch ((int) punishment) {
            case 1 -> {
                punishString = "Мут";
            }
            case 2 -> {
                punishString = "Кик";
            }
            case 3 -> {
                punishString = "Варн";
            }
            default -> {
                punishString = "не указано";
            }
        }

        wm.setAvatarUrl(commandEvent.getGuild().getJDA().getSelfUser().getEffectiveAvatarUrl());
        wm.setUsername(BotConfig.getBotName() + "-Moders");

        String lastReport = SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getIdLong(), "data_last_report").getStringValue();

        WebhookEmbedBuilder we = new WebhookEmbedBuilder();
        we.setColor(Color.decode("#4b77dd").getRGB());
        we.setTitle(new WebhookEmbed.EmbedTitle("**__Отчёт №"+lastReport+"__**", null));
        //we.setAuthor(new WebhookEmbed.EmbedAuthor(commandEvent.getUser().getEffectiveName(), commandEvent.getUser().getEffectiveAvatarUrl(), null));
        we.setFooter(new WebhookEmbed.EmbedFooter(commandEvent.getGuild().getName() + " - " + BotConfig.getAdvertisement(), commandEvent.getGuild().getIconUrl()));
        we.setTimestamp(Instant.now());
        we.setDescription(target.getAsMention() + ", вы нарушили правила проекта");
        we.addField(new WebhookEmbed.EmbedField(false, "Пункт правил", reason));
        we.addField(new WebhookEmbed.EmbedField(false, "Наказание", punishString));
        we.addField(new WebhookEmbed.EmbedField(false, "Ссылка на сообщение", link));

        wm.addEmbeds(we.build());
        wm.setContent("||"+target.getAsMention()+"||");

        Webhook webhook = SQLSession.getSqlConnector().getSqlWorker().getModWebhook(commandEvent.getGuild().getIdLong());
        WebhookUtil.sendWebhook(null, wm.build(), webhook.getWebhookId(), webhook.getToken(), false);
        SQLSession.getSqlConnector().getSqlWorker().setSetting(commandEvent.getGuild().getIdLong(), "data_last_report", "Last Report", Integer.parseInt(lastReport)+1);

        PunishmentsLog punishmentsLog = new PunishmentsLog();
        punishmentsLog.setGuildId(commandEvent.getGuild().getIdLong());
        punishmentsLog.setReason(reason);
        punishmentsLog.setUserId(target.getIdLong());
        punishmentsLog.setModerId(moder.getIdLong());
        punishmentsLog.setAction((int) punishment);
        SQLSession.getSqlConnector().getSqlWorker().updateEntity(punishmentsLog);

        commandEvent.reply("Ваш отчёт успешно записан!", 5);
    }
}
