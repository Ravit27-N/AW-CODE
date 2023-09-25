import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'toNumber',
})
export class ToNumberPipe implements PipeTransform {
  transform(value: string | number | undefined): any {
    const retNumber = Number(value);
    return isNaN(retNumber) ? 0 : retNumber;
  }
}
