import { Component, inject, OnInit, signal } from '@angular/core';
import { Libro } from '../../../services/libro';
import { LibroDato } from '../../../models/libro';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-libro-listado',
  imports: [RouterLink],
  templateUrl: './libro-listado.html',
  styleUrl: './libro-listado.css',
})

export class LibroListado implements OnInit{
  
  ngOnInit(){
    this.libroService.obtenerLibros(0).subscribe({
      next: (pagina) => {this.libros.set(pagina.content); this.cargando.set(false)},
      error: (error) => {this.cargando.set(false); console.log('Error al traer libros', error)},
    });
  }

  private libroService = inject(Libro);
  protected readonly libros = signal<LibroDato[]>([]);
  protected readonly cargando = signal(true);


}
