import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CandidateExperiences } from '../feature-candidate-detail.component';

@Component({
  selector: 'app-candidate-experience',
  templateUrl: './candidate-experience.component.html',
  styleUrls: ['./candidate-experience.component.scss'],
})
export class CandidateExperienceComponent implements OnChanges {
  tableLabel = [];
  information: CandidateExperiences[] = [];

  informationDisplay: CandidateExperiences = null;

  @Input() candidateExperience: CandidateExperiences[] = [];
  constructor() {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes) {
      this.mapData(this.candidateExperience);
    }
  }

  tabChanged(event: any) {
    this.informationDisplay = this.information[event.index];
  }

  mapData(candidateExperience: CandidateExperiences[]) {
    const label = [];
    for (const data of candidateExperience) {
      label.push(data.companyName);
    }
    this.tableLabel = label;
    this.information = candidateExperience;
    this.informationDisplay = this.information?.[0];
  }
}
