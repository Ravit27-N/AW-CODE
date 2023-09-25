import { ErrorHandler, Injectable } from "@angular/core";

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  errors = [
    'ScriptExternalLoadError',
    'ChunkLoadError'
  ]

  handleError(error: any): void {
    console.error(error);

    if(error && error.rejection) {
      const { name } = error.rejection;

      if(this.errors.includes(name)) {
        caches.keys().then(function(items) {
          for (const item of items) caches.delete(item);
        });

        window.location.reload();
      }
    } else {
      // throw error;
    }

  }

}
