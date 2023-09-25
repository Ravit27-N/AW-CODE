import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class CustomMatPaginatorIntl extends MatPaginatorIntl {
  ofLabel: string;

  constructor(private translate: TranslateService) {
    super();

    this.translate.onLangChange.subscribe(() => {
      this.getAndIntlTranslations();
    });

    this.getAndIntlTranslations();
  }

  getRangeLabel = (page: number, pageSize: number, length: number) => {
    if (length == 0 || pageSize == 0) {
      return `0 ${this.ofLabel} ${length}`;
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    // If the start index exceeds the list length, do not try and fix the end index to the end.
    const endIndex =
      startIndex < length
        ? Math.min(startIndex + pageSize, length)
        : startIndex + pageSize;

    return `${startIndex + 1} - ${endIndex} ${this.ofLabel} ${length}`;
  };

  getAndIntlTranslations() {
    this.translate.get('table.paginator').subscribe((translate) => {
      this.itemsPerPageLabel = translate.itemsPerPageLabel;
      this.nextPageLabel = translate.nextPageLabel;
      this.previousPageLabel = translate.previousPageLabel;
      this.ofLabel = translate.ofLabel;
      this.changes.next();
    });
  }
}
