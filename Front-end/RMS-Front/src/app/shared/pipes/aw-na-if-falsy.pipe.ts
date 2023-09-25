import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'awNaIfFalsy',
})
export class AwNaIfFalsyPipe implements PipeTransform {
  transform(value: any): any {
    return value ? value : 'N/A';
  }
}
