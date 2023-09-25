import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListUserServiceFilterComponent } from './list-user-service-filter.component';

describe('ListUserServiceFilterComponent', () => {
  let component: ListUserServiceFilterComponent;
  let fixture: ComponentFixture<ListUserServiceFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListUserServiceFilterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListUserServiceFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
