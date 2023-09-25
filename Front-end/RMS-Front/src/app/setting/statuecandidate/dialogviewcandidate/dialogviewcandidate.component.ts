import { StatusCandidateModel } from './../../../core/model/statuscandidate';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
@Component({
  selector: 'app-dialogviewcandidate',
  templateUrl: './dialogviewcandidate.component.html',
  styleUrls: ['./dialogviewcandidate.component.css']
})
export class DialogviewcandidateComponent implements OnInit {
  active: string;
  constructor(
    public dialogRef: MatDialogRef<DialogviewcandidateComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: StatusCandidateModel) {
  }

  ngOnInit(): void {
    if (this.data.active === true) {
      this.active = 'Active';
    }
    else {
      this.active = 'Inactive';
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
