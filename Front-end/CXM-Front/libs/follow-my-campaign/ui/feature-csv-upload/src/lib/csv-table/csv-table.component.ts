import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Sort } from '@angular/material/sort';
import { Observable } from 'rxjs';
import { SMS } from "@cxm-smartflow/shared/data-access/model";

@Component({
  selector: 'cxm-smartflow-csv-table',
  templateUrl: './csv-table.component.html',
  styleUrls: ['./csv-table.component.scss']
})
export class CsvTableComponent {

  @Input() datasource: Observable<any>;

  @Input() columnsInfo: any[];

  @Input() showColumn: string[] = [];

  @Output() onsort = new EventEmitter<Sort>();

  @Input() KO = '';

  sort(event: Sort) {
    this.onsort.emit(event);
  }

  getColumnHeader(columnHeader: string): string {
    const TelDestinataire = columnHeader.replace(SMS.key, SMS.key.replace('Tel', 'Tel '));
    return columnHeader === SMS.key ? TelDestinataire : columnHeader;
  }

}
