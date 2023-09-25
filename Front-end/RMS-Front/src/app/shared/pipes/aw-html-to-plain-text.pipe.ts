import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'awHtmlToPlainText',
})
export class AwHtmlToPlainTextPipe implements PipeTransform {
  transform(value: string): string {
    // Create a new DOMParser
    const parser = new DOMParser();

    // Parse the HTML string
    const doc = parser.parseFromString(value, 'text/html');

    // Extract plain text
    return doc.body.textContent || '';
  }
}
