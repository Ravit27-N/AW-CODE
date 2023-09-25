import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureDirectoryFeedNavigatorComponent } from './feature-directory-feed-navigator.component';

describe('FeatureDirectoryFeedNavigatorComponent', () => {
  let component: FeatureDirectoryFeedNavigatorComponent;
  let fixture: ComponentFixture<FeatureDirectoryFeedNavigatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FeatureDirectoryFeedNavigatorComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureDirectoryFeedNavigatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
