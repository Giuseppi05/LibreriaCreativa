import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormCoupon } from './form-coupon';

describe('FormCoupon', () => {
  let component: FormCoupon;
  let fixture: ComponentFixture<FormCoupon>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormCoupon]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormCoupon);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
