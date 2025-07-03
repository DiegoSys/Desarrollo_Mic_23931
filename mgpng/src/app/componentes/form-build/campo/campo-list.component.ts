import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { CampoService } from './campo.service';
import { SeccionCampoService, SeccionCampo } from './seccion-campo.service';
import { TipoCampoService, TipoCampo } from './tipo-campo.service';
import { Campo } from '../models/form-build.model';
import { MessageService, ConfirmationService } from 'primeng/api';
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
    PreviewCardComponent
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
  formModel: any = {};
  toDelete: Campo | null = null;
  
  // Código de sección desde query params
  codigoSeccion: string | null = null;

  // Paginación específica para vista dual
  availablePage = 0;
  availableSize = 10;

  // Tipos de campo
  tiposCampo: TipoCampo[] = [];
  tiposCampoLoading = false;

  constructor(
    private campoService: CampoService,
    private seccionCampoService: SeccionCampoService,
    private tipoCampoService: TipoCampoService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

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

  loadData(event: PaginatorState | { first: number, rows: number } = { first: 0, rows: 10 }) {
    this.loading = true;
    this.spinnerService.show();
    
    const first = event.first || 0;
    const rows = event.rows || 10;
    
    const params: any = {
      page: Math.floor(first / rows),
      size: rows,
      sort: this.sortField,
      direction: this.sortOrder
    };
    
    // Agregar filtro por sección si existe
    if (this.codigoSeccion) {
      params.searchCriteria = {
        ...this.searchCriteria,
        codigoSeccion: this.codigoSeccion
      };
    } else if (Object.keys(this.searchCriteria).length > 0) {
      params.searchCriteria = this.searchCriteria;
    }

    // Cargar todos los campos usando CampoService
    this.campoService.getAllPaginated(params).subscribe({
      next: data => {
        this.rows = data.content || [];
        this.totalRecords = data.totalElements || 0;
        this.page = data.number || 0;
        this.size = data.size || 10;
        this.loading = false;
        this.spinnerService.hide();
      },
      error: () => {
        this.rows = [];
        this.loading = false;
        this.spinnerService.hide();
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al cargar campos'
        });
      }
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

      // Calcular el total real de campos disponibles
      this.totalAvailableRecords = allAvailableCampos.length;

      // Aplicar paginación manual solo a los campos que se van a mostrar
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
      size: 100, // Cargar todos los tipos activos
      sort: 'nombre',
      direction: 'asc'
    }).subscribe({
      next: (data) => {
        this.tiposCampo = data.content || [];
        this.tiposCampoLoading = false;
        console.log('Tipos de campo cargados:', this.tiposCampo);
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
    return tipo ? tipo.nombre : codigoTipo;
  }

  getTipoCampoDisplay(campo: Campo): string {
    return this.getTipoCampoNombre(campo.tipoCampo);
  }

  // Inicializar el formulario con tipo por defecto
  openForm(campo?: Campo) {
    this.editing = !!campo;
    this.formModel = campo ? { ...campo } : {
      // Valores por defecto para nuevo campo
      codigoTipoCampoFk: this.tiposCampo.length > 0 ? this.tiposCampo[0].codigo : 'TXT',
      estado: 'A',
      filaMatriz: 1,
      columnaMatriz: 1
    };
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.formModel = {};
    this.editing = false;
  }

  save() {
    this.spinnerService.show();
    
    // Preparar los datos del modelo
    if (this.codigoSeccion && !this.editing) {
      // Si estamos en vista dual y creando nuevo campo
      this.formModel.codigo = `${this.codigoSeccion}-${this.formModel.codigo}`;
    }

    if (this.editing && this.formModel.codigo) {
      // Actualización
      this.campoService.update(this.formModel.codigo, this.formModel).subscribe({
        next: () => {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Campo actualizado'
          });
          this.closeForm();
          if (this.codigoSeccion) {
            this.loadCamposForDualView();
          } else {
            this.loadData({ first: this.page * this.size, rows: this.size });
          }
        },
        error: (error) => {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error?.error?.message || 'Error al actualizar'
          });
        }
      });
    } else {
      // Creación
      this.campoService.create(this.formModel).subscribe({
        next: () => {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Campo creado'
          });
          this.closeForm();
          if (this.codigoSeccion) {
            this.loadCamposForDualView();
          } else {
            this.loadData({ first: 0, rows: this.size });
          }
        },
        error: (error) => {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: error?.error?.message || 'Error al crear'
          });
        }
      });
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
    if (!codigoTipo) return 'pi-question-circle';
    
    switch (codigoTipo.toUpperCase()) {
      case 'TXT':
      case 'INPUT_TEXT':
        return 'pi-font';
      case 'AREA':
      case 'TEXTAREA':
        return 'pi-align-left';
      case 'SEL':
      case 'SELECT_SIMPLE':
        return 'pi-list';
      case 'SELM':
      case 'SELECT_MULTIPLE':
        return 'pi-bars';
      case 'NUM':
      case 'INPUT_NUMBER':
        return 'pi-hashtag';
      case 'DATE':
      case 'INPUT_DATE':
        return 'pi-calendar';
      case 'TIME':
      case 'INPUT_TIME':
        return 'pi-clock';
      case 'EMAIL':
      case 'INPUT_EMAIL':
        return 'pi-envelope';
      case 'FILE':
      case 'INPUT_FILE':
        return 'pi-file';
      case 'CHK':
      case 'CHECKBOX':
        return 'pi-check-square';
      case 'RADIO':
      case 'RADIO_BUTTON':
        return 'pi-circle';
      case 'PASS':
      case 'INPUT_PASSWORD':
        return 'pi-eye-slash';
      default:
        return 'pi-question-circle';
    }
  }

  getDateValue(dateValue?: string | Date): Date | undefined {
    if (!dateValue) return undefined;
    if (dateValue instanceof Date) return dateValue;
    return new Date(dateValue);
  }

  // Handle search input changes with debounce
  onSearchInputChange(value: string) {
    this.searchValue = value;
    
    // Clear previous timeout
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    
    // Set new timeout
    this.searchTimeout = setTimeout(() => {
      this.searchCriteria = { nombre: this.searchValue };
      if (this.codigoSeccion) {
        this.loadCamposForDualView({ first: 0, rows: this.availableSize });
      } else {
        this.loadData({ first: 0, rows: this.size });
      }
    }, 500);
  }

  // Handle search button click
  handleSearch() {
    this.searchCriteria = { nombre: this.searchValue };
    if (this.codigoSeccion) {
      this.loadCamposForDualView({ first: 0, rows: this.availableSize });
    } else {
      this.loadData({ first: 0, rows: this.size });
    }
  }

  // Handle pagination changes
  handlePageChange(event: PaginatorState) {
    const pageEvent = {
      first: event.first || 0,
      rows: event.rows || (this.codigoSeccion ? this.availableSize : this.size)
    };
    if (this.codigoSeccion) {
      this.loadCamposForDualView(pageEvent);
    } else {
      this.loadData(pageEvent);
    }
  }
}
