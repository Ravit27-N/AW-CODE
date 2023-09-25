import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DirectoryFeedSelectionComponent } from './directory-feed-selection.component';

describe('DirectoryFeedSelectionComponent', () => {
  let component: DirectoryFeedSelectionComponent;
  let fixture: ComponentFixture<DirectoryFeedSelectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DirectoryFeedSelectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DirectoryFeedSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
