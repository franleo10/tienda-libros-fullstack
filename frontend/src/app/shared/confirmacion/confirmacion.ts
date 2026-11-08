import { Component, ElementRef, effect, input, output, viewChild } from '@angular/core';

// Modal de confirmación reutilizable. El padre decide cuándo está abierto y qué hacer con la respuesta.
@Component({
  selector: 'app-confirmacion',
  imports: [],
  templateUrl: './confirmacion.html',
  styleUrl: './confirmacion.css',
})
export class Confirmacion {
  abierto = input(false);
  titulo = input('¿Confirmás esta acción?');
  mensaje = input('');
  textoConfirmar = input('Confirmar');
  procesando = input(false);
  peligro = input(false);
  confirmar = output<void>();
  cancelar = output<void>();

  private dialogo = viewChild.required<ElementRef<HTMLDialogElement>>('dialogo');

  constructor() {
    // showModal() muestra el <dialog> como modal: oscurece el fondo y bloquea el resto de la página
    effect(() => {
      const dialogo = this.dialogo().nativeElement;
      if (this.abierto() && !dialogo.open) {
        dialogo.showModal();
      } else if (!this.abierto() && dialogo.open) {
        dialogo.close();
      }
    });
  }

  // El <dialog> se cierra solo con Escape; lo evitamos para que el padre siga controlando si está abierto
  alApretarEscape(evento: Event) {
    evento.preventDefault();
    if (!this.procesando()) {
      this.cancelar.emit();
    }
  }
}
