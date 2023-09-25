export const OverlayStyle = {
  zIndex999: 'z-index-999',
  zIndex1000: 'z-index-1000'
};

export const MaterialCssClass = {
  cdkOverlayContainer: 'cdk-overlay-container'
}

export class CustomAngularMaterialUtil {
  /**
   * Decrease z-index cdk overlay container class) to 999 of material overlay.
   */
  public static decrease_cdk_overlay_container_z_index() {
    const bodyElement = document.body;
    if (bodyElement) {
      const overlay = bodyElement.getElementsByClassName(MaterialCssClass.cdkOverlayContainer);
      const htmlCollections = Array.prototype.slice.call(overlay);

      htmlCollections?.forEach((element: Element) => {
        // Add new custom class.
        element?.classList?.add(OverlayStyle.zIndex999);
      });
    }
  }

  /**
   * Increase z-index (cdk overlay container class) to 1000 of material overlay.
   */
  public static increase_cdk_overlay_container_z_index() {
    const bodyElement = document.body;
    if (bodyElement) {
      const overlay = bodyElement.getElementsByClassName(MaterialCssClass.cdkOverlayContainer);
      const htmlCollections = Array.prototype.slice.call(overlay);

      htmlCollections?.forEach((element: Element) => {
        // Remove old custom class.
        element?.classList?.remove(OverlayStyle.zIndex999);
      });
    }
  }
}

