import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TemplateModel } from '@cxm-smartflow/shared/data-access/model';
import { Observable } from 'rxjs';
import { PreviewEmailTemplateComponent } from './preview-email-template.component';

@Injectable({
  providedIn: 'root'
})
export class PreViewEmailTemplateService {

  constructor(private matDialog: MatDialog, private dialogRef: MatDialogRef<PreviewEmailTemplateComponent>) { }

  /**
   * Method used to show preview email template.
   * @param emailTemplate - object of {@param HTMLSource @link TemplateModel}
   * @return value of {@link Observable}
   */
  previewEmailTemplate(emailTemplate: TemplateModel, HTMLSource?: string): Observable<any>{
     this.dialogRef = this.matDialog.open(PreviewEmailTemplateComponent,
      {
        data: { emailTemplate, HTMLSource },
        width: '100%',
        maxWidth: '1019px',
        height: '615px',
        panelClass: 'common-border-dialog',
      });
     return this.dialogRef.afterClosed();
  }
}
