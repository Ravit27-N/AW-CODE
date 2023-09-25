import { DataSource } from "@angular/cdk/collections";
import { Observable, ReplaySubject } from "rxjs";


export class FlowtrackingDatasource<T> extends DataSource<T> {

  private _datastream = new ReplaySubject<any[]>();
  constructor(initialData: any[]) {
    super();
    this.setData(initialData);
  }

  connect(): Observable<T[]> {
    return this._datastream;
  }

  disconnect() {
    //
  }

  setData(data: T[]) {
    this._datastream.next(data);
  }
}
