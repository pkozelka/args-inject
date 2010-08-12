package net.sf.buildbox.args;

import java.lang.reflect.Method;
import java.util.List;
import javax.xml.namespace.QName;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CommandlineDeclaration;
import net.sf.buildbox.args.model.OptionDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;
import net.sf.buildbox.args.model.SubCommandDeclaration;
import net.sourceforge.xmlfacade.XmlFacade;
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
        final Document doc = createDomDescriptor(declaration);
        XmlFacade.saveXml(doc, System.out);
        return 0;
    }

    private static Document createDomDescriptor(CommandlineDeclaration declaration) {
        final Document doc = XmlFacade.createDocument();
        final Element root = XmlFacade.createElement(doc, new QName(NS, "commandline"));
        root.setAttribute("program", declaration.getProgramName());
        doc.appendChild(root);
        if (declaration.getDefaultSubCommand() != null) {
            showCommand(root, declaration.getDefaultSubCommand(), true);
        }
        for (SubCommandDeclaration decl : declaration.getCommandDeclarations()) {
            showCommand(root, decl, false);
        }
        // all option declarations
        for (OptionDeclaration decl : declaration.getOptionDeclarations()) {
            final Method method = decl.getOptionMethod();
            final Element opt = XmlFacade.createElement(doc, new QName(NS, "option"));
            root.appendChild(opt);
            opt.setAttribute("id", method.getDeclaringClass().getName() + ":" +method.getName());
            showDescription(opt, decl.getDescription());
            final String longName = decl.getLongName();
            final String shortName = decl.getShortName();
            if (longName != null) {
                opt.setAttribute("long-name", longName);
            }
            if (shortName != null) {
                opt.setAttribute("short-name", shortName);
            }
            showParams(opt, decl.getParamDeclarations());
        }
        return doc;
    }

    private static void showCommand(Element parent, SubCommandDeclaration decl, boolean isDefault) {
        final Document doc = parent.getOwnerDocument();
        final Element cmd = XmlFacade.createElement(doc, new QName(NS, "command"));
        parent.appendChild(cmd);
        cmd.setAttribute("name", decl.getName());
        cmd.setAttribute("class", decl.getCommandClass().getCanonicalName());
        if (isDefault) {
            cmd.setAttribute("default", "true");
        }
        showDescription(cmd, decl.getDescription());
        // aliases
        for (String alias : decl.getAlternateNames()) {
            final Element aliasElem = XmlFacade.createElement(doc, new QName(NS, "alias"));
            cmd.appendChild(aliasElem);
            aliasElem.setAttribute("name", alias);
        }
        showParams(cmd, decl.getParamDeclarations());

        // valid options
        for (OptionDeclaration optionDeclaration : decl.getOptionDeclarations()) {
            final Method method = optionDeclaration.getOptionMethod();
            final Element opt = XmlFacade.createElement(doc, new QName(NS, "valid-option"));
            opt.setAttribute("ref", method.getDeclaringClass().getName() + ":" +method.getName());
            cmd.appendChild(opt);
        }
    }

    private static void showDescription(Element parent, String description) {
        if (description != null) {
            final Document doc = parent.getOwnerDocument();
            parent.appendChild(XmlFacade.createElement(doc, new QName(NS, "description"), doc.createTextNode(description)));
        }
    }

    private static void showParams(Element parent, List<ParamDeclaration> params) {
        final Document doc = parent.getOwnerDocument();
        // params
        for (ParamDeclaration paramDecl : params) {
            final Element param = XmlFacade.createElement(doc, new QName(NS, "param"));
            param.setAttribute("symbolic-name", paramDecl.getSymbolicName());
            param.setAttribute("type", paramDecl.getType().getCanonicalName());
            parent.appendChild(param);
            final String format = paramDecl.getFormat();
            if (format != null) {
                param.setAttribute("format", format);
            }
            final String listSep = paramDecl.getListSeparator();
            if (paramDecl.isVarArgs()) {
                param.setAttribute("varargs", "true");
            } else if (listSep != null) {
                param.setAttribute("list-separator", listSep);
            }
        }
    }
}
