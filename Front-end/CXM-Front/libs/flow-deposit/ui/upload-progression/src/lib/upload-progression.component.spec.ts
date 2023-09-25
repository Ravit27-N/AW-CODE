import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadProgressionComponent } from './upload-progression.component';

describe('UploadProgressionComponent', () => {
  let component: UploadProgressionComponent;
  let fixture: ComponentFixture<UploadProgressionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadProgressionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadProgressionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
