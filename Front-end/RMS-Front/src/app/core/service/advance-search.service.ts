import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, ReplaySubject, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { IAdvanceSearchResultItem } from '../model/Search';
import { CandidateService } from './candidate.service';

@Injectable()
export class AdvanceSearchService {

  private activation = new BehaviorSubject<boolean>(false);
  private dataContainer = new ReplaySubject<DataContainer>(1);
  private loading = new BehaviorSubject<boolean>(false);


  public activated$ = this.activation.asObservable();
  public data$ = this.dataContainer.asObservable();
  public loading$ = this.loading.asObservable();

  constructor(private candiateService: CandidateService) { }

  open(): void {
    this.activation.next(true);
  }

  close(): void {
    this.activation.next(false);
  }

  toggle(): void {
    this.activation.next(!this.activation.value);
  }

  export(): Observable<any> {
    return this.data$.pipe(switchMap(last => {
      const form = last.lastForm;
      return this.candiateService.advanceSearch(form, last.items.total, 1);
    }));
  }

  search(formData: IAdvanceSearchForm): void {
    this.loading.next(true);
    this.candiateService.advanceSearch(formData, formData.pagination?.pageSize, formData.pagination?.pageIndex + 1)
      .pipe(catchError((err) => {
        this.loading.next(false);
        return throwError(err);
      }))
      .subscribe((data) => {
        this.dataContainer.next({ items: data, lastForm: formData });
        this.loading.next(false);
      });
  }
}

export interface IAdvanceSearchForm {
  name?: string;
  gender?: string;
  from?: string;
  gpa?: string;
  position?: string;
  sortDirection?: string;
  sortByField?: string;

  pagination?: {
    pageIndex: number;
    pageSize: number;
  };

  checkRequireOneField(): boolean;
  page(index: number): void;
  clear(): void;
}

export class AdvanceSearchForm implements IAdvanceSearchForm {
  name?: string;
  gender?: string;
  from?: string;
  gpa?: string;
  position?: string;

  pagination?: { pageIndex: number; pageSize: number };

  constructor() {
    this.clear();
  }

  clear(): void {
    this.name = '';
    this.gender = undefined;
    this.from = '';
    this.gpa = '';
    this.position = '';
  }

  page(index: number): void {
    this.pagination = {
      ...this.pagination,
      pageIndex: index
    };
  }

  checkRequireOneField(): boolean {
    return ((this.name && this.name !== '') || (this.gender !== undefined) || (this.from && this.from !== '')
    || (this.gpa &&  this.gpa !== '') || (this.position && this.position !== ''));
  }
}

interface DataContainer {
  items: {
    contents: IAdvanceSearchResultItem[];
    total: number;
    page: number;
    pageSize: number;
  };
  lastForm: IAdvanceSearchForm;
}

