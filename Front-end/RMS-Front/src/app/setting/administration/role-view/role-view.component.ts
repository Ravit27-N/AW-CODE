import { AfterViewInit, Component, Inject, ViewChild } from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatSelectionList } from '@angular/material/list';
import { MatPaginator } from '@angular/material/paginator';
import { merge, Subject } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';
import { PAGINATION_SIZE, RoleService } from 'src/app/core';
import { UserRoleModel } from 'src/app/core/model/user-role.model';
import { RoleFormComponent } from '../role-form/role-form.component';

@Component({
  selector: 'app-role-view',
  templateUrl: './role-view.component.html',
  styleUrls: ['./role-view.component.css'],
})
export class RoleViewComponent implements AfterViewInit {
  userRoles: UserRoleModel[] = [];

  paginationSize = PAGINATION_SIZE;
  resultLength = 0;
  private detectChange$ = new Subject<boolean>();

  @ViewChild(MatPaginator) paginator: MatPaginator;

  @ViewChild(MatSelectionList, { static: true })
  private selectionList: MatSelectionList;

  @ViewChild(RoleFormComponent, { static: true })
  private roleForm: RoleFormComponent;

  constructor(
    private userRoleService: RoleService,
    public dialog: MatDialog,
  ) {}

  ngAfterViewInit(): void {
    merge(this.paginator.page, this.detectChange$)
      .pipe(
        startWith({}),
        switchMap(() =>
          this.userRoleService.get(
            this.paginator.pageIndex + 1,
            this.paginator.pageSize,
          ),
        ),
        map((data) => {
          this.resultLength = data.total;
          return data.contents;
        }),
      )
      .subscribe((data) => (this.userRoles = data));
  }

  create(): void {
    const newRole: UserRoleModel = {
      description: '',
      name: '',
      id: 0,
      privileges: [],
    };

    const dialogRef = this.dialog.open(RoleFormDailogComponent, {
      data: newRole,
      width: '600px',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result && result.changed) {
        this.loadUserRole();
      }
    });
  }

  clearSelect(): void {
    this.selectionList.deselectAll();
    this.roleForm.clear();
    this.loadUserRole();
  }

  loadUserRole(): void {
    this.detectChange$.next(true);
  }
}

@Component({
  selector: 'app-role-form-dialog',
  templateUrl: './role-form-dialog.component.html',
})
export class RoleFormDailogComponent {
  constructor(
    public dialogRef: MatDialogRef<RoleFormDailogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserRoleModel,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  success(): void {
    this.dialogRef.close({ changed: true });
  }
}
