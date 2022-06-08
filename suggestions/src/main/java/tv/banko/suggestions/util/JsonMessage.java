package tv.banko.suggestions.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;

public class JsonWebhook {

    public static RestAction<Message> sendMessage(GuildMessageChannel channel, JsonObject json) {
        JsonPrimitive content = json.getAsJsonPrimitive("content");

        channel.sendMessage()
    }

    /**
     * Converts a {@link JsonObject} to {@link MessageEmbed}.
     * Supported Fields: Title, Author, Description, Color, Fields, Thumbnail, Footer.
     *
     * @param json The JsonObject
     * @return The Embed
     */
    private static MessageEmbed jsonToEmbed(JsonObject json) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        JsonPrimitive title = json.getAsJsonPrimitive("title");

        if (title != null) {
            embedBuilder.setTitle(title.getAsString());
        }

        JsonObject author = json.getAsJsonObject("author");

        if (author != null) {
            embedBuilder.setAuthor(author.get("name").getAsString(),
                    author.get("url").getAsString(), author.get("icon_url").getAsString());
        }

        JsonPrimitive description = json.getAsJsonPrimitive("description");

        if (description != null) {
            embedBuilder.setDescription(description.getAsString());
        }

        JsonPrimitive color = json.getAsJsonPrimitive("color");

        if (color != null) {
            embedBuilder.setColor(new Color(color.getAsInt()));
        }

        JsonArray fields = json.getAsJsonArray("fields");

        if (fields != null) {
            fields.forEach(element -> embedBuilder.addField(element.getAsJsonObject().get("name").getAsString(),
                    element.getAsJsonObject().get("value").getAsString(),
                    element.getAsJsonObject().get("inline").getAsBoolean()));
        }

        JsonPrimitive thumbnail = json.getAsJsonPrimitive("thumbnail");

        if (thumbnail != null) {
            embedBuilder.setThumbnail(thumbnail.getAsString());
        }

        JsonObject footer = json.getAsJsonObject("footer");

        if (footer != null) {
            embedBuilder.setFooter(footer.get("text").getAsString(), footer.get("icon_url").getAsString());
        }

        return embedBuilder.build();
    }

}
