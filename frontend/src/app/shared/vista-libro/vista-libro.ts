import { Component, ElementRef, effect, input, output, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LibroDato } from '../../models/libro';
import { Portada } from '../portada/portada';

// Modal para ver los datos de un libro sin salir de la pantalla. Está abierto mientras el padre le pase un libro.
@Component({
  selector: 'app-vista-libro',
  imports: [RouterLink, Portada],
  templateUrl: './vista-libro.html',
  styleUrl: './vista-libro.css',
})
export class VistaLibro {
  libro = input<LibroDato | null>(null);
  cerrar = output<void>();

  private dialogo = viewChild.required<ElementRef<HTMLDialogElement>>('dialogo');

  private readonly formatoPrecio = new Intl.NumberFormat('es-AR', {
    style: 'currency',
    currency: 'ARS',
  });

  constructor() {
    effect(() => {
      const dialogo = this.dialogo().nativeElement;
      if (this.libro() && !dialogo.open) {
        dialogo.showModal();
      } else if (!this.libro() && dialogo.open) {
        dialogo.close();
      }
    });
  }

  // Igual que en el modal de confirmación: el padre decide cuándo se cierra
  alApretarEscape(evento: Event) {
    evento.preventDefault();
    this.cerrar.emit();
  }

  // Un clic en el fondo oscuro (fuera de la tarjeta) también cierra el modal
  alHacerClic(evento: MouseEvent) {
    if (evento.target === this.dialogo().nativeElement) {
      this.cerrar.emit();
    }
  }

  protected formatearPrecio(precio: number) {
    return this.formatoPrecio.format(precio);
  }

  protected nombreAutores(libro: LibroDato) {
    return libro.autores.map((autor) => `${autor.nombre} ${autor.apellido}`).join(', ');
  }

  protected nombreGeneros(libro: LibroDato) {
    return libro.generos.map((genero) => genero.nombre).join(', ');
  }

  protected anio(libro: LibroDato) {
    return libro.fecha_lanzamiento?.slice(0, 4) ?? '';
  }
}
