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
  ConnectedPosition,
  Overlay,
  OverlayPositionBuilder,
  OverlayRef,
} from '@angular/cdk/overlay';
import { DomSanitizer } from '@angular/platform-browser';
import { ComponentPortal } from '@angular/cdk/portal';
import { AwTooltipInfoComponent } from './aw-tooltip-info.component';

@Directive({
  selector: '[appAwTooltipInfo]',
})
export class AwTooltipInfoDirective implements OnInit, OnDestroy {
  /** Set tooltip message **/
  @Input() tooltipMessage = '';
  /** Adjust tooltip position **/
  @Input() tooltipPosition: 'top' | 'left' | 'bottom' | 'right' = 'top';
  @Input() width = '';
  @Input() showLoader = false;
  @Input() xLocation = 0;
  @Input() yLocation = 0;

  /**
   * Reference to an overlay that has been created with the Overlay service.
   * Used to manipulate or dispose of said overlay.
   */
  private overlayRef: OverlayRef;

  /**
   * Constructor
   */
  constructor(
    private overlay: Overlay,
    private overlayPositionBuilder: OverlayPositionBuilder,
    private elementRef: ElementRef,
    private sanitizer: DomSanitizer,
  ) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * Get the position of tooltip to display
   */
  private get position(): ConnectedPosition[] {
    switch (this.tooltipPosition) {
      case 'top': {
        return [
          {
            originX: 'start',
            originY: 'top',
            overlayX: 'center',
            overlayY: 'bottom',
            offsetY: this.yLocation? this.yLocation : -15,
            offsetX: this.xLocation? this.xLocation : 10,
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
            offsetY: this.yLocation? this.yLocation : 8,
            offsetX: this.xLocation? this.xLocation : 13,
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
            offsetY: this.yLocation? this.yLocation : 45,
            offsetX: this.xLocation? this.xLocation : 10,
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
            offsetY: this.yLocation? this.yLocation : 0,
            offsetX: this.xLocation? this.xLocation : -35,
          },
        ];
      }
    }
  }

  /**
   * On init
   */
  ngOnInit(): void {
    // Define an overlay position.
    const positionStrategy = this.overlayPositionBuilder
      .flexibleConnectedTo(this.elementRef)
      .withPositions(this.position);

    // Creates an overlay on the element reference.
    this.overlayRef = this.overlay.create({ positionStrategy });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Event listener
  // -----------------------------------------------------------------------------------------------------

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    // Cleans up the overlay from the DOM.
    this.overlayRef.dispose();
  }

  /**
   * Mouse enter
   */
  @HostListener('mouseenter') onMouseEnter() {
    this.showTooltip();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Accessor Modifier
  // -----------------------------------------------------------------------------------------------------

  /**
   * Mouse leave
   */
  @HostListener('mouseleave') onMouseLeave() {
    this.hideTooltip();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Show the tooltip
   */
  private showTooltip() {
    // Create tooltip portal.
    const tooltipPortal = new ComponentPortal(AwTooltipInfoComponent);

    // Attach tooltip portal to overlay.
    const tooltipRef: ComponentRef<AwTooltipInfoComponent> =
      this.overlayRef.attach(tooltipPortal);

    // Pass content to tooltip component instance.
    tooltipRef.instance.tooltipMessage = this.sanitizer.bypassSecurityTrustHtml(
      this.tooltipMessage,
    );
    tooltipRef.instance.position = this.tooltipPosition;
    tooltipRef.instance.width = this.width ? this.width : '200px';
    tooltipRef.instance.showLoader = this.showLoader;
  }

  /**
   * Hide the tooltip
   */
  private hideTooltip() {
    // Detach the overlay.
    this.overlayRef.detach();
  }
}
