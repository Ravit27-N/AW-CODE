import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CXMDateFormatPipe } from './date';
import { ToNumberPipe } from './to-number.pipe';
import { NotAvailablePipe } from './not-available.pipe';
import { PreviewPipe } from './preview.pipe';
import { SafeHtmlPipe } from './safeHtml.pipe';
import { ColorPipe } from './cxm-color.pipe';
import { FileSizePipe } from './file-size-pipe';
import { TranslateModule } from '@ngx-translate/core';
import { PercentFormatPipe } from './percent-format.pipe';

@NgModule({
  imports: [CommonModule, TranslateModule],
  exports: [
    CXMDateFormatPipe,
    ToNumberPipe,
    NotAvailablePipe,
    PreviewPipe,
    SafeHtmlPipe,
    ColorPipe,
    FileSizePipe,
    PercentFormatPipe,
  ],
  declarations: [
    CXMDateFormatPipe,
    ToNumberPipe,
    NotAvailablePipe,
    PreviewPipe,
    SafeHtmlPipe,
    ColorPipe,
    FileSizePipe,
    PercentFormatPipe,
  ],
})
export class SharedPipesModule {}
