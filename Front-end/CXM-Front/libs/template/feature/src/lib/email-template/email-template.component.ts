import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

interface SideBarDataSource {
  icon?: string;
  text: string;
  link: string;
}

@Component({
  selector: 'cxm-smartflow-email-template',
  templateUrl: './email-template.component.html',
  styleUrls: ['./email-template.component.scss'],
})
export class EmailTemplateComponent implements OnInit {
  sideBarDataSource: SideBarDataSource[];
  name = 'Get Current Url Route Demo';
  currentRoute: string;
  constructor(private router: Router, private activatedRoute: ActivatedRoute) {
    this.sideBarDataSource = [
      {
        icon: 'settings',
        text: 'Paramètres',
        link: 'feature-create-email-template',
      },
      { icon: 'edit', text: 'Composition', link: 'destination' },
      {
        icon: 'description',
        text: 'Récapitulatif',
        link: 'feature-summary-email-template',
      },
    ];
  }

  ngOnInit() {
    this.router.url.split('/').forEach((element) => {
      this.currentRoute = element;
    });
  }
}
