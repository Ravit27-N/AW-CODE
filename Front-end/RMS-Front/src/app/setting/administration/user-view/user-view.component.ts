import {
  AfterViewInit,
  Component,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatSelectionList } from '@angular/material/list';
import { MatPaginator } from '@angular/material/paginator';
import { merge, Subject } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';
import { PAGINATION_SIZE } from 'src/app/core/constant';
import { UserPayload } from 'src/app/core/model/user-admin.model';
import { MessageService } from 'src/app/core/service/message.service';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { UserAdminService } from '../../../core/service/user-admin.service';

@Component({
  selector: 'app-user-view',
  templateUrl: './user-view.component.html',
  styleUrls: ['./user-view.component.css'],
})
export class UserViewComponent implements OnInit, AfterViewInit {
  paginationSize = PAGINATION_SIZE;
  resultLength = 0;

  @ViewChild(MatSelectionList) selectionList: MatSelectionList;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  detectChanged$ = new Subject<boolean>();

  users: UserPayload[];

  constructor(
    private userAdminService: UserAdminService,
    private dialog: MatDialog,
    private messageService: MessageService,
  ) {}

  ngAfterViewInit(): void {
    merge(this.paginator.page, this.detectChanged$)
      .pipe(
        startWith({}),
        switchMap(() =>
          this.userAdminService.get(
            this.paginator.pageIndex + 1,
            this.paginator.pageSize,
          ),
        ),
        map((data) => {
          this.resultLength = data.total;
          return data.contents;
        }),
      )
      .subscribe((data) => (this.users = data));
  }

  create(): void {
    const newUser: UserPayload = {
      email: '',
      enabled: true,
      firstName: '',
      lastName: '',
      userGroup: null,
      username: '',
      id: '',
    };

    this.dialog
      .open(UserFormDialogComponent, {
        data: newUser,
        width: '600px',
        disableClose: true,
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe(() => {
        this.detectChanged$.next(true);
      });
  }

  updated(): void {
    this.detectChanged$.next(true);
    this.messageService.showSuccess('Success', 'Update user');
  }

  ondelete(): void {
    this.dialog
      .open(ComfirmDailogComponent, {
        data: {
          title: 'User',
        },
        width: '450px',
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((ok) => {
        if (ok) {
          this.userAdminService
            .delete(this.selectionList.selectedOptions.selected[0]?.value.id)
            .subscribe(() => {
              this.messageService.showSuccess('Success', 'Delete user');
              this.detectChanged$.next(true);
            });
        }
      });
  }

  ngOnInit(): void {
    this.userAdminService.get().subscribe((data) => {
      this.users = data.contents;
      this.resultLength = data.total;
    });
  }

  get hidden() {
    return this.selectionList?.selectedOptions.selected[0] ?? false;
  }
}

@Component({
  selector: 'app-user-form-dialog',
  templateUrl: './user-form-dialog.component.html',
})
export class UserFormDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UserFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserPayload,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  success(): void {
    this.dialogRef.close({ changed: true });
  }
}
