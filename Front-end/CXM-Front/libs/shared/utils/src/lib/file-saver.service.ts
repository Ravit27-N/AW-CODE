import { Observable, of } from 'rxjs';
import { Injectable } from '@angular/core';
import * as fileSaver from 'file-saver';

export interface Base64Model{
  content?: string;
  fileSize?: number;
  filename?: string;
}

/**
 * @author Pisey CHORN.
 * @author Sokhour LACH.
 */
@Injectable({providedIn:'root'})
export class FileSaverUtil {

  /**
   * Method used to download file from base64.
   * @param base64
   * @param filename
   */
  public downloadBase64(base64?: string, filename?: string): Observable<boolean>{
    if(!base64) return of(false);

    try {
      const source = `data:application/octet-stream;base64,${base64}`;
      const link = document.createElement("a");
      link.href = source;
      link.download = filename || 'file download';
      link.click();
      return of(true);
    }catch (e){
      return of(false);
    }
  }

  /**
   * Open pdf on new tab on browser.
   * @param base64 - value of {@link string}.
   * @param filename - name of base64 file.
   */
  public async openPdfOnNewTabFromBase64(base64: string, filename?: string): Promise<Observable<boolean>> {
    if (!base64) return of(false);

    try {
      const base64Response = await fetch(
        `data:image/jpeg;base64,${base64}`
      ).then(
        async (d) =>
          new File([await d.blob()], <string>filename, { type: 'application/pdf' })
      );
      //Open pdf to new tab on browser.
      window.open(URL.createObjectURL(base64Response));
      return of(true);
    } catch (e) {
      return of(false);
    }
  }

  public saveCsvFile(data: any, filename?: string) {
    const blob = new Blob([data], {type: 'text/csv; charset=utf-8'});
    fileSaver.saveAs(blob, filename);
  }
}
