import {Component, OnDestroy, OnInit, EventEmitter, Output, OnChanges, SimpleChanges, Input} from '@angular/core';
import { ClientList, ClientModel, FormMode } from '@cxm-smartflow/definition-directory/data-access';
import { TranslateService } from '@ngx-translate/core';
import { CxmProfileService } from '@cxm-smartflow/shared/data-access/api';
import { cxmProfileEnv as env } from '@env-cxm-profile';
import { HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Router } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-form-directory-step-three',
  templateUrl: './form-directory-step-three.component.html',
  styleUrls: ['./form-directory-step-three.component.scss']
})
export class FormDirectoryStepThreeComponent implements OnInit, OnChanges, OnDestroy {

  clientForm$ = new BehaviorSubject<number []>([]);
  ownerId$ = new BehaviorSubject<number>(0);
  clientSource$ = new BehaviorSubject<ClientModel[]>([]);

  @Input() step3SelectedClient: number[] = [];
  @Input() selectOwnerId: number;
  @Input() formMode: FormMode = 'create';
  @Output() submitFormEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() stepThreePreviousPageEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() selectClientEvent = new EventEmitter<number>();
  @Output() removeClientEvent = new EventEmitter<number>();

  hasDuplicate = false;

  constructor(private translate: TranslateService, private profileService: CxmProfileService,
              private snackBar: SnackBarService, private router: Router) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  ngOnInit(): void {
    let httpParams = new HttpParams();
    httpParams = httpParams.set('page', 0);
    httpParams = httpParams.set('pageSize', 0);
    httpParams = httpParams.set('sortByField', 'name');
    httpParams = httpParams.set('sortDirection', 'asc');

    this.profileService.get(`${env.profileContext}/clients`, httpParams).subscribe((clientList: ClientList) => {
        const clientMaps = clientList.contents?.map(client => {
          const clientMap: ClientModel = {
            id: client.id,
            name: client.name,
            active: true
          };
          return clientMap;
        });
        this.clientSource$.next(clientMaps || []);
      },
      () => {
        this.translate.get('directory.definition.client-directory-form.loadClientFail').toPromise()
          .then(message => {
            this.snackBar.openCustomSnackbar({ icon: 'close', type: 'error', message: message });
          });
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.clientForm$.next(this.step3SelectedClient);
    this.ownerId$.next(this.selectOwnerId);
  }

  onSelectClient(client: ClientModel) {
    this.selectClientEvent.emit(client.id);
  }

  onRemoveClient(client: ClientModel) {
    this.removeClientEvent.emit(client.id);
  }

  onDuplicateClient(hasDuplicate: boolean) {
    this.hasDuplicate = hasDuplicate;
  }

  onPrevious() {
   this.stepThreePreviousPageEvent.next(true);
  }

  onSubmit() {
    if (!this.hasDuplicate && (this.formMode !== 'view') ) {
      this.submitFormEvent.next(true);
    }
  }

  ngOnDestroy(): void {
    this.clientForm$.unsubscribe();
    this.clientSource$.unsubscribe();
  }
}
