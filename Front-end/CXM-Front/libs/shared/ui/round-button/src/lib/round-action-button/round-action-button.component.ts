import { Component, ChangeDetectionStrategy, Input, Output, EventEmitter, HostListener, OnInit, Directive, ElementRef, ComponentRef, OnDestroy } from '@angular/core';
import { Overlay, OverlayPositionBuilder, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { ReplaySubject, Subscription } from 'rxjs';
import { distinctUntilChanged, debounceTime } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-round-action-button',
  templateUrl: './round-action-button.component.html',
  styleUrls: ['./round-action-button.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RoundActionButtonComponent {

  @Input() hint: string;
  @Input() disabled: boolean;
  @Output() onclick = new EventEmitter<MouseEvent>();

}



@Directive({
  selector: '[cxmSmartflowRoundButtonTooltip]'
})
export class RoundButtonTooltipDirective implements OnInit, OnDestroy {
  @Input() hint: string;

  private overlayRef: OverlayRef;
  private replay$ = new ReplaySubject(1);
  private subscription: Subscription;

  ngOnDestroy(): void {
    this.overlayRef.dispose();

    if(this.subscription) this.subscription.unsubscribe();
  }

  ngOnInit(): void {
    const positionStrategy = this.overlayPositionBuilder
      .flexibleConnectedTo(this.elementRef)
      .withPositions([{
        originX: 'start',
        originY: 'bottom',
        overlayX: 'start',
        overlayY: 'bottom',
      }]);


    this.overlayRef = this.overlay.create({ positionStrategy });

    this.subscription =
    this.replay$.pipe(distinctUntilChanged(), debounceTime(500)).subscribe(v => {
      if(v) {
        this.overlayRef.detach();
        const tooltipPortal = new ComponentPortal(RoundActionButtonTooltipComponent)
        const tooltipRef: ComponentRef<RoundActionButtonTooltipComponent> = this.overlayRef.attach(tooltipPortal);
        tooltipRef.instance.hint = this.hint;
      } else {
        this.overlayRef.detach();
      }
    })
  }

  @HostListener('mouseleave')
  hide() {

    this.replay$.next(false);
  }

  @HostListener('mouseover')
  show() {
    this.replay$.next(true);
  }


  constructor(private overlay: Overlay, private overlayPositionBuilder: OverlayPositionBuilder, private elementRef: ElementRef) { }
}



@Component({
  selector: 'cxm-smartflow-round-action-button-tooltip',
  styleUrls: ['tooltips.component.style.scss'],
  template: `
    <div class="tooltipable-content">
      <p class="tooltipable px-4 py-1">{{hint}}</p>
    </div>
  `
})

class RoundActionButtonTooltipComponent {
  @Input() hint: string;
}
