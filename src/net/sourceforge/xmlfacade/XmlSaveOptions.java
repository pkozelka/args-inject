
package net.sourceforge.xmlfacade;


/**
 * Represents options which can be used for adjusting how XML is saved.
 */
public class XmlSaveOptions {

  private boolean disableFormatting;

  public boolean isDisableFormatting() {
    return disableFormatting;
  }
  /**
   * If set to true saved XML will not contain additional formatting line breaks and spaces.
   */
  public void setDisableFormatting(boolean disableFormatting) {
    this.disableFormatting = disableFormatting;
  }

}
