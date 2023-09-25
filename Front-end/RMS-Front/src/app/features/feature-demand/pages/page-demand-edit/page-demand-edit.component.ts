import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ScopedServiceDemandService } from '../../services/scoped-service-demand.service';
import { ScopedModelDemandDetails } from '../../models/scoped-model-demand-details.model';

@Component({
  selector: 'app-feature-demand-edit',
  templateUrl: './page-demand-edit.component.html',
  styleUrls: ['./page-demand-edit.component.scss'],
})
export class PageDemandEditComponent implements OnInit {
  demandDetails: ScopedModelDemandDetails = {
    id: 0,
    active: false,
    candidate: {},
    candidateId: 0,
    createdAt: new Date(),
    status: false,
    createdBy: new Date(),
    deadLine: new Date(),
    deleted: false,
    experienceLevel: '',
    jobDescriptionId: 0,
    jobDescription: {
      id: 0,
      description: '',
      active: false,
      filename: '',
      title: '',
    },
    nbCandidates: '',
    nbRequired: 0,
    project: {
      id: 0,
      active: false,
      deleted: false,
      description: '',
      name: '',
    },
    projectId: 0,
    updatedAt: new Date(),
  };

  constructor(
    private activatedRoute: ActivatedRoute,
    private scopedServiceDemandService: ScopedServiceDemandService,
  ) {}

  ngOnInit(): void {
    const demandId = this.activatedRoute.snapshot.paramMap.get('id');
    if (demandId) {
      this.scopedServiceDemandService
        .getDemandById(Number(demandId))
        .then((response) => {
          this.demandDetails = response;
        });
    }
  }
}
