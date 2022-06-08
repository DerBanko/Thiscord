
package tv.banko.suggestions.listener;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tv.banko.suggestions.Suggestions;
import tv.banko.suggestions.suggestion.Suggestion;

import java.util.Optional;

public record MessageListener(Suggestions suggestions) implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof MessageReceivedEvent event)) {
            return;
        }

        if (!(event.getChannel() instanceof ThreadChannel channel)) {
            return;
        }

        Optional<Suggestion> optional = suggestions.getManager().getSuggestionByThreadId(channel.getId());

        if (optional.isEmpty()) {
            return;
        }

        Suggestion suggestion = optional.get();

        if (!suggestion.vote()) {
            return;
        }

        Emote upVote = suggestions.getBot().getEmoteById("983837699377070150");
        Emote downVote = suggestions.getBot().getEmoteById("983838754710097980");

        if (upVote == null || downVote == null) {
            return;
        }

        event.getMessage().addReaction(upVote).and(event.getMessage().addReaction(downVote)).queue(unused -> {}, Throwable::printStackTrace);
    }
}
