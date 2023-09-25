import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureMailConfigListComponent } from './feature-mail-config-list/feature-mail-config-list.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../../shared';
import { FeatureMailConfigRoutingModule } from './feature-mail-config-routing.module';
import {FormsModule} from "@angular/forms";

@NgModule({
  declarations: [FeatureMailConfigListComponent],
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatSlideToggleModule,
    MatSortModule,
    MatTableModule,
    RouterModule,
    SharedModule,
    FeatureMailConfigRoutingModule,
    FormsModule,
  ],
})
export class FeatureMailConfigModule {}
