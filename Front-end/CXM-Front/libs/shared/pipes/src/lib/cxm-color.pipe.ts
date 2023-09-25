import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'cxmColor'
})
export class ColorPipe implements PipeTransform {

  static COLORS = [
    "flow.deposit.productionCriteria.form.label.quardchrome",
    "flow.deposit.productionCriteria.form.label.monochrome"
  ];

  transform(value: any, ...args: any[]) {
    try {
      const colorNumber = parseInt(value);
      if(colorNumber < 0 || colorNumber > ColorPipe.COLORS.length - 1)
        return value;

      return ColorPipe.COLORS[colorNumber];
    } catch(e) {
      return value;
    }
  }

}
