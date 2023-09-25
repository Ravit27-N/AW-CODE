import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { ActivatedRoute } from '@angular/router';
import {
  changeCellValue,
  filterChanged,
  loadFeedData,
  loadFeedForm,
  selectFeedData,
  selectPagination,
  selectRowFeed,
  selectSelectedRow,
  selectTableSchemas,
  unloadFeedForm
} from '@cxm-smartflow/directory-feed/data-access';
import { PaginatorComponent } from '@cxm-smartflow/shared/ui/paginator';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';


@Component({
  selector: 'cxm-smartflow-feed-table-form',
  templateUrl: './feed-table-form.component.html',
  styleUrls: ['./feed-table-form.component.scss']
})
export class FeedTableFormComponent implements OnInit, OnDestroy {

  destoryed$ = new Subject();
  selectedRow$: Observable<any>;
  schemes: any;
  tableColumns: string[];

  data$: Observable<any[]>;

  @ViewChild(PaginatorComponent, { static: true })  paginator: PaginatorComponent;

  ngOnInit(): void {
    const { id } = this.activatedRoute.snapshot.params;
    this.store.dispatch(loadFeedForm({ directoryId: id }));
    this.store.dispatch(loadFeedData({ directoryId: id }));

  }


  ngOnDestroy(): void {
    this.destoryed$.next(true);
    this.store.dispatch(unloadFeedForm())
  }

  public rowClick(row: any) {
    this.store.dispatch(selectRowFeed({ row }));
  }

  // public doubleClickCell(row: any, order: number) {
  //   // open edit mode on cell
  //   const cell = row.values[order];
  // }

  public dblclickCell(cell: HTMLElement) {
    // document.querySelector('td.editable')?.classList.remove('editable');
    // cell.classList.add('editable');

    // (document.querySelector('td.editable .feed-input') as HTMLInputElement).focus();
  }

  public cellReveertBack(cell: HTMLElement) {
    cell.classList.remove('editable');
  }

  public cellValueChange($event: any, row: any, order: number) {
    this.store.dispatch(changeCellValue({ value: $event.target.value, row, order }));
  }



  public addNewRow() {
    // add new row
    // this.store.dispatch(createRow())
  }

  public removeAllRow() {
    // this.store.dispatch(removeAllRow());
  }


  public deleteActions(row: any) {
    // this.store.dispatch(removeSelectedRow({ row }));
  }

  public shouldShow(length: number | undefined) {
    return Math.max(length || 0, 0);
  }

  private setupTable() {
    this.data$ = this.store.select(selectFeedData).pipe(takeUntil(this.destoryed$));
    this.store.select(selectPagination).pipe(takeUntil(this.destoryed$)).subscribe( paging => this.updatePaginator(paging) );
    this.selectedRow$ = this.store.select(selectSelectedRow).pipe(takeUntil(this.destoryed$));

    this.store.select(selectTableSchemas).pipe(takeUntil(this.destoryed$)).subscribe(schemes => {
      this.schemes = schemes;
      const { columns } = this.schemes;
      // this.tableColumns = ['no', ...columns, 'actions'];
      // this.tableColumns = [...columns, 'actions'];
      this.tableColumns = [...columns ];
    });
  }

  updatePaginator(listable: any) {
    if (this.paginator) {
      this.paginator.pageIndex = listable.page - 1;
      this.paginator.pageSize = listable.pageSize;
      this.paginator.length = listable.total;

      // this.shouldShowPagination = this.pagination.length <= this.pagination.pageSize;
    }
  }

  paginationChange(page: PageEvent) {
    this.store.dispatch(filterChanged({ page: page.pageIndex + 1, pageSize: page.pageSize }));
  }

  constructor(private store: Store, private activatedRoute: ActivatedRoute) {
    this.setupTable();
  }


}
