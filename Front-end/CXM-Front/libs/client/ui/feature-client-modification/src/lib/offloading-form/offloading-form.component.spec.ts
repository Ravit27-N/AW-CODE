import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OffloadingFormComponent } from './offloading-form.component';

describe('OffloadingFormComponent', () => {
  let component: OffloadingFormComponent;
  let fixture: ComponentFixture<OffloadingFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OffloadingFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OffloadingFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
