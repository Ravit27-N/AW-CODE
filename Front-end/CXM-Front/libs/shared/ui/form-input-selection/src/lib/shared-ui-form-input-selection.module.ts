import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputSelectionComponent } from './input-selection/input-selection.component';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { MultiInputSelectionComponent } from './multi-input-selection/multi-input-selection.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';

@NgModule({
  imports: [
    CommonModule,
    MatIconModule,
    MatMenuModule,
    SharedDirectivesTooltipModule,
    ReactiveFormsModule,
    TranslateModule,
    DragDropModule,
  ],
  declarations: [InputSelectionComponent, MultiInputSelectionComponent],
  exports: [InputSelectionComponent, MultiInputSelectionComponent],
})
export class SharedUiFormInputSelectionModule {}
