import { Directive, HostListener } from '@angular/core';
import { TitleCaseUtil } from '@cxm-smartflow/shared/utils';

@Directive({
  selector: '[cxmSmartflowTitleCase]',
})
export class TitleCaseDirective {
  @HostListener('input', ['$event']) onInputChange(event: any): string {
    event.target.value = TitleCaseUtil.convert(event.target.value);
    return event.target.value;
  }
}
