import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {InformationModel} from "./information.model";
import {SnackBarService} from "@cxm-smartflow/shared/data-access/services";
import {TranslateService} from "@ngx-translate/core";
import {take} from "rxjs/operators";

@Component({
  selector: 'cxm-smartflow-information-popup',
  templateUrl: './information-pop-up.component.html',
  styleUrls: ['./information-pop-up.component.scss']
})
export class InformationPopUpComponent {

  constructor(
    public informationDialogRef: MatDialogRef<InformationPopUpComponent>,
    private _snackbar: SnackBarService,
    private _translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public informationData: InformationModel
  ) { }

  close(): void{
    this.informationDialogRef.close(false);
  }

  copyClipboard(text: any): void {
    this.copy(text);
    this._translate.get('cxm_setting.copyClipboardSuccess')
      .pipe(take(1)).subscribe( messages => {
        this._snackbar.openCustomSnackbar({ message: messages, type: 'success', icon: 'close' });
    })
  }

  private copy(text: any) {
    const invisibleTextArea = document.createElement('textarea');
    invisibleTextArea.value = text;
    invisibleTextArea.setAttribute('readonly', '');
    invisibleTextArea.style.position = 'absolute';
    invisibleTextArea.style.left = '-9999px';
    document.body.appendChild(invisibleTextArea);
    invisibleTextArea.select();
    document.execCommand('copy');
    document.body.removeChild(invisibleTextArea);
  }
}
