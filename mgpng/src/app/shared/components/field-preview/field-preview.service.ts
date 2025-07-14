import { Injectable } from '@angular/core';
import { Campo } from 'src/app/componentes/form-build/models/form-build.model';

@Injectable({
  providedIn: 'root'
})
export class FieldPreviewService {

  constructor() { }

  /**
   * Obtiene el icono de un tipo de campo
   */
  getFieldIcon(tipoCampo?: string): string {
    if (!tipoCampo) return 'pi-question-circle';

    switch (tipoCampo.toUpperCase()) {
      case 'TXT':
        return 'pi-font';
      case 'AREA':
        return 'pi-align-left';
      case 'SEL':
        return 'pi-list';
      case 'SELM':
        return 'pi-bars';
      case 'NUM':
        return 'pi-hashtag';
      case 'DATE':
        return 'pi-calendar';
      case 'TIME':
        return 'pi-clock';
      case 'EMAIL':
        return 'pi-envelope';
      case 'FILE':
        return 'pi-file';
      case 'CHK':
        return 'pi-check-square';
      case 'RADIO':
        return 'pi-circle';
      case 'PASS':
        return 'pi-eye-slash';
      case 'TABLA':
        return 'pi-table';
      default:
        return 'pi-question-circle';
    }
  }

  /**
   * Obtiene el nombre de un tipo de campo (usa el nombre del servidor si está disponible)
   */
  getFieldTypeName(tipoCampo?: string, serverName?: string): string {
    // Si viene el nombre del servidor, úsalo
    if (serverName) return serverName;
    
    // Fallback a nombres locales
    if (!tipoCampo) return 'Tipo desconocido';

    switch (tipoCampo.toUpperCase()) {
      case 'TXT':
        return 'Campo de Texto';
      case 'AREA':
        return 'Área de Texto';
      case 'SEL':
        return 'Selector Simple';
      case 'SELM':
        return 'Selector Múltiple';
      case 'NUM':
        return 'Campo Numérico';
      case 'DATE':
        return 'Campo de Fecha';
      case 'TIME':
        return 'Campo de Hora';
      case 'EMAIL':
        return 'Campo de Email';
      case 'FILE':
        return 'Campo de Archivo';
      case 'CHK':
        return 'Checkbox';
      case 'RADIO':
        return 'Botón de Radio';
      case 'PASS':
        return 'Campo de Contraseña';
      case 'TABLA':
        return 'Tabla de Datos';
      default:
        return 'Tipo desconocido';
    }
  }

  /**
   * Verifica si un tipo de campo tiene opciones
   */
  hasOptions(tipoCampo?: string): boolean {
    if (!tipoCampo) return false;

    const typesWithOptions = ['SEL', 'SELM', 'RADIO', 'CHK'];
    return typesWithOptions.includes(tipoCampo.toUpperCase());
  }

  /**
   * Verifica si un tipo de campo tiene configuración de tabla
   */
  hasTableConfig(tipoCampo?: string): boolean {
    if (!tipoCampo) return false;
    return tipoCampo.toUpperCase() === 'TABLA';
  }

  /**
   * Verifica si un tipo de campo tiene configuración de archivo
   */
  hasFileConfig(tipoCampo?: string): boolean {
    if (!tipoCampo) return false;
    return tipoCampo.toUpperCase() === 'FILE';
  }

  /**
   * Genera la vista previa de campos para una sección
   */
  getSectionFieldsPreview(fields: Campo[]): string {
    if (!fields || fields.length === 0) {
      return 'No hay campos';
    }
    
    if (fields.length === 1) {
      const field = fields[0];
      return `${this.getFieldIcon(field.tipoCampo)}:${field.label || 'Campo sin etiqueta'}`;
    } else if (fields.length <= 3) {
      return fields.map(f => 
        `${this.getFieldIcon(f.tipoCampo)}:${f.label || 'Campo sin etiqueta'}`
      ).join('|');
    } else {
      const firstTwo = fields.slice(0, 2).map(f => 
        `${this.getFieldIcon(f.tipoCampo)}:${f.label || 'Campo sin etiqueta'}`
      );
      return `${firstTwo.join('|')}|pi-ellipsis-h:y ${fields.length - 2} más`;
    }
  }

  /**
   * Obtiene la descripción de campos para una sección
   */
  getSectionFieldsDescription(fields: Campo[]): string {
    if (!fields || fields.length === 0) {
      return 'No contiene campos';
    }
    return `Contiene ${fields.length} campo${fields.length === 1 ? '' : 's'}`;
  }

  /**
   * Obtiene las opciones de un campo (con fallback)
   */
  getFieldOptions(campo: Campo): string[] {
    if (!campo) return [];
    
    if (campo.opciones && Array.isArray(campo.opciones)) {
      return campo.opciones;
    }
    
    // Fallback para campos que deberían tener opciones
    if (this.hasOptions(campo.tipoCampo)) {
      return ['Opción 1', 'Opción 2', 'Opción 3'];
    }
    
    return [];
  }
} 