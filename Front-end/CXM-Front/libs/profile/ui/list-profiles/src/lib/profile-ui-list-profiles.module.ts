import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListProfilesComponent } from './list-profiles.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProfileUiListProfileTableModule } from '@cxm-smartflow/profile/ui/list-profile-table';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedUiRoundButtonModule } from '@cxm-smartflow/shared/ui/round-button';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { ProfileUiProfileHeaderModule } from '@cxm-smartflow/profile/ui/profile-header';
import { SharedUiFilterBoxModule } from '@cxm-smartflow/shared/ui/filter-box';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    ProfileUiListProfileTableModule,
    MaterialModule,
    SharedPipesModule,
    SharedUiRoundButtonModule,
    SharedDirectivesCanVisibilityModule,
    AuthDataAccessModule.forRoot(),
    SharedCommonTypoModule,
    ProfileUiProfileHeaderModule,
    SharedTranslateModule.forRoot(),
    RouterModule.forChild([
      {
        path: '',
        component: ListProfilesComponent,
      },
    ]),
    SharedUiFilterBoxModule,
    SharedDirectivesTooltipModule,
    SharedUiSearchBoxModule,
  ],
  declarations: [ListProfilesComponent],
  exports: [ListProfilesComponent],
})
export class ProfileUiListProfilesModule {}
