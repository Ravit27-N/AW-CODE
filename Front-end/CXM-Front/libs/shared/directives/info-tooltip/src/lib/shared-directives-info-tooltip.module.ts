import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InfoTooltipDirective } from './info-tooltip.directive';
import { InfoTooltipComponent } from './info-tooltip.component';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';

@NgModule({
  imports: [CommonModule, SharedUiSpinnerModule],
  declarations: [
    InfoTooltipDirective,
    InfoTooltipComponent
  ],
  exports: [InfoTooltipDirective]
})
export class SharedDirectivesInfoTooltipModule {}
