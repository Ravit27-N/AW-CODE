import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgressionCircleComponent } from './progression-circle/progression-circle.component';
import { CircleProgressOptionsInterface, NgCircleProgressModule } from 'ng-circle-progress';
import { ArchProgressionComponent } from './arch-progression/arch-progression.component';
import { NgxGaugeModule } from 'ngx-gauge';


// default progress bar config
const progressConfig: CircleProgressOptionsInterface = {
  animationDuration: 300,
  showTitle: true,
  showUnits: true,
  showSubtitle: true,
  showInnerStroke: true,
  responsive: false,
  radius: 60,
  backgroundPadding: 0,
  outerStrokeWidth: 9,
  innerStrokeColor: '#ECECEC',
  innerStrokeWidth: 9,
  space: -9,
  toFixed: 2,
  titleFontSize: '26',
  subtitleFontSize: '16',
  titleFontWeight: '700',
  unitsFontSize: '26',
  maxPercent: 100,
  outerStrokeLinecap: 'square',
  startFromZero: false,
  showZeroOuterStroke: true
}

@NgModule({
  imports: [CommonModule,
    NgCircleProgressModule.forRoot(progressConfig),
    NgxGaugeModule
  ],
  exports: [ProgressionCircleComponent, ArchProgressionComponent],
  declarations: [
    ProgressionCircleComponent,
    ArchProgressionComponent
  ],
})
export class SharedUiProgressionCircleModule { }
