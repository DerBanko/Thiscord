package tv.banko.butils.bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import tv.banko.butils.bot.Bot;

public abstract class CommandObject {

    protected final Bot bot;

    public CommandObject(Bot bot) {
        this.bot = bot;
    }

    public abstract CommandData getCommand();

    public abstract void respond(SlashCommandInteractionEvent event);
}
