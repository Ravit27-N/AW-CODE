import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthGuard} from '@cxm-smartflow/auth/data-access';
import { DashboardComponent } from './dashboard.component';
import { RouterModule, Routes } from '@angular/router';
import { DashboarduidashboardCardModule } from '@cxm-smartflow/dashboard/ui/dashboard-card';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { DashboardDataAccessModule } from '@cxm-smartflow/dashboard/data-access';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';

import { CalendarContentComponent } from './calendar-content/calendar-content.component';
import { CalendarDateSelectorComponent } from './calendar-date-selector/calendar-date-selector.component'
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent
  }
]

@NgModule({
  imports: [CommonModule,
    RouterModule.forChild(routes),
    DashboarduidashboardCardModule,
    SharedCommonTypoModule,
    SharedTranslateModule.forRoot(),
    DashboardDataAccessModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedPipesModule,
    SharedDirectivesCanVisibilityModule,
    AuthDataAccessModule.forRoot()
  ],
  declarations: [
    DashboardComponent,
    CalendarContentComponent,
    CalendarDateSelectorComponent
  ],
})
export class DashboardFeatureModule { }
