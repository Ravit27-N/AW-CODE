import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImportUserCsvDialogComponent } from './import-user-csv-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { ImportUserCsvDialogService } from './import-user-csv-dialog.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslateModule } from '@ngx-translate/core';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';

@NgModule({
  imports: [
    CommonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TranslateModule,
    SharedPipesModule,
    SharedUiButtonModule,
  ],
  declarations: [ImportUserCsvDialogComponent],
  exports: [ImportUserCsvDialogComponent],
  providers: [ImportUserCsvDialogService],
})
export class UserUiImportUserCsvDialogModule {}
