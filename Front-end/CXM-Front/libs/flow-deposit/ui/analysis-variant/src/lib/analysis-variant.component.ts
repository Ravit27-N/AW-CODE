import { Component, OnDestroy, OnInit } from '@angular/core';;
import { BehaviorSubject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-analysis-variant',
  templateUrl: './analysis-variant.component.html',
  styleUrls: ['./analysis-variant.component.scss']
})
export class AnalysisVariantComponent implements OnInit, OnDestroy {

  variantType$ = new BehaviorSubject<string>('warning');
  title$ = new BehaviorSubject<string>('');
  document$ = new BehaviorSubject<string>('');
  page$ = new BehaviorSubject<string>('');

  constructor(private translateService: TranslateService) {
  }

  ngOnInit(): void {
    this.translateService.get('flow.deposit.analysisResult.analysisVariant').subscribe((value) => {
      this.title$.next(value?.titleSuccess);
      this.document$.next(value?.table?.numberDoc+" "+ 4 +" "+ value?.table?.document + value?.table?.of +" " + 0 +" "+ value?.table?.error);
      this.page$.next(value?.table?.numberPage +" "+ 10 + " " + value?.table?.page);
    });
  }

  ngOnDestroy() {
    this.variantType$.unsubscribe();
    this.document$.unsubscribe();
    this.page$.unsubscribe();
    this.title$.unsubscribe();
  }

}
