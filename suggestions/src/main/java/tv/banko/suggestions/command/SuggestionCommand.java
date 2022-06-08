package tv.banko.butils.bot.command.license;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import tv.banko.butils.bot.Bot;
import tv.banko.butils.bot.command.CommandObject;

public class LicenseCommand extends CommandObject {

    public LicenseCommand(Bot bot) {
        super(bot);
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash("license", "Befehl zum Managen der Lizenzen.")
            .addSubcommands(new SubcommandData("add", "Füge eine Lizenz hinzu.")
                .addOption(OptionType.STRING, "license", "Lizenz", true)
                .addOption(OptionType.STRING, "name", "Benenne Deine Lizenz", false))
            .addSubcommands(new SubcommandData("list", "Liste Deine Lizenzen auf."));
    }
    @Override
    public void respond(SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) {
            return;
        }

        Member member = event.getMember();

        if (member == null) {
            return;
        }

        if (event.getSubcommandName() == null) {
            return;
        }

        event.deferReply(true).queue(interactionHook -> {
            switch (event.getSubcommandName().toLowerCase()) {
                case "list" -> {
//                    EmbedBuilder builder = new EmbedBuilder()
//                        .setAuthor("Lizenzen", null, bot.getConfig().getString("default.avatar"));

//                    bot.getBackend().getDatabase().getLicense().getLicensesByDiscordId(member.getId())
//                        .whenCompleteAsync((documents, throwable) -> {
//                            for (Document document : documents) {
//                                String license = document.getString("license");
//                                LicenseType type = LicenseType.valueOf(document.getString("type"));
//
//                                String time = document.getLong("expire") != -1 ? "(<t:" +
//                                    (document.getLong("expire") / 1000) + ":f>)" : "";
//
//                                builder.addField(type.getEmoji() + " | " + type.getName(),
//                                    "> ||`" + license + "`|| " + time, false);
//                            }
//
//                            if (documents.isEmpty()) {
//                                builder.setDescription(":no_entry_sign: | " + bot.getLocalization().get("command.license.no_licenses"));
//                            } else {
//                                builder.setDescription(":bookmark_tabs: | " + bot.getLocalization().get("command.license.list_licenses"));
//                            }
//
//                            interactionHook.editOriginalEmbeds(builder.build()).queue();
//                        });
                        throw new UnsupportedOperationException("Not yet implemented.");
                        // on hold because new backend.
                }
            }
        });
    }
}
