import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ProjectService } from '../../../core/service/project.service';
import { AwSnackbarService } from '../../../shared/services/aw-snackbar-service/aw-snackbar.service';

@Component({
  selector: 'app-add-form-project',
  templateUrl: './add-form-project.component.html',
  styleUrls: ['./add-form-project.component.css'],
})
export class AddFormProjectComponent implements OnInit {
  validateProjectForm: FormGroup;
  @Output() saving = new EventEmitter<boolean>();
  @Output() canceled = new EventEmitter<boolean>();

  constructor(
    private projectService: ProjectService,
    private awSnackbarService: AwSnackbarService,
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.validateProjectForm = new FormGroup({
      name: new FormControl(''),
      description: new FormControl(''),
    });
  }

  onSubmit(): void {
    if (
      this.validateProjectForm.invalid ||
      !this.validateProjectForm.get('name').value
    ) {
      if (!this.validateProjectForm.get('name').value) {
        this.validateProjectForm.controls.name.setErrors({ incorrect: true });
      }
      return;
    }
    this.projectService
      .nameValidation(this.validateProjectForm.get('name').value)
      .subscribe((response) => {
        if (response === 0) {
          this.projectService
            .create(this.validateProjectForm.value)
            .subscribe(() => {
              this.showSuccessMessage('Add Project Success');
              this.saving.emit(true);
            });
        } else {
          this.alertSnackbarMessage('Project Name Already Exist');
        }
      });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  cancel(): void {
    this.initForm();
    this.canceled.emit(true);
  }
}
