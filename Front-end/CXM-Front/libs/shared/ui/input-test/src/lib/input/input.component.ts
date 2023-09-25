import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.css']
})
export class InputComponent{

  @Input() value: string;
  @Output() onchange = new EventEmitter<string> ();


}
