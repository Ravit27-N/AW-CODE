import { DialogviewcandidateComponent } from './dialogviewcandidate/dialogviewcandidate.component';
import { UpdatestatuscandidateComponent } from './../updatestatuscandidate/updatestatuscandidate.component';
import { AddstatuscandidateComponent } from './../addstatuscandidate/addstatuscandidate.component';
import { MessageService } from './../../core/service/message.service';
import { StatusCandidateService } from './../../core/service/status-candidate.service';
import { merge, Subject } from 'rxjs';
import { StatusCandidateModel } from './../../core/model/statuscandidate';
import { map, startWith, switchMap } from 'rxjs/operators';
import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { PAGINATION_SIZE } from 'src/app/core';
import { RestoreDialogComponent } from '../_dialog/restoreDialog/restoreDialog.component';
import { IsLoadingService } from '@service-work/is-loading';

@Component({
  selector: 'app-statuecandidate',
  templateUrl: './statuecandidate.component.html',
  styleUrls: ['./statuecandidate.component.css']
})
export class StatuecandidateComponent implements OnInit, AfterViewInit {
  search: string;
  active: string;
  length: number;
  status = '';
  displayedColumns: string[] = ['#', 'title', 'description', 'active', 'action'];
  dataSource: MatTableDataSource<StatusCandidateModel>;
  content: StatusCandidateModel[];
  private detectChange$ = new Subject<boolean>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;

  constructor(public dialog: MatDialog, private service: StatusCandidateService, private message: MessageService,
    private isloadingService: IsLoadingService) {
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.content);
  }

  ngAfterViewInit(): void {
    this.sort.active = 'title';
    this.sort.direction = 'asc';

    merge(this.sort.sortChange, this.paginator.page, this.detectChange$).pipe(
      startWith({}),
      switchMap(() => {
        this.isloadingService.add({ key: 'statuscandidate', unique: 'statuscandidate' });
        return this.service.getList(this.paginator.pageIndex + 1, this.paginator.pageSize, this.status, this.sort.active
          , this.sort.direction, this.search);
      }),
      map(data => {
        this.length = data.total;
        this.isloadingService.remove({ key: 'statuscandidate' });
        return data.contents;
      })
    ).subscribe(data => this.dataSource.data = data);
  }

  page(): void {
    this.paginator.firstPage();
  }

  applyFilter(event?: Event): void {
    if (this.search.length >= 3 || this.search === '' || this.search === null) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.dataSource.filter = filterValue.trim().toLowerCase();
      this.paginator.firstPage();
      this.detectChange$.next(true);
    }
  }
  click(row): void {
    if (!row.deleted) {
      this.service.changeStatus(row.id, !row.active).subscribe(() => {
        this.message.showSuccess('Sucess Update Status', 'Status Candidate');
        if (this.status !== '') {
          this.detectChange$.next(true);
        }
      }, () => {
        this.message.showError('Fail Update Status', 'Status Candidate');
      });
    }
  }
  delete(row): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data:{ title: row.title},
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.softDelete(row.id, true).subscribe(() => {
          this.message.showSuccess('Delete Sucess', 'Status Candidate');
          this.detectChange$.next(true);
        }, () => {
          this.message.showError('Delete Fail', 'Status Candidate');
        });
      }
    });
  }

  restore(row: any): void {
    const dialogRef = this.dialog.open(RestoreDialogComponent, {
      data: row.title,
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.softDelete(row.id, false).subscribe(() => {
          this.message.showSuccess('Restore Sucess', 'Status Candidate');
          this.detectChange$.next(true);
        }, () => {
          this.message.showError('Restore Fail', 'Status Candidate');
        });
        this.paginator.firstPage();
      }
    });
  }

  openDialog(row: any): void {
    this.dialog.open(DialogviewcandidateComponent, {
      data: row,
      width: '40%',
      panelClass: 'overlay-scrollable'
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
  add(): void {
    const dialogRef = this.dialog.open(AddstatuscandidateComponent, {
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  edit(row): void {
    const dialogRef = this.dialog.open(UpdatestatuscandidateComponent, {
      width: '40%',
      data: row,
      disableClose: true,
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  pageEvent(event: PageEvent): void {
    this.paginator.pageIndex = event.pageIndex;
    this.paginator.pageSize = event.pageSize;
    this.detectChange$.next(true);
  }
}
