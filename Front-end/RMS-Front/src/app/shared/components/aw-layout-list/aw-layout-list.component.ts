import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-aw-layout-list',
  templateUrl: './aw-layout-list.component.html',
  styleUrls: ['./aw-layout-list.component.scss'],
})
export class AwLayoutListComponent implements OnInit {
  @Input() pageTitle = '';
  @Input() pageSubtitle = '';

  constructor() {}

  ngOnInit(): void {}
}
