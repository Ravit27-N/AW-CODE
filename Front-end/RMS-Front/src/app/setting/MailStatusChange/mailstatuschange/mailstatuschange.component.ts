import { MailStatusChangeService } from './../../../core/service/mailStatusChange.service';
import { map, startWith, switchMap } from 'rxjs/operators';
import { merge, Subject } from 'rxjs';
import { MailStatuschangeModel } from './../../../core/model/mailStatusChange';
import { DialogviewmailstatusComponent } from './dialogviewmailstatus/dialogviewmailstatus.component';
import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { PAGINATION_SIZE } from 'src/app/core';
import { RestoreDialogComponent } from '../../_dialog/restoreDialog/restoreDialog.component';
import { MessageService } from 'src/app/core/service/message.service';
import { IsLoadingService } from '@service-work/is-loading';

@Component({
  selector: 'app-mailstatuschange',
  templateUrl: './mailstatuschange.component.html',
  styleUrls: ['./mailstatuschange.component.scss']
})
export class MailstatuschangeComponent implements OnInit, AfterViewInit {
  selectDelete = 'Deleted';
  id: string;
  subject: string;
  body: string;
  status = '';
  search: string;
  active: string;
  length: number;
  displayedColumns: string[] = ['#', 'title', 'From', 'To', 'Status Candidate', 'Mail Template', 'Status', 'Action'];
  dataSource: MatTableDataSource<MailStatuschangeModel>;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;

  content: MailStatuschangeModel[];
  private detectChange$ = new Subject<boolean>();

  constructor(public dialog: MatDialog, private service: MailStatusChangeService, private message: MessageService,
    private isloadingService: IsLoadingService) {
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.content);
  }

  ngAfterViewInit(): void {
    merge(this.sort.sortChange, this.paginator.page, this.detectChange$).pipe(
      startWith({}),
      switchMap(() => {
        this.isloadingService.add({ key: 'mailstatus', unique: 'mailstatus' });
        return this.service.getList(this.paginator.pageIndex + 1, this.paginator.pageSize, this.search, this.sort.direction,
          this.sort.active, this.status);
      }),
      map(data => {
        this.length = data.total;
        this.isloadingService.remove({ key: 'mailstatus' });
        return data.contents;
      })
    ).subscribe(data => this.dataSource.data = data);
  }
  page(): void {
    this.paginator.firstPage();
  }

  applyFilter(): void {
    if (this.search.length >= 3 || this.search === '' || this.search === null) {
      this.detectChange$.next(true);
      this.paginator.firstPage();
    }
  }

  click(row): void {
    if (!row.deleted) {
      this.service.updateActive(row.id, !row.active).subscribe(() => {
        this.message.showSuccess('Sucess Update', 'Mail Configuration');
        if (this.status !== '') {
          this.detectChange$.next(true);
        }
      }, () => {
        this.message.showError('Error Update', 'Mail Configuration');
      });
    }
  }

  delete(row): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: row,
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.restoreMailConfig(row.id, true).subscribe(() => {
          this.message.showSuccess('Delete Sucess', 'Delete Mail Configuration');
          this.detectChange$.next(true);
        }, () => {
          this.message.showError('Delete Error', 'Delete Mail Configuration');
        });
      }
    });
  }

  restore(id: number, title): void {
    const dialogRef = this.dialog.open(RestoreDialogComponent, {
      data: title,
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.restoreMailConfig(id, false).subscribe(() => {
          this.message.showSuccess('Restore Sucess', 'Mail Configuration');
          this.detectChange$.next(true);
        }, () => {
          this.message.showError('Restore Fail', 'Mail Configuration');
        });
        this.paginator.firstPage();
      }
    });
  }

  openDialog(row: any): void {
    this.dialog.open(DialogviewmailstatusComponent, {
      data: row,
      width: '40%'
    });
  }
  selectdata(value): void {
    this.status = value;
    this.detectChange$.next(true);
  }
  clear(): void {
    const filterValue = this.search = '';
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }
}
