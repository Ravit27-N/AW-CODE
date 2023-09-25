import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DndDirective } from './dnd.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [
    DndDirective
  ],
  exports: [
    DndDirective
  ],
})
export class SharedDirectivesDragDropModule {}
