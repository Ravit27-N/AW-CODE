import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CheckedListKeyValue } from '@cxm-smartflow/flow-traceability/data-access';
import { BehaviorSubject } from 'rxjs';
import { CheckListModel } from '@cxm-smartflow/shared/ui/dropdown-filter-criterial';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-dropdown-filter-deposit-mode',
  templateUrl: './dropdown-filter-deposit-mode.component.html',
  styleUrls: ['./dropdown-filter-deposit-mode.component.scss']
})
export class DropdownFilterDepositModeComponent implements OnChanges {

  @Input() depositMode: CheckedListKeyValue [] = [];
  @Output() formChangeEvent = new EventEmitter<string []>();
  @Input() useModeFilter = false;

  depositMode$ = new BehaviorSubject([] as CheckListModel[]);

  reset() {
    this.depositMode$.next(this.depositMode.map((item) => ({ ...item, checked: false })));
    this.formChangeEvent.emit([]);
  }

  checkEvent($event: any) {
    this.formChangeEvent.emit($event);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.depositMode) {
      this.depositMode$.next(this.depositMode);
    }
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }
}
