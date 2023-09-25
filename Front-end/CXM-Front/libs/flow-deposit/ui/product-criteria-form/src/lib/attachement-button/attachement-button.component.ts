import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'cxm-smartflow-attachement-button',
  templateUrl: './attachement-button.component.html',
  styleUrls: ['./attachement-button.component.scss']
})
export class AttachementButtonComponent implements OnInit {


  @Input() placeholder = '';
  @Input() disabled = false;
  @Output() attach = new EventEmitter();

  constructor() {
    //
  }

  ngOnInit(): void {
  }

  doAttach(): void {
    this.attach.emit();
  }

}
