import { Directive, ElementRef, Renderer2 } from '@angular/core';

@Directive({
  selector: '[cxmSmartflowInput]',
})
export class InputDirective {
  constructor(private el: ElementRef, private render: Renderer2) {}

  private buildStyleInput() {
    this.render.setStyle(this.el.nativeElement, 'fontSize', '14px');
    this.render.setStyle(this.el.nativeElement, 'padding', '8px 15px');
    this.render.setStyle(this.el.nativeElement, 'width', '100%');
    this.render.setStyle(
      this.el.nativeElement,
      'border',
      '1px solid rgb(228,228,228,.8)'
    );
    this.render.setStyle(this.el.nativeElement, 'borderRadius', '5px');
    this.render.setStyle(
      this.el.nativeElement,
      'boxShadow',
      'rgba(0, 30, 32, 0.5)'
    );
    this.render.setStyle(this.el.nativeElement, 'boxSizing', 'border-box');
    this.render.setStyle(this.el.nativeElement, 'border', '0px');
    this.render.setStyle(this.el.nativeElement, 'outline', 'none 0px');
  }

  public ngAfterViewInit(): void {
    this.buildStyleInput();
  }

}
