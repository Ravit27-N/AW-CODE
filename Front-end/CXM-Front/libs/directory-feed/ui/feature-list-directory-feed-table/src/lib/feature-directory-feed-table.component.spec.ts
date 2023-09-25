import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureDirectoryFeedTableComponent } from './feature-directory-feed-table.component';

describe('FeatureDirectoryFeedTableComponent', () => {
  let component: FeatureDirectoryFeedTableComponent;
  let fixture: ComponentFixture<FeatureDirectoryFeedTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FeatureDirectoryFeedTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureDirectoryFeedTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
