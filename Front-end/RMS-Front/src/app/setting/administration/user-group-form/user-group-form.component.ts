import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { map } from 'rxjs/operators';
import { RoleService } from 'src/app/core';
import { UserPayload } from 'src/app/core/model/user-admin.model';
import { UserGroup } from 'src/app/core/model/user-group';
import { UserRoleModel } from 'src/app/core/model/user-role.model';
import { UserGroupAdminService } from 'src/app/core/service/user-group.service';
import { UserAdminService } from '../../../core/service/user-admin.service';

@Component({
  selector: 'app-user-group-form',
  templateUrl: './user-group-form.component.html',
  styleUrls: ['./user-group-form.component.css'],
})
export class UserGroupFormComponent implements OnInit, OnChanges {
  @Input() userGroup: UserGroup;
  @Input() editMode: boolean;

  @Output() onsuccess = new EventEmitter();
  @Output() ondelete = new EventEmitter();

  model: UserGroup;
  userRoles: UserRoleModel[];

  memeber: UserPayload[];

  private allUser: UserPayload[];

  constructor(
    private service: UserGroupAdminService,
    private roleService: RoleService,
    private userService: UserAdminService,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.userGroup) {
      this.model = changes.userGroup.currentValue;

      if (this.editMode && this.userGroup?.id) {
        this.fetchGroupInfo();
      }
    }
  }

  ngOnInit(): void {
    this.model = this.userGroup;
    this.roleService.get().subscribe((x) => (this.userRoles = x.contents));
    this.userService
      .get()
      .pipe(map((x) => (this.allUser = x.contents)))
      .subscribe((x) => (this.allUser = x));
  }

  get assignedRole(): string[] {
    return this.model.clientRoles?.[
      Object.keys(this.model.clientRoles)[0]
    ] as string[];
  }

  get assignableRole(): UserRoleModel[] {
    return this.userRoles?.filter((x) => !this.assignedRole?.includes(x.name));
  }

  get availableUser(): UserPayload[] {
    return this.allUser?.filter(
      (x) => !this.memeber?.map((m) => m.username).includes(x.username),
    );
  }

  save(): void {
    if (this.editMode) {
      this.service
        .update(this.userGroup)
        .subscribe(() => this.onsuccess.emit());
    } else {
      this.service
        .create(this.userGroup)
        .subscribe(() => this.onsuccess.emit());
    }
  }

  removeRoleFromGroup(roleName: string): void {
    this.service
      .resignRole(roleName, this.userGroup)
      .subscribe(() => this.fetchGroupInfo());
  }

  assignRole(roleName: string): void {
    this.service
      .asignRole(roleName, this.userGroup)
      .subscribe(() => this.fetchGroupInfo());
  }

  addMember(selectedUser: UserPayload): void {
    this.userService
      .joinGroup(selectedUser.username, this.userGroup.id)
      .subscribe(() => this.fetchGroupInfo());
  }

  removeMemeber(selectedUser: UserPayload): void {
    this.userService
      .leaveGroup(selectedUser.username, this.userGroup.id)
      .subscribe(() => this.fetchGroupInfo());
  }

  fetchGroupInfo(): void {
    this.service.getById(this.userGroup.id).subscribe((x) => {
      this.model = x;
    });

    this.service
      .getMembers(this.userGroup)
      .pipe(map((x) => x.contents))
      .subscribe((x) => (this.memeber = x));
  }
}
