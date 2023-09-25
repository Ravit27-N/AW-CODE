import { DataSource } from '@angular/cdk/collections';
import { Observable, ReplaySubject } from 'rxjs';

export class TableDatasource<T> extends DataSource<T>{
  private _dataStream = new ReplaySubject<T[]>();

  constructor(initialData: T[]) {
    super();
    this.setData(initialData);
  }

  connect(): Observable<T[]> {
    return this._dataStream;
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  disconnect(): void {}

  public setData(data: T[]) {
    this._dataStream.next(data);
  }
}
