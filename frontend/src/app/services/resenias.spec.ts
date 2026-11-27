import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { Resenias } from './resenias';

describe('Resenias', () => {
  let service: Resenias;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(Resenias);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
