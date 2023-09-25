import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ModuleService, PrivilegeModel, RoleService } from 'src/app/core';
import { UserRoleModel } from 'src/app/core/model/user-role.model';
import { MessageService } from 'src/app/core/service/message.service';
import { ComfirmDailogComponent } from 'src/app/shared/components';

@Component({
  selector: 'app-role-form',
  templateUrl: './role-form.component.html',
  styleUrls: ['./role-form.component.css'],
})
export class RoleFormComponent implements OnInit, OnChanges {
  @Input() userRole: UserRoleModel;
  @Input() editMode: boolean;

  @Output() onsuccess = new EventEmitter();
  @Output() oncancel = new EventEmitter();
  @Output() ondelete = new EventEmitter();

  model: RoleFormModel;
  allFeatures: PrivilegeModel[];

  constructor(
    private moduleService: ModuleService,
    private roleService: RoleService,
    private messageService: MessageService,
    private dialog: MatDialog,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.userRole && this.allFeatures && changes.userRole.currentValue) {
      this.model = this.mapRoleToFormModel(changes.userRole.currentValue);
    }
  }

  ngOnInit(): void {
    this.moduleService.get(1, 100).subscribe((x) => {
      this.allFeatures = x.contents;
      if (this.userRole) {
        this.model = this.mapRoleToFormModel(this.userRole);
      }
    });
  }

  remove(): void {
    this.dialog
      .open(ComfirmDailogComponent, {
        data: { title: 'Role' },
        width: '450px',
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.roleService.delete(this.userRole.id).subscribe(() => {
            this.messageService.showSuccess('Success', 'Role delete');
            this.ondelete.emit();
          });
        }
      });
  }

  save(): void {
    // id: number;
    // name: string;
    // description: string;
    // permission: PermissionModel;
    const privileges = this.model.features.map((value) => ({
      id: value.id,
      name: '',
      description: '',
      permission: {
        deleteAble: value.deleteAble ?? false,
        editAble: value.editAble ?? false,
        viewAble: value.viewAble ?? false,
        insertAble: value.insertAble ?? false,
      },
    }));

    const payload: UserRoleModel = {
      privileges,
      description: this.model.description,
      name: this.model.name,
      id: this.model.id,
    };

    if (this.editMode) {
      this.roleService.update(payload).subscribe(() => {
        this.messageService.showSuccess('Success', 'Role updated');
        this.onsuccess.emit();
      });
    } else {
      this.roleService.create(payload).subscribe(() => {
        this.messageService.showSuccess('Success', 'Role created');
        this.onsuccess.emit();
      });
    }
  }

  setAll(checked: boolean, feature: RoleFeature): void {
    feature.deleteAble =
      feature.editAble =
      feature.viewAble =
      feature.insertAble =
        checked;
  }

  updateAllComplete(feature: RoleFeature): void {
    feature.allChecked =
      feature.viewAble &&
      feature.editAble &&
      feature.insertAble &&
      feature.deleteAble;
  }

  clear(): void {
    this.model = undefined;
  }

  private mapRoleToFormModel(role: UserRoleModel): RoleFormModel {
    const form: RoleFormModel = {
      description: role.description,
      id: role.id,
      name: role.name,
      features: this.allFeatures.map(
        (x) =>
          ({
            id: x.id,
            allChecked: false,
            name: x.name,
            description: x.description,
          }) as RoleFeature,
      ),
    };

    role.privileges.forEach((detail) => {
      const feature = form.features.find((f) => f.id === detail.id);
      if (feature) {
        feature.deleteAble = detail.permission.deleteAble;
        feature.editAble = detail.permission.editAble;
        feature.viewAble = detail.permission.viewAble;
        feature.insertAble = detail.permission.insertAble;

        this.updateAllComplete(feature as RoleFeature);
      }
    });

    return form;
  }
}

interface RoleFeature {
  id: number;
  name: string;
  description: string;
  active: boolean;
  allChecked: boolean;
  viewAble: boolean;
  insertAble: boolean;
  deleteAble: boolean;
  editAble: boolean;
}

export interface RoleFormModel {
  id: number;
  name: string;
  description: string;
  features: RoleFeature[];
}
