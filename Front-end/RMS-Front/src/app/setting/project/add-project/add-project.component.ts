import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';

import {MatDialogRef} from '@angular/material/dialog';
import {ProjectModel} from '../../../core/model/ProjectModel';
import {ProjectService} from '../../../core/service/project.service';
import {MessageService} from '../../../core/service/message.service';
import {IsLoadingService} from '@service-work/is-loading';

@Component({
  selector: 'app-add-project',
  templateUrl: './add-project.component.html',
  styleUrls: ['./add-project.component.css']
})
export class AddProjectComponent implements OnInit {

  validateForm: FormGroup;
  model: ProjectModel;
  constructor(
    private dialogRef: MatDialogRef<AddProjectComponent>,
    private projectService: ProjectService,
    private messageService: MessageService,
    private isLoadingService: IsLoadingService,
  ) {
    this.model={
      name:'',
      description:''
    };
  }

  ngOnInit(): void {

  //  Form Control
    this.validateForm= new FormGroup({
      name: new FormControl(this.model.name,[Validators.required]),
      description : new FormControl(this.model.description,)
    });
  }
  onSubmit(): void{
    if(this.validateForm.invalid){
      return;
    };
    // original
    // const subscription = this.projectService.create(this.validateForm.value)
    //   .subscribe((data)=>{
    //     this.messageService.showSuccess('Add Success','Add Project');
    //     this.dialogRef.close(data);
    //   },(error)=>{
    //     this.messageService.showError(error.message,'Add Project');
    //   });
    // this.isLoadingService.add(subscription,{key:'AddProjectComponent',unique:'AddProjectComponent'});
    this.projectService.nameValidation(this.model.name).subscribe((reponse) => {
      if(reponse === 0) {
        const subscription = this.projectService.create(this.validateForm.value)
          .subscribe((response) => {
            this.messageService.showSuccess('Add Success', 'Add Project');
            this.dialogRef.close(response);
          });
        this.isLoadingService.add(subscription, {key: 'AddProjectComponent', unique: 'AddProjectComponent'});
      }else{
        this.messageService.showError('Project Name Already Exist','Add Project');
      }
    });
  }

  onNoClick(): void{
    this.dialogRef.close();
  }


}
