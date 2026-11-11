package com.github.kenzisiaufandi.commands;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.Help;

@Command("example")
@CommandPermission("example.admin")
public class AdminCommand {
    private static final int ENTRIES_PER_PAGE = 5;

    /**
     * Shows a page of commands available to the sender.
     */
    @Subcommand("help")
    @Description("Show available commands")
    public void helpCommand(
            BukkitCommandActor actor,
            @Range(min = 1) @Default("1") int page,
            Help.RelatedCommands<BukkitCommandActor> commands
    ) {
        if (commands.all().isEmpty()) {
            actor.reply("No commands are available.");
            return;
        }

        int totalPages = commands.numberOfPages(ENTRIES_PER_PAGE);
        if (page < 1 || page > totalPages) {
            actor.error("Help page " + page + " does not exist. Choose a page from 1 to " + totalPages + ".");
            return;
        }

        actor.reply("Example — Help (Page " + page + "/" + totalPages + ")");
        for (var command : commands.paginate(page, ENTRIES_PER_PAGE)) {
            String description = command.description();
            actor.reply(
                    "/" + command.usage() + (description == null || description.isBlank() ? "" : " — " + description)
            );
        }
        if (page > 1) {
            actor.reply("Previous page: /example help " + (page - 1));
        }
        if (page < totalPages) {
            actor.reply("Next page: /example help " + (page + 1));
        }
    }
}
