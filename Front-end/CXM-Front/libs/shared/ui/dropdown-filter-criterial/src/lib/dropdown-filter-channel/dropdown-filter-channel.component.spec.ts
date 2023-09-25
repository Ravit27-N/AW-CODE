import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DropdownFilterChannelComponent } from './dropdown-filter-channel.component';

describe('DropdownFilterChannelComponent', () => {
  let component: DropdownFilterChannelComponent;
  let fixture: ComponentFixture<DropdownFilterChannelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DropdownFilterChannelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DropdownFilterChannelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
