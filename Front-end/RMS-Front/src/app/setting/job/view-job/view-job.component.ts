import { UpdateJobComponent } from './../update-job/update-job.component';
import { AddJobComponent } from './../add-job/add-job.component';
import { DialogViewJobComponent } from './dialog-view-job/dialog-view-job.component';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { startWith, switchMap, map } from 'rxjs/operators';
import { MessageService } from './../../../core/service/message.service';
import { JobService } from './../../../core/service/job.service';
import { MatDialog } from '@angular/material/dialog';
import { PAGINATION_SIZE } from 'src/app/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Subject, merge } from 'rxjs';
import { JobModel } from './../../../core/model/Job';
import { MatTableDataSource } from '@angular/material/table';
import { Component,  ViewChild, AfterViewInit } from '@angular/core';
import { IsLoadingService } from '@service-work/is-loading';

@Component({
  selector: 'app-view-job',
  templateUrl: './view-job.component.html',
  styleUrls: ['./view-job.component.css']
})
export class ViewJobComponent implements AfterViewInit {

  search: string;
  active: string;
  length: number;
  status = 'all';
  fileType: string;
  displayedColumns: string[] = ['#', 'title', 'description', 'status', 'action'];
  dataSource: MatTableDataSource<JobModel>;
  content: JobModel[];
  private detectChange$ = new Subject<boolean>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;

  constructor(public dialog: MatDialog, private service: JobService, private message: MessageService,
    private isloadingService: IsLoadingService) {
    this.dataSource = new MatTableDataSource(this.content);
  }
  ngAfterViewInit(): void {
    merge(this.sort.sortChange, this.paginator.page, this.detectChange$).pipe(
      startWith({}),
      switchMap(() => {
        this.isloadingService.add({ key: 'job', unique: 'job' });
        return this.service.getlist(
          this.paginator.pageIndex + 1,
          this.paginator.pageSize,
          this.search,
          this.sort.direction,
          this.sort.active,
        );
      }),
      map(data => {
        this.length = data.total;
        this.isloadingService.remove({ key: 'job' });
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
  delete(row): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: { title: row.title },
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.delete(row.id).subscribe(() => {
          this.message.showSuccess('Sucess Delete', 'Job Description');
          this.detectChange$.next(true);
        }, () => {
          this.message.showError('Fail Delete', 'Job Description');
        });
      }
    });
  }

  openDialog(row: any): void {
    this.dialog.open(DialogViewJobComponent, {
      data: row,
      width: '40%'
    });
  }
  clear(): void {
    const filterValue = this.search = '';
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }
  add(): void {
    const dialogRef = this.dialog.open(AddJobComponent, {
      width: '40%',
      disableClose: true
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  edit(row): void {
    const dialogRef = this.dialog.open(UpdateJobComponent, {
      width: '40%',
      data: row,
      disableClose: true
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  click(id: number, active: any): void {
    this.paginator.firstPage();
    this.service.changeStatus(id, !active).subscribe(() => {
      this.message.showSuccess('Update', 'Update Active');
    }, () => {
      this.detectChange$.next(true);
    });
  }
  getFile(row) {
    this.service.fileView(row.id, row.filename).subscribe(res => {
      const blob = new Blob([res], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    }, () => {});
  }
}
