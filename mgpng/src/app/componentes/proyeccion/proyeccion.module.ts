import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProyeccionRoutingModule } from './proyeccion-routing.module';
import { ExampleComponent } from './example/example.component';


@NgModule({
  declarations: [
    ExampleComponent
  ],
  imports: [
    CommonModule,
    ProyeccionRoutingModule
  ]
})
export class ProyeccionModule { }
