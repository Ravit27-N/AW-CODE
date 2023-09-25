import { Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';

export interface CsvUploadComponentState {
  prepared: boolean;
  sending: boolean;
  done: boolean;
  error: any;
  progress: number;
  progressFileName: string;
};

const initialState: CsvUploadComponentState = {
  done: false, error: false, prepared: false, progress: 0, sending: false, progressFileName: ''
};

@Injectable()
export class CsvUploadComponentStore extends ComponentStore<CsvUploadComponentState> {

  constructor() {
    super(initialState);
  }

  readonly uploadState$ = this.select(s => s);

  readonly dropFile = this.updater((state, props: any) => ({ ...state, prepared: true, progressFileName: props.fileName }));

  readonly uploadFile = this.updater((state) => ({ ...state, sending: true  }));

  readonly uploadFileProgression = this.updater((state, props: any) => ({ ...state, progress: props.progress }));


  readonly uploadDone = this.updater((state, props: any) => ({ ...state, done: true, sending: false, loading: false }));

  readonly uploadFail = this.updater((state, props: any) => ({ ...state, done: false, sending: false, error:  props.error.statusCode   }))

  // readonly uploadEffect$ = this.effect()

}
