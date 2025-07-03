import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { OdsService } from '../../lineamiento/estrategico/obj-des-sost/ods.service';
import { ItemService } from '../../lineamiento/operativo/clasif-presup/item/item.service';
import { ActividadService } from '../../lineamiento/operativo/estruct-presup/actividad/actividad.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DtoPOA } from '../models/gestion-poa.model'; // Asegúrate de tener este modelo definido
import { MatrizPoaConsultaService } from '../gestion-poa-list.service'; // Asegúrate de tener el servicio correcto para manejar la creación del POA
import { ActivatedRoute } from '@angular/router';

interface EntityConfig {
  service: any;
  data: any[];
  selected: any[];
  page: number;
  size: number;
  sortField: string;
  sortOrder: string;
  totalRecords: number;
  searchCriteria: { [key: string]: string };
  multiple: boolean;
  labelField: string;
  descField?: string;
  loading: boolean;
}

@Component({
  selector: 'app-gestion-poa-form',
  templateUrl: './gestion-poa-form.component.html',
  styleUrls: ['./gestion-poa-form.component.scss'],
})
export class GestionPOAFormComponent implements OnInit {
  loading$ = new BehaviorSubject<boolean>(true);
  showOdsTable = false;
  firstFormGroup: FormGroup;
  currentStep = 0;
  wizardCompleted = false;
    // Dentro de la clase GestionPOAFormComponent
  searchValues: { [key: string]: string } = {
    ods: '',
    item: '',
    actividad: ''
  };

  // Configuración genérica para cada entidad del wizard
  entities: { [key: string]: EntityConfig } = {};

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private odsService: OdsService,
    private itemService: ItemService,
    private actividadService: ActividadService, // Asegúrate de inyectar ActividadService
    private fb: FormBuilder,
    private matrizPoaConsultaService: MatrizPoaConsultaService, // Asegúrate de inyectar el servicio correcto para manejar la creación del POA
  ) {
    this.firstFormGroup = this.fb.group({
      ods: ['', Validators.required]
    });

    // Configura cada entidad aquí
    this.entities = {
      ods: {
        service: this.odsService,
        data: [],
        selected: [],
        page: 0,
        size: 10,
        sortField: 'fechaCreacion',
        sortOrder: 'desc',
        totalRecords: 0,
        searchCriteria: {},
        multiple: false,
        labelField: 'codigo',
        descField: 'descripcion',
        loading: false
      },
      item: {
        service: this.itemService,
        data: [],
        selected: [],
        page: 0,
        size: 10,
        sortField: 'fechaCreacion',
        sortOrder: 'desc',
        totalRecords: 0,
        searchCriteria: {},
        multiple: true,
        labelField: 'codigo',
        descField: 'descripcion',
        loading: false
      },
        actividad: {
        service: this.actividadService, // Asegúrate de inyectar ActividadService
        data: [],
        selected: [],
        page: 0,
        size: 10,
        sortField: 'fechaCreacion',
        sortOrder: 'desc',
        totalRecords: 0,
        searchCriteria: {},
        multiple: true,
        labelField: 'codigo',
        descField: 'descripcion',
        loading: false
      }
      // Agrega más entidades aquí si lo necesitas
    };

    this.router.events.subscribe(() => {
      this.showOdsTable = this.router.url.includes('obj-des-sost');
      if (this.showOdsTable) {
        this.loadEntity('ods');
      }
    });
  }

  ngOnInit() {
    this.loadEntity('ods');
  }

  onSearchValueChange(entityKey: string, value: string) {
    this.searchValues[entityKey] = value;
  }

  // Método genérico para cargar cualquier entidad
  loadEntity(entityKey: string, event?: { page?: number, size?: number, sortField?: string, sortOrder?: string, searchCriteria?: { [key: string]: string } }) {
    const entity = this.entities[entityKey];
    entity.loading = true;
    this.loading$.next(true);
  
    if (event) {
      entity.page = event.page ?? entity.page;
      entity.size = event.size ?? entity.size;
      entity.sortField = event.sortField ?? entity.sortField;
      entity.sortOrder = event.sortOrder ?? entity.sortOrder;
      entity.searchCriteria = event.searchCriteria ?? entity.searchCriteria;
    }
  
    const params = {
      page: entity.page,
      size: entity.size,
      sort: entity.sortField,
      direction: entity.sortOrder,
      searchCriteria: entity.searchCriteria
    };
  
    // Llama al método correcto según la entidad
    let obs;
    if (entityKey === 'item') {
      obs = entity.service.getAllPaginated(params);
    } else if (entityKey === 'actividad') {
      obs = entity.service.getAllActivosPaginated(params);
    } else {
      obs = entity.service.getAll(params);
    }
  
    obs.subscribe({
      next: (response: any) => {
        entity.data = Array.isArray(response.content) ? response.content : [];
        entity.totalRecords = response.totalElements || 0;
        entity.loading = false;
        this.loading$.next(false);
      },
      error: (err) => {
        entity.data = [];
        entity.loading = false;
        this.loading$.next(false);
      }
    });
  }

  // Métodos de paginación, búsqueda y ordenamiento genéricos
  onPageChange(entityKey: string, event: any) {
    this.loadEntity(entityKey, { page: event.page, size: event.rows });
  }
  onSortChange(entityKey: string, event: { sortField: string, sortOrder: string | number }) {
    let direction = event.sortOrder === 1 || event.sortOrder === 'asc' ? 'asc' : 'desc';
    this.loadEntity(entityKey, { sortField: event.sortField, sortOrder: direction });
  }
  onSearch(entityKey: string, criteria: { [key: string]: string }) {
    this.loadEntity(entityKey, { searchCriteria: criteria, page: 0 });
  }

  onSelectionChange(entityKey: string, selected: any[]) {
    this.entities[entityKey].selected = selected;
    if (entityKey === 'ods') {
      this.firstFormGroup.get('ods')?.setValue(selected.map(item => item.codigo).join(','));
    }
  }

 


  // Wizard step change
  onStepChange(stepIndex: number) {
    this.currentStep = stepIndex;
    if (stepIndex === 0) this.loadEntity('ods');
    if (stepIndex === 1) this.loadEntity('item');
    if (stepIndex === 2) this.loadEntity('actividad');
  }

onWizardComplete() {
  // Construye el DTO con los IDs seleccionados
  const dto: DtoPOA = {
    actividadID: this.entities['actividad'].selected[0]?.id,
    itemID: this.entities['item'].selected[0]?.id,
    // Puedes agregar otros campos si es necesario
  };


  this.matrizPoaConsultaService.create(dto).subscribe({
    next: (created) => {
      // Maneja la respuesta, por ejemplo, mostrar mensaje de éxito o refrescar la vista
      this.router.navigate(['../'], { relativeTo: this.route });

    },
    error: (err) => {
      // Maneja el error
      console.error('Error al crear POA:', err);
    }
  });
}
}