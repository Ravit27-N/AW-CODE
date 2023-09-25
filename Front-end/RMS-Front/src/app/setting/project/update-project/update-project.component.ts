import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';

import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AddProjectComponent} from '../add-project/add-project.component';
import {ProjectModel} from '../../../core/model/ProjectModel';
import {ProjectService} from '../../../core/service/project.service';
import {MessageService} from '../../../core/service/message.service';
import {IsLoadingService} from '@service-work/is-loading';

@Component({
  selector: 'app-update-project',
  templateUrl: './update-project.component.html',
  styleUrls: ['./update-project.component.css']
})
export class UpdateProjectComponent implements OnInit {

  validateForm: FormGroup;
  model: ProjectModel;

  constructor(
    private dialogRef: MatDialogRef<AddProjectComponent>,
    @Inject(MAT_DIALOG_DATA) private data: ProjectModel,
    private projectService: ProjectService,
    private messageService: MessageService,
    private isLoadingService: IsLoadingService,
  ) {
    this.model = {
      name: data.name,
      description: data.description
    };
  }

  ngOnInit(): void {
    //  Form Control
    this.validateForm = new FormGroup({
      name: new FormControl(this.model.name, [Validators.required]),
      description: new FormControl(this.model.description,)
    });
  }

  onSubmit(): void {
    if (this.validateForm.invalid) {
      return;
    }
    this.validateForm.value.id = this.data.id;
    this.projectService.validationUpdateName(this.data.id, this.model.name).subscribe((reponse) => {
      if (reponse === 0) {
        const subscription = this.projectService.update(this.data.id, this.validateForm.value)
          .subscribe((respone) => {
            this.messageService.showSuccess('Update Success', 'Update Project');
            this.dialogRef.close(respone);
          });
        this.isLoadingService.add(subscription, {key: 'UpdateProjectComponent', unique: UpdateProjectComponent});
      } else {
        this.messageService.showError('Project Name Already Exist', 'Update Project');
      }
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
