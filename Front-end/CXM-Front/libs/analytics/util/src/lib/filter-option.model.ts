import { HttpParams } from '@angular/common/http';

type AnyObject = { [key: string]: any };

export class HttpParamsBuilder<T extends AnyObject> {
  private data: T = {} as T;

  constructor() {}

  set(data: T): HttpParamsBuilder<T> {
    this.data = { ...this.data, ...data };
    return this;
  }

  removeFalsyFields(): HttpParamsBuilder<T> {
    for (const [key, value] of Object.entries(this.data)) {
      if (!value || (Array.isArray(value) && value.length === 0)) {
        delete this.data[key];
      }
    }
    return this;
  }

  build(): HttpParams {
    let params = new HttpParams();
    for (const [key, value] of Object.entries(this.data)) {
      if (Array.isArray(value)) {
          params = params.set(key, value.join(','));
      } else {
        params = params.set(key, value);
      }
    }
    return params;
  }
}
