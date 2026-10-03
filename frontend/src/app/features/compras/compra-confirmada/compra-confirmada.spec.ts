import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompraConfirmada } from './compra-confirmada';

describe('CompraConfirmada', () => {
  let component: CompraConfirmada;
  let fixture: ComponentFixture<CompraConfirmada>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompraConfirmada]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompraConfirmada);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
