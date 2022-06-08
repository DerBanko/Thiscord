package tv.banko.suggestions.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import tv.banko.suggestions.Suggestions;

public abstract class CommandObject {

    protected final Suggestions suggestions;

    public CommandObject(@NotNull Suggestions suggestions) {
        this.suggestions = suggestions;
    }

    public abstract CommandData getCommand();

    public abstract void respond(SlashCommandInteractionEvent event);
}
