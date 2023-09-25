import { MessageService } from './../../core/service/message.service';
import { AddmailtemplateComponent } from '../addmailtemplate/addmailtemplate.component';
import { MailTemplateModel, MailTemplateFormModel } from './../../core/model/Mailtemplate';
import { MailtemplateService } from '../../core/service/Mailtemplate.service';
import { map, startWith, switchMap } from 'rxjs/operators';
import { Subject, merge } from 'rxjs';
import { DialogviewComponent } from './dialogview/dialogview.component';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { PAGINATION_SIZE } from 'src/app/core';
import { UpdatemailtemplateComponent } from '../updatemailtemplate/updatemailtemplate.component';
import { RestoreDialogComponent } from '../_dialog/restoreDialog/restoreDialog.component';
import { IsLoadingService } from '@service-work/is-loading';


@Component({
  selector: 'app-mail-template',
  templateUrl: './mail-template.component.html',
  styleUrls: ['./mail-template.component.css']
})
export class MailTemplateComponent implements OnInit, AfterViewInit {
  id: string;
  subject: string;
  body: string;
  status = '';
  content: MailTemplateFormModel[];
  search: string;
  displayedColumns: string[] = ['#', 'subject', 'body', 'active', 'action'];
  dataSource: MatTableDataSource<MailTemplateModel>;
  private detectChange$ = new Subject<boolean>();
  length: number;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;

  constructor(public dialog: MatDialog, private service: MailtemplateService, private message: MessageService,
    private isloadingService: IsLoadingService) { }
  ngAfterViewInit(): void {

    merge(this.sort.sortChange, this.paginator.page, this.detectChange$).pipe(
      startWith({}),
      switchMap(() => {
        this.isloadingService.add({ key: 'mailtemplate', unique: 'mailtemplate' });
        return this.service.getList(this.paginator.pageIndex + 1, this.paginator.pageSize, this.search, this.sort.direction
          , this.sort.active, this.status);
      }),
      map(data => {
        this.length = data.total;
        return data.contents;
      })
    ).subscribe(data => {
      this.dataSource.data = data;
      this.isloadingService.remove({ key: 'mailtemplate' });
    });
  }

  click(row): void {
    if (row.deleted) {
      return;
    }
    this.service.updateActive(row.id, !row.active).subscribe(() => {
      this.message.showSuccess('Update Status', 'Mail Template');
      if (this.status !== '') {
        this.detectChange$.next(true);
      }
    }, () => {
      this.message.showError('Error Update Status', 'Mail Template');
    });
  }

  openDialog(r: any): void {
    this.dialog.open(DialogviewComponent, {
      data: r,
      width: '40%'
    });
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.content);
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

  delete(id: number, subject: string): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: { title: subject },
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.service.updateDeleted(id, true).subscribe(() => {
          this.message.showSuccess('Delete Sucess', 'Delete Mail Template');
          this.detectChange$.next(true);
        }, () => {
          this.message.showError('Delete Fail', 'Delete Mail Template');
          this.detectChange$.next(true);
        });
      }
    });
  }

  restore(id: number, subject): void {
    const dialogRef = this.dialog.open(RestoreDialogComponent, {
      data: subject,
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.updateDeleted(id, false).subscribe(() => {
          this.message.showSuccess('Restore Sucess', 'Restore Mail Template');
          this.detectChange$.next(true);
        }, () => {
          this.message.showError('Restore Fail', 'Restore Mail Template');
          this.detectChange$.next(true);
        });
        this.paginator.firstPage();
      }
    });
  }

  clear(): void {
    const filterValue = this.search = '';
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }
  selectdata(value): void {
    this.status = value;
    this.detectChange$.next(true);
  }
  add(): void {
    const dialogRef = this.dialog.open(AddmailtemplateComponent, {
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  edit(row): void {
    const dialogRef = this.dialog.open(UpdatemailtemplateComponent, {
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

