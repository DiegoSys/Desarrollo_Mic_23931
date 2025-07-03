import { Component, OnInit } from '@angular/core';
import { FormBuildListService } from './form-build-list.service';
import { SeccionService } from './seccion/seccion.service';
import { TipoProyecto } from './models/form-build.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaginatorModule } from 'primeng/paginator';
import { InputTextModule } from 'primeng/inputtext';
import type { PaginatorState } from 'primeng/paginator';
import { Router } from '@angular/router';
import { SpinnerModule } from 'src/app/spinner/spinner.module';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { SpinnerService } from 'src/app/spinner/spinner.service';
import { PreviewCardComponent } from 'src/app/shared/components/preview-card/preview-card.component';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-form-build-list',
  standalone: true,
  templateUrl: './form-build-list.component.html',
  styleUrls: ['./form-build-list.component.scss'],
  imports: [
    CommonModule, 
    FormsModule, 
    PaginatorModule, 
    InputTextModule,
    SpinnerModule,
    ConfirmDialogModule,
    PreviewCardComponent
  ],
  providers: [ConfirmationService]
})
export class FormBuildListComponent implements OnInit {
  
  // Cache para nombres de secciones
  private sectionNamesCache: { [codigo: string]: string } = {};
  
  // Método SINCRÓNICO para obtener la descripción del formulario
  getFormDescription(tipo: any): string {
    const seccionesCount = tipo.secciones?.length || 0;
    if (seccionesCount === 0) {
      return 'Sin secciones configuradas';
    }
    
    // Obtener hasta 3 secciones para mostrar
    const seccionesToShow = tipo.secciones.slice(0, 3);
    const sectionNames: string[] = [];
    
    seccionesToShow.forEach((seccionRel: any) => {
      const codigoSeccion = seccionRel.codigoSeccionFk;
      
      // Usar cache si existe, sino usar código como fallback
      const nombre = this.sectionNamesCache[codigoSeccion] || `Sección ${codigoSeccion}`;
      sectionNames.push(nombre);
    });
    
    let description = `${seccionesCount} sección${seccionesCount > 1 ? 'es' : ''}`;
    if (sectionNames.length > 0) {
      description += `: ${sectionNames.join(', ')}`;
      if (seccionesCount > 3) {
        description += `... (+${seccionesCount - 3} más)`;
      }
    }
    
    return description;
  }

  // Método sincrónico para mostrar mientras se carga
  getFormDescriptionSync(tipo: any): string {
    const seccionesCount = tipo.secciones?.length || 0;
    if (seccionesCount === 0) {
      return 'Sin secciones configuradas';
    }
    
    return `${seccionesCount} sección${seccionesCount > 1 ? 'es' : ''} configuradas`;
  }

    // Método para obtener iconos aleatorios para mostrar como campos
  getFieldsForSection(sectionIndex: number, tipoCodigo?: string): string[] {
    const fieldIcons = [
      'pi-user', 'pi-envelope', 'pi-phone', 'pi-home', 'pi-calendar', 
      'pi-clock', 'pi-map-marker', 'pi-file', 'pi-image', 'pi-star',
      'pi-heart', 'pi-bookmark', 'pi-tag', 'pi-paperclip', 'pi-link',
      'pi-pencil', 'pi-check', 'pi-times', 'pi-plus', 'pi-minus',
      'pi-dollar', 'pi-euro', 'pi-calculator', 'pi-chart-bar', 'pi-chart-pie',
      'pi-globe', 'pi-wifi', 'pi-mobile', 'pi-desktop', 'pi-tablet',
      'pi-camera', 'pi-video', 'pi-microphone', 'pi-volume-up', 'pi-music',
      'pi-users', 'pi-user-plus', 'pi-user-minus', 'pi-key', 'pi-lock'
    ];
    
    // Usar fecha/hora actual y zona horaria para hacer más variable
    const now = new Date();
    const timezoneOffset = now.getTimezoneOffset();
    const dayOfYear = Math.floor((now.getTime() - new Date(now.getFullYear(), 0, 0).getTime()) / (1000 * 60 * 60 * 24));
    const hourMinute = now.getHours() * 60 + now.getMinutes();
    
    // Incluir el código del tipo para hacer únicos los campos por tipología
    const tipoHash = tipoCodigo ? this.hashCode(tipoCodigo) : 0;
    
    // Crear una semilla más compleja basada en sección, zona horaria, tiempo Y tipología
    const seed = (sectionIndex * 1000) + (timezoneOffset * 10) + (dayOfYear % 100) + (hourMinute % 50) + tipoHash;
    const selectedIcons: string[] = [];
    
    // Generar cantidad aleatoria de iconos
    const variabilityFactor = (Math.abs(timezoneOffset) + hourMinute + dayOfYear + sectionIndex * 7 + tipoHash) % 4;
    const iconCount = 4 + variabilityFactor; // Entre 4 y 7 iconos
    
    for (let i = 0; i < iconCount; i++) {
      const randomIndex = (seed + i * 13 + timezoneOffset * 3 + tipoHash * 5) % fieldIcons.length;
      selectedIcons.push(fieldIcons[randomIndex]);
    }
    
    return selectedIcons;
  }

    // Método para usar en el template con fallback - actualizado para proyectos
  getSectionNamesForPreview(tipo: any): string[] {
    if (!tipo.secciones || tipo.secciones.length === 0) {
      return ['No hay secciones'];
    }
    
    const sectionNames: string[] = [];
    const seccionesToShow = tipo.secciones.slice(0, 3); // Mostrar hasta 3 secciones
    
    seccionesToShow.forEach((seccionRel: any) => {
      const codigoSeccion = seccionRel.codigoSeccionFk;
      const nombre = this.sectionNamesCache[codigoSeccion] || `Sección ${codigoSeccion}`;
      sectionNames.push(nombre);
    });
    
    return sectionNames.slice(0, 3);
  }

  // Función auxiliar para generar hash de string
  private hashCode(str: string): number {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convertir a 32bit integer
    }
    return Math.abs(hash);
  }

  rows: TipoProyecto[] = [];
  totalRecords = 0;
  loading = false;
  showForm = false;
  editing = false;
  toDelete: TipoProyecto | null = null;
  globalMessage = '';
  formMessage = '';
  formModel: Partial<TipoProyecto & { descripcion: string }> = {};

  // Paginación
  page = 0;
  size = 10;
  sortField = 'fechaCreacion';
  sortOrder = 'desc';
  searchCriteria: { [key: string]: string } = {};
  
  // Búsqueda
  searchValue = '';
  private searchTimeout: any;

  constructor(
    private service: FormBuildListService,
    private seccionService: SeccionService,
    private router: Router,
    private confirmationService: ConfirmationService,
    public spinnerService: SpinnerService
  ) {}

  ngOnInit() {
    this.loadData({ first: 0, rows: this.size });
  }

  async loadData(event: { first: number, rows: number } = { first: 0, rows: 10 }) {
    this.loading = true;
    this.spinnerService.show();
    const params: any = {
      page: Math.floor((event.first || 0) / (event.rows || 10)),
      size: event.rows || 10,
      sort: this.sortField,
      direction: this.sortOrder
    };
    if (Object.keys(this.searchCriteria).length > 0) {
      params.searchCriteria = this.searchCriteria;
    }
    
    this.service.getAllPaginated(params).subscribe({
      next: async (data) => {
        this.rows = data.content || [];
        this.totalRecords = data.totalElements || 0;
        this.page = data.number || 0;
        this.size = data.size || 10;
        
        // Pre-cargar nombres de secciones para mejor UX
        await this.preloadSectionNames();
        
        this.loading = false;
        this.spinnerService.hide();
      },
      error: () => {
        this.rows = [];
        this.loading = false;
        this.spinnerService.hide();
        this.showGlobalMessage('Error al cargar tipologías');
      }
    });
  }

  // Pre-cargar nombres de secciones para mejor performance
  private async preloadSectionNames() {
    const allSectionCodes = new Set<string>();
    
    // Recopilar todos los códigos de sección únicos
    this.rows.forEach(tipo => {
      tipo.secciones?.forEach((seccionRel: any) => {
        allSectionCodes.add(seccionRel.codigoSeccionFk);
      });
    });

    // Cargar nombres solo para los que no están en cache
    const codesToLoad = Array.from(allSectionCodes).filter(codigo => 
      !this.sectionNamesCache[codigo]
    );

    // Cargar nombres en paralelo (máximo 5 a la vez para no saturar)
    const batchSize = 5;
    for (let i = 0; i < codesToLoad.length; i += batchSize) {
      const batch = codesToLoad.slice(i, i + batchSize);
      const promises = batch.map(async (codigo) => {
        try {
          const seccion = await firstValueFrom(this.seccionService.getByCodigo(codigo));
          if (seccion?.nombre) {
            this.sectionNamesCache[codigo] = seccion.nombre;
          }
        } catch (error) {
          // Silenciar errores individuales y usar fallback
          this.sectionNamesCache[codigo] = `Sección ${codigo}`;
        }
      });
      
      await Promise.all(promises);
    }
  }

  handlePageChange(event: PaginatorState) {
    this.page = event.page ?? 0;
    this.size = event.rows ?? 10;
    this.loadData({ first: event.first ?? 0, rows: event.rows ?? 10 });
  }

  onSortChange(event: { sortField: string, sortOrder: string }) {
    this.sortField = event.sortField;
    this.sortOrder = event.sortOrder;
    this.loadData({ first: 0, rows: this.size });
  }

  onSearch(criteria: { [key: string]: string }) {
    this.searchCriteria = criteria;
    this.loadData({ first: 0, rows: this.size });
  }

  onSearchInputChange(value: string) {
    this.searchValue = value;
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.searchTimeout = setTimeout(() => {
      this.handleSearch();
    }, 500);
  }

  handleSearch() {
    if (this.searchValue.trim()) {
      this.searchCriteria = { 
        nombre: this.searchValue.trim()
      };
    } else {
      this.searchCriteria = {};
    }
    this.page = 0;
    this.loadData({ first: 0, rows: this.size });
  }

  clearSearch() {
    this.searchValue = '';
    this.searchCriteria = {};
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }
    this.page = 0;
    this.loadData({ first: 0, rows: this.size });
  }

  openForm(tipo?: TipoProyecto) {
    this.editing = !!tipo;
    this.formModel = tipo ? { ...tipo } : {};
    this.formMessage = '';
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.formModel = {};
    this.formMessage = '';
  }

  save() {
    this.spinnerService.show();
    if (this.editing && this.formModel.codigo) {
      this.service.update(this.formModel.codigo, this.formModel as TipoProyecto).subscribe({
        next: () => {
          this.showGlobalMessage('Tipología actualizada');
          this.closeForm();
          this.loadData({ first: this.page * this.size, rows: this.size });
        },
        error: () => {
          this.spinnerService.hide();
          this.formMessage = 'Error al actualizar';
        }
      });
    } else {
      this.service.create(this.formModel as TipoProyecto).subscribe({
        next: () => {
          this.showGlobalMessage('Tipología creada');
          this.closeForm();
          this.loadData({ first: 0, rows: this.size });
        },
        error: () => {
          this.spinnerService.hide();
          this.formMessage = 'Error al crear';
        }
      });
    }
  }

  confirmDelete(tipo: TipoProyecto) {
    this.confirmationService.confirm({
      message: `¿Está seguro de eliminar la tipología "${tipo.nombre}"?`,
      header: 'Confirmar eliminación',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Eliminar',
      rejectLabel: 'Cancelar',
      accept: () => this.delete(tipo),
    });
  }

  delete(tipo: TipoProyecto) {
    this.spinnerService.show();
    this.service.delete(tipo.codigo!).subscribe({
      next: () => {
        this.showGlobalMessage('Tipología eliminada');
        this.loadData({ first: this.page * this.size, rows: this.size });
      },
      error: () => {
        this.spinnerService.hide();
        this.showGlobalMessage('Error al eliminar');
      }
    });
  }

  showGlobalMessage(msg: string) {
    this.globalMessage = msg;
    setTimeout(() => this.globalMessage = '', 2500);
  }

  get totalPages(): number {
    return Math.ceil(this.totalRecords / this.size) || 1;
  }

  goToSeccion(tipo: TipoProyecto) {
    this.router.navigate(['formBuild/seccion'], { queryParams: { tipologia: tipo.codigo } });
  }
}
