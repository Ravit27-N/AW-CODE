import { MessageService } from './../../core/service/message.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Component,Inject } from '@angular/core';
import { CandidateService } from 'src/app/core';
interface DialogData{
  candidateId: number | string;
  filename: string;
}

@Component({
  selector: 'app-remove-file-dialog',
  templateUrl: './remove-file-dialog.component.html',
  styleUrls: ['./remove-file-dialog.component.css']
})
export class RemoveFileDialogComponent {

  constructor(
    private dialogRef: MatDialogRef<RemoveFileDialogComponent>, @Inject(MAT_DIALOG_DATA) public dailogData: DialogData,
    private message: MessageService,
    private candidateService: CandidateService,
    ) { }

  ok(){
    this.candidateService.removeCandidateFile(this.dailogData.candidateId, this.dailogData.filename).subscribe(() => {
      this.message.showSuccess('This file was remove successfully.', 'Remove File');
    });
  }

}
