import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AppConfigService } from '../../core';

@Pipe({
  name: 'awDateNoTimeFormat',
})
export class AwDateNoTimeFormatPipe extends DatePipe implements PipeTransform {
  constructor(private appConfigService: AppConfigService) {
    super('en-US');
  }

  transform(value: any): any {
    const format = this.appConfigService.get('datetimeFormatNoTime');
    if (format) {
      return super.transform(value, format);
    }

    return super.transform(value, 'dd/MMM/yyyy');
  }
}
