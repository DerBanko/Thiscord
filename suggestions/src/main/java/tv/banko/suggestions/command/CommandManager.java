package tv.banko.butils.bot.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import tv.banko.butils.bot.Bot;
import tv.banko.butils.bot.command.license.LicenseCommand;
import tv.banko.butils.bot.command.user.DeleteDataCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private final List<CommandObject> list;

    public CommandManager() {
        this.list = new ArrayList<>();
    }

    public void load(Bot bot) {
        list.add(new LicenseCommand(bot));
        list.add(new DeleteDataCommand(bot));

        CommandListUpdateAction commands = bot.getBot().updateCommands();

        for (CommandObject object : list) {
            commands = commands.addCommands(object.getCommand());
        }

        commands.queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (CommandObject object : list) {
            if (!object.getCommand().getName().equalsIgnoreCase(event.getInteraction().getName())) {
                continue;
            }

            object.respond(event);
        }
    }
}
