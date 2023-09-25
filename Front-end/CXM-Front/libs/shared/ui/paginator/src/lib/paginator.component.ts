import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import {
  defaultPaginatorProps,
  globalMethods
} from '@cxm-smartflow/shared/data-access/model';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss'],
})
export class PaginatorComponent {
  paginatorProps = defaultPaginatorProps;

  constructor(private translate: TranslateService) {}

  @Input() isShowFoundItems = false;

  @Input()
  length: number;

  @Input()
  pageIndex = 0;

  @Input()
  pageSize: number;

  @Input()
  showTotalPages: number;

  @Input()
  pageSizeOptions: number[];

  @Output()
  page = new EventEmitter<PageEvent>();

  initialized: Observable<void>;
  Page($event: PageEvent | undefined) {
    this.page.emit($event);
  }

  getItemsFoundLabel(totalItems: number): string {
    let totalItemsFound = '';
    this.translate.get('table.itemsFounded').subscribe((res) => {
      totalItemsFound = globalMethods.totalItemsFound(res, totalItems);
    });
    return totalItemsFound;
  }
}
