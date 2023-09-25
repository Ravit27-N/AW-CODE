import { Component, Inject, OnInit } from '@angular/core';
import {MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface error{
  title: string;
  body: string;
}

@Component({
  selector: 'cxm-smartflow-show-information',
  templateUrl: './show-information.component.html',
  styleUrls: ['./show-information.component.scss']
})
export class ShowInformationComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data: error){}

}
