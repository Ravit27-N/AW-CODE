import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { functionalityConstant } from '@cxm-smartflow/client/data-access';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-functionality-page',
  templateUrl: './functionality-page.component.html',
  styleUrls: ['./functionality-page.component.scss'],
})
export class FunctionalityPageComponent implements OnInit, OnChanges {

  functionalities: any = [];
  @Output() valueChanged = new EventEmitter<string[]>();
  itemsChecked: string[] = [];

  initialized = false;

  @Input() clientFunctionality: any = []

  // TODO: validate privileges

  constructor(private translate: TranslateService) {
    this.mappingValue();
  }

  ngOnInit(): void {
  }

  onSlideToggle(checked: boolean, value: string) {
    const toUpdateIndex = Array.from(this.functionalities).map((x: any) => x.value).indexOf(value);
    const afterUpdate = this.functionalities.map((item: any, index: number) => {
      return toUpdateIndex === index ? { ...item, checked } : item;
    })
    Object.assign(this, { functionalities: afterUpdate });
    this.functionalityChanged();
  }

  mappingValue() {
    this.translate.get('client.functionality').toPromise().then((func: any) => {
      this.functionalities = functionalityConstant.map((item) => ({
        checked: false,
        value: item,
        t: func[item]
      })).sort(this.sortAZ);
    });
  }

  functionalityChanged() {
    const itemsChecked = this.functionalities.filter((x: any) => x.checked).map((x: any) => x.value);
    this.valueChanged.emit(itemsChecked);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.clientFunctionality
      // && !changes.clientFunctionality.firstChange && !this.initialized
    ) {
        const prepared = changes.clientFunctionality.currentValue.map((f: any) => f);

        this.translate.get('client.functionality').toPromise().then((func: any) => {

          this.functionalities = functionalityConstant.map((item) => ({
            checked: Array.from(prepared).includes(item),
            value: item,
            t: func[item]
          })).sort(this.sortAZ);

        })
        this.initialized = true;
      }
  }

  private sortAZ(a: any, b: any) {
    return a.t.localeCompare(b.t);
  }
}
