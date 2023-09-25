import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLoadingComponent } from './router-loading.component';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@NgModule({
  imports: [CommonModule, MatProgressBarModule],
  declarations: [
    RouterLoadingComponent
  ],
  exports: [RouterLoadingComponent]
})
export class SharedUiRouterLoadingModule {
}
