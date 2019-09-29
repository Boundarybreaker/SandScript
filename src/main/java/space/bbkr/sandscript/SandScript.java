package space.bbkr.sandscript;

import com.hrznstudio.sandbox.api.SandboxAPI;
import com.hrznstudio.sandbox.api.addon.Addon;
import com.hrznstudio.sandbox.api.util.Identity;
import space.bbkr.sandscript.helper.EventHelper;
import space.bbkr.sandscript.helper.RegistryHelper;
import space.bbkr.sandscript.makers.BlockMaker;
import space.bbkr.sandscript.makers.ItemMaker;

import javax.script.ScriptContext;
import java.util.List;

public class SandScript implements Addon {

    @Override
    public void init(SandboxAPI api) {
        List<Identity> initScripts = ScriptManager.INSTANCE.getScriptsAt("init/");
        ScriptManager.INSTANCE.runScripts(initScripts, (id, ctx) -> {
            ctx.setAttribute("api", api, ScriptContext.ENGINE_SCOPE);
            ctx.setAttribute("Event", new EventHelper(api), ScriptContext.ENGINE_SCOPE);
        });
    }

    @Override
    public void register() {
        List<Identity> regScripts = ScriptManager.INSTANCE.getScriptsAt("register/");
        ScriptManager.INSTANCE.runScripts(regScripts, (id, ctx) -> {
            ctx.setAttribute("register", new RegistryHelper(id.getNamespace()), ScriptContext.ENGINE_SCOPE);
            ctx.setAttribute("Block", BlockMaker.INSTANCE, ScriptContext.ENGINE_SCOPE);
            ctx.setAttribute("Item", ItemMaker.INSTANCE, ScriptContext.ENGINE_SCOPE);
        });
    }
}
