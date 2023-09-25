import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CheckListModel } from '@cxm-smartflow/shared/ui/dropdown-filter-criterial';
import { CustomAngularMaterialUtil, Sort } from '@cxm-smartflow/shared/utils';
import { CheckedListKeyValue } from '@cxm-smartflow/flow-traceability/data-access';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-dropdown-filter-user',
  templateUrl: './dropdown-filter-user.component.html',
  styleUrls: ['./dropdown-filter-user.component.scss']
})
export class DropdownFilterUserComponent implements OnChanges {
  @Input() users: CheckListModel [] = [];
  @Output() formChangeEvent = new EventEmitter<string []>();

  @Input() useUserFilter = false;

  sortDirection = Sort.DESC;
  users$ = new BehaviorSubject([] as CheckListModel[]);
  allUsers: CheckListModel [] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.users) {
      this.sortUsers(this.sortDirection);
      this.allUsers = this.users$.getValue();
      this.users$.next(this.filterUniqueUsers());
      this.users = this.users$.getValue();
    } else {
      this.preventCheckboxUncheckFromSorting();
    }
  }

  preventCheckboxUncheckFromSorting() {
    this.users$.next(this.users$.getValue()); // prevent checkbox from sorting when user not click change direction
    this.users = this.users$.getValue();
  }

  filterUniqueUsers(): CheckListModel [] {
    const uniqueUsers = new Map<string, CheckListModel>();
    this.allUsers.forEach(user => {
      if (!uniqueUsers.has(user.key)) {
        uniqueUsers.set(user.key, user);
      }
    });
    return Array.from(uniqueUsers.values())
  }

  reset(){
    this.sortDirection = Sort.DESC;
    this.sortUsers(this.sortDirection);
    this.formChangeEvent.emit([]);
    this.resetUsers();
  }

  resetUsers() {
    this.users$.next(this.users.map((item) => ({...item, checked: false})));
    const allUserChecked = this.users$.getValue().map(user => user.value);
    const invisibleUsers = this.allUsers.filter(user => !allUserChecked.includes(user.value));
    this.allUsers = this.users$.getValue().concat(invisibleUsers);
  }

  itemCheckEvent($event: string[]) {
    this.formChangeEvent.emit($event);
  }

  changeDirection(): void {
    this.sortDirection = this.sortDirection === Sort.ASC ? Sort.DESC : Sort.ASC;
    this.sortUsers(this.sortDirection);
  }

  sortUsers(sortDirection: string): void {
    const sorted = this.users.sort((a: CheckedListKeyValue, b: CheckedListKeyValue) => a.key?.localeCompare(b.key));
    this.users$.next(sortDirection === Sort.ASC ? sorted : sorted.reverse());
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }
}
