import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
  name: 'formatCell'
})
export class CellFormatPipe implements PipeTransform {
  constructor(private datePipe: DatePipe) {}

  transform(value: any, type?: string, format?: string): any {
    if (value === null || value === undefined) return '';

    switch(type) {
      case 'date':
        return this.datePipe.transform(value, format || 'mediumDate');
      case 'number':
        return format ? new Intl.NumberFormat('es', { 
          minimumFractionDigits: parseInt(format),
          maximumFractionDigits: parseInt(format)
        }).format(value) : value;
      case 'boolean':
        return value ? 'Sí' : 'No';
      default:
        return value;
    }
  }
}
