package tv.banko.suggestions.suggestion;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import org.jetbrains.annotations.NotNull;
import tv.banko.suggestions.Suggestions;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class SuggestionManager {

    private final Suggestions suggestions;
    private final List<Suggestion> list;

    public SuggestionManager(@NotNull Suggestions suggestions) {
        this.suggestions = suggestions;
        this.list = new ArrayList<>();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task();
            }
        }, 60 * 1000, 24 * 60 * 60 * 1000);
    }

    public void addSuggestion(@NotNull Suggestion suggestion) {
        if (this.list.contains(suggestion)) {
            return;
        }

        this.list.add(suggestion);
    }

    public CompletableFuture<Boolean> createSuggestion(@NotNull Message message, @NotNull String name, boolean voteReactions) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        message.createThreadChannel(name)
                .queue(threadChannel -> {
                    Suggestion suggestion = new Suggestion(threadChannel.getGuild().getId(), threadChannel.getId(), voteReactions);

                    this.addSuggestion(suggestion);
                    this.suggestions.getDatabase().getSuggestion().setSuggestion(suggestion);

                    threadChannel.getManager().setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_1_WEEK).setArchived(false)
                            .setLocked(false)
                            .and(threadChannel.getPermissionContainer().getManager().putRolePermissionOverride(
                                    threadChannel.getGuild().getPublicRole().getIdLong(), List.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND,
                                            Permission.MESSAGE_SEND_IN_THREADS), Collections.emptyList()))
                            .queue(unused -> future.complete(true));
                });
        return future;
    }

    public Optional<Suggestion> getSuggestionByThreadId(@NotNull String threadId) {
        return this.list.stream().filter(suggestion -> suggestion.threadId().equals(threadId)).findFirst();
    }

    public Suggestion recreateSuggestion(@NotNull Suggestion suggestion, boolean vote) {
        this.suggestions.getDatabase().getSuggestion().deleteSuggestion(suggestion);
        this.list.remove(suggestion);

        Suggestion newSuggestion = new Suggestion(suggestion.guildId(), suggestion.threadId(), vote);

        this.list.add(newSuggestion);
        this.suggestions.getDatabase().getSuggestion().setSuggestion(newSuggestion);
        return newSuggestion;
    }

    public void deleteSuggestion(@NotNull Suggestion suggestion) {
        suggestion.removeChannel(suggestions);
        this.list.remove(suggestion);
        this.suggestions.getDatabase().getSuggestion().deleteSuggestion(suggestion);
    }

    private void task() {
        AtomicInteger integer = new AtomicInteger(0);
        new ArrayList<>(this.list).forEach(suggestion -> suggestion.resetArchiveTimer(suggestions, integer.getAndAdd(250)));
    }
}
