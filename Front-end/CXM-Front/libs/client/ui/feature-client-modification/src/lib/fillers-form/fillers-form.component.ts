import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';


interface IFiller { key: string; value: string; enabled: boolean, id?: string };

@Component({
  selector: 'cxm-smartflow-fillers-form',
  templateUrl: './fillers-form.component.html',
  styleUrls: ['./fillers-form.component.scss']
})
export class FillersFormComponent implements OnInit, OnChanges, OnDestroy {

  @Output() valueChanged = new EventEmitter<Array<IFiller>>();

  fillersConfigOld: Array<IFiller> = [];
  @Input() fillersConfig: Array<IFiller>;
  initialized = false;

  form: FormGroup;
  formModel: Array<IFiller> = [];
  initFormModel: Array<IFiller> = [];
  oneTimeInit = 0;

  subscription: Subscription;

  private setupForm() {
    this.removeAllControl();

    const controls = this.formModel.reduce(this.mapCreateFromControl, { })
    Object.keys(controls).forEach((k) => this.form.addControl(k, controls[k], { emitEvent: false }));
  }

  private formValueChanges() {
    let result = Array.from(Object.keys(this.form.controls)).map(k => {
      return { value: this.form.controls[k].value, enabled: this.form.controls[k].enabled, key: k }
    })


    result = result.map(item => {
      const f = this.formModel.find(x => x.key === item.key);
      return f ? { ...item, id: f.id } : item;
    })
    this.valueChanged.emit(result);
  }

  mapCreateFromControl = (prev: any, cur: any, index: number) => {
    const control = { [cur.key]: this.fb.control({ value: cur.value, disabled: cur.enabled===false }, Validators.maxLength(20))};
    return Object.assign(prev, control);
  }

  private removeAllControl() {
    Object.keys(this.form.controls).forEach((k) => this.form.removeControl(k));
  }

  checkboxChanged($event: any, ctrl: any) {
    if($event) {
      this.form.controls[ctrl].enable();
    } else {
      this.form.controls[ctrl].disable();
    }
  }

  ngOnInit(): void {

    this.setupForm();

    // Tracking changes
    this.subscription =
    this.form.valueChanges.pipe(debounceTime(500), distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)))
    .subscribe(() => this.formValueChanges())
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.fillersConfig
      /**&& !changes.fillersConfig.firstChange && !this.initialized **/
      ) {
      this.formModel = [...changes.fillersConfig.currentValue];
      this.setupForm();
      this.assignValuesToInitFormModel(changes);
      this.initialized = true;
    }
  }

  private assignValuesToInitFormModel(changes: SimpleChanges) {
    if (this.oneTimeInit <= 2) {
      this.fillersConfigOld = this.fillersConfig;
      this.initFormModel = [...changes.fillersConfig.currentValue];
    }
    this.oneTimeInit++;
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({ });
  }
}
