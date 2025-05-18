package im.nucker.scripts.lua.libraries;

import im.nucker.scripts.interpreter.LuaValue;
import im.nucker.scripts.interpreter.compiler.jse.CoerceJavaToLua;
import im.nucker.scripts.interpreter.lib.OneArgFunction;
import im.nucker.scripts.interpreter.lib.TwoArgFunction;
import im.nucker.scripts.lua.classes.ModuleClass;

public class ModuleLibrary extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("register", new register());

        env.set("module", library);
        return library;
    }

    public class register extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            return CoerceJavaToLua.coerce(new ModuleClass(arg.toString()));
        }

    }

}
