import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { formatDate } from '@cxm-smartflow/shared/utils'

const CXM_FORMAT = 'dd/MM/yyyy HH:mm';

@Pipe({
  name: 'cxmdate'
})
export class CXMDateFormatPipe extends DatePipe implements PipeTransform {

  transform(value: any, parse?: string, targetFormat?: string): any {
    if(!value) {
      return '';
    }

    if(parse) {
      return super.transform(formatDate.formatParse(value, parse), targetFormat? targetFormat : CXM_FORMAT);
    }
    return super.transform(value, CXM_FORMAT);
  }

}
