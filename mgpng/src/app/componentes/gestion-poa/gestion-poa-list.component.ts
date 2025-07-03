import { Component, OnInit } from '@angular/core';
import { MatrizPoaConsultaService } from './gestion-poa-list.service';
import { DtoMatrizPoaConsulta } from './models/gestion-poa.model';

@Component({
  selector: 'app-gestion-poa-list',
  templateUrl: './gestion-poa-list.component.html'
})
export class GestionPoaListComponent implements OnInit {
  rows: DtoMatrizPoaConsulta[] = [];
  totalRecords = 0;
  loading = false;

  // Configuración de columnas para el componente genérico
  columns = [
    { field: '', header: 'ODS' },
    { field: '', header: 'EJE' },
    { field: '', header: 'OBJETIVOS NACIONALES' },
    { field: '', header: 'POLÍTICAS PND' },
    { field: '', header: 'METAS PND' },
    { field: '', header: 'PROGRAMA NACIONAL' },
    { field: '', header: 'OBJETIVOS ESTRATÉGICOS PEDI' },
    { field: '', header: 'OBJETIVOS OPERATIVOS' },
    { field: '', header: 'PRODUCTO INSTITUCIONAL' },
    { field: '', header: 'DESCRIPCION DE LA ACTIVIDAD / PROYECTO ' },
    { field: '', header: 'CENTRO DE GESTOR (UOD)' },
    { field: '', header: 'UOD' },


    { field: 'programa.codigo', header: 'PROGRAMA' },
    //{ field: 'subprograma.codigo', header: 'Subprograma' },
    { field: 'proyecto.codigo', header: 'PROYECTO' },
    { field: 'actividad.codigo', header: 'ACTIVIDAD' },
    { field: 'actividad.nombre', header: 'NOMBRE DE LA ACTIVIDAD' },
    { field: 'estructura', header: 'ESTRUCTURA' },
    { field: 'item.nombre', header: 'NOMBRE ITEM' },
    { field: 'item.codigo', header: 'ITEM' },
    //{ field: 'presSubgrupo.codigo', header: 'Subgrupo' },
    { field: 'presGrupo.codigo', header: 'GRUPO' },
    // { field: 'presNaturaleza.codigo', header: 'Naturaleza' },
    //{ field: 'existeRelacion', header: 'Existe Relación', filterType: 'boolean' },
    { field: 'observacion', header: 'OBSERVACIÓN' }
  ];

  // Parámetros de paginación, orden y búsqueda
  page = 0;
  size = 10;
  sortField = 'fechaCreacion';
  sortOrder = 'desc';
  searchValue = '';

  constructor(private matrizService: MatrizPoaConsultaService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData(event?: any) {
    this.loading = true;
    // Depuración: muestra el evento recibido
    if (event) {
      console.log('loadData event:', event);
      this.page = event.first / event.rows;
      this.size = event.rows;
      this.sortField = event.sortField || this.sortField;
      this.sortOrder = event.sortOrder === 1 ? 'asc' : 'desc';
    }

    const params: any = {
      page: this.page,
      size: this.size,
      sort: this.sortField,
      direction: this.sortOrder,
      searchCriteria: this.searchValue ? { global: this.searchValue } : {}
    };

    this.matrizService.getAllPaginated(params).subscribe({
      next: resp => {
        this.rows = resp.content || [];
        this.totalRecords = resp.totalElements || 0;
        this.loading = false;
                this.rows = resp.content.map(item => ({
          ...item,
          estructura: [
            item.programa?.codigo,
            item.subprograma?.codigo,
            item.proyecto?.codigo,
            item.actividad?.codigo
          ].filter(Boolean).join(' ')
        }));
        // Depuración: muestra los datos cargados
        console.log('rows cargados:', this.rows);
      },
      error: () => {
        this.rows = [];
        this.totalRecords = 0;
        this.loading = false;
      }
    });

    
  }

  onSearch() {
    this.page = 0;
    this.loadData();
  }

}