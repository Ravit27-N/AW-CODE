import { Component, OnDestroy } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-espace-validation',
  templateUrl: './espace-validation.component.html',
  styleUrls: ['./espace-validation.component.scss'],
})
export class EspaceValidationComponent implements OnDestroy {
  ngOnDestroy(): void {
    localStorage.setItem('list-espace-flow', JSON.stringify({}));
  }
}
