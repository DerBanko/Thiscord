# Thiscord Suggestions

Create suggestion threads within **one channel**.

## How to use

1. Invite the [Suggestions Discord Bot](https://discord.com/api/oauth2/authorize?client_id=983671605706240010&permissions=326417517648&scope=bot%20applications.commands).
2. Create a channel which should contain the suggestion threads.
3. Send a message or [create a message with json](https://github.com/DerBanko/Thiscord/README.md#json-message) to use this as the parent message of the thread.
4. Execute the following command ``/suggestion create <name> [json] [vote]``.
5. **OPTIONAL**: Set the vote parameter to **True** if you want that every message sent in this thread receives an **up-vote** and **down-vote** reaction.

### Commands

* ``/suggestion create <name> [json] [vote]`` - create a suggestion thread in the channel you execute the command in.
  * ``name`` - the name of the thread channel
  * ``json`` - the bot creates a message from the json data [How to?](https://github.com/DerBanko/Thiscord/README.md#json-message)
  * ``vote`` - the bot automatically reacts with **up-vote** and **down-vote** emotes on every message sent in this thread.

* ``/suggestion vote`` - toggle the automatic **up-** and **down-vote reactions**

* ``/suggestion lock`` - **lock** / **unlock** the **suggestion thread**

* ``/suggestion delete`` - delete the **suggestion thread** ***permanently***

## Example

Example on https://banko.tv/discord: *(its german lol)*
 ![Example on https://banko.tv/discord](https://i.imgur.com/dIEGhRG.png)

## Json-Message

1. Enter the data you want on [discohook.org](https://discohook.org).
2. Click the **JSON Data Editor** button at the end of the page.
3. Click the **Copy to Clipboard** button.
4. Paste your clipboard in the **json** parameter of the **/suggestion create**

