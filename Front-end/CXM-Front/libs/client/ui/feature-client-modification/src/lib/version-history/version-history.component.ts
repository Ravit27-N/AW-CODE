import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { PostalConfigurationVersion } from '@cxm-smartflow/client/data-access';
import {
  ConfirmRevertConfigurationVersionService
} from '../confirm-revert-configuration-version/confirm-revert-configuration-version.service';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-version-history',
  templateUrl: './version-history.component.html',
  styleUrls: ['./version-history.component.scss']
})
export class VersionHistoryComponent implements OnChanges {
  @Input() configurationVersion: PostalConfigurationVersion [];

  @Output() onViewEvent = new EventEmitter<PostalConfigurationVersion>();
  @Output() onPreviousEvent = new EventEmitter<PostalConfigurationVersion>();
  @Output() onCurrentEvent = new EventEmitter<PostalConfigurationVersion>();

  versionHighlighted: number;
  versionSelected: number;
  lastVersionSelected = true;

  constructor(private _confirmRevertConfigurationVersionService: ConfirmRevertConfigurationVersionService,
              private _translationService: TranslateService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
        if(this.configurationVersion !== undefined){
          this.lastVersionSelected = true;
        }
    }

  getTooltipMsg(configVersion: PostalConfigurationVersion): Observable<string> {
    return this._translationService.get('client').pipe(map(messages => {
      return `${messages.configuration_reference_version} ${configVersion.referenceVersion}`;
    }));
  }

  matMenuOpen(configVersion: PostalConfigurationVersion, index: number): void {
    this.versionHighlighted = index;
    document.querySelector('.mat-menu-config-version-action-panel')
      ?.classList?.add('custom-mat-menu-config-version-action-button');
  }

  matMenuClose(configVersion: PostalConfigurationVersion, index: number): void {
    this.versionHighlighted = -1;
  }

  onSelectViewEvent(configVersion: PostalConfigurationVersion, index: number): void {
    this.versionSelected = index;
    this.lastVersionSelected = false;
    this.onViewEvent.next(configVersion);
  }

  async onSelectPreviousEvent(configVersion: PostalConfigurationVersion): Promise<void> {
    const confirmRevert = await this._confirmRevertConfigurationVersionService.show().toPromise();
    if (confirmRevert) {
      this.versionSelected = -1;
      this.onPreviousEvent.next(configVersion);
    }
  }

  onSelectCurrentEvent(configVersion: PostalConfigurationVersion): void {
    this.versionHighlighted = -1;
    this.versionSelected = -1;
    this.lastVersionSelected = true;
    this.onCurrentEvent.next(configVersion);
  }

  formatDate(date: Date): Observable<string> {
    date =  new Date(date);

    const year = `${date.getFullYear()}`;
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    const hours = `${date.getHours()}`.padStart(2, '0');
    const minutes = `${date.getMinutes()}`.padStart(2, '0');

    return this._translationService.get('client').pipe(map(messages => {
      return `${messages.configuration_on} ${day}/${month}/${year} ${messages.configuration_at} ${hours}:${minutes}`;
    }));
  }

  formatSelectedVersion(version: number, selected: boolean): Observable<string> {
    return this._translationService.get('client').pipe(map(messages => {
      let message = `${messages.configuration_version} ${version}`;

      if (selected) {
        message = message.replace(':', '');
      }

      return message;
    }));
  }
}
