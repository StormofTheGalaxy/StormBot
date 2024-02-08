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
import de.presti.ree6.logger.events.LogTyp;
import de.presti.ree6.logger.events.implentation.LogMessageMember;
import de.presti.ree6.main.Main;
import de.presti.ree6.sql.SQLSession;
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
        if (!commandEvent.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            commandEvent.reply(commandEvent.getResource("message.default.needPermission", Permission.MODERATE_MEMBERS.name()), 5);
            return;
        }

        if (commandEvent.getMember().hasPermission(Permission.MODERATE_MEMBERS)) {

            if (commandEvent.isSlashCommand()) {

                OptionMapping targetOption = commandEvent.getOption("target");
                OptionMapping reasonOption = commandEvent.getOption("reason");
                OptionMapping ruleOption = commandEvent.getOption("rule");
                OptionMapping proofOption = commandEvent.getOption("proof");

                if (targetOption != null && reasonOption != null && targetOption.getAsMember() != null) {
                    sendModWebhook(commandEvent, targetOption.getAsMember(), reasonOption.getAsString(), ruleOption.getAsString(), proofOption.getAsString());
                } else {
                    commandEvent.reply(commandEvent.getResource("message.default.noMention.user"), 5);
                }

            } else {
                if (commandEvent.getArguments().length == 4) {
                    assert commandEvent.getMessage() != null;
                    if (commandEvent.getMessage().getMentions().getMembers().isEmpty()) {
                        commandEvent.reply(commandEvent.getResource("message.default.noMention.user"), 5);
                        commandEvent.reply(commandEvent.getResource("message.default.usage","report @user"), 5);
                    } else {
                        String reason = commandEvent.getArguments()[1];
                        String rule = commandEvent.getArguments()[2];
                        String proof = commandEvent.getArguments()[3];
                        sendModWebhook(commandEvent, reason, rule, proof);
                    }
                } else {
                    commandEvent.reply(commandEvent.getResource("message.default.invalidQuery"), 5);
                    commandEvent.reply(commandEvent.getResource("message.default.usage","report @user reason rule proof"), 5);
                }
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
        return new CommandDataImpl("report", "Отправляет отчёт на вебхук")
                .addOptions(new OptionData(OptionType.USER, "target", "Кого сегодня забаним?").setRequired(true))
                .addOptions(new OptionData(OptionType.STRING, "reason", "Что мы с ним сделаем?").setRequired(true))
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

    public void sendModWebhook(CommandEvent commandEvent, Member target, String punishment, String reason, String link) {
        WebhookMessageBuilder wm = new WebhookMessageBuilder();

        wm.setAvatarUrl(commandEvent.getGuild().getJDA().getSelfUser().getEffectiveAvatarUrl());
        wm.setUsername(BotConfig.getBotName() + "-Moders");

        String lastReport = SQLSession.getSqlConnector().getSqlWorker().getSetting(commandEvent.getGuild().getIdLong(), "data_last_report").getStringValue();

        WebhookEmbedBuilder we = new WebhookEmbedBuilder();
        we.setColor(Color.BLACK.getRGB());
        we.setTitle(new WebhookEmbed.EmbedTitle("**__Отчёт №"+lastReport+"__**", null));
        we.setAuthor(new WebhookEmbed.EmbedAuthor(commandEvent.getUser().getEffectiveName(), commandEvent.getUser().getEffectiveAvatarUrl(), null));
        we.setFooter(new WebhookEmbed.EmbedFooter(commandEvent.getGuild().getName() + " - " + BotConfig.getAdvertisement(), commandEvent.getGuild().getIconUrl()));
        we.setTimestamp(Instant.now());
        we.setDescription(target.getAsMention() + ", вы нарушили правила проекта");
        we.addField(new WebhookEmbed.EmbedField(false, "Пункт правил", reason));
        we.addField(new WebhookEmbed.EmbedField(false, "Наказание", punishment));
        we.addField(new WebhookEmbed.EmbedField(false, "Ссылка на сообщение", link));

        wm.addEmbeds(we.build());

        Webhook webhook = SQLSession.getSqlConnector().getSqlWorker().getModWebhook(commandEvent.getGuild().getIdLong());
        WebhookUtil.sendWebhook(null, wm.build(), webhook.getWebhookId(), webhook.getToken(), false);
        SQLSession.getSqlConnector().getSqlWorker().setSetting(commandEvent.getGuild().getIdLong(), "data_last_report", "Last Report", Integer.parseInt(lastReport)+1);

        commandEvent.reply("Ваш отчёт успешно записан!", 5);
    }
}
