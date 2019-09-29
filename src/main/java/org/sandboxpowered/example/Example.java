package org.sandboxpowered.example;

import org.sandboxpowered.sandbox.api.SandboxAPI;
import org.sandboxpowered.sandbox.api.addon.Addon;

public class Example implements Addon {
    @Override
    public void init(SandboxAPI api) {
        api.getLog().info("Loading Example Addon");
    }

    @Override
    public void register() {

    }
}
