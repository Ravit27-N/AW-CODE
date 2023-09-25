import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureAddDirectoryFeedComponent } from './feature-add-directory-feed.component';
import { RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import {SharedUiButtonModule} from "@cxm-smartflow/shared/ui/button";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {SharedUiCxmDatetimePickerModule} from "@cxm-smartflow/shared/ui/cxm-datetime-picker";
import {ReactiveFormsModule} from "@angular/forms";
import {SharedDirectivesTooltipModule} from "@cxm-smartflow/shared/directives/tooltip";

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {path: '', component: FeatureAddDirectoryFeedComponent},
    ]),
    MatDividerModule,
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    TranslateModule,
    SharedUiButtonModule,
    MatIconModule,
    MatInputModule,
    MatDatepickerModule,
    SharedUiCxmDatetimePickerModule,
    ReactiveFormsModule,
    SharedDirectivesTooltipModule
  ],
  declarations: [FeatureAddDirectoryFeedComponent],
})
export class DirectoryFeedUiFeatureAddDirectoryFeedModule {}
