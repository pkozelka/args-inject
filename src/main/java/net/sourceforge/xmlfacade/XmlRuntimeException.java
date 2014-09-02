
package net.sourceforge.xmlfacade;


public class XmlRuntimeException extends RuntimeException {
  
  static final long serialVersionUID = 1;

  public XmlRuntimeException() {
    super();
  }

  public XmlRuntimeException(String message) {
    super(message);
  }

  public XmlRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlRuntimeException(Throwable cause) {
    super(cause);
  }

}
