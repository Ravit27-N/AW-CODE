import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-import-user-csv-dialog',
  templateUrl: './import-user-csv-dialog.component.html',
  styleUrls: ['./import-user-csv-dialog.component.scss'],
})
export class ImportUserCsvDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public config: { status: 'in_progress' | 'done'; content: any },
    private _sanitizer: DomSanitizer,
    private _translationService: TranslateService,
    private _dialogRef: MatDialogRef<ImportUserCsvDialogComponent>
  ) {}

  closeDialog(): void {
    this._dialogRef.close(false);
  }

  transform(value: string): SafeHtml {
    return this._sanitizer.bypassSecurityTrustHtml(value);
  }

  getTranslation(translationKey: string, replacer: string = '') {
    return this._translationService
      .get(translationKey)
      .pipe(map((item: string) => this._sanitizer.bypassSecurityTrustHtml(item.replace('${Variable}', replacer))));
  }



}
