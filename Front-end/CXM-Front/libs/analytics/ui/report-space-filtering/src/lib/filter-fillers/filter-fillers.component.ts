import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { FilterListModel } from '@cxm-smartflow/analytics/data-access';
import { BehaviorSubject, Subscription } from 'rxjs';


@Component({
  selector: 'cxm-smartflow-filter-fillers',
  templateUrl: './filter-fillers.component.html',
  styleUrls: ['./filter-fillers.component.scss'],
})
export class FilterFillersComponent implements OnInit, OnDestroy, OnChanges {


  @Input() fillers: FilterListModel[] = [];
  @Input() fillersSelected: string[] = [];
  @Input() fillersSearchTermInput = '';
  @Output() fillersChange = new EventEmitter<string[]>();
  @Output() searchTermChange = new EventEmitter<string>();
  fillersDisplay$= new BehaviorSubject<FilterListModel[]>([])

  fillerFormGroup: FormGroup;

  private _subscriptions = new Subscription();


  constructor(private _formBuilder: FormBuilder) {}


  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------


  ngOnInit(): void {
    // Init.
  }


  ngOnChanges(changes: SimpleChanges): void {
    // Simple changes.
    this._subscriptions.unsubscribe();
    this._subscriptions = new Subscription();
    this.fillersDisplay$.next(this.fillers);
    this._setupForm();
    this._observeForm();
  }


  ngOnDestroy(): void {
    this._subscriptions.unsubscribe();
  }


  // -----------------------------------------------------------------------------------------------------
  // @ Access modifier
  // -----------------------------------------------------------------------------------------------------


  get isUseFilter(): boolean {
    return this.fillerFormGroup.controls['keyTerm']?.value?.length > 0;
  }


  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------


  mainMenuOpen() {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }


  mainManuClose() {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  resetForm(): void {
    this.fillersDisplay$.next(this.fillers);
    this.fillerFormGroup.controls['keyTerm'].setValue('');
    this.fillers.forEach(item => {
      this.fillerFormGroup.controls[`${item.id}`].setValue(false);
    });
  }

  emitData(): void {
    const fillers = this.fillerFormGroup.getRawValue();

    let keysWithTrueValue = Object.keys(fillers).filter(key => fillers[key] === true);
    keysWithTrueValue = keysWithTrueValue.map(id => {
      return this.fillers.find(filler => filler.id === Number(id))?.key || '';
    })
    this.fillersChange.emit(keysWithTrueValue);
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _setupForm(): void {
    this._setupFillerForm();
  }


  private _setupFillerForm(): void {
    this.fillerFormGroup = this._formBuilder.group({});
    this.fillers.forEach((filler) => {
      this.fillerFormGroup.addControl(`${filler.id}`, new FormControl(this.fillersSelected?.includes(`${filler.key}`)), { emitEvent: false });
    });

    this.fillerFormGroup.addControl('keyTerm', new FormControl(this.fillersSearchTermInput), { emitEvent: false });
  }


  private _observeForm(): void {
    this._subscriptions.add(this.fillerFormGroup.controls['keyTerm'].valueChanges.subscribe(rarValue => {
      this.searchTermChange.emit(rarValue);
      this.emitData();
    }));
  }

}
