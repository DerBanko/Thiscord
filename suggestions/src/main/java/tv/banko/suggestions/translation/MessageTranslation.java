package tv.banko.suggestions.translation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageTranslation extends Translation {

    public MessageTranslation() {
        super("message", MessageTranslation.class.getClassLoader());
    }

    public EmbedBuilder getEmbed(@Guild guild, String key, String authorUrl, String authorIconUrl, String titleUrl, String footerIcon, int fields) {
        Locale locale = guild.getLocale();

        String author = getNull(key + ".author", locale);
        String title = getNull(key + ".title", locale);
        String description = getNull(key + ".description", locale);
        String footer = getNull(key + ".footer", locale);

        List<MessageEmbed.Field> list = new ArrayList<>();

        for (int i = 0; i < fields; i++) {
            String fieldName = getNull(key + ".field" + i + ".name", locale);
            String fieldDescription = getNull(key + ".field" + i + ".description", locale);

            if (fieldName == null || fieldDescription == null) {
                continue;
            }

            list.add(new MessageEmbed.Field("", "", false));
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title, titleUrl)
                .setDescription(description)
                .setAuthor(author, authorUrl, authorIconUrl)
                .setFooter(footer, footerIcon);

        list.forEach(embed::addField);

        return embed;
    }

    public EmbedBuilder getEmbed(Guild guild, String key) {
        return this.getEmbed(guild, key, null, null, null, null, 0);
    }

    public String get(String key, Guild guild, Object... format) {
        return getNull(key, guild.getLocale(), format);
    }
}
