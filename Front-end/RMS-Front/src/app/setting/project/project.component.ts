import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';

import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {MatDialog} from '@angular/material/dialog';
import {AddProjectComponent} from './add-project/add-project.component';
import {UpdateProjectComponent} from './update-project/update-project.component';
import {ComfirmDailogComponent} from '../../shared';
import {ProjectModel} from '../../core/model/ProjectModel';
import {PAGINATION_SIZE} from '../../core';
import {merge, Subject} from 'rxjs';
import {map, startWith, switchMap} from 'rxjs/operators';
import {IsLoadingService} from '@service-work/is-loading';
import {ProjectService} from '../../core/service/project.service';
import {MessageService} from '../../core/service/message.service';
import {ArchiveProjectDialogComponent} from './archive-project-dialog/archive-project-dialog.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit,AfterViewInit {
  btnArchiev = '';
  search: string;
  active: string;
  length: number;
  displayedColumns: string[]=['no','name','description','created_at','action'];
  dataSource= new MatTableDataSource<ProjectModel>();
  pageSizeOptions=PAGINATION_SIZE;
  private detectChange$ = new Subject<boolean>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  isDeleted = false;
  filter = '';
  sortByField = 'createdAt';
  sortDirection = 'desc';

  constructor(
    private router: Router,
    private _liveAnnouncer: LiveAnnouncer,
    private dialog: MatDialog,
    private isLoadingService: IsLoadingService,
    private projectService: ProjectService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    // GET DATA FROM API
    this.loadDataByFunction();
  }

  loadDataByFunction(): void{
    merge(this.sort.sortChange,this.paginator.page,this.detectChange$)
      .pipe(
        startWith({}),
        switchMap(()=>{
          this.isLoadingService.add({key:'project',unique:'project'});
          return this.projectService.getList(this.paginator.pageIndex+1,this.paginator.pageSize,this.search,this.isDeleted,this.sort.direction);
        }),
        map(data=>{
          this.length = data.total;
          this.isLoadingService.remove({key:'project'});
          return data.contents;
        })
      ).subscribe(data=>this.dataSource.data=data);
  }

  page(): void{
    this.paginator.firstPage();
  }

  announceSortChange(sortState: Sort) {
    if (sortState.direction) {
      this._liveAnnouncer.announce(`Sort ${sortState.direction} ending`);
    } else {
      this._liveAnnouncer.announce('Sorting Cleared');
    }
  }


  add(): void{
    const dialogRef= this.dialog.open(AddProjectComponent,{disableClose:true,width:'40%'});
    dialogRef.afterClosed().subscribe(()=>{
      this.detectChange$.next(true);
    })
  }

  edit(row): void{
    const dialogRef=this.dialog.open(UpdateProjectComponent,{data:row,width:'40%'});
    dialogRef.afterClosed().subscribe(()=>{
      this.detectChange$.next(true);
    })
  }
  delete(data): void{
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: { title: data.name },
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.projectService.softDelete(data.id).subscribe(() => {
          this.messageService.showSuccess('Success Delete', 'Project');
          this.detectChange$.next(true);
        }, () => {
          this.messageService.showError('Fail Delete', 'Project');
        });
      }
    });
  }
  applyFilter(event: Event){
    if (this.search.length >= 3 || this.search === ''|| this.search === null) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.dataSource.filter = filterValue.trim().toLowerCase();
      this.paginator.firstPage();
      this.detectChange$.next(true);
    }
  }
  clear(): void{
    const filterValue = this.search = '';
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }

  getDateFormat(date): any{
    return new Date(date);
  }

  getProjectListByIsDeleted(): void{
    if(this.isDeleted === false) {
      this.isDeleted = true;
      this.btnArchiev = 'warn';
      this.paginator.pageIndex = 0;
    }else {
      this.isDeleted = false;
      this.btnArchiev = '';
    }
    this.loadDataByFunction();
  }

  restoreOrPermanentDelete(id: number, isDelete: boolean, type?: string, projectName?: string): void{
    const dialogRef = this.dialog.open(ArchiveProjectDialogComponent, {
      width: '650px',
      data: {
        id,
        isDelete,
        type,
        projectName
      },
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(() => {
      this.loadData();
    });
  }

  loadData(): void {
    this.filter = '';
    this.sortDirection = 'desc';
    this.sortByField = 'createdAt';
    this.loadDataByFunction();
  }

}
