import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  selector: 'app-aw-setting',
  template: '',
})
export class AwSettingComponent implements OnInit {
  constructor(private location: Location) {}

  ngOnInit(): void {
    this.location.back();
  }
}
