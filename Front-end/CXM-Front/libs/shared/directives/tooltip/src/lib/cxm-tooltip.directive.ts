import {
  Directive,
  ElementRef,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Renderer2,
  SimpleChanges,
  TemplateRef
} from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';
import { Overlay, OverlayPositionBuilder, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { CxmCustomTooltipComponent } from './cxm-custom-tooltip.component';
import { take } from 'rxjs/operators';

export declare type InputMode = 'normal' | 'textarea';

@Directive({
  selector: '[cxmSmartflowCxmTooltip]',
  providers: [MatTooltip]
})
export class CxmTooltipDirective implements OnChanges, OnDestroy, OnInit {

  // Tooltip style properties.
  @Input() tooltipText: string;
  @Input() inputMode?: InputMode = 'normal';
  @Input() showTooltip?: boolean | null;
  @Input() showTooltipWhenValueChange?: boolean | null;
  @Input() isRemoveBorder?: boolean;

  // Element style properties.
  @Input() showBorderEl?: boolean | null = true;
  @Input() showBackgroundEl?: boolean | null = true;

  // Css properties.
  @Input() normalBorderColorEl?: string = '0.5px solid #8194b4';
  @Input() errorBorderColorEl?: string = '0.5px solid red';

  @Input() normalBackgroundColorEl?: string = '#FFFFFF';
  @Input() errorBackgroundColorEl?: string = '#F7EBEB';
  @Input() X: number|undefined = undefined;
  @Input() Y: number|undefined = undefined;

  //If this is specified then specified template will be rendered in the tooltip
  @Input() contentTemplate: TemplateRef<any>;
  private _overlayRef: OverlayRef;
  private _tooltipInstance: any;
  // private _mouseInTooltip: boolean;

  constructor(private tooltip: MatTooltip, private el: ElementRef, private render: Renderer2,
              private _overlay: Overlay,
              private _overlayPositionBuilder: OverlayPositionBuilder,
              private _elementRef: ElementRef,
              private _r2: Renderer2) {


  }
  ngOnInit(): void {
    this.setup();
  }


  setup() {
    const positionStrategy = this._overlayPositionBuilder
    .flexibleConnectedTo(this._elementRef)
    .withPositions([
      {
        originX: 'start',
        originY: 'bottom',
        overlayX: 'start',
        overlayY: 'top',
        offsetY: +5,
        offsetX: this.X ? this.X : -5
      }
    ]);

    this._overlayRef = this._overlay.create({ positionStrategy });
  }

  ngOnDestroy(): void {
    this.hideMatTooltip()
  }

  @HostListener('mouseenter', ['$event']) mouseover(e: MouseEvent) {
    if (this.showTooltip) {
      this.showMatTooltip();
      if(this.showBackgroundEl){
        this.showBackgroundElementStyle();
      }
    }
  }

  @HostListener('mouseleave', ['$event']) mouseleave(e: MouseEvent) {
    this.hideMatTooltip();
    if(this.showBackgroundEl){
      this.hideBackgroundElementStyle();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.showTooltip) {
      if(this.showBorderEl){
        this.showBorderElementStyle();
      }
      if(this.showTooltipWhenValueChange){
        this.showMatTooltip();
      }
    } else {
      this.hideMatTooltip();
      this.hideBorderElementStyle();
      if(this.showBackgroundEl){
        this.hideBackgroundElementStyle();
      }
    }
  }

  showMatTooltip() {
    if (this._overlayRef && !this._overlayRef.hasAttached()) {
      //set tooltip instance
      this._tooltipInstance = this._overlayRef.attach(
        new ComponentPortal(CxmCustomTooltipComponent)
      ).instance;

      //set CustomToolTipComponent content/inputs
      this._tooltipInstance.text = this.tooltipText;
      this._tooltipInstance.contentTemplate = this.contentTemplate;

      //render tooltip
      this._tooltipInstance?.show(0);

      //sub to detach after hide anitmation is complete
      this._tooltipInstance
        .afterHidden()
        .pipe(take(1))
        .subscribe(() => {
          this._overlayRef.detach();
        });
    }
  }

  hideMatTooltip() {
    this._tooltipInstance?._onHide.next();
  }

  showBorderElementStyle() {
    const nativeEl = this.el.nativeElement;
    if(!this.isRemoveBorder) {
      this.render.setStyle(nativeEl, 'border', this.errorBorderColorEl);
    }
  }

  hideBorderElementStyle() {
    const nativeEl = this.el.nativeElement;
    if(!this.isRemoveBorder) {
      this.render.setStyle(nativeEl, 'border', this.normalBorderColorEl);
    }
  }

  showBackgroundElementStyle() {
    const nativeEl = this.el.nativeElement;
    if(!this.isRemoveBorder) {
      this.render.setStyle(nativeEl, 'background-color', this.errorBackgroundColorEl);
      this.render.setStyle(nativeEl, 'color', 'black');
    }
  }

  hideBackgroundElementStyle() {
    const nativeEl = this.el.nativeElement;
    this.render.setStyle(nativeEl, 'background-color', this.normalBackgroundColorEl);
  }
}
