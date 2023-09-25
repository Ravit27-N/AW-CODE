import { ComponentRef, Directive, ElementRef, HostListener, Input, OnDestroy, OnInit } from '@angular/core';
import { Overlay, OverlayPositionBuilder, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { RichTooltipComponent, TooltipComponent } from './tooltip.component';
import { BehaviorSubject } from 'rxjs';

@Directive({
  selector: '[cxmSmartflowTooltip]',
  exportAs: 'cxmSmartflowTooltip'
})
export class TooltipDirective implements OnInit, OnDestroy {

  @Input() hint: string;
  @Input() mode: 'success' | 'error' | 'rich' = 'success';
  @Input() disabledMouseEvent: boolean;

  private overlayRef: OverlayRef;
  private isOpening: boolean;

  private hideToggleWhenOutsize = new BehaviorSubject(false);
  private isActive = new BehaviorSubject(false);

  public get getIsActive(){
    return this.isActive.value;
  }

  public set setHideToggleWhenOutsize(value: boolean){
    this.hideToggleWhenOutsize.next(value);
  }

  ngOnInit(): void {
    const positionStrategy = this.overlayPositionBuilder
      .flexibleConnectedTo(this.elementRef)
      .withPositions([{
        originX: 'start',
        originY: 'top',
        overlayX: 'start',
        overlayY: 'bottom',
        // offsetY: -10
        offsetY: this.mode === 'rich' ? -10 : 0 ,
        offsetX: this.mode === 'rich' ? -8 : 0
      }]);


    this.overlayRef = this.overlay.create({ positionStrategy });
  }


  @HostListener('mouseout')
  hide() {
    if(!this.disabledMouseEvent)
      this._hide();
  }

  @HostListener('mouseenter')
  show() {
    if(!this.disabledMouseEvent)
      this._show();
  }

  @HostListener('document:click', ['$event'])
  clickOut(event: any) {
    if (!this.elementRef.nativeElement?.contains(event?.target) && this.hideToggleWhenOutsize.value) {
      this.toggle(false);
      this.disabledMouseEvent = false;
    }
  }

  private _show() {
    this.isOpening = true;
    this.overlayRef.detach();
    let tooltipPortal: any;
    let tooltipRef: ComponentRef<any>;

    if(this.mode == 'rich') {
      tooltipPortal = new ComponentPortal(RichTooltipComponent)
      tooltipRef = this.overlayRef.attach(tooltipPortal);
    } else {
      tooltipPortal = new ComponentPortal(TooltipComponent)
      tooltipRef = this.overlayRef.attach(tooltipPortal);
    }

    tooltipRef.instance.text = this.hint;
    tooltipRef.instance.mode = this.mode;
  }

  private _hide() {
    this.isOpening = false;
    this.overlayRef?.detach();
  }

  toggle(active?: boolean | undefined) {
    if(active)
    {
      this._show();
    } else {
      this._hide();
    }
    this.isActive.next(active || false);
  }


  ngOnDestroy(): void {
    this.overlayRef.dispose();
  }

  constructor(private overlay: Overlay, private overlayPositionBuilder: OverlayPositionBuilder, private elementRef: ElementRef) { }

}
