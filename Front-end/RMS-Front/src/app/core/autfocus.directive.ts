import { AfterContentInit, Directive, ElementRef, Input } from '@angular/core';

@Directive({
  selector: '[appAutoFocus]',
})
export class AutofocusDirective implements AfterContentInit {
  @Input() public autofocu = true;

  constructor(private el: ElementRef) {}

  ngAfterContentInit(): void {
    setTimeout(() => {
      this.el.nativeElement.focus();
    }, 300);
  }
}
