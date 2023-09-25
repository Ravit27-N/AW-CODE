import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class GrapeJsUtil {
  public static removeGrapeJsProperties(): Observable<boolean> {
    localStorage.removeItem('gjs-styles');
    localStorage.removeItem('gjs-css');
    localStorage.removeItem('gjs-components');
    localStorage.removeItem('gjs-html');
    localStorage.removeItem('gjs-assets');
    localStorage.removeItem('gjs-css-saved');
    localStorage.removeItem('htmlFile');
    localStorage.removeItem('gjs-html-saved');
    return of(true);
  }
}
