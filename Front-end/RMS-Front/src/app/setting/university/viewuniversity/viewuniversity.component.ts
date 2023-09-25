import { MessageService } from './../../../core/service/message.service';
import { UpdateuniversityComponent } from './../updateuniversity/updateuniversity.component';
import { DialogViewUniversityComponent } from './dialog-view-university/dialog-view-university.component';
import { UniversityModel } from 'src/app/core/model/university';

import { UniversityService } from './../../../core/service/university.service';
import { map, startWith, switchMap } from 'rxjs/operators';
import { MatSort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Subject, merge } from 'rxjs';
import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { PAGINATION_SIZE } from 'src/app/core';
import { AdduniversityComponent } from '../adduniversity/adduniversity.component';
import { IsLoadingService } from '@service-work/is-loading';

@Component({
  selector: 'app-viewuniversity',
  templateUrl: './viewuniversity.component.html',
  styleUrls: ['./viewuniversity.component.css']
})
export class ViewUniversityComponent implements AfterViewInit {
  search: string;
  active: string;
  length: number;
  status = 'all';
  displayedColumns: string[] = ['#', 'name', 'address', 'createdAt', 'action'];
  dataSource: MatTableDataSource<UniversityModel>;
  content: UniversityModel[];
  private detectChange$ = new Subject<boolean>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;

  constructor(public dialog: MatDialog, private service: UniversityService, private message: MessageService,
    private isloadingService: IsLoadingService) {
    this.dataSource = new MatTableDataSource(this.content);
  }

  ngAfterViewInit(): void {
    merge(this.sort.sortChange, this.paginator.page, this.detectChange$).pipe(
      startWith({}),
      switchMap(() => {
        this.isloadingService.add({ key: 'university', unique: 'university' });
        return this.service.getList(this.paginator.pageIndex + 1, this.paginator.pageSize, this.search, this.sort.direction,
          this.sort.active);
      }),
      map(data => {
        this.length = data.total;
        this.isloadingService.remove({ key: 'university' });
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
      data: { title: row.name },
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.delete(row.id).subscribe(() => {
          this.message.showSuccess('Delete Sucess', 'University');
          this.detectChange$.next(true);
        }, (err) => {
          if (err.apierror.statusCode === 403) {
            this.message.showError(name + ' is already use with candidate', 'University');
          } else {
            this.message.showError(err.apierror.statusCode, 'University');
          }
        });
      }
    });
  }

  openDialog(row: any): void {
    this.dialog.open(DialogViewUniversityComponent, {
      data: row,
      width: '40%',
      panelClass: 'overlay-scrollable'
    });
  }
  selectData(value): void {
    this.status = value;
    this.detectChange$.next(true);
  }
  clear(): void {
    const filterValue = this.search = '';
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }

  pageEvent(event: PageEvent): void {
    this.paginator.pageIndex = event.pageIndex;
    this.paginator.pageSize = event.pageSize;
    this.detectChange$.next(true);
  }
  openEdit(row): void {
    const dialogRef = this.dialog.open(UpdateuniversityComponent, {
      data: row,
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  add(): void {
    const dialogRef = this.dialog.open(AdduniversityComponent, {
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
}
