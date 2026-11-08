import { Component, computed, input } from '@angular/core';

// Colores de tapa para los libros sin portada
const COLORES_TAPA = ['#33241a', '#1d2b36', '#3a1d24', '#22301f', '#2c2440'];

// Tapa de un libro: la portada de OpenLibrary si existe, o una tapa armada con el título y el autor.
// El tamaño lo decide quien la usa; con `chica` no muestra texto (para miniaturas como en el carrito).
@Component({
  selector: 'app-portada',
  imports: [],
  templateUrl: './portada.html',
  styleUrl: './portada.css',
  host: { '[class.chica]': 'chica()', '[class.con-imagen]': '!!urlPortada()' },
})
export class Portada {
  titulo = input.required<string>();
  autor = input('');
  urlPortada = input<string | null | undefined>(null);
  chica = input(false);

  // El color sale del título, así el mismo libro tiene la misma tapa en todas las pantallas
  protected readonly color = computed(() => {
    let suma = 0;
    for (const letra of this.titulo()) {
      suma += letra.charCodeAt(0);
    }
    return COLORES_TAPA[suma % COLORES_TAPA.length];
  });
}
