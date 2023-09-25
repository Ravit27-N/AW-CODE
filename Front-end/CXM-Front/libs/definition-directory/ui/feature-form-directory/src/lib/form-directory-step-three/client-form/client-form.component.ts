import { Component, EventEmitter, Input, OnChanges, OnDestroy, Output, SimpleChanges } from '@angular/core';
import { ClientModel, FormMode } from '@cxm-smartflow/definition-directory/data-access';
import { BehaviorSubject } from 'rxjs';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { TranslateService } from '@ngx-translate/core';
import { CanModificationService } from '@cxm-smartflow/shared/data-access/services';
import { DirectoryManagement } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-client-form',
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.scss']
})
export class ClientFormComponent implements OnChanges, OnDestroy {

  @Input() clientSources: ClientModel [] = [];
  @Input() clientForm: number [] = [];
  @Input() ownerId: number;
  @Input() formMode: FormMode = 'create';

  @Output() onSelect = new EventEmitter<ClientModel>();
  @Output() onRemove = new EventEmitter<ClientModel>();
  @Output() onDuplicate = new EventEmitter<boolean>();

  clientSelects$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  clientInternal$ = new BehaviorSubject<ClientModel []>([]);

  canModify = true;
  duplicate = false;
  toggleSelect = false;
  arrowIcon = 'add';

  constructor(private translate: TranslateService,
              private canModificationService: CanModificationService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(this.ownerId !== 0 && this.ownerId !== undefined){
      this.canModify = this.canModificationService.hasModify(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
        DirectoryManagement.MODIFY_DEFINITION_DIRECTORY,
        this.ownerId, true);
    }

    if (this.clientSources !== [] && this.clientSources !== undefined) {
      const clientMaps = this.clientSources.map(client => {
        return { key: client.id || 0, value: client.name || '' };
      });

      this.clientSelects$.next(clientMaps);
    }

    if (this.clientForm !== [] && this.clientForm !== undefined
      && this.clientSources !== [] && this.clientSources !== undefined) {
      const clientFormMap = this.clientSources
        ?.filter(item => this.clientForm?.some(clientId => clientId === item.id))
        ?.map(item => {
          return {
            ... item,
            canModify: this.canModify
          }
        })
        ?.sort((a: ClientModel, b: ClientModel) => (a?.name || '') < (b?.name || '') ? -1 : 1);
      this.clientInternal$.next(clientFormMap);
    }
  }

  ngOnDestroy() {
    this.clientSelects$.unsubscribe();
    this.clientInternal$.unsubscribe();
  }

  selectEvent(sourceSelect: number) {
    const clientMaps = [...this.clientInternal$.value];
    // validate client duplicate.
    if (clientMaps?.some(value => value.id === sourceSelect)) {
      this.setDuplicate(true);
    } else {
      this.clientSources?.filter(value => sourceSelect === value.id)
        .forEach(client => {
          client.canModify = true;
          clientMaps.push(client);
          this.onSelect.emit(client);
        });

      const clientSorted = clientMaps?.sort((a: ClientModel, b: ClientModel) => (a?.name || '') < (b?.name || '') ? -1 : 1);
      this.clientInternal$.next(clientSorted);
      this.toggleSelect = false;
      this.switchArrowIcon(this.toggleSelect);
      this.setDuplicate(false);
    }
  }

  removeEvent(client: ClientModel) {
    const clientMaps = this.clientInternal$.value?.filter(item => item.id !== client.id)
      ?.sort((a: ClientModel, b: ClientModel) => (a?.name || '') < (b?.name || '') ? -1 : 1);
    this.setDuplicate(false);
    this.clientInternal$.next(clientMaps);
    this.onRemove.emit(client);
  }

  addClient() {
    this.toggleSelect = !this.toggleSelect;
    this.setDuplicate(false);
    this.switchArrowIcon(this.toggleSelect);
  }

  private switchArrowIcon(toggleSelect: boolean) {
    this.arrowIcon = toggleSelect ? 'remove' : 'add';
  }

  private setDuplicate(hasDuplicate: boolean) {
    this.duplicate = hasDuplicate;
    this.onDuplicate.next(hasDuplicate);
  }
}
