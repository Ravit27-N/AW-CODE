import { B_USER_ICON, G_USER_ICON } from '../../../core';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { merge } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  skip,
  startWith,
  switchMap,
  tap,
} from 'rxjs/operators';
import {
  AdvanceSearchForm,
  AdvanceSearchService,
  IAdvanceSearchForm,
  JobDescriptionService,
  PAGINATION_SIZE,
} from 'src/app/core';
import { UniversityService } from 'src/app/core/service/university.service';
import { IAdvanceSearchResultItem } from 'src/app/core/model/Search';
import { exportSearchResult } from 'src/app/core/spreadsheet';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSort } from '@angular/material/sort';
import { AwDateFormatPipe } from '../../pipes';
import { getAge } from '../../utils';

@Component({
  selector: 'app-advance-search',
  templateUrl: './advance-search.component.html',
  styleUrls: ['./advance-search.component.css'],
})
export class AdvanceSearchComponent implements OnInit, AfterViewInit {
  advanceForm: FormGroup;
  list: IAdvanceSearchResultItem[];
  positions: string[] = [];
  schools: string[] = [];
  model: IAdvanceSearchForm;
  displayedColumns: string[] = [
    'avatar',
    'firstname',
    'age',
    'phone',
    'university',
    'gpa',
    'yearOfExperience',
    'priority',
    'status',
    'interview',
    'createdAt',
    'action',
  ];

  resultLength = 0;
  isloading: boolean;

  paginationSize = PAGINATION_SIZE;
  bIcon = B_USER_ICON;
  gIcon = G_USER_ICON;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  postionCtrl = new FormControl();
  universityCtrl = new FormControl();
  uniloading = false;

  constructor(
    public searchService: AdvanceSearchService,
    private schoolService: UniversityService,
    private jobDescriptionService: JobDescriptionService,
    private formBuilder: FormBuilder,
    private router: Router,
    private dateformat: AwDateFormatPipe,
  ) {}

  ngOnInit(): void {
    this.advanceForm = this.formBuilder.group({
      term: '',
      gender: '',
      university: '',
      gpa: '',
      position: '',
    });

    this.universityCtrl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((keyword: string) =>
          this.schoolService.getList(1, 10, keyword),
        ),
      )
      .subscribe((list) => (this.schools = list.contents.map((x) => x.name)));

    this.postionCtrl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((keyword: string) =>
          this.jobDescriptionService.get(1, 10, keyword),
        ),
      )
      .subscribe(
        (data) =>
          (this.positions = data.contents
            .filter((x) => x.active)
            .map((x) => x.title)),
      );

    this.searchService.loading$
      .pipe(debounceTime(100))
      .subscribe((state) => (this.isloading = state));

    this.searchService.data$.subscribe((data) => {
      this.list = data.items.contents;
      this.resultLength = data.items.total;
      this.model = data.lastForm;
    });

    if (!this.model) {
      this.model = new AdvanceSearchForm();
    }

    if (!this.list) {
      this.list = [];
    }

    this.advanceForm.valueChanges
      .pipe(skip(5))
      .pipe(debounceTime(300))
      // Keep this code for later use
      // .pipe(skipWhile((x, i) => { return !this.model.checkRequireOneField() }))
      // .pipe(tap(() => this.list = []))
      .subscribe(() => this.implicitSearch());
  }

  ngAfterViewInit(): void {
    // Restore last paging
    if (this.model && this.model.pagination) {
      this.paginator.pageSize = this.model.pagination.pageSize;
      this.paginator.pageIndex = this.model.pagination.pageIndex;
    }

    merge(this.paginator.page, this.sort.sortChange)
      .pipe(
        tap(() => {
          this.model.pagination = {
            pageIndex: this.paginator.pageIndex, // material index from 0
            pageSize: this.paginator.pageSize,
          };

          if (this.sort.active) {
            this.model.sortByField = this.sort.active;
            this.model.sortDirection = this.sort.direction;
          }
        }),
        tap(() => this.search()),
      )
      .subscribe();
  }

  search(): void {
    this.searchService.search(this.model);
  }

  explicitSearch(): void {
    this.model.page(0);
    this.paginator.pageIndex = 0;
    this.search();
  }

  implicitSearch(): void {
    if (this.model.checkRequireOneField()) {
      this.explicitSearch();
    }
  }

  clear(): void {
    this.model.clear();
    this.list = [];
    this.paginator.pageIndex = 0;
    this.resultLength = 0;
  }

  export(): void {
    const dateformatter = (value) => this.dateformat.transform(value);

    this.searchService
      .export()
      .subscribe((data) =>
        exportSearchResult(
          'candidate-export.xlsx',
          data.contents,
          dateformatter,
        ),
      );
  }

  getAge(date: string) {
    return getAge(date);
  }

  edit(row: IAdvanceSearchResultItem): void {
    this.router.navigate(['/admin/candidate/editCandidate', row.id]);
  }

  view(row: IAdvanceSearchResultItem): void {
    this.router.navigate(['/admin/candidate/candidateDetail', row.id]);
  }

  getUrl(id: number, photoUrl: string) {
    return `/candidate/${id}/view/${photoUrl}`;
  }
}
