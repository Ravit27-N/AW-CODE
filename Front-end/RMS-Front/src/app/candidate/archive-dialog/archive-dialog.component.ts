import { MessageService } from './../../core/service/message.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';
import { CandidateService } from 'src/app/core';
export interface DialogData {
  id: number;
  fullName: string;
  isDelete: boolean;
  type?: string;
}
@Component({
  selector: 'app-archive-dialog',
  templateUrl: './archive-dialog.component.html',
  styleUrls: ['./archive-dialog.component.css']
})
export class ArchiveDialogComponent implements OnInit {
  title: string;
  content: string;
  fullName: string;
  constructor(
    private dialogRef: MatDialogRef<ArchiveDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private message: MessageService,
    private candidateService: CandidateService,
  ) { }

  ngOnInit(): void {
    this.setTitleAndContent();
  }

  setTitleAndContent(): void {
    if (this.data.type === 'restore') {
      this.title = 'Restore Candidate';
      this.content = 'Are you sure to restore this candidate? If Okay, this will also restore records' +
       ' (Reminder, Result, Interview or Activity Log) that are related to this candidate.';
    } else {
      this.title = 'Permanently delete Candidate';
      this.content = 'Are you sure to permanently delete this candidate? If Okay, this will also permanently delete records' +
      ' (Reminder, Result, Interview or Activity Log) that are related to this candidate.';
    }
  }

  ok(): void {
    if (this.data.type === 'restore') {
      this.candidateService.restore(this.data?.id, false).subscribe(() => {
        this.message.showSuccess('Recursive restore candidate successfully ', 'Recursive Restore');
        this.dialogRef.close(true);
      });
    } else if(this.data.type === 'permanentyDelete') {
      this.candidateService.hardDelete(this.data?.id).subscribe(() => {
        this.message.showSuccess('Success', 'Permanently remove candidate');
        this.dialogRef.close(true);
      });
    }
  }

}
