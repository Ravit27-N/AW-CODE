import { Component, OnInit } from '@angular/core';
import {
  Confirmable,
  fromModifyClientSelector,
  fromModifyClientActions,
} from '@cxm-smartflow/client/data-access';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-setting-digital-channel-page',
  templateUrl: './setting-digital-channel-page.component.html',
  styleUrls: ['./setting-digital-channel-page.component.scss'],
})
export class SettingDigitalChannelPageComponent implements Confirmable {

  constructor(private _store: Store, private _activatedRoute: ActivatedRoute) {
    this._store.dispatch(fromModifyClientActions.setClientName({ clientName: this._activatedRoute.snapshot.params.clientName }));
  }

  back() {
    history.back();
  }

  isLocked(): Observable<boolean> {
    return this._store.select(fromModifyClientSelector.selectIsLocked);
  }

  modifyHubAccount(): void {
    this._store.dispatch(fromModifyClientActions.fetchHubAccessAccount());
  }

  modifySenderNameMetadata(): void {
    this._store.dispatch(fromModifyClientActions.fetchMetadataByType({ metadataType: 'sender_name'}));
  }

  modifySenderEmailMetadata(): void {
    this._store.dispatch(fromModifyClientActions.fetchMetadataByType({ metadataType: 'sender_mail'}));
  }

  modifyUnsubscribeLinkMetadata(): void {
    this._store.dispatch(fromModifyClientActions.fetchMetadataByType({ metadataType: 'unsubscribe_link'}));
  }

  modifySenderLabelMetadata(): void {
    this._store.dispatch(fromModifyClientActions.fetchMetadataByType({ metadataType: 'sender_label'}));
  }

  modifyServiceProvider(): void {
    this._store.dispatch(fromModifyClientActions.fetchServiceProvider());
  }


}
