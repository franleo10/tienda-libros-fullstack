import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { Autor } from './autor';

describe('Autor', () => {
  let service: Autor;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(Autor);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
