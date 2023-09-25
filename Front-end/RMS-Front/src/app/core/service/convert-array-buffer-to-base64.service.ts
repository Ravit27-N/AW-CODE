import { DomSanitizer } from '@angular/platform-browser';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConvertArrayBufferToBase64Service {
  base64: any;
  constructor(
    private sanitizer: DomSanitizer
  ) { }

  convertArrayBufferToBase64(data: any): any {
    if(!!data){
      const value = btoa([].reduce.call(new Uint8Array(data), (p, c) => p + String.fromCharCode(c), ''));
      this.base64 = this.sanitizer.bypassSecurityTrustUrl('data:image/jpg;base64, ' + value);
      return this.base64;
    }else {
      return null;
    }
  }

}
