
package net.sourceforge.xmlfacade;


public class XmlException extends Exception {

  static final long serialVersionUID = 1;

  public XmlException() {
    super();
  }

  public XmlException(String message) {
    super(message);
  }

  public XmlException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlException(Throwable cause) {
    super(cause);
  }

}
