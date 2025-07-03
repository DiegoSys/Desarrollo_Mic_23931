import { Injectable } from '@angular/core';
import { TableData } from '../models/table-data.model';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TableDataService {
  getTableData(): Observable<TableData[]> {
    // Datos de ejemplo basados en el componente original
    const data: TableData[] = [
      {
        id: 1,
        name: 'Ejemplo 1',
        country: { name: 'Colombia', code: 'co' },
        representative: { name: 'Representante 1', image: 'amyelsner.png' },
        status: 'qualified',
        verified: true,
        date: new Date()
      },
      {
        id: 2,
        name: 'Ejemplo 2', 
        country: { name: 'Ecuador', code: 'ec' },
        representative: { name: 'Representante 2', image: 'annafali.png' },
        status: 'new',
        verified: false,
        date: new Date()
      }
    ];

    return of(data);
  }
}
