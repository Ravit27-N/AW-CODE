import { InterviewTemplateModel } from '../../../../core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';

@Component({
  selector: 'app-dialogviewstatusinterview',
  templateUrl: './dialogviewstatusinterview.component.html',
  styleUrls: ['./dialogviewstatusinterview.component.css'],
})
export class DialogviewstatusinterviewComponent implements OnInit {
  active: string;
  constructor(
    public dialogRef: MatDialogRef<DialogviewstatusinterviewComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: InterviewTemplateModel,
  ) {}

  ngOnInit(): void {
    if (this.data.active === true) {
      this.active = 'Active';
    } else {
      this.active = 'Inactive';
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
