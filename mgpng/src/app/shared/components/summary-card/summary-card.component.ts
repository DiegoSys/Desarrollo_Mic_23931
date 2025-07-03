import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Importa CommonModule para usar ngFor y otras directivas comunes

@Component({
  selector: 'app-summary-card',
  standalone: true,
  templateUrl: './summary-card.component.html',
  styleUrls: ['./summary-card.component.scss'],
  imports: [CommonModule], // <-- Agrega esto

})
export class SummaryCardComponent {
  @Input() title: string = '';
  @Input() items: any[] = [];
  @Input() labelField: string = 'codigo';
  @Input() descField: string = 'descripcion';
}