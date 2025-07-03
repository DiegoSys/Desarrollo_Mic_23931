import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { SeccionService } from './seccion.service';
import { ProyectoSeccionService } from './proyecto-seccion.service';
import { CampoService } from '../campo/campo.service';
import { SeccionCampoService } from '../campo/seccion-campo.service';
import { Seccion, Campo } from '../models/form-build.model';
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
  selector: 'app-seccion-list',
  standalone: true,
  templateUrl: './seccion-list.component.html',
  styleUrls: ['./seccion-list.component.scss'],
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
export class SeccionListComponent implements OnInit {
  rows: Seccion[] = [];
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
  availableSections: Seccion[] = [];
  associatedSections: Seccion[] = [];
  totalAvailableRecords = 0;

  // Formulario
  showForm = false;
  editing = false;
  formModel: any = {};
  toDelete: Seccion | null = null;
  
  // Código de tipología desde query params
  codigoTipologia: string | null = null;

  // Paginación específica para vista dual
  availablePage = 0;
  availableSize = 10;

  // Campos asociados a cada sección
  sectionFields: { [sectionCode: string]: Campo[] } = {};
  sectionFieldsLoading: { [sectionCode: string]: boolean } = {};

  constructor(
    private seccionService: SeccionService,
    private proyectoSeccionService: ProyectoSeccionService,
    private campoService: CampoService,
    private seccionCampoService: SeccionCampoService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    // Obtener el código de tipología de los query params
    this.route.queryParams.subscribe(params => {
      this.codigoTipologia = params['tipologia'] || null;
      console.log('Código de tipología:', this.codigoTipologia);
      
      if (this.codigoTipologia) {
        this.loadSectionsForDualView();
      } else {
        this.loadData({ first: 0, rows: this.size });
      }
    });
  }

  loadData(event: { first: number, rows: number } = { first: 0, rows: 10 }) {
    this.loading = true;
    this.spinnerService.show();
    
    const params: any = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10,
      sort: this.sortField,
      direction: this.sortOrder
    };
    
    // Agregar filtro por tipología si existe
    if (this.codigoTipologia) {
      params.searchCriteria = {
        ...this.searchCriteria,
        codigoTipologia: this.codigoTipologia
      };
    } else if (Object.keys(this.searchCriteria).length > 0) {
      params.searchCriteria = this.searchCriteria;
    }

    // Cargar todas las secciones usando SeccionService
    this.seccionService.getAllPaginated(params).subscribe({
      next: async data => {
        this.rows = data.content || [];
        this.totalRecords = data.totalElements || 0;
        this.page = data.number || 0;
        this.size = data.size || 10;
        
        // Cargar campos para cada sección
        if (this.rows.length > 0) {
          await Promise.all(
            this.rows.map(seccion => this.loadSectionFields(seccion.codigo!))
          );
        }
        
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
          detail: 'Error al cargar secciones'
        });
      }
    });
  }

  async loadSectionsForDualView(event?: { first?: number, rows?: number }) {
    this.loading = true;
    this.spinnerService.show();

    // Usar paginación específica para disponibles
    const availableParams = {
      page: event ? Math.floor((event.first || 0) / (event.rows || 10)) : this.availablePage,
      size: event?.rows || this.availableSize,
      sort: 'fechaCreacion',
      direction: 'desc'
    };

    try {
      // Primero cargar las secciones asociadas a este proyecto
      const associatedData = await firstValueFrom(
        this.proyectoSeccionService.getByCodigoProyecto(this.codigoTipologia!, {
          page: 0,
          size: 1000,
          sort: 'orden', 
          direction: 'asc'
        })
      );

      const associatedSectionCodes = associatedData.content?.map(rel => rel.codigoSeccionFk) || [];
      
      // Cargar las secciones asociadas completas
      if (associatedSectionCodes.length > 0) {
        const fullAssociatedSections = await Promise.all(
          associatedSectionCodes.map(codigo => 
            firstValueFrom(this.seccionService.getByCodigo(codigo))
          )
        );
        this.associatedSections = fullAssociatedSections.filter(s => s !== null);
        
        // Cargar campos para las secciones asociadas
        await Promise.all(
          this.associatedSections.map(seccion => this.loadSectionFields(seccion.codigo!))
        );
      } else {
        this.associatedSections = [];
      }
      
      // Cargar TODAS las secciones para calcular correctamente las disponibles
      const allSectionsParams = {
        page: 0,
        size: 10000, // Obtener todas las secciones
        sort: 'fechaCreacion',
        direction: 'desc'
      };

      // Agregar criterios de búsqueda si existen
      if (Object.keys(this.searchCriteria).length > 0) {
        allSectionsParams['searchCriteria'] = this.searchCriteria;
      }

      const allSectionsData = await firstValueFrom(
        this.seccionService.getAllPaginated(allSectionsParams)
      );

      // Filtrar TODAS las secciones disponibles (no asociadas a ESTE proyecto)
      const allAvailableSections = allSectionsData.content.filter(s => 
        !associatedSectionCodes.includes(s.codigo)
      );

      // Calcular el total real de secciones disponibles
      this.totalAvailableRecords = allAvailableSections.length;

      // Aplicar paginación manual solo a las secciones que se van a mostrar
      const startIndex = availableParams.page * availableParams.size;
      const endIndex = startIndex + availableParams.size;
      
      this.availableSections = allAvailableSections.slice(startIndex, endIndex);
      
      // Cargar campos para las secciones disponibles
      if (this.availableSections.length > 0) {
        await Promise.all(
          this.availableSections.map(seccion => this.loadSectionFields(seccion.codigo!))
        );
      }
      
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
        detail: 'Error al cargar secciones'
      });
    }
  }

  async drop(event: CdkDragDrop<Seccion[]>) {
    if (event.previousContainer === event.container) {
      // Reorder within same container
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      
      // If reordering associated sections, update their order in backend
      if (event.container.data === this.associatedSections) {
        this.spinnerService.show();
        try {
          // Get all current relationships
          const relationships = await firstValueFrom(
            this.proyectoSeccionService.getByCodigoProyecto(this.codigoTipologia!, {
              page: 0,
              size: 1000
            })
          );

          // Update each section's order based on new position
          const updatePromises = this.associatedSections.map((seccion, index) => {
            const relationship = relationships.content?.find(rel => rel.codigoSeccionFk === seccion.codigo);
            if (relationship) {
              return this.proyectoSeccionService.update(relationship.codigo, {
                ...relationship,
                orden: index + 1
              }).toPromise();
            }
            return Promise.resolve();
          });

          await Promise.all(updatePromises);
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Orden actualizado correctamente'
          });
        } catch (error) {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Error al actualizar el orden'
          });
          // Reload to restore correct order
          this.loadSectionsForDualView();
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

      const section = event.container.data[event.currentIndex];
      
      if (event.container.data === this.associatedSections) {
        // Adding to typology at specific position
        this.addSectionToTypology(section, event.currentIndex, false);
      } else {
        // Removing from typology
        this.removeSectionFromTypology(section, false);
      }
    }
  }

  addSectionToTypology(seccion: Seccion, position?: number, updateUI = true) {
    if (!this.codigoTipologia || !seccion.codigo) return;

    this.spinnerService.show();
    const proyectoSeccion = {
      codigo: `${this.codigoTipologia}-${seccion.codigo}`,
      codigoProyectoFK: this.codigoTipologia,
      codigoSeccionFk: seccion.codigo,
      orden: position !== undefined ? position + 1 : this.associatedSections.length + 1
    };

    this.proyectoSeccionService.create(proyectoSeccion).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Sección agregada a la tipología',
          life: 5000
        });
        
        // Recargar manteniendo la página actual si es posible
        const currentFirst = this.availablePage * this.availableSize;
        this.loadSectionsForDualView({ first: currentFirst, rows: this.availableSize });
      },
      error: () => {
        this.spinnerService.hide();
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al agregar sección'
        });
      }
    });
  }

  removeSectionFromTypology(seccion: Seccion, updateUI = true) {
    if (!this.codigoTipologia || !seccion.codigo) return;

    this.spinnerService.show();
    this.proyectoSeccionService.getByCodigoProyecto(this.codigoTipologia, {
      page: 0,
      size: 1000
    }).subscribe({
      next: (data) => {
        const relationship = data.content?.find(rel => rel.codigoSeccionFk === seccion.codigo);
        if (relationship) {
          this.proyectoSeccionService.delete(relationship.codigo).subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Sección removida de la tipología',
                life: 5000
              });
              
              // Recargar manteniendo la página actual
              const currentFirst = this.availablePage * this.availableSize;
              this.loadSectionsForDualView({ first: currentFirst, rows: this.availableSize });
            },
            error: () => {
              this.spinnerService.hide();
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'Error al remover sección',
                life: 5000
              });
            }
          });
        } else {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'warning',
            summary: 'Advertencia',
            detail: 'No se encontró la relación para eliminar'
          });
        }
      },
      error: () => {
        this.spinnerService.hide();
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Error al buscar la relación'
        });
      }
    });
  }

  handlePageChange(event: PaginatorState) {
    if (this.codigoTipologia) {
      // Para vista dual, usar la paginación específica de disponibles
      this.loadSectionsForDualView({ 
        first: event.first ?? 0, 
        rows: event.rows ?? 10 
      });
    } else {
      // Para vista normal
      this.page = event.page ?? 0;
      this.size = event.rows ?? 10;
      this.loadData({ 
        first: event.first ?? 0, 
        rows: event.rows ?? 10 
      });
    }
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadData({ first: 0, rows: this.size });
  }

  onSearch(criteria: { [key: string]: string }) {
    this.searchCriteria = criteria;
    if (this.codigoTipologia) {
      this.loadSectionsForDualView();
    } else {
      this.loadData({ first: 0, rows: this.size });
    }
  }

  onSearchInputChange(value: string) {
    this.searchValue = value;
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.searchTimeout = setTimeout(() => {
      this.handleSearch();
    }, 500); // Debounce de 500ms
  }

  handleSearch() {
    if (this.searchValue.trim()) {
      this.searchCriteria = { 
        nombre: this.searchValue.trim(),
        codigo: this.searchValue.trim(),
        descripcion: this.searchValue.trim()
      };
    } else {
      this.searchCriteria = {};
    }
    this.page = 0;
    this.availablePage = 0;
    
    if (this.codigoTipologia) {
      this.loadSectionsForDualView({ first: 0, rows: this.availableSize });
    } else {
      this.loadData({ first: 0, rows: this.size });
    }
  }

  clearSearch() {
    this.searchValue = '';
    this.searchCriteria = {};
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.page = 0;
    this.availablePage = 0;
    
    if (this.codigoTipologia) {
      this.loadSectionsForDualView({ first: 0, rows: this.availableSize });
    } else {
      this.loadData({ first: 0, rows: this.size });
    }
  }

  openForm(seccion?: Seccion) {
    this.editing = !!seccion;
    this.formModel = seccion ? { ...seccion } : {};
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.formModel = {};
    this.editing = false;
  }

  save() {
    this.spinnerService.show();
    if (this.editing && this.formModel.codigo) {
      this.seccionService.update(this.formModel.codigo, this.formModel).subscribe({
        next: () => {
          this.spinnerService.hide();
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Sección actualizada'
          });
          this.closeForm();
          if (this.codigoTipologia) {
            this.loadSectionsForDualView();
          } else {
            this.loadData({ first: this.page * this.size, rows: this.size });
          }
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Error al actualizar'
          });
        }
      });
    } else {
      this.spinnerService.show();
      this.seccionService.create(this.formModel).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Sección creada'
          });
          this.closeForm();
          if (this.codigoTipologia) {
            this.loadSectionsForDualView();
          } else {
            this.loadData({ first: 0, rows: this.size });
          }
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Error al crear'
          });
        }
      });
    }
  }

  confirmDelete(seccion: Seccion) {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar la sección ${seccion.codigo}?`,
      header: 'Confirmación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí',
      rejectLabel: 'No',
      accept: () => this.delete(seccion.codigo!)
    });
  }

  delete(codigo: string) {
    this.loading = true;
    this.spinnerService.show();

    this.seccionService.delete(codigo).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Sección eliminada correctamente'
        });
        if (this.codigoTipologia) {
          this.loadSectionsForDualView();
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
          detail: 'Error al eliminar sección'
        });
      }
    });
  }

  // Métodos para manejo de campos asociados a secciones
  async loadSectionFields(sectionCode: string): Promise<void> {
    if (this.sectionFieldsLoading[sectionCode] || this.sectionFields[sectionCode]) {
      return; // Ya se está cargando o ya está cargado
    }

    this.sectionFieldsLoading[sectionCode] = true;

    try {
      // Obtener las relaciones sección-campo
      const seccionCampoData = await firstValueFrom(
        this.seccionCampoService.getByCodigoSeccion(sectionCode, {
          page: 0,
          size: 1000,
          sort: 'orden',
          direction: 'asc'
        })
      );

      const camposCodes = seccionCampoData.content?.map((rel: any) => rel.codigoCampoFk) || [];
      
      if (camposCodes.length > 0) {
        // Cargar los campos completos
        const camposCompletos = await Promise.all(
          camposCodes.map((codigo: string) => 
            firstValueFrom(this.campoService.getByCodigo(codigo))
          )
        );
        this.sectionFields[sectionCode] = camposCompletos.filter(c => c !== null);
      } else {
        this.sectionFields[sectionCode] = [];
      }
    } catch (error) {
      console.error('Error loading fields for section:', sectionCode, error);
      this.sectionFields[sectionCode] = [];
    } finally {
      this.sectionFieldsLoading[sectionCode] = false;
    }
  }

  getSectionFieldsForPreview(sectionCode: string): string {
    if (this.sectionFieldsLoading[sectionCode]) {
      return 'Cargando campos...';
    }
    
    const fields = this.sectionFields[sectionCode];
    if (!fields || fields.length === 0) {
      return 'No hay campos';
    }
    
    if (fields.length === 1) {
      return `1 campo: ${fields[0].nombre}`;
    } else if (fields.length <= 3) {
      return `${fields.length} campos: ${fields.map(f => f.nombre).join(', ')}`;
    } else {
      return `${fields.length} campos: ${fields.slice(0, 2).map(f => f.nombre).join(', ')} y ${fields.length - 2} más`;
    }
  }

  getSectionFieldsDescription(sectionCode: string): string {
    if (this.sectionFieldsLoading[sectionCode]) {
      return 'Cargando...';
    }
    
    const fields = this.sectionFields[sectionCode];
    if (!fields || fields.length === 0) {
      return 'No contiene campos';
    }
    
    return `Contiene ${fields.length} campo${fields.length === 1 ? '' : 's'}`;
  }

  goToCampo(seccion: Seccion) {
    // Navegar a la vista de campos de esta sección
    this.router.navigate(['formBuild/campo'], { queryParams: { seccion: seccion.codigo } });
  }
}
