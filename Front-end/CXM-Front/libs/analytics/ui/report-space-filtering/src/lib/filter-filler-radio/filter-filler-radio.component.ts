import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { FilterListModel } from '@cxm-smartflow/analytics/data-access';
import { BehaviorSubject, Subscription } from 'rxjs';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';


@Component({
  selector: 'cxm-smartflow-filter-filler-radio',
  templateUrl: './filter-filler-radio.component.html',
  styleUrls: ['./filter-filler-radio.component.scss'],
})
export class FilterFillerRadioComponent implements OnInit, OnDestroy, OnChanges {


  @Input() fillerTitle = '';
  @Input() fillers: FilterListModel[] = [];
  @Input() fillersSelected: string;
  @Input() disabledFillerKeys: string[] = [];
  @Input() hiddenFillerKeys: string[] = [];
  @Input() isDisabled = false;
  @Input() fillersSearchTermInput = '';


  @Output() fillersChange = new EventEmitter<{ reset: boolean, selectedFiller: string }>();
  @Output() searchTermChange = new EventEmitter<string>();


  fillersDisplay$= new BehaviorSubject<FilterListModel[]>([]);
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
    const fillers = this.fillerFormGroup.getRawValue();
    const allTruthyControls = Object.keys(fillers).filter((key) => fillers[key] === true);
    return this.fillerFormGroup.controls['keyTerm']?.value?.length > 0 && allTruthyControls.length > 0;
  }


  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------


  mainMenuOpen(): void {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }


  mainManuClose(): void {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  /**
   * Reset the form.
   */
  resetForm(): void {
    if (this.isDisabled) return;

    this.fillersDisplay$.next(this.fillers);
    this.fillerFormGroup.controls['keyTerm'].setValue('');
    this.fillersChange.emit({
      reset: true,
      selectedFiller: ''
    });
    this.fillers.forEach(item => {
      this.fillerFormGroup.controls[`${item.id}`].setValue(false);

      // Disable & enable item.
      if (this.disabledFillerKeys.includes(item.key)) {
        this.fillerFormGroup.controls[`${item.id}`].disable({ emitEvent: false, onlySelf: false });
      } else {
        this.fillerFormGroup.controls[`${item.id}`].enable({ emitEvent: false, onlySelf: false });
      }
    });
  }

  /**
   * Validate and emit data.
   */
  validateAndEmitData(): void {
    if (this.isDisabled) return;

    const fillers = this.fillerFormGroup.getRawValue();
    const allFalsyControls = Object.keys(fillers).filter((key) => fillers[key] === false);
    const allTruthyControls = Object.keys(fillers).filter((key) => fillers[key] === true);

    // Enable and disable checkbox button.
    if (!allTruthyControls.length) {
      allFalsyControls.forEach(item => {

        const key = this.fillersDisplay$.value.find(fillerDisplay => `${fillerDisplay.id}` === `${item}`)?.key || '';

        if (this.disabledFillerKeys.includes(key)) {
          this.fillerFormGroup.controls[`${item}`].disable({ emitEvent: false, onlySelf: false });
        } else {
          this.fillerFormGroup.controls[`${item}`].enable({ emitEvent: false, onlySelf: false });
        }

      });
    } else {
      allFalsyControls.forEach(item => {
        this.fillerFormGroup.controls[`${item}`].disable({ emitEvent: false, onlySelf: false });
      });
    }

    // Emit data.
    const key = this.fillersDisplay$.value.find(item => item.id === Number(allTruthyControls[0]))?.key || '';

    if (this.fillersSelected === key || this.isDisabled) return;
    this.fillersChange.emit({
      selectedFiller: allTruthyControls.length ? key : '',
      reset: false,
    });
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
      if (this.isDisabled || this.disabledFillerKeys.includes(filler.key)) {
        this.fillerFormGroup.controls[`${filler.id}`].disable({ emitEvent: false, onlySelf: false });
      }
    });


    this.fillerFormGroup.addControl('keyTerm', new FormControl(this.fillersSearchTermInput), { emitEvent: false });
  }


  private _observeForm(): void {
    this._subscriptions.add(this.fillerFormGroup.controls['keyTerm'].valueChanges.subscribe(rarValue => {
      this.searchTermChange.emit(rarValue);
      this.validateAndEmitData();
    }));
  }

}
