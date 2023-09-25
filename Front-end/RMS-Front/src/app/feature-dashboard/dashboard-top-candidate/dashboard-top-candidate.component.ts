import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { DashboardService, DashboardTopCandidateModel } from '../../core';
import { SlickCarouselComponent } from 'ngx-slick-carousel';
import { Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';

@Component({
  selector: 'app-dashboard-top-candidate',
  templateUrl: './dashboard-top-candidate.component.html',
  styleUrls: ['./dashboard-top-candidate.component.scss'],
})
export class DashboardTopCandidateComponent implements OnInit {
  topCandidates: Array<DashboardTopCandidateModel>;
  @ViewChild('slickModal') slickModal: SlickCarouselComponent;
  slideConfig = {
    slidesToShow: 3,
    slidesToScroll: 3,
    infinite: true,
    autoplay: true,
    autoplaySpeed: 6000,
    arrows: true,
  };

  constructor(
    private service: DashboardService,
    private router: Router,
  ) {}

  async ngOnInit(): Promise<void> {
    await this.fetchTopCandidates();
    this.resizeCarousel();
  }

  async consultTopCandidateDetails(id: number): Promise<void> {
    await this.router.navigate(['/admin/candidate/candidateDetail', id]);
  }

  getAvatarImagePath(id: number, photoUrl: any): any {
    return `/candidate/${id}/view/${photoUrl}`;
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.resizeCarousel();
  }

  private resizeCarousel(): void {
    if (innerWidth < 2085 && innerWidth >= 1808) {
      this.slideConfig = {
        ...this.slideConfig,
        slidesToShow: 2,
        slidesToScroll: 2,
      };
    } else if (innerWidth < 1808) {
      this.slideConfig = {
        ...this.slideConfig,
        slidesToShow: 1,
        slidesToScroll: 1,
      };
    } else {
      this.slideConfig = {
        ...this.slideConfig,
        slidesToShow: 3,
        slidesToScroll: 3,
      };
    }
  }

  private async fetchTopCandidates(): Promise<void> {
    const httpParams = new HttpParams().set('page', '1').set('pageSize', '15');
    const candidateHandler = await this.service
      .getTopCandidates(httpParams)
      .toPromise();
    this.topCandidates = candidateHandler.contents;
  }
}
