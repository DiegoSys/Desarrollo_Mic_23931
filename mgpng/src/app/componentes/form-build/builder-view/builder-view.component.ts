import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, Subscription, timer } from 'rxjs';
import { debounce, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { firstValueFrom } from 'rxjs';
import { MessageService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { TableConfig } from 'src/app/shared/components/generic-table/interfaces/table-config.interface';
import { FormBuildListService } from '../form-build-list.service';
import { ProyectoSeccionService } from '../seccion/proyecto-seccion.service';
import { SeccionService } from '../seccion/seccion.service';
import { SeccionCampoService } from '../campo/seccion-campo.service';
import { CampoService } from '../campo/campo.service';
import { FieldPreviewService } from 'src/app/shared/components/field-preview/field-preview.service';
import { Seccion, Campo } from '../models/form-build.model';

interface TablePageEvent {
  first: number;
  rows: number;
}

@Component({
  selector: 'app-builder-view',
  templateUrl: './builder-view.component.html',
  styleUrls: ['./builder-view.component.scss']
})
export class BuilderViewComponent implements OnInit, OnDestroy {
  formularios: any[] = [];
  formularioSeleccionado: any = null;
  formulario: any = null;
  secciones: Seccion[] = [];
  camposPorSeccion: { [codigoSeccion: string]: Campo[] } = {};
  loading = false;
  totalRecords = 0;
  sortField: string = '';
  sortOrder: string = 'asc';
  searchCriteria: { [key: string]: string } = {};
  searchValue: string = '';
  errorMessage: string = '';
  today: Date = new Date();
  currentUser: any = {};

  private searchSubject = new Subject<string>();
  private searchSubscription?: Subscription;

  tableConfig: TableConfig = {
    columns: [
      { field: 'codigo', header: 'Código', type: 'text' },
      { field: 'nombre', header: 'Nombre', type: 'text' },
      { field: 'descripcion', header: 'Descripción', type: 'text' },
      { field: 'fechaCreacion', header: 'Fecha Creación', type: 'date' }
    ],
    actions: [
      {
        icon: 'pi pi-pencil',
        label: 'Ingresar',
        handler: (item) => this.seleccionarFormulario(item),
      }
    ],
    pagination: true,
    rowsPerPage: [10, 25, 50],
    showCurrentPageReport: true,
    rowTrackBy: 'codigo',
    globalFilterFields: ['codigo', 'nombre', 'descripcion']
  };

  constructor(
    private formBuildListService: FormBuildListService,
    private formularioSeccionService: ProyectoSeccionService,
    private seccionService: SeccionService,
    private seccionCampoService: SeccionCampoService,
    private campoService: CampoService,
    private fieldPreviewService: FieldPreviewService,
    private spinnerService: SpinnerService,
    private messageService: MessageService,
    private router: Router,
  ) {}

  async ngOnInit(): Promise<void> {
    this.loadFormularios({ first: 0, rows: 10 });
    this.setupSearchDebounce();
  }

  ngOnDestroy(): void {
    this.searchSubscription?.unsubscribe();
  }

  private setupSearchDebounce(): void {
    this.searchSubscription = this.searchSubject.pipe(
      debounce(() => timer(500)),
      distinctUntilChanged(),
      switchMap(searchTerm => {
        this.searchCriteria = { global: searchTerm };
        return this.formBuildListService.getAllPaginated({
          page: 0,
          size: 10,
          sort: this.sortField,
          direction: this.sortOrder,
          ...this.searchCriteria
        });
      })
    ).subscribe({
      next: response => this.handleSearchResponse(response),
      error: error => this.handleError('Error al buscar formularios', error)
    });
  }

  onSearchInputChange(value: string): void {
    this.searchValue = value;
    this.searchSubject.next(value);
  }

  handleSearch(): void {
    this.searchCriteria = { global: this.searchValue };
    this.loadFormularios({ first: 0, rows: 10 });
  }

  async loadFormularios(event: TablePageEvent): Promise<void> {
    try {
      this.loading = true;
      this.spinnerService.show();
      this.errorMessage = '';

      const params = {
        page: Math.floor((event.first || 0) / (event.rows || 10)),
        size: event.rows || 10,
        sort: this.sortField || 'fechaCreacion',
        direction: this.sortOrder,
        ...(Object.keys(this.searchCriteria).length > 0 && {searchCriteria: this.searchCriteria})
      };

      const response = await firstValueFrom(this.formBuildListService.getAllPaginated(params));
      this.handleSearchResponse(response);
    } catch (error) {
      this.handleError('Error al cargar formularios', error);
    } finally {
      this.loading = false;
      this.spinnerService.hide();
    }
  }

  private handleSearchResponse(response: any): void {
    this.formularios = response.content || [];
    this.totalRecords = response.totalElements || 0;
  }

  onPageChange(event: TablePageEvent): void {
    this.loadFormularios(event);
  }

  onSearch(criteria: { [key: string]: string }): void {
    this.searchCriteria = criteria;
    this.loadFormularios({ first: 0, rows: 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }): void {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadFormularios({ first: 0, rows: 10 });
  }

  async seleccionarFormulario(form: any): Promise<void> {
    try {
      this.loading = true;
      this.spinnerService.show();
      this.errorMessage = '';
      this.formularioSeleccionado = form;
      this.formulario = form;

      // 1. Obtener relaciones formulario-sección (extraer content de la respuesta paginada)
      const response = await firstValueFrom(this.formularioSeccionService.getByCodigoProyecto(form.codigo));
      const relFormSec = response.content || [];
      console.log('Relaciones formulario-sección:', relFormSec);
      
      // 2. Obtener detalles de cada sección y mantener el orden
      this.secciones = [];
      for (const rel of relFormSec) {
        if (!rel.orden) {
          console.warn('Relación sin orden:', rel);
          rel.orden = 0; // Valor por defecto si no tiene orden
        }
        try {
          const seccion = await firstValueFrom(this.seccionService.getByCodigo(rel.codigoSeccionFk));
          if (seccion) {
            seccion.orden = rel.orden;
            this.secciones.push(seccion);
            console.log('Sección obtenida:', seccion);
          }
        } catch (error) {
          console.error('Error obteniendo sección:', rel.codigoSeccionFk, error);
        }
      }
      
      // 3. Ordenar secciones por el campo 'orden'
      this.secciones.sort((a, b) => a.orden - b.orden);
      console.log('Secciones cargadas y ordenadas:', this.secciones);
      
      // 4. Obtener campos para cada sección
      this.camposPorSeccion = {};
      for (const seccion of this.secciones) {
        try {
          const response = await firstValueFrom(this.seccionCampoService.getByCodigoSeccion(seccion.codigo));
          const relSecCampo = response.content || [];
          
          if (relSecCampo.length === 0) {
            this.camposPorSeccion[seccion.codigo] = [];
            console.log(`Sección ${seccion.codigo} no tiene campos asociados`);
            continue;
          }

          const campos = await Promise.all(
            relSecCampo.map(async rel => {
              try {
                const campo = await firstValueFrom(this.campoService.getByCodigo(rel.codigoCampoFk));
                if (campo) {
                  campo.orden = rel.orden; // Asigna el orden de la relación al campo
                }
                return campo;
              } catch (error) {
                console.error('Error obteniendo campo:', rel.codigoCampoFk, error);
                return null;
              }
            })
          );

          this.camposPorSeccion[seccion.codigo] = campos
            .filter(c => c !== null)
            .sort((a, b) => (a.orden || 0) - (b.orden || 0));
          console.log(`Campos ordenados para la sección ${seccion.codigo}:`, this.camposPorSeccion[seccion.codigo].map(c => ({codigo: c.codigo, label: c.label, orden: c.orden})));
        } catch (error) {
          console.error('Error obteniendo relaciones sección-campo:', seccion.codigo, error);
          this.camposPorSeccion[seccion.codigo] = [];
        }
      }
    } catch (error) {
      this.handleError('Error al cargar el formulario seleccionado', error);
    } finally {
      this.loading = false;
      this.spinnerService.hide();
    }
  }

  deseleccionarFormulario(): void {
    this.formularioSeleccionado = null;
    this.formulario = null;
    this.secciones = [];
    this.camposPorSeccion = {};
    this.errorMessage = '';
  }

  navigateToFormManagement(): void {
    if (this.formularioSeleccionado) {
      this.router.navigate(['/form-build/management'], {
        queryParams: { formId: this.formularioSeleccionado.codigo }
      });
    } else {
      this.router.navigate(['/form-build/management']);
    }
  }

  getMaxColumn(seccionCodigo: string): number {
    if (!this.camposPorSeccion[seccionCodigo] || this.camposPorSeccion[seccionCodigo].length === 0) {
      return 1;
    }
    return 1; // Valor por defecto ya que no tenemos columnaMatriz en el modelo
  }

  getMaxRow(seccionCodigo: string): number {
    if (!this.camposPorSeccion[seccionCodigo] || this.camposPorSeccion[seccionCodigo].length === 0) {
      return 1;
    }
    return 1; // Valor por defecto ya que no tenemos filaMatriz en el modelo
  }

  getOpciones(campo: any): string[] {
    return this.fieldPreviewService.getFieldOptions(campo);
  }

  // --- NUEVA LÓGICA DE TABLAS ---
  // Devuelve el número de filas a renderizar
  getNumFilas(campo: any): number {
    if (campo.filas?.length && campo.columnas?.length) {
      // Caso matriz completa: filas y columnas definidas
      return campo.filas.length;
    }
    if (campo.filas?.length) {
      // Solo filas tipo label
      return campo.filas.length;
    }
    // Solo columnas tipo label o ninguna fila definida
    return campo.maxFilas || campo.minFilas || 1;
  }

  // Devuelve el número de columnas a renderizar
  getNumColumnas(campo: any): number {
    if (campo.filas?.length && campo.columnas?.length) {
      // Caso matriz completa: filas y columnas definidas
      return campo.columnas.length;
    }
    if (campo.columnas?.length) {
      // Solo columnas tipo label
      return campo.columnas.length;
    }
    // Solo filas tipo label o ninguna columna definida
    return campo.maxColumnas || campo.minColumnas || 1;
  }

  // Devuelve la definición de la fila en la posición y
  getFila(campo: any, y: number): any {
    return campo.filas?.[y] || { tipo: 'input', y };
  }

  // Devuelve la definición de la columna en la posición x
  getColumna(campo: any, x: number): any {
    return campo.columnas?.[x] || { tipo: 'input', x };
  }

  // Devuelve true si la celda es tipo label
  isLabelCell(campo: any, x: number, y: number): boolean {
    const fila = this.getFila(campo, y);
    const col = this.getColumna(campo, x);
    // Si la fila o la columna es tipo label, la celda es label
    return fila.tipo === 'label' || col.tipo === 'label';
  }

  // Devuelve el valor de la celda
  getValorCelda(campo: any, x: number, y: number): any {
    return campo.celdas?.find((celda: any) => celda.x === x && celda.y === y)?.valor || '';
  }

  // Devuelve true si alguna columna es de tipo distinto a 'label'
  hasInputColumn(campo: any): boolean {
    return Array.isArray(campo.columnas) && campo.columnas.some((col: any) => col.tipo !== 'label');
  }

  // Devuelve true si todas las columnas son tipo label
  allColumnsAreLabel(campo: any): boolean {
    return campo.columnas?.length && campo.columnas.every((col: any) => col.tipo === 'label');
  }

  // Devuelve true si todas las filas son tipo label
  allRowsAreLabel(campo: any): boolean {
    return campo.filas?.length && campo.filas.every((fila: any) => fila.tipo === 'label');
  }

  // Devuelve el array de columnas de la tabla, asegurando que siempre sea un array de objetos
  getColumnasTabla(campo: any): any[] {
    if (Array.isArray(campo?.columnas)) {
      return campo.columnas;
    }
    return [];
  }

  // Devuelve el array de filas de la tabla, asegurando que siempre sea un array de objetos
  getFilasTabla(campo: any): any[] {
    if (Array.isArray(campo?.filas)) {
      return campo.filas;
    }
    return [];
  }

  private handleError(message: string, error: any): void {
    this.errorMessage = message;
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message,
      life: 5000
    });
    console.error(message, error);
  }
}
