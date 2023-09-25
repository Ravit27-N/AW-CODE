import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { CustomAngularMaterialUtil, Sort } from "@cxm-smartflow/shared/utils";
import { Subscription } from "rxjs";
import { distinctUntilChanged, map, tap } from "rxjs/operators";


@Component({
  selector: 'cxm-smartflow-list-admin-user-filter',
  templateUrl: 'list-admin-user-filter.component.html',
  styleUrls: ['list-admin-user-filter.component.scss']
})
export class ListAdminUserFilterComponent implements OnInit {

  options: any[] = [
    { id: "admin", name: 'user.list.table.admin' },
    { id: "nonadmin", name: 'user.list.table.nonadmin' }
  ];
  formGroup: FormGroup;
  subscription: Subscription;

  @Input() usingFilter  = false;
  @Input() restoreFilterFunction: () => any;
  @Input() sortDirection = Sort.ASC;
  @Output() onchange = new EventEmitter<string[]>();


  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }


  ngOnInit(): void {
    this.setup();

    this.subscription = this.formGroup.valueChanges
    .pipe(distinctUntilChanged())
    .pipe(map(form => Object.entries(form).filter(kv => kv[1] === true).map(kv => kv[0])))
    .subscribe(value => this.onchange.emit(value));
  }

  private setup() {
    this.formGroup = this.fb.group({
      admin: new FormControl(false),
      nonadmin: new FormControl(false)
    })

    const adminUserFilter = this.restoreFilterFunction();
    adminUserFilter && Array.from(adminUserFilter)
    .forEach((x: any) => this.formGroup.controls[x].patchValue(true, { emitEvent: false }))
  }

  reset() {
    this.sortDirection = Sort.ASC;
    // this.sortByValue(this.sortDirection);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  constructor(private fb: FormBuilder) {

  }

}
