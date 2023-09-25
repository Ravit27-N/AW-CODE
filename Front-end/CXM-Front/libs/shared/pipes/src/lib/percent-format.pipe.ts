import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'percentFormat'
})
export class PercentFormatPipe implements PipeTransform {
  transform(value: any, ...args: any[]): any {
    const number = Number(value) || 0.00;
    return new Intl.NumberFormat(
      localStorage.getItem('locale') || 'fr',
      {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }
    ).format(number).concat(' %');
  }
}
