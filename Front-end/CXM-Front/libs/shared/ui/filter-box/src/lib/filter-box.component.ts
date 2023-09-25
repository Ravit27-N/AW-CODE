import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { CustomAngularMaterialUtil, Sort } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-filter-box',
  templateUrl: './filter-box.component.html',
  styleUrls: ['./filter-box.component.scss'],
})
export class FilterBoxComponent implements OnInit, OnDestroy, OnChanges {
  @Input() filterBoxDataSource: any[] = [];
  @Input() filterBoxIconUrl = 'assets/icons/profile-white.svg';
  @Input() scaleIcon = 1;
  @Input() filterBoxName = '';
  @Input() filterBoxHasFilter = true;
  @Input() selectedItemIds: any;
  @Input() sortDirection = Sort.ASC;
  formGroup: FormGroup;
  private _subscription: Subscription;
  isFilter = false;
  @Output() filterChange = new EventEmitter<any[]>();

  ngOnInit(): void {
    this._subscription = this.formGroup.valueChanges
      .pipe(distinctUntilChanged())
      .pipe(
        map((form) =>
          Object.entries(form)
            .filter((kv) => kv[1] === true)
            .map((kv) => kv[0])
        )
      )
      .subscribe((value) => {
        this.filterChange.emit(value);
        this.isFilter = value.length > 0;
      });

    this.selectedItemIds?.forEach((x: any) => this.formGroup.controls[x].patchValue(true, { onlySelf: true, emitEvent: false }));
    this.isFilter = this.selectedItemIds.length > 0;
  }

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.filterBoxDataSource) {
      this.setup();
      this.sortByValue(this.sortDirection);
    }

  }

  private setup() {
    this.filterBoxDataSource.forEach((x: any) => {
      this.formGroup.addControl(x.id, new FormControl(false), {
        emitEvent: false,
      });
    });
  }

  mainMenuOpen() {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose() {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  constructor(private fb: FormBuilder) {
    this.formGroup = this.fb.group({});
  }

  sort() {
    this.sortDirection = Sort.ASC === this.sortDirection ? Sort.DESC : Sort.ASC;
    this.sortByValue(this.sortDirection);
  }

  private sortByValue(sortDirection: string): void {
    const sorted = Array.from(this.filterBoxDataSource).sort((a: any, b: any) => a?.name?.toLowerCase()?.localeCompare(b?.name?.toLowerCase(), undefined, {numeric: true}));
    this.filterBoxDataSource = sortDirection === Sort.ASC ? sorted : sorted.reverse();
  }

  reset() {
    this.sortDirection = Sort.ASC;
    this.sortByValue(this.sortDirection);
  }
}
