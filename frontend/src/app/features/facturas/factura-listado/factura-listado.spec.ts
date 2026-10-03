import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FacturaListado } from './factura-listado';

describe('FacturaListado', () => {
  let component: FacturaListado;
  let fixture: ComponentFixture<FacturaListado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacturaListado]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FacturaListado);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
