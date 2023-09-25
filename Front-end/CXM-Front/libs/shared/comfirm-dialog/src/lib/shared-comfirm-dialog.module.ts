import { TranslateModule } from '@ngx-translate/core';
import { HttpClientModule } from '@angular/common/http';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { ComfirmDialogComponent } from './comfirm-dialog.component';
import { ComfirmDialogService } from './comfirm-dialog.service';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    SharedUiButtonModule,
    // For Translation
    HttpClientModule,
    TranslateModule,
  ],
  declarations: [ComfirmDialogComponent],
  providers: [ComfirmDialogService],
})
export class SharedComfirmDialogModule {}
