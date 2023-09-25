import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureDirectoryFeedDetailsComponent } from './feature-directory-feed-details.component';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { MatDividerModule } from '@angular/material/divider';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { DirectoryFeedComplexFormComponent } from './directory-feed-complex-form/directory-feed-complex-form.component';
import { DirectoryFeedTableFormComponent } from './directory-feed-complex-form/directory-feed-table-form/directory-feed-table-form.component';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { CdkTableModule } from '@angular/cdk/table';
import { DirectoryFeedUtilModule } from '@cxm-smartflow/directory-feed/util';
import { DirectoryFeedSelectionComponent } from './directory-feed-complex-form/directory-feed-selection';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { DirectoryFeedSelectionServiceService } from './directory-feed-complex-form/directory-feed-selection';
import { MatIconModule } from '@angular/material/icon';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { DirectoryFeedUiFeatureImportDirectoryFeedCsvModule } from '@cxm-smartflow/directory-feed/ui/feature-import-directory-feed-csv';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { DirectoryFeedControl } from '@cxm-smartflow/directory-feed/data-access';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureDirectoryFeedDetailsComponent,
        canDeactivate: [DirectoryFeedControl],
      },
    ]),
    ReactiveFormsModule,
    SharedDirectivesTooltipModule,
    SharedUiButtonModule,
    SharedTranslateModule,
    MatDividerModule,
    NgDynamicBreadcrumbModule,
    DirectoryFeedUiFeatureImportDirectoryFeedCsvModule,
    SharedCommonTypoModule,
    SharedUiSearchBoxModule,
    MatTableModule,
    MatSortModule,
    CdkTableModule,
    FormsModule,
    DirectoryFeedUtilModule,
    MatSnackBarModule,
    MatIconModule,
    SharedUiSpinnerModule,
    MaterialModule,
  ],
  declarations: [
    FeatureDirectoryFeedDetailsComponent,
    DirectoryFeedComplexFormComponent,
    DirectoryFeedTableFormComponent,
    DirectoryFeedSelectionComponent,
  ],
  providers: [DirectoryFeedSelectionServiceService],
})
export class DirectoryFeedUiFeatureDirectoryFeedDetailsModule {}
