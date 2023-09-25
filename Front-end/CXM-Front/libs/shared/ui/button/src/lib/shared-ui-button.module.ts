import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonComponent } from './button/button.component';
import { LinkButtonComponent } from './link-button/link-button.component';
import {RouterModule} from '@angular/router';
import { RichButtonComponent } from './rich-button/rich-button.component';
@NgModule({
  imports: [CommonModule,RouterModule],
  declarations: [
    ButtonComponent,
    LinkButtonComponent,
    RichButtonComponent
  ],
  exports: [ButtonComponent,LinkButtonComponent, RichButtonComponent]
})
export class SharedUiButtonModule { }
