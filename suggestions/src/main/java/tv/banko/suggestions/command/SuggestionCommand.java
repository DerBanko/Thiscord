package tv.banko.suggestions.command;

import com.google.gson.JsonParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import tv.banko.suggestions.Suggestions;
import tv.banko.suggestions.suggestion.Suggestion;
import tv.banko.suggestions.translation.CommonTranslation;
import tv.banko.suggestions.translation.MessageTranslation;
import tv.banko.suggestions.util.JsonMessage;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class SuggestionCommand extends CommandObject {

    public SuggestionCommand(@NotNull Suggestions suggestions) {
        super(suggestions);
    }

    @Override
    public CommandData getCommand() {
        CommonTranslation common = suggestions.getCommon();
        return Commands.slash("suggestion", common.get("command.description"))
                .addSubcommands(new SubcommandData("create", common.get("command.create.description"))
                        .addOption(OptionType.STRING, "name", common.get("command.create.name.description"), true)
                        .addOption(OptionType.STRING, "json", common.get("command.create.json.description"), false)
                        .addOption(OptionType.BOOLEAN, "vote", common.get("command.create.vote.description"), false))
                .addSubcommands(new SubcommandData("lock", common.get("command.lock.description")))
                .addSubcommands(new SubcommandData("delete", common.get("command.delete.description")))
                .addSubcommands(new SubcommandData("vote", common.get("command.vote.description")));
    }

    @Override
    public void respond(SlashCommandInteractionEvent event) {
        MessageTranslation message = suggestions.getMessage();
        User user = event.getUser();

        if (event.getGuild() == null) {
            event.replyEmbeds(message.getEmbed("default.unknown_error", null)
                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                    .build()).queue();
            return;
        }

        Member member = event.getMember();

        if (member == null) {
            event.replyEmbeds(message.getEmbed("default.unknown_error", null)
                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                    .build()).setEphemeral(true).queue();
            return;
        }

        Guild guild = member.getGuild();

        if (!member.hasPermission(Permission.MANAGE_THREADS)) {
            event.replyEmbeds(message.getEmbed("command.no_permission", event.getUserLocale())
                    .setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl())
                    .build()).setEphemeral(true).queue();
            return;
        }

        String subCommand = event.getSubcommandName();

        if (subCommand == null) {
            event.replyEmbeds(message.getEmbed("default.unknown_error", event.getUserLocale())
                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue(hook -> {
            switch (subCommand.toLowerCase()) {
                case "create" -> {
                    try {
                        GuildMessageChannel channel = event.getGuildChannel();

                        OptionMapping nameMapping = event.getOption("name");

                        if (nameMapping == null) {
                            hook.editOriginalEmbeds(message.getEmbed("command.no_name", event.getUserLocale())
                                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                    .build()).queue();
                            return;
                        }

                        OptionMapping jsonMapping = event.getOption("json");
                        boolean voteReactions = event.getOption("vote") != null &&
                                Objects.requireNonNull(event.getOption("vote")).getAsBoolean();

                        if (jsonMapping == null || jsonMapping.getAsString().isEmpty()) {
                            channel.retrieveMessageById(channel.getLatestMessageIdLong()).queue(latestMessage -> {
                                if (latestMessage == null) {
                                    hook.editOriginalEmbeds(message.getEmbed("command.no_message", event.getUserLocale())
                                            .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                            .build()).queue();
                                    return;
                                }

                                createSuggestion(hook, user, latestMessage, nameMapping.getAsString(), event.getUserLocale(), voteReactions);
                            }, throwable -> hook.editOriginalEmbeds(message.getEmbed("command.no_message", event.getUserLocale())
                                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                    .build()).queue());
                            return;
                        }

                        JsonMessage.send(channel, new JsonParser().parse(jsonMapping.getAsString()).getAsJsonObject())
                                .queue(parentMessage -> createSuggestion(hook, user, parentMessage,
                                        nameMapping.getAsString(), event.getUserLocale(), voteReactions));
                    } catch (Exception e) {
                        e.printStackTrace();
                        hook.editOriginalEmbeds(message.getEmbed("default.unknown_error", event.getUserLocale())
                                .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                .build()).queue();
                    }
                }
                case "lock", "delete", "vote" -> {
                    try {
                        MessageChannel messageChannel = event.getChannel();

                        if (!(messageChannel instanceof ThreadChannel channel)) {
                            hook.editOriginalEmbeds(message.getEmbed("default.no_suggestion_channel", event.getUserLocale())
                                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                    .build()).queue();
                            return;
                        }

                        Optional<Suggestion> optional = suggestions.getManager().getSuggestionByThreadId(channel.getId());

                        if (optional.isEmpty()) {
                            hook.editOriginalEmbeds(message.getEmbed("default.no_suggestion_channel", event.getUserLocale())
                                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                    .build()).queue();
                            return;
                        }

                        final Suggestion suggestion = optional.get();
                        switch (subCommand.toLowerCase()) {
                            case "lock" -> {
                                boolean state = !channel.isLocked();

                                channel.getManager().setLocked(state).queue(unused -> {
                                    if (state) {
                                        hook.editOriginalEmbeds(message.getEmbed("command.lock", event.getUserLocale())
                                                        .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                                        .build())
                                                .setActionRows(ActionRow.of(Button.danger("lock.confirm", message.get("command.lock.button"))))
                                                .queue();
                                        return;
                                    }

                                    hook.editOriginalEmbeds(message.getEmbed("command.unlocked", event.getUserLocale())
                                            .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                            .build()).queue();
                                });
                            }
                            case "delete" ->
                                    hook.editOriginalEmbeds(message.getEmbed("command.delete", event.getUserLocale())
                                                    .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                                    .build())
                                            .setActionRows(ActionRow.of(Button.danger("delete.confirm", message.get("command.delete.button"))))
                                            .queue();
                            case "vote" -> {
                                Suggestion newSuggestion = suggestions.getManager().recreateSuggestion(suggestion, !suggestion.vote());

                                if (newSuggestion.vote()) {
                                    hook.editOriginalEmbeds(message.getEmbed("command.vote.enable", event.getUserLocale())
                                            .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                            .build()).queue();
                                    return;
                                }

                                hook.editOriginalEmbeds(message.getEmbed("command.vote.disable", event.getUserLocale())
                                        .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                        .build()).queue();
                            }
                        }
                    } catch (Exception e) {
                        hook.editOriginalEmbeds(message.getEmbed("default.unknown_error", event.getUserLocale())
                                .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                                .build()).queue();
                    }
                }
                default -> hook.editOriginalEmbeds(message.getEmbed("default.unknown_error", event.getUserLocale())
                        .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                        .build()).queue();
            }
        });
    }

    private void createSuggestion(InteractionHook hook, User user, Message message, String name, Locale locale, boolean voteReactions) {
        suggestions.getManager().createSuggestion(message, name, voteReactions).whenCompleteAsync((unused, throwable) ->
                hook.editOriginalEmbeds(suggestions.getMessage().getEmbed("command.created", locale)
                        .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                        .build()).queue());
    }
}
