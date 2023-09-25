import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CanAccessDirective } from './can-access-directive';

@NgModule({
  imports: [CommonModule],
  declarations: [CanAccessDirective],
  exports: [CanAccessDirective],
  providers: [CanAccessDirective]
})
export class SharedDirectivesCanAccessModule {}
