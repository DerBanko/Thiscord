
package tv.banko.suggestions.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import tv.banko.butils.common.license.LicenseType;
import tv.banko.suggestions.Suggestions;
import tv.banko.suggestions.suggestion.Suggestion;
import tv.banko.suggestions.translation.MessageTranslation;

import java.util.Optional;
import java.util.function.Predicate;

public record InteractionListeners(Suggestions suggestions) implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof ButtonInteractionEvent event) {

            if (event.getComponent().getId() == null) {
                return;
            }

            switch (event.getComponent().getId()) {
                case "delete.confirm" -> buttonDelete(event);
                case "lock.confirm" -> buttonLock(event);
            }
            return;
        }
    }

    private void buttonDelete(ButtonInteractionEvent event) {
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

        optional.get().remove(suggestions);
        event.replyEmbeds(message.getEmbed("default.no_suggestion_channel", event.getUserLocale())
                .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                .build()).setEphemeral(true).queue();
    }

    private void buttonSubscription(ButtonInteractionEvent event, Predicate<Member> hasPermission,
                                    LicenseType type, boolean patreon, long expire) {
        if (event.getComponent().getId() == null) {
            return;
        }

        Member member = event.getMember();

        if (member == null) {
            return;
        }

        event.deferReply(true).queue(hook -> {
            if (!hasPermission.test(member)) {
                suggestions.getMessage().getVerification().respondSubscriptionDecline(hook);
                return;
            }

            manageSubscription(hook, member, type, patreon, expire);
        });
    }

    private void buttonLock(ButtonInteractionEvent event) {

        if (event.getComponent().getId() == null) {
            return;
        }

        Member member = event.getMember();

        if (member == null) {
            return;
        }

        suggestions.getMessage().getVerification().respondSubscription(event);
    }

    private void manageSubscription(InteractionHook hook, Member member, LicenseType type, boolean patreon, long expire) {
        suggestions.getLicense().hasLicenseWithPattern(member.getId(), type, patreon)
                .whenCompleteAsync((hasLicense, throwable) -> {
                    if (hasLicense) {
                        suggestions.getMessage().getVerification().respondSubscriptionAlreadyObtained(hook);
                        return;
                    }

                    suggestions.getLicense().generateLicense(member.getId(), type, patreon, expire).whenCompleteAsync((license, throwable1) -> {

                        if (license == null) {
                            suggestions.getMessage().getVerification().respondSubscriptionError(hook);
                            return;
                        }

                        suggestions.getMessage().getVerification().respondSubscriptionSuccess(hook, license, type, expire);
                    });
                });
    }

    private void modalVerifyHasKey(ModalInteractionEvent event) {

        Member member = event.getMember();

        if (member == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor(suggestions.getLocalization().get("verify.integration.has_key.author"), "",
                            suggestions.getConfig().getString("default.avatar"))
                    .setDescription(":no_entry: | " +
                            suggestions.getLocalization().get("verify.integration.has_key.error"))
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.getInteraction().deferReply(true).queue(hook -> {

            ModalMapping license = event.getValue("license");

            if (license == null) {
                hook.editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor(suggestions.getLocalization().get("verify.integration.has_key.author"), "",
                                suggestions.getConfig().getString("default.avatar"))
                        .setDescription(":no_entry: | " +
                                suggestions.getLocalization().get("verify.integration.has_key.error"))
                        .build()).queue();
                return;
            }

            // TODO: Check if license exists, change discord id of license

            suggestions.getMessage().getVerification().respondHasKeyAccepted(hook, license.getAsString());
        });
    }

    private void modalDeleteData(ModalInteractionEvent event) {

        Member member = event.getMember();

        if (member == null) {
            return;
        }

        event.getInteraction().deferReply(true).queue(hook -> {

            ModalMapping input = event.getValue("input");

            if (input == null) {
                hook.deleteOriginal().queue();
                return;
            }

            if (!input.getAsString().equalsIgnoreCase("DELETE DATA")) {
                hook.editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor(suggestions.getLocalization().get("command.delete_data.author"), "",
                                suggestions.getConfig().getString("default.avatar"))
                        .setDescription(":no_entry: | " +
                                suggestions.getLocalization().get("command.delete_data.wrong_input"))
                        .build()).queue();
                return;
            }

            // TODO: Delete user data

            hook.editOriginalEmbeds(new EmbedBuilder()
                    .setAuthor(suggestions.getLocalization().get("command.delete_data.author"), "",
                            suggestions.getConfig().getString("default.avatar"))
                    .setDescription(":white_check_mark: | " +
                            suggestions.getLocalization().get("command.delete_data.deleted"))
                    .build()).queue();
        });
    }

}
