import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { CompraConfirmada } from './compra-confirmada';

describe('CompraConfirmada', () => {
  let httpTesting: HttpTestingController;

  function crear(queryParams: Record<string, string>) {
    TestBed.configureTestingModule({
      imports: [CompraConfirmada],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParamMap: convertToParamMap(queryParams) } },
        },
      ],
    });
    httpTesting = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(CompraConfirmada);
    fixture.detectChanges();
    return fixture;
  }

  function texto(fixture: ReturnType<typeof crear>): string {
    return fixture.nativeElement.textContent.replace(/\s+/g, ' ');
  }

  afterEach(() => httpTesting.verify());

  it('confirms the payment and shows the order summary with a link to the library', () => {
    const fixture = crear({ payment_id: '555', status: 'approved' });

    const request = httpTesting.expectOne('http://localhost:8080/api/pagos/555/confirmar');
    expect(request.request.method).toBe('POST');
    request.flush({
      idFactura: 10,
      monto: 21500,
      fechaCompra: '2026-10-24T16:30:00',
      metodoPago: 'credit_card',
      externalReference: 'carrito-3',
      idUsuario: 7,
      titulosLibrosComprados: ['Rayuela', 'Bestiario'],
    });
    fixture.detectChanges();

    expect(texto(fixture)).toContain('¡Gracias por tu compra!');
    expect(texto(fixture)).toContain('N.º 10');
    expect(texto(fixture)).toContain('Tarjeta de crédito');
    expect(texto(fixture)).toContain('24/10/2026 16:30');
    expect(fixture.nativeElement.querySelectorAll('.libros li').length).toBe(2);
    expect(texto(fixture)).toMatch(/21\.500/);
    expect(fixture.nativeElement.querySelector('a.boton-principal')?.getAttribute('href')).toBe(
      '/mi-biblioteca'
    );
  });

  it('shows the backend message when the payment is not approved', () => {
    const fixture = crear({ payment_id: '555', status: 'pending' });

    httpTesting
      .expectOne('http://localhost:8080/api/pagos/555/confirmar')
      .flush('Tu pago todavía está pendiente de aprobación.', { status: 409, statusText: 'Conflict' });
    fixture.detectChanges();

    expect(texto(fixture)).toContain('Tu compra no se completó');
    expect(texto(fixture)).toContain('Tu pago todavía está pendiente de aprobación.');
    expect(fixture.nativeElement.querySelector('a.boton-principal')?.getAttribute('href')).toBe(
      '/carrito'
    );
  });

  it('does not call the backend when MercadoPago returns without a payment', () => {
    const fixture = crear({ payment_id: 'null', status: 'null' });

    httpTesting.expectNone('http://localhost:8080/api/pagos/null/confirmar');
    expect(texto(fixture)).toContain('No encontramos un pago');
  });

  it('shows an error when the confirmation fails', () => {
    const fixture = crear({ payment_id: '555' });

    httpTesting
      .expectOne('http://localhost:8080/api/pagos/555/confirmar')
      .flush('No se pudo consultar el pago en MercadoPago.', { status: 502, statusText: 'Bad Gateway' });
    fixture.detectChanges();

    expect(texto(fixture)).toContain('No pudimos confirmar tu compra');
  });
});
