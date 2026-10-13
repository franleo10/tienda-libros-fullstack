import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private auth = inject(Auth);
  protected readonly sesionIniciada = computed(() => this.auth.token() !== null);
  protected readonly esAdmin = signal(false);
  protected readonly menuAbierto = signal(false);

  salir() {
    this.auth.logout();
  }

  alternarMenu(){
    this.menuAbierto.update(valorActual => !valorActual);
  }
}
