package net.kozelka.args;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import javax.xml.namespace.QName;
import net.kozelka.args.model.CommandlineDeclaration;
import net.kozelka.args.model.OptionDeclaration;
import net.kozelka.args.model.ParamDeclaration;
import net.kozelka.args.model.SubCommandDeclaration;
import net.sourceforge.xmlfacade.XmlException;
import net.sourceforge.xmlfacade.XmlFacade;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlModelSerializer {
    public static final String NS = "http://code.kozelka.net/args-inject";

    /**
     * Creates XML DOM Document containing complete commandline structure declaration
     *
     * @param declaration -
     * @return dom document
     */
    public static Document createDomDescriptor(CommandlineDeclaration declaration) {
        final Document doc = XmlFacade.createDocument();
        final Element root = XmlFacade.createElement(doc, new QName(NS, "commandline"));
        root.setAttribute("program", declaration.getProgramName());
        doc.appendChild(root);
        if (declaration.getDefaultSubCommand() != null) {
            createCommandElement(root, declaration.getDefaultSubCommand(), true);
        }
        for (SubCommandDeclaration decl : declaration.getCommandDeclarations()) {
            createCommandElement(root, decl, false);
        }
        // all option declarations
        for (OptionDeclaration decl : declaration.getOptionDeclarations()) {
            final Method method = decl.getOptionMethod();
            final Element opt = XmlFacade.createElement(doc, new QName(NS, "option"));
            root.appendChild(opt);
            opt.setAttribute("id", method.getDeclaringClass().getName() + ":" + method.getName());
            createDescriptionElement(opt, decl.getDescription());
            final String longName = decl.getLongName();
            final String shortName = decl.getShortName();
            if (longName != null) {
                opt.setAttribute("long-name", longName);
            }
            if (shortName != null) {
                opt.setAttribute("short-name", shortName);
            }
            if (decl.isGlobal()) {
                opt.setAttribute("global", "true");
            }
            createParamElements(opt, decl.getParamDeclarations());
        }
        return doc;
    }

    static void createCommandElement(Element parent, SubCommandDeclaration decl, boolean isDefault) {
        final Document doc = parent.getOwnerDocument();
        final Element cmd = XmlFacade.createElement(doc, new QName(NS, "command"));
        parent.appendChild(cmd);
        cmd.setAttribute("name", decl.getName());
        cmd.setAttribute("class", decl.getCommandClass().getCanonicalName());
        if (isDefault) {
            cmd.setAttribute("default", "true");
        }
        createDescriptionElement(cmd, decl.getDescription());
        // aliases
        for (String alias : decl.getAlternateNames()) {
            final Element aliasElem = XmlFacade.createElement(doc, new QName(NS, "alias"));
            cmd.appendChild(aliasElem);
            aliasElem.setAttribute("name", alias);
        }
        createParamElements(cmd, decl.getParamDeclarations());

        // valid options
        for (OptionDeclaration optionDeclaration : decl.getOptionDeclarations()) {
            final Method method = optionDeclaration.getOptionMethod();
            final Element opt = XmlFacade.createElement(doc, new QName(NS, "valid-option"));
            opt.setAttribute("ref", method.getDeclaringClass().getName() + ":" + method.getName());
            cmd.appendChild(opt);
        }
    }

    static void createDescriptionElement(Element parent, String description) {
        if (description != null) {
            final Document doc = parent.getOwnerDocument();
            parent.appendChild(XmlFacade.createElement(doc, new QName(NS, "description"), doc.createTextNode(description)));
        }
    }

    static void createParamElements(Element parent, List<ParamDeclaration> params) {
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

    /**
     * If property args.xml is set, its value is used as the name of output file which receives xml representation of the model.
     *
     * @param declaration the declaration to print
     * @throws XmlException -
     */
    public static void checkArgsXml(CommandlineDeclaration declaration) throws XmlException {
        final String xmlFileName = System.getProperty("args.xml");
        if (xmlFileName != null) {
            final Document doc = createDomDescriptor(declaration);
            final File file = new File(xmlFileName);
            XmlFacade.saveXml(doc, file);
        }
    }
}
