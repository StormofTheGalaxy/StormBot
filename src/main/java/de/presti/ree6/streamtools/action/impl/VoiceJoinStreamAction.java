package de.presti.ree6.streamtools.action.impl;

import de.presti.ree6.main.Main;
import de.presti.ree6.streamtools.action.StreamActionInfo;
import de.presti.ree6.streamtools.action.IStreamAction;
import io.sentry.Sentry;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;

/**
 * StreamAction used to join a voice channel.
 */
@NoArgsConstructor
@StreamActionInfo(name = "VoiceJoin", command = "voice-join", description = "Joins a voice channel.", introduced = "2.2.0")
public class VoiceJoinStreamAction implements IStreamAction {

    /**
     * @inheritDoc
     */
    @Override
    public void runAction(@NotNull Guild guild, String[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return;
        }

        try {
            VoiceChannel voiceChannel = guild.getVoiceChannelById(arguments[0]);
            Main.getInstance().getMusicWorker().connectToAudioChannel(guild.getAudioManager(), voiceChannel);
        } catch (Exception exception) {
            Sentry.captureMessage("Invalid Voice Channel ID! Related guild: " + guild.getIdLong());
        }
    }
}
