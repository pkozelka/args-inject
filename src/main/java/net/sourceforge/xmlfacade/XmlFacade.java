
package net.sourceforge.xmlfacade;

import java.io.*;
import java.net.URL;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * Utilities class for easier XML creation and querying.
 */
public final class XmlFacade {

  private XmlFacade() {
  }

  // create methods section

  /**
   * Creates new empty document.
   * @return new document
   */
  public static Document createDocument() {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      return documentBuilder.newDocument();
    } catch (ParserConfigurationException e) {
      throw new XmlRuntimeException(e);
    }
  }

  /**
   * Creates element with specified name and contens.
   * Type of objects in contents can be one of the following: org.w3c.dom.Attr, org.w3c.dom.Node, java.lang.String and
   * Iterable or array which can again contain objects with all mentioned types.
   * @param d document used as factory for creating element
   * @param name qualified name
   * @param contents element contents
   * @return new element with specified name and contens
   */
  public static Element createElement(Document d, QName name, Object... contents) {
    String qualifiedName;
    if (name.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
      qualifiedName = name.getLocalPart();
    } else {
      qualifiedName = name.getPrefix() + ":" + name.getLocalPart();
    }
    Element element = d.createElementNS(name.getNamespaceURI(), qualifiedName);
    insertBefore(d, element, null, contents);
    return element;
  }

  /**
   * Creates attribute with specified name and value.
   * @param d document used as factory for creating attribute
   * @param name qualified name
   * @param value attribute value
   * @return new attribute with specified qualified name and value
   */
  public static Attr createAttribute(Document d, QName name, String value) {
    String qualifiedName;
    if (name.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
      qualifiedName = name.getLocalPart();
    } else {
      qualifiedName = name.getPrefix() + ":" + name.getLocalPart();
    }
    Attr attr;
    attr = d.createAttributeNS(name.getNamespaceURI(), qualifiedName);
    attr.setValue(value);
    return attr;
  }

  private static void insertBefore(Document d, Element parent, Node refChild, Object... contents) {
    for (Object object : contents) {
      if (object == null) {
        continue;
      } else if (object instanceof Attr) {
        Attr attr = (Attr)object;
        if (attr.getNamespaceURI() == null) {
          if (parent.hasAttribute(attr.getName())) {
            throw new XmlRuntimeException(String.format("Element %s already contains attribute %s.", getName(parent), getName(attr)));
          } else {
            parent.setAttributeNode(attr);
          }
        } else {
          if (parent.hasAttributeNS(attr.getNamespaceURI(), attr.getLocalName())) {
            throw new XmlRuntimeException(String.format("Element %s already contains attribute %s.", getName(parent), getName(attr)));
          } else {
            parent.setAttributeNodeNS(attr);
          }
        }
        parent.setAttributeNodeNS((Attr)object);
      } else if (object instanceof Node) {
        parent.insertBefore((Node)object, refChild);
      } else if (object instanceof Iterable<?>) {
        for (Object object2 : (Iterable<?>)object) {
          insertBefore(d, parent, refChild, object2);
        }
      } else if (object instanceof Object[]) {
        insertBefore(d, parent, refChild, (Object[])object);
      } else if (object instanceof String) {
        parent.insertBefore(d.createTextNode((String)object), refChild);
      } else {
        throw new XmlRuntimeException("Unsupported object type: " + object.getClass().getName());
      }
    }
  }

  // query methods section

  /**
   * Returns attribute with specified name. If such attribute doesn't exist returns null.
   * @param parent queried Element
   * @param name qualified name
   * @return attribute with specified name. If such attribute doesn't exist returns null
   */
  public static Attr getAttribute(final Element parent, final QName name) {
    if (name.getNamespaceURI() == null || name.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
      return parent.getAttributeNode(name.getLocalPart());
    } else {
      return parent.getAttributeNodeNS(name.getNamespaceURI(), name.getLocalPart());
    }
  }

  /**
   * Returns all descendant elements with specified name.
   * @param parent queried Element
   * @param name qualified name
   * @return all descendant elements with specified name.
   */
  @SuppressWarnings("unchecked")
  public static Iterable<Element> getDescendants(final Element parent, final QName name) {
    return (Iterable)nodeListToList(parent.getElementsByTagNameNS(name.getNamespaceURI(), name.getLocalPart()));
  }

  /**
   * Returns qualified name of specified node.
   * @param node node which name will be returned
   * @return qualified name of specified node
   */
  public static QName getName(Node node) {
    String namespaceURI = node.getNamespaceURI();
    if (namespaceURI == null) {
      return new QName(node.getNodeName());
    } else {
      String prefix = node.getPrefix() != null ? node.getPrefix() : XMLConstants.DEFAULT_NS_PREFIX;
      return new QName(namespaceURI, node.getLocalName(), prefix);
    }
  }

  private static List<Node> nodeListToList(final NodeList nodeList) {
    return new AbstractList<Node>() {

      @Override
      public Node get(int index) {
        return nodeList.item(index);
      }

      @Override
      public int size() {
        return nodeList.getLength();
      }
    };
  }

  // load, save

  /**
   * Loads XML document from specified File.
   * @param file file to parse
   * @return parsed document
   * @throws XmlException if XML document cannot be loaded
   */
  public static Document loadXml(File file) throws XmlException {
    return loadXml(new InputSource(file.toURI().toString()));
  }

  /**
   * Loads XML document from specified URL.
   * @param url URL of XML document to parse
   * @return parsed document
   * @throws XmlException if XML document cannot be loaded
   */
  public static Document loadXml(URL url) throws XmlException {
    return loadXml(new InputSource(url.toString()));
  }

  /**
   * Loads XML document from specified InputStream.
   * @param inputStream InputStream with XML document to parse
   * @return parsed document
   * @throws XmlException if XML document cannot be loaded
   */
  public static Document loadXml(InputStream inputStream) throws XmlException {
    return loadXml(new InputSource(inputStream));
  }

  /**
   * Loads XML document from specified Reader.
   * @param reader Reader with XML document to parse
   * @return parsed document
   * @throws XmlException if XML document cannot be loaded
   */
  public static Document loadXml(Reader reader) throws XmlException {
    return loadXml(new InputSource(reader));
  }

  private static Document loadXml(InputSource inputSource) throws XmlException {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      documentBuilderFactory.setValidating(false);
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      // todo
//      documentBuilder.setEntityResolver(er);
      documentBuilder.setErrorHandler(new ErrorHandler() {
        public void warning(SAXParseException exception) throws SAXException {
          throw exception;
        }
        public void error(SAXParseException exception) throws SAXException {
          throw exception;
        }
        public void fatalError(SAXParseException exception) throws SAXException {
          throw exception;
        }
      });
      return documentBuilder.parse(inputSource);
    } catch (ParserConfigurationException e) {
      throw new XmlRuntimeException(e);
    } catch (SAXException e) {
      throw new XmlException(e);
    } catch (IOException e) {
      throw new XmlException(e);
    }
  }

  /**
   * Saves XML to specified file.
   * @param node XML Node to save
   * @param file File where to save XML
   * @throws XmlException if XML document cannot be saved
   */
  public static void saveXml(Node node, File file) throws XmlException {
    saveXml(node, file, null);
  }

  /**
   * Saves XML to specified file.
   * @param node XML Node to save
   * @param file File where to save XML
   * @param options can be used for disabling formatting (indenting)
   * @throws XmlException if XML document cannot be saved
   */
  public static void saveXml(Node node, File file, XmlSaveOptions options) throws XmlException {
    try {
      OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
      saveXml(node, new StreamResult(writer), options);
      writer.close();
    } catch (IOException e) {
      throw new XmlException(e);
    }
  }

  /**
   * Saves XML to specified OutputStream.
   * @param node XML Node to save
   * @param outputStream OutputStream where to save XML
   * @throws XmlException if XML document cannot be saved
   */
  public static void saveXml(Node node, OutputStream outputStream) throws XmlException {
    saveXml(node, outputStream, null);
  }

  /**
   * Saves XML to specified OutputStream.
   * @param node XML Node to save
   * @param outputStream OutputStream where to save XML
   * @param options can be used for disabling formatting (indenting)
   * @throws XmlException if XML document cannot be saved
   */
  public static void saveXml(Node node, OutputStream outputStream, XmlSaveOptions options) throws XmlException {
    try {
      OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
      saveXml(node, new StreamResult(writer), options);
      writer.flush();
    } catch (UnsupportedEncodingException e) {
      throw new XmlRuntimeException(e);
    } catch (IOException e) {
      throw new XmlException(e);
    }
  }

  /**
   * Saves XML to specified Writer.
   * @param node XML Node to save
   * @param writer Writer where to save XML
   * @throws XmlException if XML document cannot be saved
   */
  public static void saveXml(Node node, Writer writer) throws XmlException {
    saveXml(node, writer, null);
  }

  /**
   * Saves XML to specified Writer.
   * @param node XML Node to save
   * @param writer Writer where to save XML
   * @param options can be used for disabling formatting (indenting)
   * @throws XmlException if XML document cannot be saved
   */
  public static void saveXml(Node node, Writer writer, XmlSaveOptions options) throws XmlException {
    try {
      saveXml(node, new StreamResult(writer), options);
      writer.flush();
    } catch (IOException e) {
      throw new XmlException(e);
    }
  }

  /**
   * Returns string representation of specified XML document or element.
   * @param node XML document or element
   * @return indented XML
   */
  public static String toXmlString(Node node) {
    final StringWriter stringWriter = new StringWriter();
    try {
      saveXml(node, stringWriter);
    } catch (XmlException e) {
      throw new XmlRuntimeException(e);
    }
    return stringWriter.toString();
  }

  private static void saveXml(Node node, StreamResult streamResult, XmlSaveOptions options) throws XmlException {
    try {
      TransformerFactory factory = TransformerFactory.newInstance();
      if (options == null || ! options.isDisableFormatting()) {
        try {
          // This works only with Writers
          factory.setAttribute("indent-number", 2);
        } catch (IllegalArgumentException e) {
          // ignore
        }
      }

      Transformer transformer = factory.newTransformer();
      transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
      if (!(node instanceof Document)) {
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      }
      if (options == null || ! options.isDisableFormatting()) {
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      }

      DOMSource domSource = new DOMSource(node);
      
      transformer.transform(domSource, streamResult);
      
    } catch (TransformerConfigurationException e) {
      throw new XmlRuntimeException(e);
    } catch (TransformerException e) {
      throw new XmlException(e);
    }
  }

}
