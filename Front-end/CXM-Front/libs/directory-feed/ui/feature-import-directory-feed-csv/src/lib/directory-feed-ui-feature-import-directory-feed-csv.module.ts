import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureImportDirectoryFeedCsvComponent } from './feature-import-directory-feed-csv.component';
import { ImportDirectoryFeedCsvService } from './import-directory-feed-csv.service';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SharedUiUploadingFragmentModule } from '@cxm-smartflow/shared/ui/uploading-fragment';
import { TranslateModule } from '@ngx-translate/core';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { DirectoryFeedDataAccessModule } from '@cxm-smartflow/directory-feed/data-access';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {SharedPipesModule} from "@cxm-smartflow/shared/pipes";

@NgModule({
  imports: [
    CommonModule,
    MatIconModule,
    ReactiveFormsModule,
    SharedDirectivesTooltipModule,
    SharedUiButtonModule,
    SharedUiFormInputSelectionModule,
    SharedUiUploadingFragmentModule,
    TranslateModule,
    SharedCommonTypoModule,
    SharedUiUploadingFragmentModule,
    DirectoryFeedDataAccessModule,
    MatCheckboxModule,
    SharedPipesModule,
  ],
  declarations: [FeatureImportDirectoryFeedCsvComponent],
  providers: [ImportDirectoryFeedCsvService],
})
export class DirectoryFeedUiFeatureImportDirectoryFeedCsvModule {}
