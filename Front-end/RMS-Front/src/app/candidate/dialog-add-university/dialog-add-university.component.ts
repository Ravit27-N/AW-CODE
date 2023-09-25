import { VALIDATION_MESSAGE } from './../../core/model/validationMessage';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UniversityService } from 'src/app/core/service/university.service';
import { MessageService } from './../../core/service/message.service';
import { UniversityModel } from 'src/app/core/model/university';
import { Component, OnInit, Inject } from '@angular/core';
interface IData{
  title: string;
}

@Component({
  selector: 'app-dialog-add-university',
  templateUrl: './dialog-add-university.component.html',
  styleUrls: ['./dialog-add-university.component.css']
})
export class DialogAddUniversityComponent implements OnInit {
  universityForm: UniversityModel;
  validationMessage = VALIDATION_MESSAGE;
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: IData,
    private message: MessageService,
    private universityService: UniversityService
  ) {
    this.universityForm = {
      id: null,
      name: null,
      createdAt: null,
      updatedAt: null,
      description: null,
      address : null
    };
  }

  ngOnInit(): void {
    this.universityForm.name = this.data.title;
  }

  save(): void{
    this.universityService.create(this.universityForm).subscribe(() => {
      this.message.showSuccess('University was save successfully', 'Add University');
    }, err => {
      if (err.apierror.statusCode === 409){
        this.message.showError('This university has already!', 'Error');
      }
    });
  }

  clearUniversity(): void{
    this.universityForm.name = null;
  }
}
