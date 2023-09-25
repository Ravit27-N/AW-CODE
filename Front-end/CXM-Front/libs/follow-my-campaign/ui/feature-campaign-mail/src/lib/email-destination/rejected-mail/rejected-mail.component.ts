import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface RejectedMail {
  totalRejected?: number;
  lineRejected?: string;
  value?: any;
}

@Component({
  selector: 'cxm-smartflow-rejected-mail',
  templateUrl: './rejected-mail.component.html',
  styleUrls: ['./rejected-mail.component.scss']
})
export class RejectedMailComponent {

  data: RejectedMail;
  constructor(public dialogRef: MatDialogRef<RejectedMailComponent>, @Inject(MAT_DIALOG_DATA) public dialogData: RejectedMail) {
    this.data = dialogData?.value;
  }
}
