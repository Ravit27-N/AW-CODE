import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  HtmlPrewviewComponent,
  IframelyEmbedComponent,
} from './iframely-embed.component';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';

@NgModule({
  imports: [
    CommonModule,
    SharedPipesModule,
    SharedCommonTypoModule
  ],
  declarations: [
    IframelyEmbedComponent,
    HtmlPrewviewComponent
  ],
  exports: [IframelyEmbedComponent]
})
export class SharedUiIframelyEmbedModule {}
