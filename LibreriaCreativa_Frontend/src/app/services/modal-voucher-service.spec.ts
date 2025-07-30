import { TestBed } from '@angular/core/testing';

import { ModalVoucherService } from './modal-voucher-service';

describe('ModalVoucherService', () => {
  let service: ModalVoucherService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModalVoucherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
