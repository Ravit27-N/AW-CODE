import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonMultipleBrowseInputComponent } from './common-multiple-browse-input.component';

describe('CommomMultipleBrowseInputComponent', () => {
  let component: CommonMultipleBrowseInputComponent;
  let fixture: ComponentFixture<CommonMultipleBrowseInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CommonMultipleBrowseInputComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommonMultipleBrowseInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
