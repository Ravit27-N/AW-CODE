import { Pipe, PipeTransform } from '@angular/core';


const convertReturnToBr = (text: string) => text.replace(/\n/g, "<br />");

@Pipe({
  name: 'preview'
})
export class PreviewPipe implements PipeTransform {

  transform(value: any, ...args: string[]): string {
    // TODO: escape html
    return convertReturnToBr(value);
  }

}
