import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RoundActionButtonComponent, RoundButtonTooltipDirective } from './round-action-button/round-action-button.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {  OverlayModule } from '@angular/cdk/overlay';

@NgModule({
  imports: [CommonModule, MatButtonModule, MatIconModule, OverlayModule],
  declarations: [
    RoundActionButtonComponent,
    RoundButtonTooltipDirective
  ],
  exports: [
    RoundActionButtonComponent
  ]
})
export class SharedUiRoundButtonModule {}
