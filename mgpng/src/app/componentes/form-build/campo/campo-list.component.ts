import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA, ViewChild } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { CampoService } from './campo.service';
import { SeccionCampoService, SeccionCampo } from './seccion-campo.service';
import { TipoCampoService, TipoCampo } from './tipo-campo.service';
import { Campo, ColumnaTabla } from '../models/form-build.model';
import { FieldPreviewService } from 'src/app/shared/components/field-preview/field-preview.service';import { MessageService, ConfirmationService } from 'primeng/api';
import { PaginatorState } from 'primeng/paginator';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaginatorModule } from 'primeng/paginator';
import { InputTextModule } from 'primeng/inputtext';
import { SpinnerModule } from 'src/app/spinner/spinner.module';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DragDropModule, CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { PreviewCardComponent } from 'src/app/shared/components/preview-card/preview-card.component';
import { FieldPreviewComponent } from '../../../shared/components/field-preview/field-preview.component';


@Component({
  selector: 'app-campo-list',
  standalone: true,
  templateUrl: './campo-list.component.html',
  styleUrls: ['./campo-list.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    PaginatorModule,
    InputTextModule,
    SpinnerModule,
    ConfirmDialogModule,
    DragDropModule,
    PreviewCardComponent,
    FieldPreviewComponent,
  ],
  providers: [MessageService, ConfirmationService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CampoListComponent implements OnInit {

  rows: Campo[] = [];
  totalRecords = 0;
  loading = false;
  page = 0;
  size = 10;
  sortField = 'fechaCreacion';
  sortOrder = 'desc';
  searchCriteria: { [key: string]: string } = {};

  // --- LÓGICA PARA FORMULARIO INTELIGENTE ---
  // Configuraciones específicas por tipo de campo
  formModel: any = {
    label: '',
    tipoCampo: '',
    requerido: false,
    soloLectura: false,
    esMultiple: false,
    opciones: [],
    columnas: [] as ColumnaTabla[],
    filas: [],
    esMutable: false,
    minFilas: 1,
    maxFilas: 10,
    tipoConfiguracionTabla: 'COLUMNAS_Y_FILAS' as 'SOLO_COLUMNAS' | 'SOLO_FILAS' | 'COLUMNAS_Y_FILAS' | 'TABLA_PERSONALIZADA',
    estructuraTablaPersonalizada: null
  };

  // Búsqueda
  searchValue = '';
  private searchTimeout: any;

  // Dual column data
  availableCampos: Campo[] = [];
  associatedCampos: Campo[] = [];
  totalAvailableRecords = 0;

  // Formulario
  showForm = false;
  editing = false;
  toDelete: Campo | null = null;
  
  // Clonación
  showCloneModal = false;
  campoToClone: Campo | null = null;

  // Código de sección desde query params
  codigoSeccion: string | null = null;

  // Paginación específica para vista dual
  availablePage = 0;
  availableSize = 10;

  // Tipos de campo
  tiposCampo: TipoCampo[] = [];
  tiposCampoLoading = false;

  // Al inicio de la clase
  // El enum ahora es: 'SOLO_COLUMNAS' | 'SOLO_FILAS' | 'COLUMNAS_Y_FILAS'
  // Por defecto, columnas y filas
  // tablaConfig eliminado, ahora usamos formModel.tipoConfiguracionTabla

  showPreviewModal = false;
  campoParaVer: Campo | null = null;

  viewOnly = false;

  constructor(
    private campoService: CampoService,
    private seccionCampoService: SeccionCampoService,
    private tipoCampoService: TipoCampoService,
    private fieldPreviewService: FieldPreviewService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  // Llamar esto cuando cambia el tipo de campo
  onTipoCampoChange() {
    // Limpiar configuraciones específicas cuando cambia el tipo
    this.formModel.opciones = [];
    this.formModel.columnas = [];
    this.formModel.filas = [];
    this.formModel.esMutable = false;
    this.formModel.esMultiple = false;
    this.formModel.tipoConfiguracionTabla = 'COLUMNAS_Y_FILAS'; // Valor por defecto
    this.formModel.estructuraTablaPersonalizada = null; // Limpiar estructura personalizada
    
    // Inicializar configuraciones según el tipo
    if (this.formModel.tipoCampo === 'SEL' || this.formModel.tipoCampo === 'SELM') {
      this.formModel.opciones = ['Opción 1'];
    } else if (this.formModel.tipoCampo === 'TABLA') {
      this.formModel.columnas = [{ nombre: 'Columna 1' }];
      this.formModel.filas = [{ nombre: 'Fila 1' }];
      // Forzar detección de cambios para los arrays
      this.formModel.columnas = [...this.formModel.columnas];
      this.formModel.filas = [...this.formModel.filas];
    }
  }

  // Métodos para manejar opciones de select
  addOpcion() { 
    this.formModel.opciones.push(`Opción ${this.formModel.opciones.length + 1}`); 
  }
  
  removeOpcion(i: number) { 
    this.formModel.opciones.splice(i, 1); 
  }

  // Métodos para manejar columnas de tabla
  addColumna() { 
    this.ensureArrayExists('columnas');
    this.formModel.columnas.push({ nombre: `Columna ${this.formModel.columnas.length + 1}`, tipo: 'input' });
    this.updateColumnasIndices();
  }
  
  removeColumna(i: number) { 
    this.formModel.columnas.splice(i, 1);
    this.updateColumnasIndices();
  }

  // Métodos para manejar filas de tabla
  addFila() { 
    this.ensureArrayExists('filas');
    this.formModel.filas.push({ nombre: `Fila ${this.formModel.filas.length + 1}`, tipo: 'input' });
    this.updateFilasIndices();
  }
  
  removeFila(i: number) { 
    this.formModel.filas.splice(i, 1);
    this.updateFilasIndices();
  }

  // Métodos helper para evitar duplicación
  private ensureArrayExists(arrayName: 'columnas' | 'filas') {
    if (!this.formModel[arrayName]) {
      this.formModel[arrayName] = [];
    }
  }

  private updateColumnasIndices() {
    this.formModel.columnas = this.formModel.columnas.map((col, idx) => ({
      ...col,
      x: idx,
      y: 0
    }));
  }

  private updateFilasIndices() {
    this.formModel.filas = this.formModel.filas.map((fila, idx) => ({
      ...fila,
      y: idx,
      x: 0
    }));
  }

  // Métodos trackBy para mejorar el rendimiento y evitar problemas de binding
  trackByColumnIndex(index: number, item: any): number {
    return index;
  }

  trackByRowIndex(index: number, item: any): number {
    return index;
  }



  // Preparar DTO para guardar
  prepareCampoDTO() {
    console.log('formModel antes de preparar DTO:', this.formModel);
    
    const dto: any = {
      ...this.formModel,
      ...(this.editing ? { codigo: this.formModel.codigo } : {}),
      opciones: this.formModel.opciones || [],
      columnas: this.shouldIncludeColumnas() ? this.formModel.columnas || [] : [],
      filas: this.shouldIncludeFilas() ? this.formModel.filas || [] : [],
      minFilas: this.formModel.minFilas || 1,
      maxFilas: this.formModel.maxFilas || 10
    };

    console.log('Tipo configuración tabla:', this.formModel.tipoConfiguracionTabla);

    // Configurar propiedades booleanas
    this.setBooleanProperties(dto);

    // Validar y procesar tabla personalizada
    if (this.formModel.tipoConfiguracionTabla === 'TABLA_PERSONALIZADA') {
      const validationError = this.validateCustomTable();
      if (validationError) {
        this.showError(validationError);
        return null;
      }
      dto.estructuraTablaPersonalizada = this.formModel.estructuraTablaPersonalizada;
    } else {
      // Validar tablas normales
      if (this.formModel.tipoCampo === 'TABLA' && !dto.columnas.length && !dto.filas.length) {
        this.showError('Debes configurar al menos una columna o una fila para la tabla.');
        return null;
      }
    }

    // Asegurar índices x/y para tablas normales
    if (this.formModel.tipoCampo === 'TABLA' && this.formModel.tipoConfiguracionTabla !== 'TABLA_PERSONALIZADA') {
      this.setTableIndices(dto);
    }

    console.log('DTO final preparado:', dto);
    return dto;
  }

  private shouldIncludeColumnas(): boolean {
    return this.formModel.tipoConfiguracionTabla === 'COLUMNAS_Y_FILAS' || 
           this.formModel.tipoConfiguracionTabla === 'SOLO_COLUMNAS';
  }

  private shouldIncludeFilas(): boolean {
    return this.formModel.tipoConfiguracionTabla === 'COLUMNAS_Y_FILAS' || 
           this.formModel.tipoConfiguracionTabla === 'SOLO_FILAS';
  }

  private setBooleanProperties(dto: any) {
    const booleanProps = ['requerido', 'soloLectura', 'esMultiple', 'esMutable'];
    booleanProps.forEach(prop => {
      if (this.formModel[prop] === true) {
        dto[prop] = true;
      }
    });
  }

  private validateCustomTable(): string | null {
    const estructura = this.formModel.estructuraTablaPersonalizada;
    
    if (!estructura || !estructura.filas || estructura.filas.length === 0) {
      return 'Debes configurar al menos una fila para la tabla personalizada.';
    }
    
    for (let i = 0; i < estructura.filas.length; i++) {
      const fila = estructura.filas[i];
      if (!fila.celdas || fila.celdas.length === 0) {
        return `La fila ${i + 1} debe tener al menos una celda.`;
      }
    }
    
    return null;
  }

  private setTableIndices(dto: any) {
    dto.columnas = (dto.columnas || []).map((col: any, idx: number) => ({
      ...col,
      x: idx,
      y: 0
    }));
    dto.filas = (dto.filas || []).map((fila: any, idx: number) => ({
      ...fila,
      y: idx,
      x: 0
    }));
  }

  private showError(message: string) {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: message
    });
  }

  ngOnInit() {
    // Cargar tipos de campo activos
    this.loadTiposCampo();

    // Obtener el código de sección de los query params
    this.route.queryParams.subscribe(params => {
      this.codigoSeccion = params['seccion'] || null;
      console.log('Código de sección:', this.codigoSeccion);

      if (this.codigoSeccion) {
        this.loadCamposForDualView();
      } else {
        this.loadData({ first: 0, rows: this.size });
      }
    });
  }

  // Cargar campo para edición
  loadCampoForEdit(campo: any) {
    this.formModel = { 
      ...campo,
      opciones: campo.opciones || [],
      columnas: campo.columnas || [],
      filas: campo.filas || [],
      minFilas: campo.minFilas || 1,
      maxFilas: campo.maxFilas || 10,
      estructuraTablaPersonalizada: campo.estructuraTablaPersonalizada || null,
      ...this.extractBooleanProperties(campo)
    };
  }

  private extractBooleanProperties(campo: any) {
    const booleanProps = ['requerido', 'soloLectura', 'esMultiple', 'esMutable'];
    const result: any = {};
    
    booleanProps.forEach(prop => {
      result[prop] = campo[prop] === true ? true : undefined;
    });
    
    return result;
  }

  loadData(event: PaginatorState | { first: number, rows: number } = { first: 0, rows: 10 }) {
    this.loading = true;
    this.spinnerService.show();

    const params = this.buildLoadParams(event);

    this.campoService.getAllPaginated(params).subscribe({
      next: data => this.handleLoadSuccess(data),
      error: () => this.handleLoadError()
    });
  }

  private buildLoadParams(event: PaginatorState | { first: number, rows: number }) {
    const first = event.first || 0;
    const rows = event.rows || 10;

    const params: any = {
      page: Math.floor(first / rows),
      size: rows,
      sort: this.sortField,
      direction: this.sortOrder
    };

    if (this.codigoSeccion) {
      params.searchCriteria = {
        ...this.searchCriteria,
        codigoSeccion: this.codigoSeccion
      };
    } else if (Object.keys(this.searchCriteria).length > 0) {
      params.searchCriteria = this.searchCriteria;
    }

    return params;
  }

  private handleLoadSuccess(data: any) {
    this.rows = data.content || [];
    this.totalRecords = data.totalElements || 0;
    this.page = data.number || 0;
    this.size = data.size || 10;
    this.loading = false;
    this.spinnerService.hide();
  }

  private handleLoadError() {
    this.rows = [];
    this.loading = false;
    this.spinnerService.hide();
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Error al cargar campos'
    });
  }

  async loadCamposForDualView(event?: PaginatorState | { first?: number, rows?: number }) {
    this.loading = true;
    this.spinnerService.show();

    // Usar paginación específica para disponibles
    const first = event?.first || 0;
    const rows = event?.rows || this.availableSize;

    const availableParams = {
      page: Math.floor(first / rows),
      size: rows,
      sort: 'fechaCreacion',
      direction: 'desc'
    };

    try {
      // Primero cargar los campos asociados a esta sección
      const associatedData = await firstValueFrom(
        this.seccionCampoService.getByCodigoSeccion(this.codigoSeccion!, {
          page: 0,
          size: 1000,
          sort: 'orden',
          direction: 'asc'
        })
      );

      const associatedCampoCodes = associatedData.content?.map(rel => rel.codigoCampoFk) || [];

      // Cargar los campos asociados completos
      if (associatedCampoCodes.length > 0) {
        const fullAssociatedCampos = await Promise.all(
          associatedCampoCodes.map(codigo =>
            firstValueFrom(this.campoService.getByCodigo(codigo))
          )
        );
        this.associatedCampos = fullAssociatedCampos.filter(c => c !== null);
      } else {
        this.associatedCampos = [];
      }

      // Cargar TODOS los campos para calcular correctamente los disponibles
      const allCamposParams = {
        page: 0,
        size: 10000, // Obtener todos los campos
        sort: 'fechaCreacion',
        direction: 'desc'
      };

      // Agregar criterios de búsqueda si existen
      if (Object.keys(this.searchCriteria).length > 0) {
        allCamposParams['searchCriteria'] = this.searchCriteria;
      }

      const allCamposData = await firstValueFrom(
        this.campoService.getAllPaginated(allCamposParams)
      );

      // Filtrar TODOS los campos disponibles (no asociados a ESTA sección)
      const allAvailableCampos = allCamposData.content.filter(c =>
        !associatedCampoCodes.includes(c.codigo)
      );

      // Aplicar paginación manual a los campos disponibles
      this.totalAvailableRecords = allAvailableCampos.length;
      const startIndex = availableParams.page * availableParams.size;
      const endIndex = startIndex + availableParams.size;
      this.availableCampos = allAvailableCampos.slice(startIndex, endIndex);

      // Actualizar estado de paginación
      this.availablePage = availableParams.page;
      this.availableSize = availableParams.size;

      this.loading = false;
      this.spinnerService.hide();
    } catch (error) {
      this.loading = false;
      this.spinnerService.hide();
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Error al cargar campos'
      });
    }
  }

  async drop(event: CdkDragDrop<Campo[]>) {
    if (event.previousContainer === event.container) {
      // Reorder within same container
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);

      // If reordering associated campos, update their order in backend IMMEDIATELY
      if (event.container.data === this.associatedCampos) {
        this.spinnerService.show();
        try {
          // Get all current relationships ordered by current UI position
          const relationships = await firstValueFrom(
            this.seccionCampoService.getByCodigoSeccion(this.codigoSeccion!, {
              page: 0,
              size: 1000,
              sort: 'orden',
              direction: 'asc'
            })
          );

          // Create mapping of campo codes to relationships for quick lookup
          const relationMap = new Map<string, SeccionCampo>();
          relationships.content?.forEach(rel => {
            relationMap.set(rel.codigoCampoFk!, rel);
          });

          // Update ALL relationships to match current UI order
          const updatePromises = this.associatedCampos.map((campo, index) => {
            const relationship = relationMap.get(campo.codigo!);
            if (relationship && relationship.orden !== index + 1) {
              return this.seccionCampoService.update(relationship.codigo!, {
                ...relationship,
                orden: index + 1  // New order based on current position
              }).toPromise();
            }
            return Promise.resolve();
          });

          // Execute all updates in parallel
          await Promise.all(updatePromises);

          // Verify the order was saved correctly
          const updatedRelations = await firstValueFrom(
            this.seccionCampoService.getByCodigoSeccion(this.codigoSeccion!, {
              page: 0,
              size: 1000,
              sort: 'orden',
              direction: 'asc'
            })
          );

          // Update UI with confirmed order from backend
          this.associatedCampos = updatedRelations.content?.map(rel =>
            this.associatedCampos.find(c => c.codigo === rel.codigoCampoFk)!
          ).filter(c => c !== undefined) || [];

          this.spinnerService.hide();
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Orden guardado correctamente',
            life: 3000
          });
        } catch (error) {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Error al guardar el orden',
            life: 5000
          });
          // Reload to restore correct order from backend
          this.loadCamposForDualView();
        }
      }
    } else {
      // Transfer between containers - update UI immediately
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );

      const campo = event.container.data[event.currentIndex];

      if (event.container.data === this.associatedCampos) {
        // Adding to section at specific position
        this.addCampoToSeccion(campo, event.currentIndex, false);
      } else {
        // Removing from section
        this.removeCampoFromSeccion(campo, false);
      }
    }
  }

  async addCampoToSeccion(campo: Campo, position?: number, updateUI = true) {
    if (!this.codigoSeccion || !campo.codigo) {
      console.error('Faltan parámetros requeridos');
      return;
    }

    this.spinnerService.show();

    try {
      // Validar que el campo no esté ya asociado
      const existingRelations = await firstValueFrom(
        this.seccionCampoService.getByCodigoCampo(campo.codigo, {
          page: 0,
          size: 1000
        })
      );

      // Verificar si ya existe relación con esta sección
      const alreadyAssociated = existingRelations.content?.some(
        rel => rel.codigoSeccionFk === this.codigoSeccion
      );

      if (alreadyAssociated) {
        this.messageService.add({
          severity: 'warn',
          summary: 'Advertencia',
          detail: 'Este campo ya está asociado a la sección'
        });
        return;
      }

      // Obtener relaciones actuales de la sección
      const currentRelations = await firstValueFrom(
        this.seccionCampoService.getByCodigoSeccion(this.codigoSeccion, {
          page: 0,
          size: 1000,
          sort: 'orden',
          direction: 'asc'
        })
      );

      // Calcular nuevo orden basado en posición o total actual
      let newOrder = position !== undefined ?
        Math.max(1, Math.min(position + 1, (currentRelations.content?.length || 0) + 1)) :
        (currentRelations.content?.length || 0) + 1;

      // Actualizar órdenes posteriores si es necesario
      const updatePromises = [];
      for (const relation of currentRelations.content || []) {
        if (relation.orden >= newOrder) {
          updatePromises.push(
            this.seccionCampoService.update(relation.codigo!, {
              ...relation,
              orden: relation.orden + 1
            }).toPromise()
          );
        }
      }

      if (updatePromises.length > 0) {
        await Promise.all(updatePromises);
      }

      // Crear nueva relación con timestamp único para el código
      const timestamp = new Date().getTime();
      const seccionCampo = {
        codigo: `${this.codigoSeccion}-${campo.codigo}-${timestamp}`,
        codigoSeccionFk: this.codigoSeccion,
        codigoCampoFk: campo.codigo,
        orden: newOrder,
        fechaCreacion: new Date()
      };

      await firstValueFrom(this.seccionCampoService.create(seccionCampo));

      this.spinnerService.hide();
      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: 'Campo agregado a la sección',
        life: 5000
      });

      // Recargar para ver los cambios
      const currentFirst = this.availablePage * this.availableSize;
      this.loadCamposForDualView({ first: currentFirst, rows: this.availableSize });

    } catch (error) {
      this.spinnerService.hide();
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Error al agregar campo'
      });
      // Recargar para restaurar el estado correcto
      this.loadCamposForDualView();
    }
  }

  async removeCampoFromSeccion(campo: Campo, updateUI = true) {
    if (!this.codigoSeccion || !campo.codigo) return;

    this.spinnerService.show();

    try {
      // Obtener todas las relaciones actuales
      const currentRelations = await firstValueFrom(
        this.seccionCampoService.getByCodigoSeccion(this.codigoSeccion, {
          page: 0,
          size: 1000,
          sort: 'orden',
          direction: 'asc'
        })
      );

      // Encontrar la relación a eliminar
      const relationToDelete = currentRelations.content?.find(rel => rel.codigoCampoFk === campo.codigo);

      if (relationToDelete) {
        // Eliminar la relación
        await firstValueFrom(this.seccionCampoService.delete(relationToDelete.codigo!));

        // Reordenar los campos que estaban después de este
        const updatePromises = [];
        for (const relation of currentRelations.content || []) {
          if (relation.orden > relationToDelete.orden) {
            updatePromises.push(
              this.seccionCampoService.update(relation.codigo!, {
                ...relation,
                orden: relation.orden - 1
              }).toPromise()
            );
          }
        }

        // Ejecutar todas las actualizaciones de orden
        if (updatePromises.length > 0) {
          await Promise.all(updatePromises);
        }

        this.spinnerService.hide();
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Campo removido de la sección',
          life: 5000
        });

        // Recargar para ver los cambios
        const currentFirst = this.availablePage * this.availableSize;
        this.loadCamposForDualView({ first: currentFirst, rows: this.availableSize });

      } else {
        this.spinnerService.hide();
        this.messageService.add({
          severity: 'warning',
          summary: 'Advertencia',
          detail: 'No se encontró la relación para eliminar'
        });
      }

    } catch (error) {
      this.spinnerService.hide();
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Error al remover campo'
      });
      // Recargar para restaurar el estado correcto
      this.loadCamposForDualView();
    }
  }

  // Cargar tipos de campo activos
  loadTiposCampo() {
    this.tiposCampoLoading = true;

    this.tipoCampoService.getAllActivosPaginated({
      page: 0,
      size: 10000, // Cargar todos los tipos activos
      sort: 'nombre',
      direction: 'asc'
    }).subscribe({
      next: (data) => {
        this.tiposCampo = data.content || [];
        this.tiposCampoLoading = false;
      },
      error: (error) => {
        this.tiposCampoLoading = false;
        console.error('Error al cargar tipos de campo:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar tipos de campo'
        });
        // Fallback: usar lista básica
        this.tiposCampo = [];
      }
    });
  }

  // Método para obtener el nombre del tipo de campo
  getTipoCampoNombre(codigoTipo?: string): string {
    if (!codigoTipo) return 'Sin tipo';

    const tipo = this.tiposCampo.find(t => t.codigo === codigoTipo);
    // Usa el nombre del servidor si está disponible, sino el fallback local
    return this.fieldPreviewService.getFieldTypeName(codigoTipo, tipo?.nombre);
  }

  getTipoCampoDisplay(campo: Campo): string {
    return this.getTipoCampoNombre(campo.tipoCampo);
  }

  // Método para manejar el cambio de configuración de tabla
  onTablaConfigChange() {
    this.resetTableConfiguration();
    this.initializeTableByType();
    this.updateArrayReferences();
  }

  private resetTableConfiguration() {
    this.formModel.columnas = [];
    this.formModel.filas = [];
  }

  private initializeTableByType() {
    switch (this.formModel.tipoConfiguracionTabla) {
      case 'COLUMNAS_Y_FILAS':
        this.formModel.columnas = [{ nombre: 'Columna 1' }];
        this.formModel.filas = [{ nombre: 'Fila 1' }];
        break;
      case 'SOLO_COLUMNAS':
        this.formModel.columnas = [{ nombre: 'Columna 1' }];
        break;
      case 'SOLO_FILAS':
        this.formModel.filas = [{ nombre: 'Fila 1' }];
        break;
      case 'TABLA_PERSONALIZADA':
        this.initializeCustomTable();
        break;
    }
  }

  private initializeCustomTable() {
    this.formModel.estructuraTablaPersonalizada = {
      filas: [
        {
          celdas: [
            { tipo: 'header', contenido: 'Encabezado 1' },
            { tipo: 'header', contenido: 'Encabezado 2' },
            { tipo: 'header', contenido: 'Encabezado 3' }
          ]
        },
        {
          celdas: [
            { tipo: 'label', contenido: 'Etiqueta 1' },
            { tipo: 'input', contenido: '' },
            { tipo: 'input', contenido: '' }
          ]
        },
        {
          celdas: [
            { tipo: 'label', contenido: 'Etiqueta 2' },
            { tipo: 'input', contenido: '' },
            { tipo: 'input', contenido: '' }
          ]
        }
      ]
    };
  }

  private updateArrayReferences() {
    // Forzar detección de cambios
    this.formModel.columnas = [...this.formModel.columnas];
    this.formModel.filas = [...this.formModel.filas];
  }

  // 5. Modifica openForm para inicializar extraList:
  openForm(campo?: Campo, soloVista: boolean = false) {
    this.editing = !!campo;
    this.viewOnly = soloVista;
    
    if (campo) {
      this.loadCampoForEdit(campo);
    } else {
      this.initializeFormModel();
      this.onTipoCampoChange();
    }
    
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editing = false;
    this.viewOnly = false;
    this.initializeFormModel();
  }

  private initializeFormModel() {
    this.formModel = {
      label: '',
      tipoCampo: '',
      opciones: [],
      columnas: [],
      filas: [],
      minFilas: 1,
      maxFilas: 10,
      // No inicializar propiedades booleanas para que sean undefined por defecto
    };
  }

  // Sobrescribe save para serializar estructura flexible
  save() {
    this.spinnerService.show();

    const dto = this.prepareCampoDTO();
    console.log('DTO preparado:', dto);

    if (dto === null) {
      this.spinnerService.hide();
      return;
    }

    if (this.codigoSeccion && !this.editing) {
      dto.codigo = `${this.codigoSeccion}-${dto.codigo}`;
    }

    console.log('DTO final a enviar:', dto);

    const operation = this.editing && dto.codigo ? 
      this.campoService.update(dto.codigo, dto) : 
      this.campoService.create(dto);

    operation.subscribe({
      next: () => this.handleSaveSuccess(),
      error: (error) => this.handleSaveError(error)
    });
  }

  private handleSaveSuccess() {
    this.spinnerService.hide();
    this.messageService.add({
      severity: 'success',
      summary: 'Éxito',
      detail: this.editing ? 'Campo actualizado' : 'Campo creado'
    });
    this.closeForm();
    this.reloadData();
  }

  private handleSaveError(error: any) {
    this.spinnerService.hide();
    console.error('Error al guardar:', error);
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: error?.error?.message || `Error al ${this.editing ? 'actualizar' : 'crear'}`
    });
  }

  private reloadData() {
    if (this.codigoSeccion) {
      this.loadCamposForDualView();
    } else {
      this.loadData({ first: this.editing ? this.page * this.size : 0, rows: this.size });
    }
  }

  confirmDelete(campo: Campo) {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar el campo ${campo.codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.delete(campo.codigo!)
    });
  }

  confirmClone(campo: Campo) {
    this.campoToClone = campo;
    this.showCloneModal = true;
  }

  closeCloneModal() {
    this.showCloneModal = false;
    this.campoToClone = null;
  }

  async cloneCampo() {
    if (!this.campoToClone) return;

    try {
      this.spinnerService.show();
      
      // Crear una copia del campo con modificaciones
      const campoClonado = {
        ...this.campoToClone,
        id: undefined, // No incluir ID para que se genere uno nuevo
        label: `${this.campoToClone.label}`, // Modificar etiqueta
        //opciones iguales 
        
      };

      // Guardar el campo clonado
      await firstValueFrom(this.campoService.create(campoClonado));
      
      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: `Campo "${this.campoToClone.label}" clonado exitosamente`
      });
      
      // Recargar los datos
      this.loadData();
    } catch (error: any) {
      console.error('Error al clonar campo:', error);
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: error.message || 'Error al clonar el campo'
      });
    } finally {
      this.spinnerService.hide();
      this.closeCloneModal();
    }
  }

  delete(codigo: string) {
    this.loading = true;
    this.spinnerService.show();

    this.campoService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Campo eliminado correctamente'
        });
        if (this.codigoSeccion) {
          this.loadCamposForDualView();
        } else {
          this.loadData({ first: this.page * this.size, rows: this.size });
        }
      },
      error: () => {
        this.loading = false;
        this.spinnerService.hide();
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al eliminar campo'
        });
      }
    });
  }

  // Método para obtener el icono basado en el tipo de campo
  getFieldIcon(codigoTipo?: string): string {
    return this.fieldPreviewService.getFieldIcon(codigoTipo);
  }

  // Elimino getTablePreviewCampo y métodos auxiliares de inputs para la vista previa de tabla, ya que ahora solo se muestran labels.
  // El resto de la lógica de la clase permanece igual.

  getDateValue(dateValue?: string | Date): Date | undefined {
    if (!dateValue) return undefined;
    if (dateValue instanceof Date) return dateValue;
    return new Date(dateValue);
  }

  // Handle search and pagination
  onSearchInputChange(value: string) {
    this.searchValue = value;
    this.debounceSearch();
  }

  private debounceSearch() {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }

    this.searchTimeout = setTimeout(() => {
      this.performSearch();
    }, 500);
  }

  private performSearch() {
    this.searchCriteria = { label: this.searchValue };
    this.loadDataWithSearch();
  }

  handleSearch() {
    this.performSearch();
  }

  handlePageChange(event: PaginatorState) {
    const pageEvent = {
      first: event.first || 0,
      rows: event.rows || (this.codigoSeccion ? this.availableSize : this.size)
    };
    this.loadDataWithSearch(pageEvent);
  }

  private loadDataWithSearch(pageEvent?: any) {
    if (this.codigoSeccion) {
      this.loadCamposForDualView(pageEvent || { first: 0, rows: this.availableSize });
    } else {
      this.loadData(pageEvent || { first: 0, rows: this.size });
    }
  }

  openPreview(campo: Campo) {
    this.campoParaVer = campo;
    this.showPreviewModal = true;
  }

  closePreview() {
    this.showPreviewModal = false;
    this.campoParaVer = null;
  }

  moverCampoAAociados(campo: Campo) {
    // Llama al método de asociación igual que el drag and drop
    this.addCampoToSeccion(campo);
  }

  // Métodos para manejar tabla personalizada
  addCustomRow() {
    this.ensureCustomTableExists();
    this.formModel.estructuraTablaPersonalizada.filas.push({
      celdas: [
        { tipo: 'label', contenido: 'Nueva etiqueta' },
        { tipo: 'input', contenido: '' }
      ]
    });
  }

  removeCustomRow(index: number) {
    if (this.canRemoveCustomRow()) {
      this.formModel.estructuraTablaPersonalizada.filas.splice(index, 1);
    }
  }

  addCustomCell(rowIndex: number) {
    if (this.isValidCustomRow(rowIndex)) {
      this.formModel.estructuraTablaPersonalizada.filas[rowIndex].celdas.push({
        tipo: 'input',
        contenido: ''
      });
    }
  }

  removeCustomCell(rowIndex: number, cellIndex: number) {
    if (this.canRemoveCustomCell(rowIndex, cellIndex)) {
      this.formModel.estructuraTablaPersonalizada.filas[rowIndex].celdas.splice(cellIndex, 1);
    }
  }

  resetCustomTable() {
    this.initializeCustomTable();
  }

  getCellPlaceholder(tipo: string): string {
    const placeholders = {
      'header': 'Ej: Datos Personales',
      'label': 'Ej: Complete los siguientes campos',
      'input': 'Ej: Nombre completo'
    };
    return placeholders[tipo] || 'Contenido';
  }

  getCellTooltip(tipo: string): string {
    const tooltips = {
      'header': 'Encabezado: Se mostrará en negrita como título de sección',
      'label': 'Etiqueta: Texto descriptivo que se mostrará como información',
      'input': 'Campo de entrada: Los usuarios podrán escribir aquí'
    };
    return tooltips[tipo] || 'Contenido de la celda';
  }

  getCellInputTooltip(tipo: string): string {
    const inputTooltips = {
      'header': 'Escribe el título de la sección. Se guardará como encabezado en negrita.',
      'label': 'Escribe el texto descriptivo. Se guardará como etiqueta informativa.',
      'input': 'Escribe el nombre del campo. Este texto aparecerá como etiqueta del campo de entrada en el formulario final.'
    };
    return inputTooltips[tipo] || 'Escribe el contenido que se guardará en la celda';
  }

  // Métodos para reordenar filas
  moveRowUp(index: number) {
    if (index > 0 && this.formModel.estructuraTablaPersonalizada?.filas) {
      const filas = this.formModel.estructuraTablaPersonalizada.filas;
      [filas[index], filas[index - 1]] = [filas[index - 1], filas[index]];
    }
  }

  moveRowDown(index: number) {
    if (this.formModel.estructuraTablaPersonalizada?.filas) {
      const filas = this.formModel.estructuraTablaPersonalizada.filas;
      if (index < filas.length - 1) {
        [filas[index], filas[index + 1]] = [filas[index + 1], filas[index]];
      }
    }
  }

  // Métodos para reordenar celdas
  moveCellLeft(rowIndex: number, cellIndex: number) {
    if (cellIndex > 0 && this.isValidCustomRow(rowIndex)) {
      const celdas = this.formModel.estructuraTablaPersonalizada.filas[rowIndex].celdas;
      [celdas[cellIndex], celdas[cellIndex - 1]] = [celdas[cellIndex - 1], celdas[cellIndex]];
    }
  }

  moveCellRight(rowIndex: number, cellIndex: number) {
    if (this.isValidCustomRow(rowIndex)) {
      const celdas = this.formModel.estructuraTablaPersonalizada.filas[rowIndex].celdas;
      if (cellIndex < celdas.length - 1) {
        [celdas[cellIndex], celdas[cellIndex + 1]] = [celdas[cellIndex + 1], celdas[cellIndex]];
      }
    }
  }

  // Métodos helper para tabla personalizada
  private ensureCustomTableExists() {
    if (!this.formModel.estructuraTablaPersonalizada) {
      this.formModel.estructuraTablaPersonalizada = { filas: [] };
    }
  }

  private canRemoveCustomRow(): boolean {
    return this.formModel.estructuraTablaPersonalizada && 
           this.formModel.estructuraTablaPersonalizada.filas.length > 1;
  }

  private isValidCustomRow(rowIndex: number): boolean {
    return this.formModel.estructuraTablaPersonalizada && 
           this.formModel.estructuraTablaPersonalizada.filas[rowIndex];
  }

  private canRemoveCustomCell(rowIndex: number, cellIndex: number): boolean {
    return this.isValidCustomRow(rowIndex) &&
           this.formModel.estructuraTablaPersonalizada.filas[rowIndex].celdas.length > 1 &&
           cellIndex >= 0 &&
           cellIndex < this.formModel.estructuraTablaPersonalizada.filas[rowIndex].celdas.length;
  }
}
