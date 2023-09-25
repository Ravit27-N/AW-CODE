import { AfterViewInit, Component, Inject, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSelectionList } from '@angular/material/list';
import { MatPaginator } from '@angular/material/paginator';
import { merge, Subject } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';
import { PAGINATION_SIZE } from 'src/app/core/constant';
import { UserGroup } from 'src/app/core/model/user-group';
import { MessageService } from 'src/app/core/service/message.service';
import { UserGroupAdminService } from 'src/app/core/service/user-group.service';
import { ComfirmDailogComponent } from 'src/app/shared/components';


@Component({
  selector: 'app-user-group-view',
  templateUrl: './user-group-view.component.html',
  styleUrls: ['./user-group-view.component.css']
})
export class UserGroupViewComponent implements AfterViewInit {

  userGroups: UserGroup[];

  paginationSize = PAGINATION_SIZE;
  resultLength = 0;

  @ViewChild(MatSelectionList) selectionList: MatSelectionList;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  detectChanged$ = new Subject<boolean>();

  constructor(
    private userGroupService: UserGroupAdminService,
    private messageService: MessageService,
    private dialog: MatDialog
  ) { }

  ngAfterViewInit(): void {
    merge(this.paginator.page, this.detectChanged$).pipe(
      startWith({}),
      switchMap(() => this.userGroupService.get(this.paginator.pageIndex + 1, this.paginator.pageIndex)),
      map(data => {
        this.resultLength = data.total;
        return data.contents;
      })
    ).subscribe(data => {
      this.userGroups = data;
    });
  }

  create(): void {
    const group: UserGroup = {
      name: ''
    };

    this.dialog.open(UserGroupDialogComponent, {
      data: group,
      width: '400px',
      disableClose: true,
      panelClass: 'overlay-scrollable'
    }).afterClosed().subscribe(ok => {
      if (ok) {
        this.messageService.showSuccess('Success', 'Create group');
        this.detectChanged$.next(true);
      }
    });
  }

  updated(): void {
    this.detectChanged$.next(true);
    this.messageService.showSuccess('Success', 'Update group');
  }

  clearSelect(): void {
    this.dialog.open(ComfirmDailogComponent, {
      data: {
        title: 'Group'
      },
      width: '450px',
      panelClass: 'overlay-scrollable'
    }).afterClosed().subscribe(ok => {
      if (ok) {
        this.userGroupService.delete(this.selectionList.selectedOptions.selected[0]?.value)
          .subscribe(() => {
            this.detectChanged$.next(true);
            this.messageService.showSuccess('Success', 'Delete group');
            this.selectionList.deselectAll();
          });
      }
    });
  }
}

@Component({
  selector: 'app-user-group-dialog',
  templateUrl: './user-group-dialog.component.html'
})
export class UserGroupDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UserGroupDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserGroup,
    private service: UserGroupAdminService
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  success(): void {
    this.service.create(this.data).subscribe(() => this.dialogRef.close({ changed: true }));
  }
}
