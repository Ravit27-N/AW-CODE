import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-filter-group',
  templateUrl: './filter-group.component.html',
  styleUrls: ['./filter-group.component.scss']
})
export class FilterGroupComponent {

  @Input() items: { title: string }[] = [];
  @Input() value: string;
  @Output() onchange = new EventEmitter<string>();
}
