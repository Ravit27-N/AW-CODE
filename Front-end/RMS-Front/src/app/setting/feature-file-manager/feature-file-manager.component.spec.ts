import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureFileManagerComponent } from './feature-file-manager.component';

describe('FeatureFileManagerComponent', () => {
  let component: FeatureFileManagerComponent;
  let fixture: ComponentFixture<FeatureFileManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FeatureFileManagerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeatureFileManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
