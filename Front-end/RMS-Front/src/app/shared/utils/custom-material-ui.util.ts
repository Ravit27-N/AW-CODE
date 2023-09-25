export const overlayStyle = {
  zIndex999: 'z-index-999',
  zIndex1000: 'z-index-1000',
};

export const materialCssClass = {
  cdkOverlayContainer: 'cdk-overlay-container',
};

export class CustomMaterialUiUtil {
  /**
   * Decrease z-index cdk overlay container class) to 999 of material overlay.
   */
  public static decreaseCdkOverlayContainerZIndex() {
    const bodyElement = document.body;
    if (bodyElement) {
      const overlay = bodyElement.getElementsByClassName(
        materialCssClass.cdkOverlayContainer,
      );
      const htmlCollections = Array.prototype.slice.call(overlay);

      htmlCollections?.forEach((element: Element) => {
        // Add new custom class.
        element?.classList?.add(overlayStyle.zIndex999);
      });
    }
  }

  /**
   * Increase z-index (cdk overlay container class) to 1000 of material overlay.
   */
  public static increaseCdkOverlayContainerZIndex() {
    const bodyElement = document.body;
    if (bodyElement) {
      const overlay = bodyElement.getElementsByClassName(
        materialCssClass.cdkOverlayContainer,
      );
      const htmlCollections = Array.prototype.slice.call(overlay);

      htmlCollections?.forEach((element: Element) => {
        // Remove old custom class.
        element?.classList?.remove(overlayStyle.zIndex999);
      });
    }
  }
}
