package tv.banko.suggestions.model;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ThreadChannel;
import org.jetbrains.annotations.Nullable;
import tv.banko.suggestions.Suggestions;

public record Suggestion(String guildId, String threadId) {

    /**
     * Get the ThreadChannel of the Suggestion.
     *
     * @param suggestions The bot class; use the instance which has been dependency injected in your class before
     * @return The ThreadChannel; may be null if the guild or the ThreadChannel does not exist anymore
     */
    @Nullable
    public ThreadChannel getThreadChannel(Suggestions suggestions) {
        Guild guild = suggestions.getBot().getGuildById(guildId);

        if (guild == null) {
            this.remove(suggestions);
            return null;
        }

        ThreadChannel channel = guild.getThreadChannelById(threadId);

        if (channel == null) {
            this.remove(suggestions);
            return null;
        }

        return channel;
    }

    /**
     * Reset the automatic archive timer of the ThreadChannel.
     *
     * @param suggestions The bot class; use the instance which has been dependency injected in your class before
     */
    public void resetArchiveTimer(Suggestions suggestions) {
        ThreadChannel channel = getThreadChannel(suggestions);

        if (channel == null) {
            return;
        }

        if (channel.isLocked()) {
            return;
        }

        channel.sendMessage(suggestions.getMessage().get("suggestion.reset_auto_archive", channel.getGuild()))
                .queue(message -> message.delete().queue());
        channel.getManager().setArchived(false).setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_1_WEEK).queue();
    }

    /**
     * Remove the Suggestion
     *
     * @param suggestions The bot class; use the instance which has been dependency injected in your class before
     */
    public void remove(Suggestions suggestions) {
        // TODO: 07.06.2022 REMOVE Suggestion from Database and Collection
    }

}
