import { MaterialModule } from '../../material';
import { SharedModule } from '../../shared';
import { RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DefaultComponent } from './default.component';
import { QuillModule } from 'ngx-quill';


@NgModule({
  declarations: [
    DefaultComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    MaterialModule,
    QuillModule.forRoot()
  ]
})
export class DefaultModule { }
