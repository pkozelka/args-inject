package net.sf.buildbox.args.zer;

import java.io.File;
import net.sf.buildbox.args.annotation.SubCommand;

@SubCommand(name = "unregister", aliases = "unreg")
public class ZerUnregister extends ZerXmlfulCommand {
    private final File[] modules;

    public ZerUnregister(File... modules) {
        this.modules = modules;
    }

    public Integer call() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        return 0;
    }
}