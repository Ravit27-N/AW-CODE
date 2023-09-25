import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CsvUploaderFragementComponent } from './csv-uploader-fragement/csv-uploader-fragement.component';
import { SharedDirectivesDragDropModule } from '@cxm-smartflow/shared/directives/drag-drop';
import { CsvUploadComponentStore } from './csv-upload-component.store';
import { CsvTableComponent } from './csv-table/csv-table.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { CdkTableModule } from '@angular/cdk/table';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesDragDropModule,
    MaterialModule,
    CdkTableModule,
  ],
  declarations: [CsvUploaderFragementComponent, CsvTableComponent],
  exports: [CsvUploaderFragementComponent, CsvTableComponent],
  providers: [CsvUploadComponentStore],
})
export class FollowMyCampaignUiFeatureCsvUploadModule {}
