import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {TitleCaseDirective} from "./title-case.directive";

@NgModule({
  imports: [CommonModule],
  declarations: [TitleCaseDirective],
  exports: [TitleCaseDirective],
  providers: [TitleCaseDirective]
})
export class SharedDirectivesTitleCaseModule {}
