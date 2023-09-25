import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TaskModel } from '../../../core/model/task.model';
import {
  CreateUserRoleModel,
  ModuleService,
  PrivilegeModel,
  RoleService,
} from '../../../core';
import { AwSnackbarService } from '../../../shared';
import { distinctUntilChanged, skip } from 'rxjs/operators';

@Component({
  selector: 'app-feature-role-form',
  templateUrl: './feature-role-form.component.html',
  styleUrls: ['./feature-role-form.component.scss'],
})
export class FeatureRoleFormComponent implements OnInit {
  @Input() isUpdateRole: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);
  roleValidator: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  form: FormGroup;
  userRoleModel: CreateUserRoleModel;
  privileges: Array<PrivilegeModel> = [];
  rolePrivilegesChecklist: Array<TaskModel> = [];
  permissionTitles: Array<string> = ['View', 'Create', 'Modify', 'Delete'];

  constructor(
    private formBuilder: FormBuilder,
    private userRoleService: RoleService,
    private moduleService: ModuleService,
    private awSnackbarService: AwSnackbarService,
  ) {
    this.form = formBuilder.group({});
  }

  ngOnInit(): void {
    if (localStorage.getItem('form-role')) {
      this.userRoleModel = JSON.parse(localStorage.getItem('form-role'));
    }
    this.initRoleForm();
    this.fetchPrivileges();
    this.formChanged();
  }

  formChanged(): void {
    this.form.valueChanges
      .pipe(distinctUntilChanged())
      .pipe(skip(1))
      .subscribe((result) => {
        if (Object.values(result)) {
          this.mapUserRoleModel();
        }
      });
  }

  initRoleForm(): void {
    this.form.addControl(
      'name',
      new FormControl(this.userRoleModel?.name || ''),
    );
    this.form.addControl(
      'description',
      new FormControl(this.userRoleModel?.description || ''),
    );
  }

  initPrivilegesForm(): void {
    this.privileges.forEach((privilege: PrivilegeModel): void => {
      const subTask: Array<TaskModel> = [];
      const subTaskTitle: Array<string> = this.permissionTitles;
      const privilegeKeys: Array<string> = Object.keys(privilege.permission);
      const privilegeValues: Array<boolean> = Object.values(
        privilege.permission,
      );

      privilegeKeys.forEach((key: string, index: number): void => {
        subTask.push({
          name: key,
          title: subTaskTitle[index],
          completed: privilegeValues[index],
        });
      });

      const task: TaskModel = {
        title: privilege.name,
        name: privilege.name,
        description: privilege.description,
        completed: false,
        subtasks: subTask,
      };
      this.rolePrivilegesChecklist.push(task);
    });
  }

  fetchPrivileges(): void {
    if (this.userRoleModel?.privileges) {
      this.privileges = this.userRoleModel.privileges;
      this.initPrivilegesForm();
    } else {
      this.moduleService
        .get(1, 0)
        .toPromise()
        .then((result) => {
          this.privileges = result.contents;
          this.initPrivilegesForm();
        });
    }
  }

  async submit(): Promise<void> {
    if (this.validateForm()) {
      if (!this.form.get('name').value) {
        this.roleValidator.next(false);
        this.form.get('name').setErrors({ incorrect: true });
      }
      if (!this.form.get('description').value) {
        this.form.get('description').setErrors({ incorrect: true });
      }
      return;
    }
    const nameDuplicate = await this.validateDuplicateRole(
      this.form.get('name').value,
    );
    if (!nameDuplicate) {
      this.userRoleService
        .create(this.userRoleModel)
        .toPromise()
        .then((): void => {
          localStorage.removeItem('form-role');
          this.showSuccessMessage('Created role successfully.');
          history.back();
        })
        .catch((): void => {
          this.showErrorMessage(
            'Create role failed cannot communicate with the server.',
          );
        });
    }
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private showErrorMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  mapUserRoleModel(): void {
    this.userRoleModel = {
      name: this.form.get('name').value,
      description: this.form.get('description').value,
      privileges: this.privileges,
    };
    localStorage.setItem('form-role', JSON.stringify(this.userRoleModel));
  }

  validateForm(): boolean {
    return (
      this.form.invalid ||
      !this.form.get('name').value ||
      !this.form.get('description').value
    );
  }

  async validateDuplicateRole(name: string): Promise<boolean> {
    return await this.userRoleService
      .validateRole(name)
      .toPromise()
      .then((response: boolean) => {
        if (response) {
          this.form.get('name').setErrors({ incorrect: true });
        }
        this.roleValidator.next(response);
        return response;
      });
  }

  cancel(): void {
    history.back();
  }

  checklistResult(event: any): void {
    this.mapPrivileges(event);
  }

  mapPrivileges(event: any): void {
    const privilegeName: string = Array.from(Object.keys(event))
      .slice(0, 1)
      .toString();
    delete event[privilegeName];
    const subPrivilege = event;
    this.privileges
      .filter(
        (privilege: PrivilegeModel): boolean =>
          privilege.name === privilegeName,
      )
      .forEach(
        (privilege: PrivilegeModel): void =>
          (privilege.permission = subPrivilege),
      );
    this.mapUserRoleModel();
  }
}
