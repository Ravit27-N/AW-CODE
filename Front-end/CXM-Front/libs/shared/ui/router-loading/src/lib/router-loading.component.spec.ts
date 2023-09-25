import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterLoadingComponent } from './router-loading.component';

describe('RouterLoadingComponent', () => {
  let component: RouterLoadingComponent;
  let fixture: ComponentFixture<RouterLoadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RouterLoadingComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RouterLoadingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
