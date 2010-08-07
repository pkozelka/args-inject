package net.sf.buildbox.args;

import javax.xml.namespace.QName;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CliDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;
import net.sf.buildbox.args.model.SubcommandDeclaration;
import net.sourceforge.xmlfacade.XmlFacade;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SubCommand(name = "help-xml")
public class DefaultHelpXmlCommand implements MetaCommand {
    private CliDeclaration declaration = null;
    private static final String NS = "http://buildbox.sf.net/args/help.xml";

    public void setDeclaration(CliDeclaration declaration) {
        this.declaration = declaration;
    }

    public void call() throws Exception {
        final Document doc = XmlFacade.createDocument();
        final Element root = XmlFacade.createElement(doc, new QName(NS, "commandline"),
                XmlFacade.createAttribute(doc, new QName("program"), declaration.getProgramName())
        );
        doc.appendChild(root);
        for (SubcommandDeclaration decl : declaration.getCommandDeclarations()) {
            final Element cmd = XmlFacade.createElement(doc, new QName(NS, "command"),
                    createAttribute(doc, "name", decl.getName()),
                    createAttribute(doc, "class", decl.getCommandClass().getCanonicalName()));
            root.appendChild(cmd);
            for (ParamDeclaration paramDecl : decl.getParamDeclarations()) {
                final Element param = XmlFacade.createElement(doc, new QName(NS, "param"),
                        createAttribute(doc, "type", paramDecl.getType().getCanonicalName())
                );
                cmd.appendChild(param);
            }
        }
        XmlFacade.saveXml(doc, System.out);
    }

    public static Attr createAttribute(Document d, String name, String value) {
        return XmlFacade.createAttribute(d, new QName(name), value);
    }

}
