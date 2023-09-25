import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-redirect',
  templateUrl: './redirect.component.html'
})
export class RedirectComponent implements OnInit {

  @Input() path: string;

  constructor(private router: Router) { }

  ngOnInit(): void {
    if (this.path) {
      this.router.navigateByUrl(this.path);
    }
  }

}
