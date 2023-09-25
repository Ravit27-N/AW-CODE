import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InputDirective } from './input.directive';
import { InputComponent } from './input/input.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [InputComponent, InputDirective],
  exports: [InputComponent, InputDirective],

})
export class SharedUiInputModule { }
