import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { AppConfigService } from '../../core';

@Pipe({
  name: 'awDateFormat',
})
export class AwDateFormatPipe extends DatePipe implements PipeTransform {
  constructor(private appConfigService: AppConfigService) {
    super('en-US');
  }

  transform(value: any): any {
    const format = this.appConfigService.get('datetimeFormat');
    if (format) {
      return super.transform(value, format);
    }

    return super.transform(value, 'dd/MMM/yyyy hh:mm a');
  }
}
