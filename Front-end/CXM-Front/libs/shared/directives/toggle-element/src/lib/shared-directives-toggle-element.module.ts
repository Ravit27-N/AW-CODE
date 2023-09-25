import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToggleElementDirective } from './toggle-element.directive';

@NgModule({
  imports: [CommonModule],
  declarations: [ToggleElementDirective],
  exports: [ToggleElementDirective]
})
export class SharedDirectivesToggleElementModule {}
