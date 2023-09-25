import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'na',
})
export class NoAvailablePipe implements PipeTransform {

  transform(value: any) {
    if (
      !value ||
      typeof value === 'number' && value <= 0
    ) {
      return 'N/A';
    }

    return value;
  }
}
