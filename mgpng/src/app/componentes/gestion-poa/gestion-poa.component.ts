import { Component, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { MenuItem } from 'primeng/api';
import { Router } from '@angular/router';
import { OdsService } from './obj-des-sost/ods.service';
import { OdsModel } from './obj-des-sost/models/obj-des-sost.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-gestion-poa',
  templateUrl: './gestion-poa.component.html',
  styleUrls: ['./gestion-poa.component.scss']
})
export class GestionPOAComponent implements OnInit {
  loading$ = new BehaviorSubject<boolean>(true);
  showOdsTable = false;
  activeIndex = 0;
  selectedOds: any;
  odsOptions: any[] = [];
  odsData: any[] = [];
  odsSelected: any;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  
  steps: MenuItem[] = [
    { label: 'Selección ODS' },
    { label: 'Datos Generales' },
    { label: 'Confirmación' }
  ];

  constructor(
    private router: Router,
    private odsService: OdsService,
    private fb: FormBuilder
  ) {
    this.firstFormGroup = this.fb.group({
      ods: ['', Validators.required]
    });

    this.secondFormGroup = this.fb.group({
      actividad: ['', Validators.required]
    });

    this.router.events.subscribe(() => {
      this.showOdsTable = this.router.url.includes('obj-des-sost');
      if (this.showOdsTable) {
        this.loadOds();
      }
    });
  }

  ngOnInit() {
    this.loadOds();
  }

  loadOds() {
    this.odsService.getAll().subscribe({
      next: (response: any) => {
        const odsList = response.content ? response.content : response;
        
        if (Array.isArray(odsList)) {
          this.odsOptions = odsList.map(ods => ({
            label: ods.descripcion,
            value: ods.codigo
          }));
          this.odsData = odsList.map(ods => ({
            title: ods.descripcion,
            value: ods.codigo
          }));
        } else {
          this.odsOptions = [];
          this.odsData = [];
        }
      },
      error: (err) => {
        console.error("Error al cargar ODS:", err);
        this.odsOptions = [];
        this.odsData = [];
      }
    });
  }

  nextStep() {
    if (this.activeIndex < this.steps.length - 1) {
      this.activeIndex++;
    }
  }

  prevStep() {
    if (this.activeIndex > 0) {
      this.activeIndex--;
    }
  }

  onStepChange(index: number) {
    this.activeIndex = index;
  }

  confirmSubmit() {
    // Lógica para confirmar el envío
    console.log('Formulario enviado');
  }
}
