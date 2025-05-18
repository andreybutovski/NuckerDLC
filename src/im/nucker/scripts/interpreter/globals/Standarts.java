package im.nucker.scripts.interpreter.globals;

import im.nucker.scripts.interpreter.compiler.LuaC;
import im.nucker.scripts.interpreter.Globals;
import im.nucker.scripts.interpreter.LoadState;
import im.nucker.scripts.interpreter.lib.*;
import im.nucker.scripts.lua.libraries.ModuleLibrary;
import im.nucker.scripts.lua.libraries.PlayerLibrary;

public class Standarts {
    public static Globals standardGlobals() {
        Globals globals = new Globals();
        globals.load(new BaseLib());
        globals.load(new Bit32Lib());
        globals.load(new MathLib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new PlayerLibrary());
        globals.load(new ModuleLibrary());
        LoadState.install(globals);
        LuaC.install(globals);
        return globals;
    }
}
