import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { FileUtils } from '@cxm-smartflow/shared/utils';

/**
 * Handle process transform filesize to another size.
 * @author Pisey CHORN.
 * @author Sokhour LACH
 */
@Pipe({name: 'fileSize'})
export class FileSizePipe implements PipeTransform{
  private label = new BehaviorSubject<any>({});
  private sizes = ['Bytes', 'KB', 'MB', 'GB'];

  transform(bytes: number = 0, decimals = 3): string {
    return FileUtils.formatBytes(bytes, decimals);
  }

  private getResult(bytes: number, convertTo: string, decimals = 0): string{
    let result = '';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;

    const shouldInBk = bytes / 1048576;
    if(shouldInBk < k ) {
      return shouldInBk.toFixed(dm).concat(" ").concat(this.label.value?.kb);
    }

    switch (convertTo){
      case this.sizes[1]:
        result = (bytes / k).toFixed(dm).concat(" ").concat(this.label.value?.kb);
        break;
      case this.sizes[2]:
        result = ((bytes / k) / k).toFixed(dm).concat(" ").concat(this.label.value?.mb);
        break;
      case this.sizes[3]:
        result = (((bytes / k) / k) / k).toFixed(dm).concat(" ").concat(this.label.value?.gb);
        break;
      default:
        result = bytes.toFixed(dm).concat(" ").concat(this.label.value?.bytes);
        break;
    }
    return result;
  }

  constructor(private translate: TranslateService) {
    this.translate.get('flowTraceability.documentTraceability.summaryData').subscribe(v => this.label.next(v));
  }

}
