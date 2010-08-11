package net.sf.buildbox.args;

import javax.xml.namespace.QName;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CommandlineDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;
import net.sf.buildbox.args.model.SubCommandDeclaration;
import net.sourceforge.xmlfacade.XmlFacade;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SubCommand(name = "help-xml")
public class DefaultHelpXmlCommand implements MetaCommand {
    private CommandlineDeclaration declaration = null;
    private static final String NS = "http://buildbox.sf.net/args/help.xml";

    public void setDeclaration(CommandlineDeclaration declaration) {
        this.declaration = declaration;
    }

    public Integer call() throws Exception {
        final Document doc = XmlFacade.createDocument();
        final Element root = XmlFacade.createElement(doc, new QName(NS, "commandline"),
                XmlFacade.createAttribute(doc, new QName("program"), declaration.getProgramName())
        );
        doc.appendChild(root);
        for (SubCommandDeclaration decl : declaration.getCommandDeclarations()) {
            final Element cmd = XmlFacade.createElement(doc, new QName(NS, "command"),
                    createAttribute(doc, "name", decl.getName()),
                    createAttribute(doc, "class", decl.getCommandClass().getCanonicalName()));
            root.appendChild(cmd);
            // aliases
            for (String alias : decl.getAlternateNames()) {
                Element aliasElem = XmlFacade.createElement(doc, new QName(NS, "alias"),
                        createAttribute(doc, "name", alias));
                cmd.appendChild(aliasElem);
            }
            // params
            for (ParamDeclaration paramDecl : decl.getParamDeclarations()) {
                final Element param = XmlFacade.createElement(doc, new QName(NS, "param"),
                        createAttribute(doc, "symbolic-name", paramDecl.getSymbolicName()),
                        createAttribute(doc, "type", paramDecl.getType().getCanonicalName())
                );
                final String format = paramDecl.getFormat();
                if (format != null) {
                    param.setAttributeNode(createAttribute(doc, "format", format));
                }
                final String listSep = paramDecl.getListSeparator();
                if (listSep != null) {
                    param.setAttributeNode(createAttribute(doc, "list-separator", listSep));
                }
                if (paramDecl.isVarArgs()) {
                    param.setAttributeNode(createAttribute(doc, "varargs", "true"));
                }
                cmd.appendChild(param);
            }
            //TODO: valid options
        }
        //TODO: option declarations
        XmlFacade.saveXml(doc, System.out);
        return 0;
    }

    public static Attr createAttribute(Document d, String name, String value) {
        return XmlFacade.createAttribute(d, new QName(name), value);
    }

}
