import {
  ComponentRef,
  Directive,
  ElementRef,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import {
  Overlay,
  OverlayPositionBuilder,
  OverlayRef,
} from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { InfoTooltipComponent } from './info-tooltip.component';
import { ConnectedPosition } from '@angular/cdk/overlay/position/flexible-connected-position-strategy';
import { DomSanitizer } from '@angular/platform-browser';

/**
 * Show info tooltip.
 *
 * This directive can set
 * - tooltip message
 * - tooltip position.
 *
 * @author Chamrong THOR
 * @since 12/2/2022
 */
@Directive({
  selector: '[cxmSmartflowInfoTooltip]',
})
export class InfoTooltipDirective implements OnInit, OnDestroy {
  /** Set tooltip message **/
  @Input() tooltipMessage = '';
  /** Adjust tooltip position **/
  @Input() tooltipPosition: 'top' | 'left' | 'bottom' | 'right' = 'top';
  @Input() width = '';
  @Input() showLoader = false;

  /**
   * Reference to an overlay that has been created with the Overlay service.
   * Used to manipulate or dispose of said overlay.
   */
  private _overlayRef: OverlayRef;

  /**
   * Constructor
   */
  constructor(
    private _overlay: Overlay,
    private _overlayPositionBuilder: OverlayPositionBuilder,
    private _elementRef: ElementRef,
    private _sanitizer: DomSanitizer
  ) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    // Define an overlay position.
    const positionStrategy = this._overlayPositionBuilder
      .flexibleConnectedTo(this._elementRef)
      .withPositions(this._position);

    // Creates an overlay on the element reference.
    this._overlayRef = this._overlay.create({ positionStrategy });
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    // Cleans up the overlay from the DOM.
    this._overlayRef.dispose();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Event listener
  // -----------------------------------------------------------------------------------------------------

  /**
   * Mouse enter
   */
  @HostListener('mouseenter') onMouseEnter() {
    this._showTooltip();
  }

  /**
   * Mouse leave
   */
  @HostListener('mouseleave') onMouseLeave() {
    this._hideTooltip();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Accessor Modifier
  // -----------------------------------------------------------------------------------------------------

  /**
   * Get the position of tooltip to display
   */
  private get _position(): ConnectedPosition[] {
    switch (this.tooltipPosition) {
      case 'top': {
        return [
          {
            originX: 'start',
            originY: 'top',
            overlayX: 'center',
            overlayY: 'bottom',
            offsetY: -15,
            offsetX: 10,
          },
        ];
      }

      case 'right': {
        return [
          {
            originX: 'end',
            originY: 'top',
            overlayX: 'start',
            overlayY: 'center',
            offsetY: 8,
            offsetX: 13,
          },
        ];
      }

      case 'bottom': {
        return [
          {
            originX: 'start',
            originY: 'top',
            overlayX: 'center',
            overlayY: 'top',
            offsetY: 45,
            offsetX: 10,
          },
        ];
      }
      case 'left': {
        return [
          {
            originX: 'end',
            originY: 'center',
            overlayX: 'end',
            overlayY: 'center',
            offsetY: 0,
            offsetX: -35,
          },
        ];
      }
    }
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Show the tooltip
   */
  private _showTooltip() {
    // Create tooltip portal.
    const tooltipPortal = new ComponentPortal(InfoTooltipComponent);

    // Attach tooltip portal to overlay.
    const tooltipRef: ComponentRef<InfoTooltipComponent> =
      this._overlayRef.attach(tooltipPortal);

    // Pass content to tooltip component instance.
    tooltipRef.instance.tooltipMessage = this._sanitizer.bypassSecurityTrustHtml(this.tooltipMessage);
    tooltipRef.instance.position = this.tooltipPosition;
    tooltipRef.instance.width = this.width? this.width : '200px';
    tooltipRef.instance.showLoader = this.showLoader;
  }

  /**
   * Hide the tooltip
   */
  private _hideTooltip() {
    // Detach the overlay.
    this._overlayRef.detach();
  }
}
