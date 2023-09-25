import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { ClientService } from '@cxm-smartflow/client/data-access';
import { map } from 'rxjs/operators';

export class WhiteSpaceValidator {
    static noWhiteSpace(control: AbstractControl) : ValidationErrors | null {
        if((control.value as string).indexOf(' ') >= 0){
            return {noWhiteSpace: true}
        }
        return null;
    }
}

export function checkDuplicatedClientName(clientService: ClientService): AsyncValidatorFn {
  return (control: AbstractControl) => {
    return clientService.checkDuplicatedClientName({ name: control.value }).pipe(
      map(existed => {
        return existed === true ? { 'existed': true } : null;
      })
    )
  }
}
