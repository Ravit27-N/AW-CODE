import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DirectoryFeedTableFormComponent } from './directory-feed-table-form.component';

describe('DirectoryFeedTableFormComponent', () => {
  let component: DirectoryFeedTableFormComponent;
  let fixture: ComponentFixture<DirectoryFeedTableFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DirectoryFeedTableFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DirectoryFeedTableFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
