package tv.banko.suggestions.translation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MessageTranslation extends Translation {

    public MessageTranslation() {
        super("message", MessageTranslation.class.getClassLoader());
    }

    @NotNull
    public EmbedBuilder getEmbed(@NotNull String key, @Nullable Locale locale, @Nullable String authorUrl, @Nullable String authorIconUrl,
                                 @Nullable String titleUrl, @Nullable String footerIcon, int fields) {

        if (locale == null) {
            locale = Locale.ENGLISH;
        }

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

    @NotNull
    public EmbedBuilder getEmbed(@NotNull String key, @Nullable Locale locale) {
        return this.getEmbed(key, locale, null, null, null, null, 0);
    }

    @NotNull
    public String get(String key, Guild guild, Object... format) {
        return Objects.requireNonNullElse(getNull(key, guild.getLocale(), format), key);
    }
}
