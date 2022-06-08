
package tv.banko.suggestions.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import tv.banko.suggestions.Suggestions;
import tv.banko.suggestions.suggestion.Suggestion;
import tv.banko.suggestions.translation.MessageTranslation;

import java.util.Optional;

public record InteractionListener(Suggestions suggestions) implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (!(genericEvent instanceof ButtonInteractionEvent event)) {
            return;
        }

        if (event.getComponent().getId() == null) {
            return;
        }

        MessageTranslation message = suggestions.getMessage();
        User user = event.getUser();

        if (event.getComponent().getId() == null) {
            event.replyEmbeds(message.getEmbed("default.unknown_error", event.getUserLocale())
                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                    .build()).setEphemeral(true).queue();
            return;
        }

        Member member = event.getMember();

        if (member == null) {
            event.replyEmbeds(message.getEmbed("default.unknown_error", event.getUserLocale())
                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                    .build()).setEphemeral(true).queue();
            return;
        }

        if (!(event.getChannel() instanceof ThreadChannel channel)) {
            event.replyEmbeds(message.getEmbed("default.unknown_error", event.getUserLocale())
                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                    .build()).setEphemeral(true).queue();
            return;
        }

        Optional<Suggestion> optional = suggestions.getManager().getSuggestionByThreadId(channel.getId());

        if (optional.isEmpty()) {
            event.replyEmbeds(message.getEmbed("default.no_suggestion_channel", event.getUserLocale())
                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                    .build()).setEphemeral(true).queue();
            return;
        }

        switch (event.getComponent().getId()) {
            case "delete.confirm" -> {
                optional.get().remove(suggestions);
                event.replyEmbeds(message.getEmbed("button.deleted", event.getUserLocale())
                        .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                        .build()).setEphemeral(true).queue();
            }
            case "lock.confirm" -> channel.getManager().setLocked(true).queue(unused ->
                    event.replyEmbeds(message.getEmbed("button.lock", event.getUserLocale())
                            .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                            .build()).setEphemeral(true).queue());
        }
    }
}
