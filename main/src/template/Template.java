package template;

import arc.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.world.*;
import template.gen.*;

import static mindustry.Vars.*;

public class Template extends Mod {
    public Template() {
        // Constructor implementation; do assets loading here.
        // Waiting for FileTreeInitEvent to be fired is mandatory for assets loading.

        Log.infoTag("template", "[scarlet]You shall not pass.[]");

        Events.on(FileTreeInitEvent.class, e -> ModSounds.load());
        Events.on(DisposeEvent.class, e -> ModSounds.dispose());
    }

    @Override
    public void init() {
        // Called on client and server when assets loading is finished.
        Core.app.exit();
    }

    @Override
    public void loadContent() {
        // Asynchronous content loading. Load your contents such as blocks/units here.
        // Usage of ContentList#load() is encouraged.

        new Block("oh-no") {
            {
                size = 2;
                update = destructible = false;
            }

            @Override
            public void init() {
                Log.infoTag("template", "[scarlet]You fool.[]");
            }
        };
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        // Register server-side commands inputted from the server console.
        handler.register("owo", "what's this", args -> {
            Log.infoTag("template", "[scarlet]You fool.[]");

            net.closeServer();
            Core.app.exit();
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        // Register client commands inputted from in-game player's chat console.
        handler.<Player>register("owo", "what's this", (args, player) -> {
            Log.infoTag("template", "[scarlet]You fool.[]");
            player.con.kick("[scarlet]You fool.[]", Long.MAX_VALUE);
        });
    }
}
