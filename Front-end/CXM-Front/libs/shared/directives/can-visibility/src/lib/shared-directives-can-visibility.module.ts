import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CanVisibilityDirective } from './can-visibility.directive';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { AuthForDirective } from './auth-for.directive';

@NgModule({
  imports: [
    CommonModule,
    SharedDataAccessServicesModule,
  ],
  declarations: [
    CanVisibilityDirective,
    AuthForDirective
  ],
  exports: [
    CanVisibilityDirective,
    AuthForDirective
  ],
  providers: [
    CanVisibilityDirective
  ]
})
export class SharedDirectivesCanVisibilityModule {}
