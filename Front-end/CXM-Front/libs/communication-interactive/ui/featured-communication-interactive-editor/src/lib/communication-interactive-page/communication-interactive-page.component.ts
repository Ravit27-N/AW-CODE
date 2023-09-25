import { Component } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-communication-interactive-page',
  templateUrl: './communication-interactive-page.component.html',
  styleUrls: ['./communication-interactive-page.component.scss']
})
export class CommunicationInteractivePageComponent {

  ticketUrl$: Observable<SafeResourceUrl>;

  getTicket() {
    const url = window.prompt("Enter interactive URL :");

    if(url) {
      this.ticketUrl$ = of(this.domSanitizer.bypassSecurityTrustResourceUrl(url));
    }
  }


  log() {
    this.ticketUrl$ = of();
  }

  constructor(private readonly domSanitizer: DomSanitizer ) { }

}
