import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-confirm-logout',
  templateUrl: './confirm-logout.component.html',
  styleUrls: ['./confirm-logout.component.scss']
})
export class ConfirmLogoutComponent implements OnInit {

  constructor(private router: Router, private activatedRoutes: ActivatedRoute) {}

  ngOnInit(): void {

    const locale = localStorage.getItem('locale') || 'fr';

    localStorage.clear();
    localStorage.setItem('locale', locale);

    const { returnTo } = this.activatedRoutes.snapshot.queryParams;
    if(returnTo) {
      this.router.navigate(['/'], { queryParams: { returnTo }  });
    } else {
      this.router.navigateByUrl('/');
    }


  }


}
