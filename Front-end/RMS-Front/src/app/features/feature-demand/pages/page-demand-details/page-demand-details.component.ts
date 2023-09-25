import { Component, OnInit } from '@angular/core';
import { DemandService } from '../../../../core';
import { ActivatedRoute } from '@angular/router';
import { JobDescriptionModels, ProjectDetailModel, Resource } from '../../../../core';

@Component({
  selector: 'app-feature-demand-details',
  templateUrl: './page-demand-details.component.html',
  styleUrls: ['./page-demand-details.component.scss'],
})
export class PageDemandDetailsComponent implements OnInit {
  project: ProjectDetailModel;

  constructor(
    private demandService: DemandService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const id =
      this.route.snapshot.paramMap.get('id') !== null
        ? this.route.snapshot.paramMap.get('id')
        : '';
    if (id) {
      this.demandService.getDemandDetailsById(id).subscribe((res) => {
        this.project = res;
      });
    }
  }

  getFullName(resource: Resource): string {
    return resource.firstname.concat(' ').concat(resource.lastname);
  }

  getPositionTitle(jobDescription: JobDescriptionModels): string {
    return jobDescription.title;
  }

  isShowLineSeparators(resource: Resource[], index: number): boolean {
    return resource.length > 1 && index < resource.length - 1;
  }

  navigateCandidateDetailById(id: number): string {
    return '/admin/candidate/candidateDetail/'.concat(id.toString());
  }
}
