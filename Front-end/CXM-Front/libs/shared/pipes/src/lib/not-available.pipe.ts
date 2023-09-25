import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'na'
})
export class NotAvailablePipe implements PipeTransform {

  default_value = '-';

  transform(value: any, ...args: string[]): string {
    // eslint-disable-next-line no-extra-boolean-cast
    if(!!value) {
      return value;
    }

    return this.default_value;
  }

}
