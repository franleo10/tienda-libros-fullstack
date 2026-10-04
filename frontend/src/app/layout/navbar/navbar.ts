import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {

  protected readonly sesionIniciada = signal(false);
  protected readonly esAdmin = signal(false);
  protected readonly menuAbierto = signal(false);

  alternarMenu(){
    this.menuAbierto.update(valorActual => !valorActual);
  }
}
