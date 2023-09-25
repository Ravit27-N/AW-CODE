import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DirectoryFeedComplexFormComponent } from './directory-feed-complex-form.component';

describe('DirectoryFeedComplexFormComponent', () => {
  let component: DirectoryFeedComplexFormComponent;
  let fixture: ComponentFixture<DirectoryFeedComplexFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DirectoryFeedComplexFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DirectoryFeedComplexFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
