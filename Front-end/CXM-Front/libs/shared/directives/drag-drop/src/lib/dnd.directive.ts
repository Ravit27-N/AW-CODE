import { Directive, EventEmitter, HostBinding, HostListener, Output } from '@angular/core';

@Directive({
  selector: '[cxmSmartflowDnd]'
})
export class DndDirective {

  @HostBinding('class.fileover') fileOver: boolean;
  @Output() fileDropped = new EventEmitter<FileList>();

  @HostListener('dragover', ['$event']) onDragOver(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    this.fileOver = true;
  }

  @HostListener('dragleave', ['$event']) public onDragLeave(event: Event) {
    event.preventDefault();
    event.stopPropagation();
    this.fileOver = false;
  }

  @HostListener('drop', ['$event'])
  public ondrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();

    this.fileOver = false;
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.fileDropped.emit(files);
    }
  }

}
