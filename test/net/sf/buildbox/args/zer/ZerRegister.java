package net.sf.buildbox.args.zer;

import java.io.File;
import net.sf.buildbox.args.annotation.SubCommand;

@SubCommand(name = "register", aliases = "reg")
public class ZerRegister extends ZerXmlfulCommand {
    private final File[] modules;

    public ZerRegister(File... modules) {
        this.modules = modules;
    }

    public Integer call() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        return 0;
    }
}