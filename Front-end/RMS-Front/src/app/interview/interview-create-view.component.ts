import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-interview-create-view',
  templateUrl: './interview-create-view.component.html'
})
export class InterviewCreateViewComponent implements OnInit {

  preloadData: any;

  constructor(private pageHistory: Location, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.data.subscribe(data => this.preloadData = data);
  }

  back(): void {
    this.pageHistory.back();
  }
}
