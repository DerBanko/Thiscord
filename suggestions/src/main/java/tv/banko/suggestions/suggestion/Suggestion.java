package tv.banko.suggestions.suggestion;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tv.banko.suggestions.Suggestions;

import java.util.concurrent.TimeUnit;

public record Suggestion(String guildId, String threadId, boolean vote) {

    /**
     * Get the ThreadChannel of the Suggestion.
     *
     * @param suggestions The suggestions class; use the instance which has been dependency injected in your class before
     * @return The ThreadChannel; may be null if the guild or the ThreadChannel does not exist anymore
     */
    @Nullable
    public ThreadChannel getThreadChannel(@NotNull Suggestions suggestions) {
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
     * @param suggestions The suggestions class; use the instance which has been dependency injected in your class before
     */
    public void resetArchiveTimer(@NotNull Suggestions suggestions, long delay) {
        ThreadChannel channel = getThreadChannel(suggestions);

        if (channel == null) {
            remove(suggestions);
            return;
        }

        if (channel.isLocked()) {
            return;
        }

        channel.sendMessage(suggestions.getMessage().get("suggestion.reset_auto_archive", channel.getGuild()))
                .queueAfter(delay, TimeUnit.MILLISECONDS, message -> message.delete().queue());
        channel.getManager().setArchived(false).setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_1_WEEK).queue();
    }

    /**
     * Remove the Suggestion
     *
     * @param suggestions The suggestions class; use the instance which has been dependency injected in your class before
     */
    public void remove(@NotNull Suggestions suggestions) {
        suggestions.getManager().deleteSuggestion(this);
    }

    /**
     * Remove the ThreadChannel
     *
     * @param suggestions The suggestions class; use the instance which has been dependency injected in your class before
     */
    public void removeChannel(@NotNull Suggestions suggestions) {
        ThreadChannel channel = suggestions.getBot().getThreadChannelById(threadId);

        if (channel == null) {
            return;
        }

        channel.delete().queue();
    }

    @Override
    public @NotNull String guildId() {
        return guildId;
    }

    @Override
    public @NotNull String threadId() {
        return threadId;
    }
}
