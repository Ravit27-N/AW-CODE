import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  DateValidationDirective,
  NumberValidationDirective,
  StringValidationDirective,
} from './directory-feed-custom-validation';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  exports: [
    StringValidationDirective,
    DateValidationDirective,
    NumberValidationDirective,
  ],
  declarations: [
    StringValidationDirective,
    DateValidationDirective,
    NumberValidationDirective,
  ],
})
export class DirectoryFeedUtilModule {}
