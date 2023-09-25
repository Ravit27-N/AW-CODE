import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CandidateUniversities } from '../feature-candidate-detail.component';

@Component({
  selector: 'app-candidate-education',
  templateUrl: './candidate-education.component.html',
  styleUrls: ['./candidate-education.component.scss'],
})
export class CandidateEducationComponent implements OnChanges {
  tableLabel = [];
  information: CandidateUniversities[] = [];

  informationDisplay: CandidateUniversities = null;

  @Input() candidateUniversities: CandidateUniversities[] = [];
  constructor() {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes) {
      this.mapData(this.candidateUniversities);
    }
  }

  tabChanged(event: any) {
    this.informationDisplay = this.information[event.index];
  }

  mapData(candidateUniversities: CandidateUniversities[]) {
    const label = [];
    for (const data of candidateUniversities) {
      label.push(data.university?.name);
    }
    this.tableLabel = label;
    this.information = candidateUniversities;
    this.informationDisplay = candidateUniversities?.[0];
  }
}
