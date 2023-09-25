import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TooltipDirective } from './tooltip.directive';
import { OverlayModule } from '@angular/cdk/overlay';
import { MatIconModule } from '@angular/material/icon';
import { TooltipComponent, RichTooltipComponent } from './tooltip.component';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { CxmTooltipDirective } from './cxm-tooltip.directive';
import { CxmCustomTooltipComponent } from './cxm-custom-tooltip.component';

@NgModule({
  imports: [CommonModule, OverlayModule, MatIconModule, SharedPipesModule],
  declarations: [
    TooltipDirective,
    TooltipComponent,
    RichTooltipComponent,
    CxmTooltipDirective,
    CxmCustomTooltipComponent
  ],
  exports: [
    TooltipDirective,
    CxmTooltipDirective
  ],
})
export class SharedDirectivesTooltipModule {}
