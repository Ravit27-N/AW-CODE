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
  TemplateRef,
} from '@angular/core';
import {
  Overlay,
  OverlayPositionBuilder,
  OverlayRef,
} from '@angular/cdk/overlay';
import { AwTooltipComponent } from './aw-tooltip.component';
import { MatTooltip } from '@angular/material/tooltip';
import { ComponentPortal } from '@angular/cdk/portal';
import { take } from 'rxjs/operators';

@Directive({
  selector: '[appAwTooltip]',
  providers: [MatTooltip]
})
export class AwTooltipDirective implements OnInit, OnChanges, OnDestroy {
  @Input() tooltipContent: string;
  @Input() tooltipMode: 'normal' | 'textarea' = 'normal';
  @Input() shouldShowTooltip: boolean | null = true;
  @Input() autoShowTooltip: boolean | null = false;
  @Input() removeBorder = false;
  @Input() shouldShowBorder = true;
  @Input() shouldShowBackground = true;
  @Input() defaultBorderColor = '0.5px solid #8194b4';
  @Input() errorBorderColor = '0.5px solid red';
  @Input() defaultBackgroundColor = '#FFFFFF';
  @Input() errorBackgroundColor = '#F7EBEB';
  @Input() tooltipPositionX: number | undefined = undefined;
  @Input() tooltipPositionY: number | undefined = undefined;
  @Input() tooltipTemplate: TemplateRef<any>;

  private overlayRef: OverlayRef;
  private tooltipInstance: AwTooltipComponent;

  constructor(
    private tooltip: MatTooltip,
    private el: ElementRef,
    private renderer: Renderer2,
    private overlay: Overlay,
    private overlayPositionBuilder: OverlayPositionBuilder,
  ) {}

  // eslint-disable-next-line @angular-eslint/contextual-lifecycle
  ngOnInit(): void {
    this.configureTooltipOverlay();
  }

  ngOnDestroy(): void {
    this.hideTooltip();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.shouldShowTooltip) {
      this.applyBorderStyle();
      if (this.autoShowTooltip) {
        this.displayTooltip();
      }
    } else {
      this.hideTooltip();
      this.resetBorderStyle();
      if (this.shouldShowBackground) {
        this.resetBackgroundStyle();
      }
    }
  }

  @HostListener('mouseenter', ['$event'])
  onMouseEnter(): void {
    if (this.shouldShowTooltip) {
      this.displayTooltip();
      if (this.shouldShowBackground) {
        this.applyBackgroundStyle();
      }
    }
  }

  @HostListener('mouseleave', ['$event'])
  onMouseLeave(): void {
    this.hideTooltip();
    if (this.shouldShowBackground) {
      this.resetBackgroundStyle();
    }
  }

  private configureTooltipOverlay(): void {
    const positionStrategy = this.overlayPositionBuilder
      .flexibleConnectedTo(this.el)
      .withPositions([
        {
          originX: 'start',
          originY: 'bottom',
          overlayX: 'start',
          overlayY: 'top',
          offsetY: 5,
          offsetX: this.tooltipPositionX ? this.tooltipPositionX : -5,
        },
      ]);

    this.overlayRef = this.overlay.create({ positionStrategy });
  }

  private displayTooltip(): void {
    if (this.overlayRef && !this.overlayRef.hasAttached()) {
      this.tooltipInstance = this.overlayRef.attach(
        new ComponentPortal(AwTooltipComponent),
      ).instance;
      this.tooltipInstance.text = this.tooltipContent;
      this.tooltipInstance.contentTemplate = this.tooltipTemplate;

      this.tooltipInstance?.show(0);

      this.tooltipInstance
        .afterHidden()
        .pipe(take(1))
        .subscribe(() => {
          this.overlayRef.detach();
        });
    }
  }

  private hideTooltip(): void {
    const m: any = this.tooltipInstance;
    m?._onHide.next();
  }

  private applyBorderStyle(): void {
    if (!this.removeBorder) {
      this.renderer.setStyle(
        this.el.nativeElement,
        'border',
        this.errorBorderColor,
      );
    }
  }

  private resetBorderStyle(): void {
    if (!this.removeBorder) {
      this.renderer.setStyle(
        this.el.nativeElement,
        'border',
        this.defaultBorderColor,
      );
    }
  }

  private applyBackgroundStyle(): void {
    if (!this.removeBorder) {
      const nativeEl = this.el.nativeElement;
      this.renderer.setStyle(
        nativeEl,
        'background-color',
        this.errorBackgroundColor,
      );
      this.renderer.setStyle(nativeEl, 'color', 'black');
    }
  }

  private resetBackgroundStyle(): void {
    const nativeEl = this.el.nativeElement;
    this.renderer.setStyle(
      nativeEl,
      'background-color',
      this.defaultBackgroundColor,
    );
  }
}
