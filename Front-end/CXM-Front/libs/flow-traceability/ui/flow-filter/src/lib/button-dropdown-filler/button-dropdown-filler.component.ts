import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { CriteriaStorage, FlowCriteriaSessionService, FlowFilterCriteriaParams } from "@cxm-smartflow/flow-traceability/data-access";
import { CustomAngularMaterialUtil } from "@cxm-smartflow/shared/utils";
import { Store } from "@ngrx/store";
import { Subscription } from "rxjs";
import { debounceTime } from "rxjs/operators";

@Component({
  selector: 'cxm-smartflow-button-filler',
  templateUrl: './button-dropdown-filler.component.html',
  styleUrls: ['button-dropdown-filler.component.scss']
})
export class ButtonDropdownFillerComponent implements OnInit, OnDestroy, OnChanges {

  form: FormGroup;
  initialized = false;
  formSubscription: Subscription;

  fillers: any[] = []
  @Input() fillersConfig: any;
  @Output() fillerChanged: any = new EventEmitter();


  @Input() forComponentType: 'flowTraceability'|'flowDocument'|'viewDocumentShipment';
  @Input() customCssClass = '';
  isUsingFiller = false;

  mainMenuOpen() {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainManuClose() {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  addCustomCssClass() {
    document
      .querySelector('.faculty-custom-dropdown')
      ?.classList.add(this.customCssClass);
  }

  ngOnDestroy(): void {
    this.formSubscription.unsubscribe();
  }

  ngOnInit(): void {
    this.formSubscription = this.form.valueChanges.pipe(debounceTime(300)).subscribe(formValue => {
      const { keyTerm } = formValue;
      const value = { ...formValue, keyTerm: undefined };
      const fillers = Object.keys(value).filter(k => value[k] && value[k]===true);

      this.isUsingFiller = !!keyTerm;
      if(keyTerm) {
        if(fillers.length > 0) {
          this.fillerChanged.emit({ keyTerm: keyTerm, fillers });
        }else {
          this.fillerChanged.emit({ keyTerm: keyTerm, fillers: [] });
        }
      } else {
        this.fillerChanged.emit({ keyTerm: '', fillers: [] });
      }
    });
  }

  private setupForm() {
    // get previous state if have
    let criteriaParams: FlowFilterCriteriaParams | undefined;

    if(this.forComponentType === 'viewDocumentShipment') {
      const storage = this.storageService.getDocumentShipmentCriteria();
      criteriaParams = storage.criteriaParams;
    } else {
      const storage = this.storageService.getDocumentCriteria();
      criteriaParams = storage.criteriaParams;
    }

    let { searchByFiller, fillers } = criteriaParams ?? { };

    searchByFiller = searchByFiller ?? "";
    fillers = fillers ?? [];
    this.isUsingFiller = !!searchByFiller;

    this.form.removeControl('keyTerm');
    Object.keys(this.form.controls).forEach((k) => this.form.removeControl(k));

    this.form.addControl('keyTerm', new FormControl(searchByFiller ?? ""), { emitEvent: false  })

    const ctrls = Array.from(this.fillers)
    .reduce((prev: any, cur: any) => {
      const control = { [cur.key]: this.fb.control(fillers?.includes(cur.key)) }
      return Object.assign(prev, control);
    }, { });
    Object.keys(ctrls).forEach((k) => this.form.addControl(k, ctrls[k], { emitEvent: false }));
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.fillersConfig && !this.initialized) {
      const { loaded, fillers} = changes.fillersConfig.currentValue;

      if(loaded === true) {
        this.fillers = fillers;
        this.setupForm();
        this.initialized = true;
      }
    }
  }


  constructor(private fb: FormBuilder, private store: Store, private storageService: FlowCriteriaSessionService) {
    this.initialized = false;
    this.form = this.fb.group({ })
    this.form.addControl('keyTerm', new FormControl(""), { emitEvent: false  })
   }


}
