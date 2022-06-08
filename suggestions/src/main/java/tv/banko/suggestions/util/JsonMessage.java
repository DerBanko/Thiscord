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
import java.util.ArrayList;
import java.util.List;

public class JsonMessage {

    public static RestAction<Message> send(GuildMessageChannel channel, JsonObject json) {
        String content = null;

        if (json.has("content") && json.get("content").isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive("content");
            content = primitive.getAsString();
        }

        List<MessageEmbed> embedList = new ArrayList<>();

        json.getAsJsonArray("embeds").forEach(jsonElement ->
                embedList.add(getEmbed(jsonElement.getAsJsonObject())));

        return channel.sendMessageEmbeds(embedList).content(content);
    }

    /**
     * Converts a {@link JsonObject} to {@link MessageEmbed}.
     * Supported Fields: Title, Author, Description, Color, Fields, Thumbnail, Footer.
     *
     * @param json The JsonObject
     * @return The Embed
     */
    private static MessageEmbed getEmbed(JsonObject json) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (json.has("title") && json.get("title").isJsonPrimitive()) {
            JsonPrimitive title = json.getAsJsonPrimitive("title");
            embedBuilder.setTitle(title.getAsString());
        }

        if (json.has("author") && json.get("author").isJsonObject()) {
            JsonObject author = json.getAsJsonObject("author");

            if (author.has("name")) {
                String url = author.has("url") ? author.get("url").getAsString() : null;
                String iconUrl = author.has("icon_url") ? author.get("icon_url").getAsString() : null;

                embedBuilder.setAuthor(author.get("name").getAsString(), url, iconUrl);
            }
        }

        if (json.has("description") && json.get("description").isJsonPrimitive()) {
            JsonPrimitive description = json.getAsJsonPrimitive("description");
            embedBuilder.setDescription(description.getAsString());
        }

        if (json.has("color") && json.get("color").isJsonPrimitive()) {
            JsonPrimitive color = json.getAsJsonPrimitive("color");
            embedBuilder.setColor(new Color(color.getAsInt()));
        }

        if (json.has("fields") && json.get("fields").isJsonArray()) {
            JsonArray fields = json.getAsJsonArray("fields");
            fields.forEach(element -> {

                JsonObject object = element.getAsJsonObject();

                if (!object.has("name") ||
                        !object.has("value")) {
                    return;
                }

                boolean inline = !object.has("inline") || object.get("inline").getAsBoolean();

                embedBuilder.addField(object.get("name").getAsString(),
                        object.get("value").getAsString(), inline);
            });
        }

        if (json.has("thumbnail") && json.get("thumbnail").isJsonObject()) {
            JsonObject thumbnail = json.getAsJsonObject("thumbnail");

            if (thumbnail.has("url")) {
                embedBuilder.setThumbnail(thumbnail.get("url").getAsString());
            }
        }

        if (json.has("image") && json.get("image").isJsonObject()) {
            JsonObject image = json.getAsJsonObject("image");

            if (image.has("url")) {
                embedBuilder.setImage(image.get("url").getAsString());
            }
        }

        if (json.has("footer") && json.get("footer").isJsonObject()) {
            JsonObject footer = json.getAsJsonObject("footer");

            if (footer.has("text") && footer.has("icon_url")) {
                embedBuilder.setFooter(footer.get("text").getAsString(), footer.get("icon_url").getAsString());
            }
        }

        return embedBuilder.build();
    }

}
