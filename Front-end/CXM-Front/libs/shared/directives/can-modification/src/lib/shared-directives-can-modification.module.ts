import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CanModificationDirective } from './can-modification.directive';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';

@NgModule({
  imports: [
    CommonModule,
    SharedDataAccessServicesModule
  ],
  declarations: [
    CanModificationDirective
  ],
  exports: [
    CanModificationDirective
  ],
  providers: [
    CanModificationDirective
  ]
})
export class SharedDirectivesCanModificationModule {}
