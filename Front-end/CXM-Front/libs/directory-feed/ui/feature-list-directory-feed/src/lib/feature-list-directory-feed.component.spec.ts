import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureListDirectoryFeedComponent } from './feature-list-directory-feed.component';

describe('FeatureListDirectoryFeedComponent', () => {
  let component: FeatureListDirectoryFeedComponent;
  let fixture: ComponentFixture<FeatureListDirectoryFeedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FeatureListDirectoryFeedComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureListDirectoryFeedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
