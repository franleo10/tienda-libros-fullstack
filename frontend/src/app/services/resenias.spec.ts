import { TestBed } from '@angular/core/testing';

import { Resenias } from './resenias';

describe('Resenias', () => {
  let service: Resenias;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Resenias);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
