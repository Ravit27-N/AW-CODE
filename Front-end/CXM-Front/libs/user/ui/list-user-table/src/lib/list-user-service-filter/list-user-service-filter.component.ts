import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { CustomAngularMaterialUtil, Sort } from '@cxm-smartflow/shared/utils';
import { Subscription } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';
@Component({
  selector: 'cxm-smartflow-list-user-service-filter',
  templateUrl: './list-user-service-filter.component.html',
  styleUrls: ['./list-user-service-filter.component.scss']
})
export class ListUserServiceFilterComponent implements OnInit {
 
  @Input() services: any;
  formGroup: FormGroup;
  subscription: Subscription;

  @Input() usingFilter = false;
  @Input() restoreFilterFunction: () => any;
  @Input() sortDirection = Sort.ASC;
  @Output() onchange = new EventEmitter<string[]>();

  ngOnInit(): void {
    this.subscription = this.formGroup.valueChanges
    .pipe(distinctUntilChanged())
    .pipe(map(form => Object.entries(form).filter(kv => kv[1] === true).map(kv => kv[0])))
    .subscribe(value => this.onchange.emit(value));
  }


  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.services) {
      this.setup();

      if(!changes.services.firstChange) {
        this.sortByValue(this.sortDirection);
      }
     // console.log("this is services"+  JSON.stringify(this.services));
      
    }
  }

  private setup() {
    if(!this.formGroup) {
      this.formGroup = this.fb.group({});
    }

    this.services.forEach((x: any) => {
      this.formGroup.addControl(x.id, new FormControl(false), { emitEvent: false })
    });

    const checkedIds = this.restoreFilterFunction();
    checkedIds?.forEach((x: any) => this.formGroup.controls[x].patchValue(true), { emitEvent: false })
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
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
    const sorted = Array.from(this.services).sort((a: any, b: any) => a.name?.toLowerCase()?.localeCompare(b.name?.toLowerCase(), undefined, {numeric: true}));
    this.services = sortDirection === Sort.ASC ? sorted : sorted.reverse();
  }

  reset() {
    this.sortDirection = Sort.ASC;
    this.sortByValue(this.sortDirection);
  }
}
