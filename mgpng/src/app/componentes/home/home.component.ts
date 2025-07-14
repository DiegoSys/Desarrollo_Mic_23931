import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { TooltipModule } from 'primeng/tooltip';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [TooltipModule, RouterModule],
  providers: [SpinnerService]
})
export class HomeComponent {
  constructor(private router: Router, private spinner: SpinnerService) {}

  goTo(destino: string) {
    // Mostrar spinner
    this.spinner.show();
    // Aquí puedes agregar logs, validaciones, permisos, etc.
    // Simular retardo de navegación para UX (puedes quitar el setTimeout en producción)
    setTimeout(() => {
      this.router.navigate(['/' + destino]).then(() => {
        this.spinner.hide();
      });
    }, 500);
  }
}
