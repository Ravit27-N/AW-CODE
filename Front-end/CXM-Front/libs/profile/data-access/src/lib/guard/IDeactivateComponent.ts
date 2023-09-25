import { Observable } from 'rxjs';

export interface IDeactivateComponent {
  canExit: () => Observable<boolean>;
}
