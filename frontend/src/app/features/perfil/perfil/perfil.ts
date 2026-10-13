import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PerfilUsuario } from '../../../models/perfil';
import { PerfilService } from '../../../services/perfil';

@Component({
  selector: 'app-perfil',
  imports: [RouterLink],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  private readonly perfilService = inject(PerfilService);

  protected readonly perfil = signal<PerfilUsuario | null>(null);
  protected readonly cargando = signal(true);
  protected readonly mensajeError = signal('');

  ngOnInit() {
    this.perfilService.obtenerPerfil().subscribe({
      next: (perfil) => {
        this.perfil.set(perfil);
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.mensajeError.set(
          error.status === 401
            ? 'Iniciá sesión para consultar tu perfil.'
            : 'No se pudieron cargar los datos del perfil. Intentá de nuevo.'
        );
      },
    });
  }
}
