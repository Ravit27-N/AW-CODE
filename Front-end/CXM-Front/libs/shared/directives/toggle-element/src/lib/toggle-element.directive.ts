import { Directive, ElementRef, EventEmitter, HostListener, Output } from '@angular/core';

@Directive({
  selector: '[cxmSmartflowToggleElement]'
})
export class ToggleElementDirective {

  @Output() inSideElementEvent = new EventEmitter<boolean>();
  @Output() outSideElementEvent = new EventEmitter<boolean>();

  constructor(private eRef: ElementRef) {
  }

  @HostListener('document:click', ['$event'])
  documentClick(event: any) {
    if (this.eRef.nativeElement.contains(event.target)) {
      this.inSideElementEvent.emit(true);
    } else {
      this.outSideElementEvent.emit(true);
    }
  }
}
