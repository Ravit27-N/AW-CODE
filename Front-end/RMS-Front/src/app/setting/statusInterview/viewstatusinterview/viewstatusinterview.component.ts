import { UpdatestatusinterviewComponent } from './../updatestatusinterview/updatestatusinterview.component';
import { InterviewTemplateModel } from '../../../core/model/interview-template';
import { InterviewTemplateService } from '../../../core/service/interview-template.service';
import { MessageService } from './../../../core/service/message.service';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Subject, merge } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { map, switchMap, startWith } from 'rxjs/operators';
import { DialogviewstatusinterviewComponent } from './dialogviewstatusinterview/dialogviewstatusinterview.component';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { PAGINATION_SIZE } from 'src/app/core';
import { AddstatusinterviewComponent } from '../addstatusinterview/addstatusinterview.component';
import { IsLoadingService } from '@service-work/is-loading';

@Component({
  selector: 'app-viewstatusinterview',
  templateUrl: './viewstatusinterview.component.html',
  styleUrls: ['./viewstatusinterview.component.css'],
})
export class ViewstatusinterviewComponent implements AfterViewInit {
  search: string;
  length: number;
  displayedColumns: string[] = ['#', 'name', 'active', 'action'];
  dataSource: MatTableDataSource<InterviewTemplateModel>;
  content: InterviewTemplateModel[];
  private detectChange$ = new Subject<boolean>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;

  constructor(
    public dialog: MatDialog,
    private service: InterviewTemplateService,
    private message: MessageService,
    private isloadingService: IsLoadingService,
  ) {
    this.dataSource = new MatTableDataSource(this.content);
  }

  ngAfterViewInit(): void {
    this.sort.active = 'name';
    this.sort.direction = 'asc';

    merge(this.sort.sortChange, this.paginator.page, this.detectChange$)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isloadingService.add({
            key: 'interviewtemplate',
            unique: 'interviewtemplate',
          });
          return this.service.getList(
            this.paginator.pageIndex + 1,
            this.paginator.pageSize,
            this.sort.active,
            this.sort.direction,
            this.search,
          );
        }),
        map((data) => {
          this.length = data.total;
          this.isloadingService.remove({ key: 'interviewtemplate' });
          return data.contents;
        }),
      )
      .subscribe((data) => (this.dataSource.data = data));
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
  click(id: number, active: any): void {
    this.service.changeStatus(id, !active).subscribe(
      () => {
        this.message.showSuccess('Success Update Status', 'Status Interview');
      },
      () => {
        this.message.showError('Error Update Status', 'Status Interview');
      },
    );
  }
  delete(row): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: { title: row.name },
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.service.delete(row.id).subscribe(
          () => {
            this.message.showSuccess('Sucess Delete', 'Status Interview');
            this.detectChange$.next(true);
          },
          () => {
            this.message.showError('Fail Delete', 'Status Interview');
          },
        );
      }
    });
  }

  openDialog(row: any): void {
    this.dialog.open(DialogviewstatusinterviewComponent, {
      data: row,
      width: '40%',
      panelClass: 'overlay-scrollable',
    });
  }
  clear(): void {
    const filterValue = (this.search = '');
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }
  add(): void {
    const dialogRef = this.dialog.open(AddstatusinterviewComponent, {
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  edit(row): void {
    const dialogRef = this.dialog.open(UpdatestatusinterviewComponent, {
      data: row,
      disableClose: true,
      width: '40%',
      panelClass: 'overlay-scrollable',
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
